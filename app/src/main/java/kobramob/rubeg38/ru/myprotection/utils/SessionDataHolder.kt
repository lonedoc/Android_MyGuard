package kobramob.rubeg38.ru.myprotection.utils

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class SessionDataHolder {

    private val mutex = Mutex()

    private var _ipAddresses: List<String> = emptyList()
    private var _token: String? = null

    suspend fun getIpAddresses(): List<String> = mutex.withLock { _ipAddresses.copy() }

    suspend fun setIpAddresses(ipAddresses: List<String>) {
        mutex.withLock {
            _ipAddresses = ipAddresses.copy()
        }
    }

    suspend fun getToken(): String? = mutex.withLock { _token?.copy() }

    suspend fun setToken(token: String?) {
        mutex.withLock {
            _token = token?.copy()
        }
    }

}

private fun List<String>.copy() = this.toList()

private fun String.copy() = String(this.toCharArray())