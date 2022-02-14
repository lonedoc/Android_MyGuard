package kobramob.rubeg38.ru.myprotection.data

import kobramob.rubeg38.ru.myprotection.domain.models.GuardService
import kobramob.rubeg38.ru.myprotection.domain.models.User

interface SharedPreferencesHelper {
    var cityName: String?
    var guardService: GuardService?
    var userPhoneNumber: String?
    var user: User?
    var token: String?
    var passcode: String?
}


private const val ADDRESS_INDEX_KEY = "addressIndex"

//class PrefsUtils(context:Context):ShPreferences {
//    private val prefsID = "myprotection.DataStorage"
//    private val prefs: SharedPreferences = context.getSharedPreferences(prefsID,Context.MODE_PRIVATE)
//
//    override var tid: String?
//        get() = prefs.getString("tid", null)
//        set(value) {
//            val editor = prefs.edit()
//            editor.putString("tid", value).apply()
//        }
//    override var token: String?
//        get() = prefs.getString("token", null)
//        set(value) {
//            val editor = prefs.edit()
//            editor.putString("token", value).apply()
//        }
//    override var googleToken: String?
//        get() = prefs.getString("googleToken", null)
//        set(value) {
//            val editor = prefs.edit()
//            editor.putString("googleToken", value).apply()
//        }

//    override var userPassword: String?
//        get() = prefs.getString("userPassword", null)
//        set(value) {
//            val editor = prefs.edit()
//            editor.putString("userPassword", value).apply()
//        }
//
//    override var addressIndex: Int
//        get() = prefs.getInt(ADDRESS_INDEX_KEY, 0)
//        set(value) {
//            val editor = prefs.edit()
//            editor.putInt(ADDRESS_INDEX_KEY, value).apply()
//        }
//

//
//    override var serverPort: Int
//        get() = prefs.getInt("port", -1)
//        set(value) {
//            val editor = prefs.edit()
//            editor.putInt("port", value).apply()
//        }


//    override var companyPhone: String?
//        get() = prefs.getString("companyPhone", null)
//        set(value) {
//            val editor = prefs.edit()
//            editor.putString("companyPhone", value).apply()
//        }
//    override var userName: String?
//        get() = prefs.getString("userName", null)
//        set(value) {
//            val editor = prefs.edit()
//            editor.putString("userName", value).apply()
//        }
//    override var pinCode: String?
//        get() = prefs.getString("pinCode", null)
//        set(value) {
//            val editor = prefs.edit()
//            editor.putString("pinCode", value).apply()
//        }
//    override val containsTid: Boolean
//        get() = prefs.contains("tid")
//    override val containsToken: Boolean
//        get() = prefs.contains("token")
//    override val containsGoogleToken: Boolean
//        get() = prefs.contains("googleToken")
//    override val containsUserPhone: Boolean
//        get() = prefs.contains("userPhone")
//    override val containsUserPassword: Boolean
//        get() = prefs.contains("userPassword")
//
//    override val containsAddressIndex: Boolean
//        get() = prefs.contains(ADDRESS_INDEX_KEY)
//
//    override val containsAddress: Boolean
//        get() = prefs.contains("ips")
//    override val containsPort: Boolean
//        get() = prefs.contains("port")
//    override val containsCompanyCity: Boolean
//        get() = prefs.contains("companyCity")
//    override val containsCompanyName: Boolean
//        get() = prefs.contains("companyName")
//    override val containsCompanyPhone: Boolean
//        get() = prefs.contains("companyPhone")
//    override val containsUserName: Boolean
//        get() = prefs.contains("userName")
//    override val containsPinCode: Boolean
//        get() = prefs.contains("pinCode")
//
//
//    override fun clearData() {
//        val editor = prefs.edit()
//
//        editor.remove("tid").apply()
//        editor.remove("token").apply()
//        editor.remove("googleToken").apply()
//        editor.remove("userPhone").apply()
//        editor.remove("userPassword").apply()
//        editor.remove("ips").apply()
//        editor.remove("port").apply()
//        editor.remove("companyCity").apply()
//        editor.remove("companyName").apply()
//        editor.remove("companyPhone").apply()
//        editor.remove("userName").apply()
//        editor.remove("pinCode").apply()
//    }
//}