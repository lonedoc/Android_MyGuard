package ru.rubeg38.protocolclient

import org.junit.Assert
import org.junit.Test

class ClientConfigTests {

    @Test
    fun builderShouldReturnConfigWithDefaultValuesForNonInitializedFields() {
        val defaultConfig = ClientConfig.default

        val config = clientConfig {
            setPacketSize(100)
            setReadTimeout(20_000)
        }

        Assert.assertEquals(100, config.packetSize)
        Assert.assertNotEquals(defaultConfig.packetSize, config.packetSize)
        Assert.assertEquals(20_000, config.readTimeout)
        Assert.assertNotEquals(defaultConfig, config.readTimeout)

        Assert.assertEquals(defaultConfig.congestionWindowSize, config.congestionWindowSize)
        Assert.assertEquals(defaultConfig.maxAttemptsCount, config.maxAttemptsCount)
        Assert.assertEquals(defaultConfig.retryInterval, config.retryInterval)
        Assert.assertEquals(defaultConfig.sleepInterval, config.sleepInterval)
    }

}