package kobramob.rubeg38.ru.myprotection.feature.applications.data

import kobramob.rubeg38.ru.myprotection.feature.applications.data.models.PredefinedApplication
import java.util.*

interface ApplicationsRepository {
    suspend fun getPredefinedApplications(): List<String>
    suspend fun sendApplication(facilityId: String, text: String, timestamp: Date): Boolean
}