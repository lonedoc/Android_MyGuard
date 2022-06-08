package kobramob.rubeg38.ru.myprotection.feature.facility.data

import android.content.SharedPreferences
import androidx.core.content.edit
import kobramob.rubeg38.ru.myprotection.domain.models.Facility
import kobramob.rubeg38.ru.myprotection.domain.toDomain
import kobramob.rubeg38.ru.myprotection.feature.facility.domain.models.RenamingResult
import kobramob.rubeg38.ru.myprotection.feature.facility.domain.toDomain

private const val CANCELLATION_TIME_KEY_PREFIX = "cancellation_time_"

class FacilityManagementRepositoryImpl(
    private val facilityManagementApi: FacilityManagementApi,
    private val sharedPreferences: SharedPreferences
) : FacilityManagementRepository {

    override suspend fun getFacility(facilityId: String): Facility =
        facilityManagementApi.getFacility(facilityId).toDomain()

    override suspend fun setName(facilityId: String, name: String): RenamingResult =
        facilityManagementApi.setName(facilityId, name).toDomain()

    override suspend fun startAlarm(facilityId: String): Boolean =
        facilityManagementApi.startAlarm(facilityId).toDomain()

    override suspend fun cancelAlarm(facilityId: String, passcode: String): Boolean =
        facilityManagementApi.cancelAlarm(facilityId, passcode).toDomain()

    override suspend fun arm(facilityId: String): Boolean =
        facilityManagementApi.arm(facilityId).toDomain()

    override suspend fun armPerimeter(facilityId: String): Boolean =
        facilityManagementApi.armPerimeter(facilityId).toDomain()

    override suspend fun disarm(facilityId: String): Boolean =
        facilityManagementApi.disarm(facilityId).toDomain()

    override fun setLastCancellationTime(facilityId: String, time: Long) {
        sharedPreferences.edit {
            putLong("$CANCELLATION_TIME_KEY_PREFIX$facilityId", time)
        }
    }

    override fun getLastCancellationTime(facilityId: String): Long? {
        val time = sharedPreferences.getLong("$CANCELLATION_TIME_KEY_PREFIX$facilityId", -1)

        if (time == -1L) {
            return null
        }

        return time
    }

    override fun removeLastCancellationTime(facilityId: String) {
        sharedPreferences.edit {
            remove("$CANCELLATION_TIME_KEY_PREFIX$facilityId")
        }
    }

}