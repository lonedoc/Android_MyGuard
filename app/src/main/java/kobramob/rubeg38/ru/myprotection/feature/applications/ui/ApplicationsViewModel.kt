package kobramob.rubeg38.ru.myprotection.feature.applications.ui

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.viewModelScope
import com.github.terrakok.cicerone.Router
import kobramob.rubeg38.ru.myprotection.R
import kobramob.rubeg38.ru.myprotection.base.BaseViewModel
import kobramob.rubeg38.ru.myprotection.base.Event
import kobramob.rubeg38.ru.myprotection.feature.applications.domain.ApplicationsInteractor
import kobramob.rubeg38.ru.myprotection.utils.SingleLiveEvent
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ApplicationsViewModel(
    private val context: Application,
    private val facilityId: String,
    private val interactor: ApplicationsInteractor,
    private val router: Router
) : BaseViewModel<ViewState>() {

    init {
        processDataEvent(DataEvent.OnUpdateApplications(emptyList()))
        processDataEvent(DataEvent.OnApplicationsRequest)
    }

    val singleEvent = SingleLiveEvent<SingleEvent>()

    override fun initialViewState() = ViewState(
        applications = emptyList(),
        selectedApplication = null,
        applicationText = "",
        date = null,
        time = null,
        dateText = "",
        timeText = "",
        isApplicationTextFieldEnabled = true,
        isSubmitButtonEnabled = false
    )

    override suspend fun reduce(event: Event, previousState: ViewState): ViewState? {
        when (event) {
            is UiEvent.OnApplicationSelected -> {
                val customVariantText = context.getString(R.string.custom_application_text)

                val applicationText = if (event.text == customVariantText) {
                    ""
                } else {
                    event.text
                }

                val isSubmitButtonEnabled = isSubmitButtonEnabled(
                    applicationText,
                    previousState.date,
                    previousState.time
                )

                return previousState.copy(
                    selectedApplication = event.text,
                    applicationText = applicationText,
                    isApplicationTextFieldEnabled = event.text == customVariantText,
                    isSubmitButtonEnabled = isSubmitButtonEnabled
                )
            }
            is UiEvent.OnApplicationTextChanged -> {
                val isSubmitButtonEnabled = isSubmitButtonEnabled(
                    event.text,
                    previousState.date,
                    previousState.time
                )

                return previousState.copy(
                    applicationText = event.text,
                    isSubmitButtonEnabled = isSubmitButtonEnabled
                )
            }
            is UiEvent.OnDateFieldClick -> {
                val date = previousState.date ?: Date()
                singleEvent.postValue(SingleEvent.OnDatePickerDialog(date))
                return null
            }
            is UiEvent.OnTimeFieldClick -> {
                val time = previousState.time ?: Date()
                singleEvent.postValue(SingleEvent.OnTimePickerDialog(time))
                return null
            }
            is UiEvent.OnDateChanged -> {
                val isSubmitButtonEnabled = isSubmitButtonEnabled(
                    previousState.applicationText,
                    event.date,
                    previousState.time
                )

                return previousState.copy(
                    date = event.date,
                    dateText = formatDate(event.date),
                    isSubmitButtonEnabled = isSubmitButtonEnabled
                )
            }
            is UiEvent.OnTimeChanged -> {
                val isSubmitButtonEnabled = isSubmitButtonEnabled(
                    previousState.applicationText,
                    previousState.date,
                    event.time,
                )

                return previousState.copy(
                    time = event.time,
                    timeText = formatTime(event.time),
                    isSubmitButtonEnabled = isSubmitButtonEnabled
                )
            }
            is UiEvent.OnCloseButtonClick -> {
                router.exit()
                return null
            }
            is UiEvent.OnSubmitButtonClick -> {
                val date = previousState.date
                val time = previousState.time

                if (date == null) {
                    val errorMessage = context.getString(R.string.missing_datetime_error_message)
                    singleEvent.postValue(SingleEvent.OnError(errorMessage))
                    return null
                }

                if (time == null) {
                    val errorMessage = context.getString(R.string.missing_datetime_error_message)
                    singleEvent.postValue(SingleEvent.OnError(errorMessage))
                    return null
                }

                val timestamp = mergeDateAndTime(date, time)
                val text = previousState.applicationText

                viewModelScope.launch {
                    interactor.sendApplication(facilityId, text, timestamp).fold(
                        onSuccess = { success ->
                            if (success) {
                                router.exit()
                            } else {
                                val errorMessage = context.getString(R.string.application_failed_message)
                                singleEvent.postValue(SingleEvent.OnError(errorMessage))
                            }
                        },
                        onError = { _ ->
                            val errorMessage = context.getString(R.string.application_failed_message)
                            singleEvent.postValue(SingleEvent.OnError(errorMessage))
                        }
                    )
                }

                return null
            }
            is DataEvent.OnApplicationsRequest -> {
                viewModelScope.launch {
                    interactor.getPredefinedApplications().fold(
                        onSuccess = { applications ->
                            processDataEvent(DataEvent.OnUpdateApplications(applications))
                        },
                        onError = { _ ->
                            processDataEvent(DataEvent.OnFailureApplicationsRequest)
                        }
                    )
                }
                return null
            }
            is DataEvent.OnUpdateApplications -> {
                val customVariantText = context.getString(R.string.custom_application_text)

                return previousState.copy(
                    applications = listOf(customVariantText) + event.applications
                )
            }
            is DataEvent.OnFailureApplicationsRequest -> {
                val errorMessage = context.getString(R.string.applications_request_failed_message)
                singleEvent.postValue(SingleEvent.OnError(errorMessage))
                return null
            }
            else -> {
                return null
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun formatDate(timestamp: Date) =
        SimpleDateFormat("dd.MM.yyyy").run { format(timestamp) }

    @SuppressLint("SimpleDateFormat")
    private fun formatTime(timestamp: Date) =
        SimpleDateFormat("HH:mm").run { format(timestamp) }

    private fun isSubmitButtonEnabled(applicationText: String, date: Date?, time: Date?) =
        applicationText.isNotBlank() && date != null && time != null

    private fun mergeDateAndTime(date: Date, time: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date

        val timeCalendar = Calendar.getInstance()
        calendar.time = time

        calendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY))
        calendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE))

        return calendar.time
    }

}