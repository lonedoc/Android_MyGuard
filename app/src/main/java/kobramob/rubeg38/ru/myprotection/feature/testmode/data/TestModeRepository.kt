package kobramob.rubeg38.ru.myprotection.feature.testmode.data

import kobramob.rubeg38.ru.myprotection.feature.testmode.domain.models.StartTestingResult
import kobramob.rubeg38.ru.myprotection.feature.testmode.domain.models.TestingStatus

interface TestModeRepository {
    suspend fun startTesting(facilityId: String): StartTestingResult
    suspend fun endTesting(facilityId: String): Boolean
    suspend fun reset(facilityId: String): Boolean
    suspend fun getTestingStatus(facilityId: String): TestingStatus
}