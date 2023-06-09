package kobramob.rubeg38.ru.myprotection.domain.models

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class Facility(
    val id: String,
    val name: String,
    val address: String,
    val statusCodes: List<StatusCode>,
    val statusDescription: String,
    val isSelfServiceEnabled: Boolean,
    val hasOnlineChannel: Boolean,
    val isOnlineChannelEnabled: Boolean,
    val isArmingEnabled: Boolean,
    val isAlarmButtonEnabled: Boolean,
    val batteryMalfunction: Boolean,
    val powerSupplyMalfunction: Boolean,
    val passcode: String?,
    val isApplicationsEnabled: Boolean,
    val accounts: List<Account>,
    val devices: List<Device>
) : Parcelable {

    @IgnoredOnParcel
    val alarm = statusCodes.contains(StatusCode.ALARM)

    @IgnoredOnParcel
    val isGuarded = statusCodes.contains(StatusCode.GUARDED)

}