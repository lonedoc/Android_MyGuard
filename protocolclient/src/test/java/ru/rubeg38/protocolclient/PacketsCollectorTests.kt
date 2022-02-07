package ru.rubeg38.protocolclient

import org.junit.Assert
import org.junit.Test
import ru.rubeg38.protocolclient.packets.PacketsCollector
import ru.rubeg38.protocolclient.packets.iterator.PacketsIterator
import kotlin.random.Random

private const val PACKET_SIZE = 960

class PacketsCollectorTests {

    @Test
    fun shouldBuildByteArrayMessageFromPackets() {
        val seed = System.currentTimeMillis()
        val messageId = Random(seed).nextLong(0, Long.MAX_VALUE)

        val sourceMessage = getRandomString(1024)
        val packetsIterator = PacketsIterator.create(sourceMessage, null, messageId, PACKET_SIZE)

        var packetsCollector: PacketsCollector? = null

        var count = 0
        while (packetsIterator.hasNext()) {
            val packet = packetsIterator.next()

            if (packetsCollector == null) {
                packetsCollector = PacketsCollector(
                    messageId = packet.headers.messageId,
                    messageSize = packet.headers.messageSize,
                    packetsCount = packet.headers.packetsCount
                )
            }

            packetsCollector.collect(packet)
            count++
        }

        Assert.assertTrue(packetsCollector!!.isDone)

        val message = String(packetsCollector.getData())

        Assert.assertEquals(message, sourceMessage)
    }

    private fun getRandomString(length: Int): String {
        val seed = System.currentTimeMillis()
        val random = Random(seed)

        val allowedCharacters = ('a'..'z').toList()

        val charArr = CharArray(length) {
            allowedCharacters.random(random)
        }

        return String(charArr)
    }

}