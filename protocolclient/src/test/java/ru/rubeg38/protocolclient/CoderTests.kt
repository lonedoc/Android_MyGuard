package ru.rubeg38.protocolclient

import org.junit.Assert
import org.junit.Test
import ru.rubeg38.protocolclient.packets.*

class CoderTests {

    @Test
    fun shouldDecodePreviouslyEncodedPacket() {
        val payload = "Payload"
        val payloadBytes = payload.toByteArray()

        val headers = Headers(
            contentType = ContentType.Binary,
            messageId = 42,
            messageSize = payloadBytes.count(),
            packetsCount = 1,
            packetNumber = 1,
            packetSize = payloadBytes.count(),
            shift = 0,
            token = "ABABAB"
        )

        val packet = Packet(headers, payloadBytes)

        val coder = Coder()
        val encoded = coder.encode(packet)
        val decodedPacket = coder.decode(encoded)

        Assert.assertNotNull(decodedPacket)
        decodedPacket!!

        Assert.assertEquals(headers.copy(token = null), decodedPacket.headers)

        val decodedPayload = decodedPacket.payload

        Assert.assertNotNull(decodedPayload)
        decodedPayload!!

        Assert.assertEquals(payload, String(decodedPayload))
    }

}