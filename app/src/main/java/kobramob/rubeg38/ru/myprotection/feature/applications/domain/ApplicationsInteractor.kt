package kobramob.rubeg38.ru.myprotection.feature.applications.domain

import kobramob.rubeg38.ru.myprotection.feature.applications.data.ApplicationsRepository
import kobramob.rubeg38.ru.myprotection.utils.attempt
import java.util.*

class ApplicationsInteractor(private val repository: ApplicationsRepository) {

    suspend fun getPredefinedApplications() = attempt {
        repository.getPredefinedApplications()
    }

    suspend fun sendApplication(facilityId: String, text: String, timestamp: Date) = attempt {
        repository.sendApplication(facilityId, text, timestamp)
    }

}