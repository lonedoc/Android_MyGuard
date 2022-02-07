package ru.rubeg38.protocolclient.packets.iterator

import ru.rubeg38.protocolclient.packets.ContentType
import ru.rubeg38.protocolclient.packets.Packet
import java.io.InputStream

/**
 * An iterator over a message.
 */
internal abstract class PacketsIterator(
    protected val token: String?,
    protected val messageId: Long,
    protected val messageSize: Int,
    protected val packetSize: Int
) : Iterator<Packet> {

    companion object {

        /**
         * Returns a new instance of PacketsIterator.
         * @param source A string source of PacketsIterator
         * @param token A token header
         * @param messageId An id of the message
         * @param packetSize Max size of packet's data
         * @return An iterator of the packets of the source
         */
        fun create(source: String, token: String?, messageId: Long, packetSize: Int): PacketsIterator =
            ByteArrayPacketsIterator(source.toByteArray(), ContentType.String, token, messageId, packetSize)

        /**
         * Returns a new instance of PacketsIterator.
         * @param source A byte array source of PacketsIterator
         * @param token A token header
         * @param messageId An id of the message
         * @param packetSize Max size of packet's data
         * @return An iterator of the packets of the source
         */
        fun create(source: ByteArray, token: String?, messageId: Long, packetSize: Int): PacketsIterator =
            ByteArrayPacketsIterator(source, ContentType.Binary, token, messageId, packetSize)

        /**
         * Returns a new instance of PacketsIterator.
         * @param source An InputStream source of PacketsIterator
         * @param size A size of the source
         * @param token A token header
         * @param messageId An id of the message
         * @param packetSize Max size of packet's data
         * @return An iterator of the packets of the source
         */
        fun create(source: InputStream, size: Int, token: String?, messageId: Long, packetSize: Int): PacketsIterator =
            InputStreamPacketsIterator(source, size, token, messageId, packetSize)

    }

    protected val packetsCount: Int

    protected var packetNumber = 1
    protected var shift = 0

    init {
        val hasResidualPacket = messageSize % packetSize != 0
        val residualPacketsCount = if (hasResidualPacket) 1 else 0
        packetsCount = messageSize / packetSize + residualPacketsCount
    }

    /**
     * Returns true if the iteration has more [Packet]s. (In other words, returns true if next()
     * would return a [Packet] rather than throwing an exception.)
     * @return true if the iteration has more [Packet]s
     */
    abstract override fun hasNext(): Boolean

    /**
     * Returns the next packet in the iteration over the message.
     * @return Next [Packet] in the iteration
     * @throws NoSuchElementException if the iteration has no more elements
     */
    abstract override fun next(): Packet

}