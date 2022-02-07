package ru.rubeg38.protocolclient.utils

import ru.rubeg38.protocolclient.Address
import java.net.Inet4Address
import java.net.InetSocketAddress
import java.net.SocketAddress

val SocketAddress.address: Address?
    get() = (this as? InetSocketAddress)?.let { inetSocketAddress ->
        val ip = (inetSocketAddress.address as? Inet4Address)?.hostAddress ?: return null
        val port = inetSocketAddress.port

        return Address.create(ip, port)
    }
