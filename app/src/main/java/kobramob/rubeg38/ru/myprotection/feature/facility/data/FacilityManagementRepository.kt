package kobramob.rubeg38.ru.myprotection.feature.facility.data

import kobramob.rubeg38.ru.myprotection.domain.models.Facility
import kobramob.rubeg38.ru.myprotection.feature.facility.domain.models.RenamingResult

interface FacilityManagementRepository {
    suspend fun getFacility(facilityId: String): Facility
    suspend fun setName(facilityId: String, name: String): RenamingResult
    suspend fun startAlarm(facilityId: String): Boolean
    suspend fun cancelAlarm(facilityId: String, passcode: String): Boolean
    suspend fun arm(facilityId: String): Boolean
    suspend fun armPerimeter(facilityId: String): Boolean
    suspend fun disarm(facilityId: String): Boolean
    fun setLastCancellationTime(facilityId: String, time: Long)
    fun getLastCancellationTime(facilityId: String): Long?
    fun removeLastCancellationTime(facilityId: String)
}