package ru.rubeg38.protocolclient

/**
 * This class represents a set of parameters to configure Protocol client.
 */
data class ClientConfig constructor(
    val packetSize: Int,
    val congestionWindowSize: Int,
    val maxAttemptsCount: Int,
    val retryInterval: Long,
    val sleepInterval: Long,
    val readTimeout: Long,
    val skipUnidentified: Boolean // Packets with messageId == 0 will be ignored
) {

    companion object {
        val default: ClientConfig by lazy {
            ClientConfig(
                packetSize = 960,
                congestionWindowSize = 32,
                maxAttemptsCount = 3,
                retryInterval = 1000,
                sleepInterval = 200,
                readTimeout = 5000,
                skipUnidentified = true
            )
        }
    }

    /**
     * A builder of [ClientConfig].
     */
    class Builder {
        private var packetSize: Int? = null
        private var congestionWindowSize: Int? = null
        private var maxAttemptsCount: Int? = null
        private var retryInterval: Long? = null
        private var sleepInterval: Long? = null
        private var readTimeout: Long? = null
        private var skipUnidentified: Boolean? = null

        fun setPacketSize(packetSize: Int): Builder {
            this.packetSize = packetSize
            return this
        }

        fun setCongestionWindowSize(congestionWindowSize: Int): Builder {
            this.congestionWindowSize = congestionWindowSize
            return this
        }

        fun setMaxAttemptsCount(maxAttemptsCount: Int): Builder {
            this.maxAttemptsCount = maxAttemptsCount
            return this
        }

        fun setRetryInterval(retryInterval: Long): Builder {
            this.retryInterval = retryInterval
            return this
        }

        fun setSleepInterval(sleepInterval: Long): Builder {
            this.sleepInterval = sleepInterval
            return this
        }

        fun setReadTimeout(dropInterval: Long): Builder {
            this.readTimeout = dropInterval
            return this
        }

        fun setSkipUnidentified(dropUnidentified: Boolean): Builder {
            this.skipUnidentified = dropUnidentified
            return this
        }

        fun build() = ClientConfig(
            packetSize = packetSize ?: default.packetSize,
            congestionWindowSize = congestionWindowSize ?: default.congestionWindowSize,
            maxAttemptsCount = maxAttemptsCount ?: default.maxAttemptsCount,
            retryInterval = retryInterval ?: default.retryInterval,
            sleepInterval = sleepInterval ?: default.sleepInterval,
            readTimeout = readTimeout ?: default.readTimeout,
            skipUnidentified = skipUnidentified ?: default.skipUnidentified
        )
    }

}

/**
 * Calls the specified function block with ClientConfig.Builder() as its receiver and returns
 * [ClientConfig] value produced by .build() method.
 * @receiver [ClientConfig.Builder]
 */
fun clientConfig(block: ClientConfig.Builder.() -> Unit) =
    ClientConfig.Builder().apply(block).build()