package kobramob.rubeg38.ru.myprotection.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Facility(
    val id: Int,
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
    val accounts: List<Account>
) : Parcelable