package kobramob.rubeg38.ru.myprotection.feature.applications.data

import kobramob.rubeg38.ru.myprotection.feature.applications.data.models.PredefinedApplication

class ApplicationsRepositoryImpl(
    private val applicationsApi: ApplicationsApi
) : ApplicationsRepository {

    override suspend fun getPredefinedApplications() =
        applicationsApi.getPredefinedApplications().map { application -> application.description }

}