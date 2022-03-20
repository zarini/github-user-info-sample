package com.milkyhead.android.payconiq.presentation.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.milkyhead.android.payconiq.di.DispatcherIO
import com.milkyhead.android.payconiq.domain.model.Either
import com.milkyhead.android.payconiq.domain.usecase.GetUserDetailsUseCase
import com.milkyhead.android.payconiq.presentation.error.ErrorHandler
import com.milkyhead.android.payconiq.presentation.state.DetailsViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
internal class UserDetailsViewModel @Inject constructor(
    private val getUserDetailsUseCase: GetUserDetailsUseCase,
    private val errorHandler: ErrorHandler,
    private val savedStateHandle: SavedStateHandle,
    @DispatcherIO private val dispatcher: CoroutineDispatcher
) : ViewModel() {


    private val _state = MutableStateFlow(DetailsViewState())
    val state: StateFlow<DetailsViewState>
        get() {
            // update state with last state if exist
            savedStateHandle.get<DetailsViewState>(KEY_LAST_STATE)?.let {
                _state.value = it
            }
            return _state.asStateFlow()
        }


    fun getUserDetails(username: String) {
        viewModelScope.launch(dispatcher) {
            _state.value = DetailsViewState(loading = true)
            when (val response = getUserDetailsUseCase(username)) {
                is Either.Success -> {
                    val detailsViewState = DetailsViewState(result = response.data)
                    savedStateHandle.set(KEY_LAST_STATE, detailsViewState)

                    _state.value = detailsViewState
                }

                is Either.Error -> {
                    val message = errorHandler.getErrorMessage(response.error)
                    _state.value = DetailsViewState(error = message)
                }
            }
        }
    }

    internal companion object {
        const val KEY_LAST_STATE = "details_last_state"
    }
}