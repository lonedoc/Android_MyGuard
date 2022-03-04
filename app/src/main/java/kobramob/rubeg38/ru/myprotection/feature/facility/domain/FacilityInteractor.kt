package kobramob.rubeg38.ru.myprotection.feature.facility.domain

import kobramob.rubeg38.ru.myprotection.feature.facility.data.FacilityManagementRepository
import kobramob.rubeg38.ru.myprotection.utils.attempt

class FacilityInteractor(private val facilityManagementRepository: FacilityManagementRepository) {

    suspend fun getFacility(facilityId: String) = attempt {
        facilityManagementRepository.getFacility(facilityId)
    }

    suspend fun setName(facilityId: String, name: String) = attempt {
        facilityManagementRepository.setName(facilityId, name)
    }

    suspend fun startAlarm(facilityId: String) = attempt {
        facilityManagementRepository.startAlarm(facilityId)
    }

    suspend fun arm(facilityId: String) = attempt {
        facilityManagementRepository.arm(facilityId)
    }

    suspend fun armPerimeter(facilityId: String) = attempt {
        facilityManagementRepository.armPerimeter(facilityId)
    }

    suspend fun disarm(facilityId: String) = attempt {
        facilityManagementRepository.disarm(facilityId)
    }

}