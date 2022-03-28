package kobramob.rubeg38.ru.myprotection.utils

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.rubeg38.protocolclient.Address

class SessionDataHolder {

    private val mutex = Mutex()

    private var addresses: List<Address> = emptyList()
    private var token: String? = null

    suspend fun getAddresses(): List<Address> = mutex.withLock { addresses }

    suspend fun setAddresses(addresses: List<Address>) {
        mutex.withLock {
            this.addresses = addresses
        }
    }

    suspend fun getToken(): String? = mutex.withLock { token }

    suspend fun setToken(token: String?) {
        mutex.withLock {
            this.token = token
        }
    }

}