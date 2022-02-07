package ru.rubeg38.protocolclient.packets.iterator

import ru.rubeg38.protocolclient.packets.ContentType
import ru.rubeg38.protocolclient.packets.Headers
import ru.rubeg38.protocolclient.packets.Packet

internal class ByteArrayPacketsIterator(
    private val source: ByteArray,
    private val contentType: ContentType,
    token: String?,
    messageId: Long,
    packetSize: Int
) : PacketsIterator(token, messageId, source.count(), packetSize) {

    override fun hasNext(): Boolean = shift < source.count()

    override fun next(): Packet {
        if (shift >= source.count()) {
            throw NoSuchElementException()
        }

        var rightBound = shift + packetSize

        if (rightBound > source.count()) {
            rightBound = source.count()
        }

        val chunk = source.sliceArray(shift until rightBound)

        val headers = Headers(
            contentType = contentType,
            messageId = messageId,
            messageSize = source.count(),
            packetsCount = packetsCount,
            packetNumber = packetNumber,
            packetSize = chunk.count(),
            shift = shift,
            token = token
        )

        packetNumber++
        shift = rightBound

        return Packet(headers, chunk)
    }

}