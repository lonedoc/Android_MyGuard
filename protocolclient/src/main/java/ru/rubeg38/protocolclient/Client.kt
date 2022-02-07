package ru.rubeg38.protocolclient

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import ru.rubeg38.protocolclient.packets.ContentType
import ru.rubeg38.protocolclient.packets.iterator.PacketsIterator
import ru.rubeg38.protocolclient.packets.Packet
import ru.rubeg38.protocolclient.packets.PacketWrapper
import ru.rubeg38.protocolclient.packets.PacketsCollector
import ru.rubeg38.protocolclient.utils.address
import java.io.IOException
import java.io.InputStream
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.DatagramChannel
import kotlin.random.Random

/**
 * A client for network communication over proprietary "Rubezh NPO" protocol
 */
class Client(private val config: ClientConfig = ClientConfig.default) {

    private lateinit var address: Address

    private var congestionWindow: MutableList<PacketWrapper> = mutableListOf()

    private val datagramChannel: DatagramChannel by lazy {
        DatagramChannel.open().apply {
            configureBlocking(false)
        }
    }

    /**
     * Binds the client to given remote address
     * @param[address] Remote address
     * @throws IllegalArgumentException
     */
    fun bind(address: Address) {
        this.address = address
    }

    /**
     * Sends a message to the bound remote address
     * @param message A message to send
     * @param token An access token
     */
    suspend fun sendMessage(message: String, token: String?) = withContext(Dispatchers.IO) {
        val messageId = generateMessageId()
        val packetsIterator = PacketsIterator.create(message, token, messageId, config.packetSize)

        send(packetsIterator)
    }

    /**
     * Sends a message to the bound remote address
     * @param message A message to send
     * @param token An access token
     */
    suspend fun sendMessage(message: ByteArray, token: String?) = withContext(Dispatchers.IO) {
        val messageId = generateMessageId()
        val packetsIterator = PacketsIterator.create(message, token, messageId, config.packetSize)

        send(packetsIterator)
    }

    /**
     * Sends a message to the bound remote address
     * @param message A message to send
     * @param size The size of the message
     * @param token An access token
     */
    suspend fun sendMessage(message: InputStream, size: Int, token: String?) =
        withContext(Dispatchers.IO) {
            val messageId = generateMessageId()
            val packetsIterator = PacketsIterator.create(message, size, token, messageId, config.packetSize)

            send(packetsIterator)
        }

    /**
     * Sends a request to the bound remote address and receives the response
     * @param query A query to send
     * @param token An access token
     * @return a server response as [ByteArray]
     */
    suspend fun sendRequest(query: String, token: String?): ByteArray = withContext(Dispatchers.IO) {
        val messageId = generateMessageId()
        val packetsIterator = PacketsIterator.create(query, token, messageId, config.packetSize)

        send(packetsIterator)
        receiveResponse(messageId)
    }

    /**
     * Sends a request to the bound remote address and receives the response
     * @param data A data to send
     * @param token An access token
     * @return a server response as [ByteArray]
     */
    suspend fun sendRequest(data: ByteArray, token: String?): ByteArray = withContext(Dispatchers.IO) {
        val messageId = generateMessageId()
        val packetsIterator = PacketsIterator.create(data, token, messageId, config.packetSize)

        send(packetsIterator)
        receiveResponse(messageId)
    }

    /**
     * Sends a request to the bound remote address and receives the response
     * @param data A data to send
     * @param size The size of the data
     * @param token An access token
     * @return a server response as [ByteArray]
     */
    suspend fun sendRequest(data: InputStream, size: Int, token: String?): ByteArray =
        withContext(Dispatchers.IO) {
            val messageId = generateMessageId()
            val packetsIterator = PacketsIterator.create(data, size, token, messageId, config.packetSize)

            send(packetsIterator)
            receiveResponse(messageId)
        }

    private fun generateMessageId(): Long {
        val seed = System.currentTimeMillis()
        return Random(seed).nextLong(0, Long.MAX_VALUE)
    }

    private suspend fun send(packetsIterator: PacketsIterator) {
        while (true) {
            retryOrFail()
            sendNextPackets(packetsIterator)
            receiveAndHandleAcknowledgments()

            if (congestionWindow.isEmpty() && !packetsIterator.hasNext()) {
                break
            }

            if (congestionWindow.count() == config.congestionWindowSize) {
                delay(config.sleepInterval)
            }
        }
    }

    private fun retryOrFail() {
        val readyToRetry = congestionWindow.filter(PacketWrapper::isReadyToRetry)

        val isFailed = readyToRetry.any { packetWrapper ->
            packetWrapper.attemptsCount >= config.maxAttemptsCount
        }

        if (isFailed) {
            throw TimeoutException("Server is not responding")
        }

        readyToRetry.forEach { packetWrapper ->
            packetWrapper.attemptsCount++
            packetWrapper.timeToRetry = System.currentTimeMillis() + config.retryInterval

            if (BuildConfig.DEBUG) {
                logPacket(
                    packet = packetWrapper.packet,
                    address = address,
                    prefix = "-> ".repeat(packetWrapper.attemptsCount).trimEnd()
                )
            }

            sendPacket(packetWrapper.packet)
        }
    }

