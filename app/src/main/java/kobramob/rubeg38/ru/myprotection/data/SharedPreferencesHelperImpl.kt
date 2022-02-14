package kobramob.rubeg38.ru.myprotection.data

import android.content.SharedPreferences
import androidx.core.content.edit
import kobramob.rubeg38.ru.myprotection.domain.models.GuardService
import kobramob.rubeg38.ru.myprotection.domain.models.User

private const val CITY_KEY = "companyCity"
private const val GUARD_SERVICE_NAME_KEY = "companyName"
private const val GUARD_SERVICE_DISPLAYED_NAME_KEY = "guardServiceDisplayedName"
private const val GUARD_SERVICE_PHONE_NUMBER_KEY = "companyPhone"
private const val GUARD_SERVICE_IP_KEY = "ip"
private const val USER_PHONE_NUMBER_KEY = "userPhone"
private const val USER_ID_KEY = "userId"
private const val USER_NAME_KEY = "userName"
private const val TOKEN_KEY = "token"
private const val PASSCODE_KEY = "pinCode"

class SharedPreferencesHelperImpl(
    private val sharedPreferences: SharedPreferences
) : SharedPreferencesHelper {

    override var cityName: String?
        get() = sharedPreferences.getString(CITY_KEY, null)
        set(value) {
            sharedPreferences.edit { putString(CITY_KEY, value) }
        }

    override var guardService: GuardService?
        get() {
            val name = sharedPreferences.getString(GUARD_SERVICE_NAME_KEY, null) ?: return null
            val ipAddressesStr = sharedPreferences.getString(GUARD_SERVICE_IP_KEY, null) ?: return null
            val ipAddresses = ipAddressesStr.split(", ")
            val displayedName = sharedPreferences.getString(GUARD_SERVICE_DISPLAYED_NAME_KEY, null)
            val phoneNumber = sharedPreferences.getString(GUARD_SERVICE_PHONE_NUMBER_KEY, null)
            return GuardService(name, displayedName, phoneNumber, ipAddresses)
        }
        set(value) {
            sharedPreferences.edit {
                putString(GUARD_SERVICE_NAME_KEY, value?.name)
                putString(GUARD_SERVICE_DISPLAYED_NAME_KEY, value?.displayedName)
                putString(GUARD_SERVICE_PHONE_NUMBER_KEY, value?.phoneNumber)
                putString(GUARD_SERVICE_IP_KEY, value?.addresses?.joinToString { it })
            }

        }

    override var userPhoneNumber: String?
        get() = sharedPreferences.getString(USER_PHONE_NUMBER_KEY, null)
        set(value) {
            sharedPreferences.edit {
                putString(USER_PHONE_NUMBER_KEY, value)
            }
        }

    override var user: User?
        get() {
            val name = sharedPreferences.getString(USER_NAME_KEY, null) ?: return null
            val id = sharedPreferences.getInt(USER_ID_KEY, -1)

            if (id == -1) {
                return null
            }

            return User(id, name)
        }
        set(value) {
            sharedPreferences.edit {
                value?.let { user ->
                    putInt(USER_ID_KEY, user.id)
                    putString(USER_NAME_KEY, user.name)
                }
            }
        }

    override var token: String?
        get() = sharedPreferences.getString(TOKEN_KEY, null)
        set(value) {
            sharedPreferences.edit {
                putString(TOKEN_KEY, value)
            }
        }

    override var passcode: String?
        get() = sharedPreferences.getString(PASSCODE_KEY, null)
        set(value) {
            sharedPreferences.edit {
                putString(PASSCODE_KEY, value)
            }
        }

}