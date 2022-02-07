package kobramob.rubeg38.ru.myprotection.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

abstract class BaseViewModel<VIEW_STATE> : ViewModel() {

    private val _viewState: MutableLiveData<VIEW_STATE> by lazy { MutableLiveData(initialViewState()) }

    val viewState: LiveData<VIEW_STATE> = _viewState

    abstract fun initialViewState(): VIEW_STATE

    abstract suspend fun reduce(event: Event, previousState: VIEW_STATE): VIEW_STATE?

    fun processUiEvent(event: Event) {
        updateState(event)
    }

    protected fun processDataEvent(event: Event) {
        updateState(event)
    }

    private fun updateState(event: Event) = viewModelScope.launch {
        val newViewState = reduce(event, _viewState.value ?: initialViewState())
        if (newViewState != null && newViewState != _viewState.value) {
            _viewState.value = newViewState
        }
    }

}