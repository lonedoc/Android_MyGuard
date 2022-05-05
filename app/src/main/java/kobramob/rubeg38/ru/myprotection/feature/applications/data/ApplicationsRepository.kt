package kobramob.rubeg38.ru.myprotection.feature.applications.data

import kobramob.rubeg38.ru.myprotection.feature.applications.data.models.PredefinedApplication

interface ApplicationsRepository {
    suspend fun getPredefinedApplications(): List<String>
}