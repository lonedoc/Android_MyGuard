package ru.rubeg38.protocolclient

/**
 * Dsl function to make a network call with given number of retries and receive a response to the request
 * @param times A number of attempts. Infinite if less than 1
 * @param addresses A list of [Address] items to iterate over while trying to send a message
 * @param makeTry A block of code containing a network call
 */
inline fun <T> retry(times: Int, addresses: List<Address>, makeTry: (Address) -> T): T {
    require(addresses.isNotEmpty())

    var lastException: Exception?
    var attemptsCount = 0
    var index = 0

    while (true) {
        val address = addresses[index]

        try {
            return makeTry(address)
        } catch (ex: ProtocolException) {
            ex.printStackTrace()
            lastException = ex
        }

        index = (index + 1) % addresses.count()

        if (times > 0) {
            attemptsCount++

            if (attemptsCount >= times) {
                break
            }
        }
    }

    if (lastException != null) {
        throw lastException
    } else {
        throw IllegalStateException()
    }
}