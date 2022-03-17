package kobramob.rubeg38.ru.myprotection.feature.testmode.domain

import kobramob.rubeg38.ru.myprotection.feature.testmode.data.TestModeRepository
import kobramob.rubeg38.ru.myprotection.utils.attempt

class TestModeInteractor(private val testModeRepository: TestModeRepository) {

    suspend fun startTesting(facilityId: String) = attempt {
        testModeRepository.startTesting(facilityId)
    }

    suspend fun endTesting(facilityId: String) = attempt {
        testModeRepository.endTesting(facilityId)
    }

    suspend fun reset(facilityId: String) = attempt {
        testModeRepository.reset(facilityId)
    }

    suspend fun getTestingStatus(facilityId: String) = attempt {
        testModeRepository.getTestingStatus(facilityId)
    }

}