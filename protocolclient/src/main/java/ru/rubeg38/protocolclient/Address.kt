package ru.rubeg38.protocolclient

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

        private fun validateIp(ip: String) {
            val octetPattern = "(\\d{1,2}|1\\d{1,2}|2[0-4]\\d|25[0-5])"
            val pattern = "($octetPattern\\.){3}$octetPattern"
            val regex = Regex(pattern)

            if (!regex.matches(ip)) {
                throw IllegalArgumentException("Incorrect IPv4 string")
            }
        }

        private fun validatePort(port: Int) {
            if (port !in 0..65535) {
                val message = getPortErrorMessage(port)
                throw IllegalArgumentException(message)
            }
        }

        private fun getPortErrorMessage(port: Int) =
            "The port parameter $port is outside the range of valid port values 0...65535"

    }

}