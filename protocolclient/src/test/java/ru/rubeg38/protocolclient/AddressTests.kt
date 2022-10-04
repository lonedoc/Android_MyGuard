package ru.rubeg38.protocolclient

import org.junit.Assert
import org.junit.Test
import java.net.UnknownHostException

// TODO: Find a way to mock DNS

class AddressTests {

    @Test
    fun factoryMethodShouldThrowExceptionWhenIpAddressIsIncorrect() {
        val ex = Assert.assertThrows(IllegalArgumentException::class.java) {
            Address.create("192.168.1.1111", 0)
        }

        Assert.assertEquals("Incorrect IPv4 string", ex.message)
    }

    @Test
    fun factoryMethodShouldThrowExceptionWhenPortNumberIsIncorrect() {
        var ex = Assert.assertThrows(IllegalArgumentException::class.java) {
            Address.create("192.168.1.1", -1)
        }

        Assert.assertEquals(
            "The port parameter -1 is outside the range of valid port values 0...65535",
            ex.message
        )

        ex = Assert.assertThrows(IllegalArgumentException::class.java) {
            Address.create("192.168.1.1", 67000)
        }

        Assert.assertEquals(
            "The port parameter 67000 is outside the range of valid port values 0...65535",
            ex.message
        )
    }

    @Test
    fun factoryMethodShouldThrowExceptionWhenDnsNameIsIncorrect() {
        val ex = Assert.assertThrows(UnknownHostException::class.java) {
            Address.create("unknown.hostname.test", 0)
        }

        Assert.assertEquals("No such host is known (unknown.hostname.test)", ex.message)
    }

    @Test
    fun factoryMethodShouldReturnNewInstanceOfAddress() {
        val address = Address.create("192.168.1.1", 3000)

        Assert.assertEquals("192.168.1.1", address.ip)
        Assert.assertEquals(3000, address.port)
    }

    @Test
    fun factoryMethodShouldReturnNewInstanceOfAddressWithResolvedDnsName() {
        val address = Address.create("crm.rubeg38.ru", 3000)

        Assert.assertEquals("194.146.201.66", address.ip)
        Assert.assertEquals(3000, address.port)
    }

}