package kobramob.rubeg38.ru.myprotection.feature.applications.data

import kobramob.rubeg38.ru.myprotection.feature.applications.domain.toDomain
import java.util.*

class ApplicationsRepositoryImpl(
    private val applicationsApi: ApplicationsApi
) : ApplicationsRepository {

    override suspend fun getPredefinedApplications() =
        applicationsApi.getPredefinedApplications().map { application -> application.description }

    override suspend fun sendApplication(facilityId: String, text: String, timestamp: Date) =
        applicationsApi.sendApplication(facilityId, text, timestamp).toDomain()

}