    private fun sendNextPackets(packetsIterator: PacketsIterator) {
        while (congestionWindow.count() < config.congestionWindowSize && packetsIterator.hasNext()) {
            val packetWrapper = PacketWrapper(
                packet = packetsIterator.next(),
                attemptsCount = 1,
                timeToRetry = System.currentTimeMillis() + config.retryInterval
            )

            congestionWindow.add(packetWrapper)

            if (BuildConfig.DEBUG) {
                logPacket(
                    packet = packetWrapper.packet,
                    address = address,
                    prefix = "-> ".repeat(packetWrapper.attemptsCount).trimEnd()
                )
            }

            sendPacket(packetWrapper.packet)
        }
    }

    private fun receiveAndHandleAcknowledgments() {
        var (incomingPacket, senderAddress) = receivePacket()

        while (incomingPacket != null && congestionWindow.isNotEmpty()) {
            if (BuildConfig.DEBUG) {
                logPacket(
                    packet = incomingPacket,
                    address = senderAddress,
                    prefix = "<-"
                )
            }

            if (incomingPacket.headers.contentType == ContentType.Acknowledgment) {
                congestionWindow.removeAll { packetWrapper ->
                    incomingPacket!!.isAcknowledgmentOf(packetWrapper.packet)
                }
            }

            if (congestionWindow.isNotEmpty()) {
                val received = receivePacket()
                incomingPacket = received.first
                senderAddress = received.second
            }
        }
    }

    private suspend fun receiveResponse(expectedMessageId: Long): ByteArray {
        var collector: PacketsCollector? = null

        var deadline = System.currentTimeMillis() + config.readTimeout

        while (true) {
            if (System.currentTimeMillis() > deadline) {
                throw TimeoutException("Server is not responding")
            }

            var (incomingPacket, senderAddress) = receivePacket()

            while (incomingPacket != null) {
                if (BuildConfig.DEBUG) {
                    logPacket(
                        packet = incomingPacket,
                        address = senderAddress,
                        prefix = "<-"
                    )
                }

                val isNotExpected = incomingPacket.headers.messageId != expectedMessageId
                val hasIdentifier = incomingPacket.headers.messageId != 0L

                if (isNotExpected && (config.skipUnidentified || hasIdentifier)) {
                    val received = receivePacket()
                    incomingPacket = received.first
                    senderAddress = received.second

                    continue
                }

                deadline = System.currentTimeMillis() + config.readTimeout

                if (incomingPacket.headers.contentType == ContentType.Error) {
                    throw ServerException("Unknown server error")
                }

                val dataContentTypes = listOf(
                    ContentType.Binary,
                    ContentType.String,
                    ContentType.LegacyString
                )

                if (incomingPacket.headers.contentType !in dataContentTypes) {
                    val received = receivePacket()
                    incomingPacket = received.first
                    senderAddress = received.second

                    continue
                }
                
                sendAcknowledgment(incomingPacket)

                // Collect data
                if (collector == null) {
                    collector = PacketsCollector(
                        incomingPacket.headers.messageId,
                        incomingPacket.headers.messageSize,
                        incomingPacket.headers.packetsCount
                    )
                }

                collector.collect(incomingPacket)

                if (collector.isDone) {
                    return collector.getData()
                }

                val received = receivePacket()
                incomingPacket = received.first
                senderAddress = received.second
            }

            delay(config.sleepInterval)
        }
    }

    private fun sendAcknowledgment(incomingPacket: Packet) {
        val acknowledgment = Packet.createAcknowledgment(incomingPacket)

        if (BuildConfig.DEBUG) {
            logPacket(
                packet = acknowledgment,
                address = address,
                prefix = "->"
            )
        }

        sendPacket(acknowledgment)
    }

    private fun sendPacket(packet: Packet) {
        val address = InetSocketAddress(address.ip, address.port)
        val buffer = ByteBuffer.wrap(packet.encode())

        datagramChannel.send(buffer, address)
    }

    private fun receivePacket(): Pair<Packet?, Address?> {
        val buffer = ByteBuffer.allocate(config.packetSize * 2)
        val senderAddress: Address?

        try {
            val socketAddress = datagramChannel.receive(buffer)
            senderAddress = socketAddress?.address

            buffer.flip()
            if (!buffer.hasRemaining()) {
                return null to senderAddress
            }
        } catch (ex: IOException) {
            if (BuildConfig.DEBUG) {
                ex.printStackTrace()
            }
            return null to null
        }

        return Packet.decode(buffer.array()) to senderAddress
    }

    private fun logPacket(packet: Packet, address: Address?, prefix: String) {
        val addressPart = address?.let { "${it.ip}:${it.port}" } ?: "unknown address"
        val entry = "$prefix [$addressPart] $packet"
        Log.d(this::class.simpleName, entry)
    }

}