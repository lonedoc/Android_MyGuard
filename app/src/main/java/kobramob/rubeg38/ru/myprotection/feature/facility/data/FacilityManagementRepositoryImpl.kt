package kobramob.rubeg38.ru.myprotection.feature.facility.data

import kobramob.rubeg38.ru.myprotection.domain.models.Facility
import kobramob.rubeg38.ru.myprotection.domain.toDomain
import kobramob.rubeg38.ru.myprotection.feature.facility.domain.models.RenamingResult
import kobramob.rubeg38.ru.myprotection.feature.facility.domain.toDomain

class FacilityManagementRepositoryImpl(
    private val facilityManagementApi: FacilityManagementApi
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

}