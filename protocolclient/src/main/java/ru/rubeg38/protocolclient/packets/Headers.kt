package ru.rubeg38.protocolclient.packets

/**
 * This class represents a set of the Protocol headers.
 */
internal data class Headers(
    val contentType: ContentType,
    val messageId: Long,
    val messageSize: Int,
    val packetsCount: Int,
    val packetNumber: Int,
    val packetSize: Int,
    val shift: Int,
    val token: String?
)