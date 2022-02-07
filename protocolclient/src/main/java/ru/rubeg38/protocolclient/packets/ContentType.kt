package ru.rubeg38.protocolclient.packets

internal enum class ContentType(val code: Byte) {
    String(0x14.toByte()),
    LegacyString(0x00.toByte()),
    Binary(0x01.toByte()),
    Error(0xFD.toByte()),
    Sync(0xFE.toByte()),
    Acknowledgment(0xFF.toByte())
}