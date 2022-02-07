package ru.rubeg38.protocolclient.packets

/**
 * This class represents a Protocol packet.
 */
internal data class Packet(
    val headers: Headers,
    val payload: ByteArray? = null
) {

    companion object {

        /**
         * Decodes a data byte array and creates a packet instance.
         * @param data The byte array of encoded packet
         * @return A packet or null if data is not encoded according to the Protocol
         */
        fun decode(data: ByteArray): Packet? = Coder().decode(data)

        /**
         * Creates an acknowledgment packet for given one.
         * @param packet The packet for which an acknowledgment is creating
         * @return A new packet with "acknowledgment" content type and without payload
         */
        fun createAcknowledgment(packet: Packet): Packet {
            val headers = packet.headers.copy(contentType = ContentType.Acknowledgment)
            return Packet(headers)
        }

    }

    /**
     * Encodes the packet to byte array.
     * @return An encoded byte array
     */
    fun encode(): ByteArray = Coder().encode(this)

    /**
     * Shows whether this is an acknowledgment for the given packet or not.
     * @return true if this is an acknowledgment for the given packet
     */
    fun isAcknowledgmentOf(packet: Packet): Boolean {
        if (headers.contentType != ContentType.Acknowledgment) {
            return false
        }

        val sameMessageId = headers.messageId == packet.headers.messageId
        val samePacketsCount = headers.packetsCount == packet.headers.packetsCount
        val samePacketNumber = headers.packetNumber == packet.headers.packetNumber

        return sameMessageId && samePacketsCount && samePacketNumber
    }

    override fun toString(): String =
        "{ content type: ${headers.contentType}, " +
        "message id: ${headers.messageId}, " +
        "packet number: ${headers.packetNumber}, " +
        "packets count: ${headers.packetsCount} }"

}