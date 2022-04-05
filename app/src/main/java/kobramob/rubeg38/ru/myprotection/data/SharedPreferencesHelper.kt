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