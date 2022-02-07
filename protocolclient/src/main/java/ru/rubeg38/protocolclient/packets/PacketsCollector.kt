package ru.rubeg38.protocolclient.packets

import java.nio.ByteBuffer

/**
 *
 */
internal class PacketsCollector(
    private val messageId: Long,
    messageSize: Int,
    packetsCount: Int
) {

    private var dataBuffer = ByteBuffer.allocate(messageSize)
    private var packetFlags = BooleanArray(packetsCount) { false }

    /**
     *
     */
    val isDone: Boolean
        get() = packetFlags.all { it }

    /**
     *
     */
    fun collect(packet: Packet): Boolean {
        if (packet.headers.messageId != messageId) {
            return isDone
        }

        packetFlags[packet.headers.packetNumber - 1] = true

        dataBuffer.position(packet.headers.shift)
        dataBuffer.put(packet.payload ?: byteArrayOf())

        return isDone
    }

    /**
     *
     */
    fun getData(): ByteArray = dataBuffer.array()

}