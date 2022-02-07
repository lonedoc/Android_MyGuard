package ru.rubeg38.protocolclient.packets

internal data class PacketWrapper(
    val packet: Packet,
    var attemptsCount: Int,
    var timeToRetry: Long
) {
    val isReadyToRetry: Boolean
        get() = timeToRetry <= System.currentTimeMillis()
}