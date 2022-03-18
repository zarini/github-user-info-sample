package com.milkyhead.android.payconiq.presentation.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.milkyhead.android.payconiq.di.DispatcherIO
import com.milkyhead.android.payconiq.domain.model.Either
import com.milkyhead.android.payconiq.domain.model.UserModel
import com.milkyhead.android.payconiq.domain.usecase.SearchUserUseCase
import com.milkyhead.android.payconiq.presentation.error.ErrorHandler
import com.milkyhead.android.payconiq.presentation.event.SearchEvent
import com.milkyhead.android.payconiq.presentation.state.SearchViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
internal class SearchUserViewModel @Inject constructor(
    private val searchUserUseCase: SearchUserUseCase,
    private val errorHandler: ErrorHandler,
    private val savedStateHandle: SavedStateHandle,
    @DispatcherIO private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private var lastPage = savedStateHandle.get<Int>(KEY_LAST_PAGE) ?: 1
    private var lastQuery = savedStateHandle.get<String>(KEY_LAST_QUERY)

    private val _state = MutableStateFlow(SearchViewState())
    val state: StateFlow<SearchViewState>
        get() {
            // update state with last state if exist
            savedStateHandle.get<SearchViewState>(KEY_LAST_STATE)?.let {
                _state.value = it
            }
            return _state.asStateFlow()
        }


    fun search(event: SearchEvent) {
        // calculate page number
        val page = when {
            event.loadMore -> lastPage + 1
            event.retry -> lastPage
            else -> 1
        }

        if (checkClearState(event)) {
            clearAllState()
            return
        }

        if (checkDuplicatedRequest(event)) {
            return
        }

        storeLastPageState(page)

        viewModelScope.launch(dispatcher) {
            _state.value = SearchViewState(loading = true)

            when (val response = searchUserUseCase(event.query, page)) {
                is Either.Success -> {
                    val searchViewState = SearchViewState(
                        result = response.data,
                        clearOldData = event.query != lastQuery
                    )

                    if (event.query != lastQuery) {
                        savedStateHandle.set(KEY_LAST_STATE, null)
                    }

                    storeLastState(searchViewState)
                    storeLastQueryState(event.query)
                    _state.value = searchViewState
                }
                is Either.Error -> {
                    val message = errorHandler.getErrorMessage(response.error)
                    _state.value = SearchViewState(error = message)
                }
            }
        }
    }

    /**
     * store last response state in order to use when view is recreated
     */
    private fun storeLastState(searchViewState: SearchViewState) {
        val old = savedStateHandle.get<SearchViewState>(KEY_LAST_STATE)
        if (old != null) {
            val searchData = mutableListOf<UserModel>()
            old.result?.users?.let {
                searchData.addAll(it)
            }
            searchViewState.result?.users?.let {
                searchData.addAll(it)
            }
            searchViewState.result?.copy(users = searchData)?.let {
                savedStateHandle.set(KEY_LAST_STATE, searchViewState.copy(result = it))
            }
        } else {
            savedStateHandle.set(KEY_LAST_STATE, searchViewState)
        }
    }

    private fun checkClearState(event: SearchEvent): Boolean {
        return event.query.isBlank()
    }

    private fun checkDuplicatedRequest(event: SearchEvent): Boolean {
        return event.query == lastQuery && !event.loadMore && !event.retry
    }

    private fun storeLastPageState(page: Int) {
        savedStateHandle.set(KEY_LAST_PAGE, page)
        lastPage = page
    }

    private fun storeLastQueryState(query: String) {
        savedStateHandle.set(KEY_LAST_QUERY, query)
        lastQuery = query
    }

    private fun clearAllState() {
        savedStateHandle.set(KEY_LAST_PAGE, 1)
        savedStateHandle.set(KEY_LAST_QUERY, null)
        savedStateHandle.set(KEY_LAST_STATE, null)
        lastPage = 1
        lastQuery = null
        _state.value = SearchViewState()
    }


    internal companion object {
        const val KEY_LAST_STATE = "search_last_state"
        const val KEY_LAST_PAGE = "search_last_page"
        const val KEY_LAST_QUERY = "search_last_query"
    }
}