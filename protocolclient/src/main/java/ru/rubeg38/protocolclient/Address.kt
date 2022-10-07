package ru.rubeg38.protocolclient

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.InetAddress
import java.net.UnknownHostException

/**
 * This class represents an address containing an IPv4 address and a port number.
 */
class Address private constructor(val ip: String, val port: Int) {

    companion object {

        /**
         * Creates an instance of Address type.
         * @param ip The IPv4 address
         * @param port The port number
         * @throws IllegalArgumentException if the port parameter is outside the specified range of
         * valid port values or the ip parameter is not in the IPv4 format
         */
        fun create(ip: String, port: Int): Address {
            validateIp(ip)
            validatePort(port)

            return Address(ip, port)
        }

        /**
         * Resolves the hostname to list of Addresses.
         * @param hostname The domain name to resolve
         * @param port The port number
         * @throws IllegalArgumentException if the port parameter is outside the specified range of
         * valid port values
         * @throws UnknownHostException if no IP address for the host could be found
         * @throws SecurityException if a security manager exists and its checkConnect method
         * doesn't allow the operation
         */
        suspend fun createAll(hostname: String, port: Int): List<Address> {
            validatePort(port)

            val addresses = resolveToIps(hostname)
                .filter { ip -> isValidIp(ip) }
                .map { ip -> Address(ip, port) }

            if (addresses.isEmpty()) {
                throw UnknownHostException("No such host is known ($hostname)")
            }

            return addresses
        }

        /**
         * Creates a list of instances of the Address type from list of IPv4 addresses or domain
         * names. Doesn't throw any exceptions. Returns an empty list if no instances of the Address
         * could be created.
         * @param hosts A list of domain names or IPv4 addresses
         * @param port A Port number
         */
        suspend fun createAll(hosts: List<String>, port: Int): List<Address> {
            validatePort(port)

            return hosts.flatMap { host ->
                if (isIpAddress(host)) {
                    return@flatMap if (isValidIp(host)) {
                        listOf(Address(host, port))
                    } else {
                        emptyList()
                    }
                }

                val ips = try {
                    resolveToIps(host)
                } catch (ex: Exception) {
                    emptyList()
                }

                ips
                    .filter { ip -> isValidIp(ip) }
                    .map { ip -> Address(ip, port) }
            }
        }

        private fun validateIp(ip: String) {
            if (!isValidIp(ip)) {
                throw IllegalArgumentException("Incorrect IPv4 string")
            }
        }

        private fun validatePort(port: Int) {
            if (port !in 0..65535) {
                val message = getPortErrorMessage(port)
                throw IllegalArgumentException(message)
            }
        }

        private fun isIpAddress(host: String) =
            host.all { char -> char.isDigit() || char == '.'}

        private suspend fun resolveToIps(hostname: String) = withContext(Dispatchers.IO) {
            InetAddress.getAllByName(hostname).mapNotNull { inetAddress -> inetAddress.hostAddress }
        }

        private fun isValidIp(ip: String): Boolean {
            val octetPattern = "(\\d{1,2}|1\\d{1,2}|2[0-4]\\d|25[0-5])"
            val pattern = "($octetPattern\\.){3}$octetPattern"
            val regex = Regex(pattern)

            return regex.matches(ip)
        }

        private fun getPortErrorMessage(port: Int) =
            "The port parameter $port is outside the range of valid port values 0...65535"

    }

}