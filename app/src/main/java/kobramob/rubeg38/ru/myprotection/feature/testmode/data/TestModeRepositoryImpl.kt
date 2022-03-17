package kobramob.rubeg38.ru.myprotection.feature.testmode.data

import kobramob.rubeg38.ru.myprotection.feature.testmode.domain.models.StartTestingResult
import kobramob.rubeg38.ru.myprotection.feature.testmode.domain.models.TestingStatus
import kobramob.rubeg38.ru.myprotection.feature.testmode.domain.toDomain

class TestModeRepositoryImpl(private val testModeApi: TestModeApi) : TestModeRepository {

    override suspend fun startTesting(facilityId: String): StartTestingResult =
        testModeApi.startTesting(facilityId).toDomain()

    override suspend fun endTesting(facilityId: String): Boolean =
        testModeApi.endTesting(facilityId).result == "ok"

    override suspend fun reset(facilityId: String): Boolean =
        testModeApi.reset(facilityId).result == "ok"

    override suspend fun getTestingStatus(facilityId: String): TestingStatus =
        testModeApi.getStatus(facilityId).toDomain()

}