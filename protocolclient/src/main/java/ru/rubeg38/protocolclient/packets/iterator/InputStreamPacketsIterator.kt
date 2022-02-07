package ru.rubeg38.protocolclient.packets.iterator

import ru.rubeg38.protocolclient.packets.ContentType
import ru.rubeg38.protocolclient.packets.Headers
import ru.rubeg38.protocolclient.packets.Packet
import java.io.BufferedInputStream
import java.io.InputStream

internal class InputStreamPacketsIterator(
    source: InputStream,
    messageSize: Int,
    token: String?,
    messageId: Long,
    packetSize: Int
) : PacketsIterator(token, messageId, messageSize, packetSize) {

    private val input = BufferedInputStream(source)
    private val buffer = ByteArray(packetSize)

    override fun hasNext(): Boolean = shift < messageSize

    override fun next(): Packet {
        val packetSize = input.read(buffer, 0, packetSize)

        if (packetSize == -1) {
            throw NoSuchElementException()
        }

        val headers = Headers(
            contentType = ContentType.Binary,
            messageId = messageId,
            messageSize = messageSize,
            packetsCount = packetsCount,
            packetNumber = packetNumber,
            packetSize = packetSize,
            shift = shift,
            token = token
        )

        packetNumber++
        shift += packetSize

        return Packet(headers, buffer.sliceArray(0 until packetSize))
    }

}