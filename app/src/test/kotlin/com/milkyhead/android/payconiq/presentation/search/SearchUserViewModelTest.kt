package com.milkyhead.android.payconiq.presentation.search

import androidx.lifecycle.SavedStateHandle
import com.google.common.truth.Truth.assertThat
import com.milkyhead.android.payconiq.domain.model.Either
import com.milkyhead.android.payconiq.domain.model.ErrorEntity
import com.milkyhead.android.payconiq.domain.model.UserListModel
import com.milkyhead.android.payconiq.domain.model.UserModel
import com.milkyhead.android.payconiq.domain.usecase.SearchUserUseCase
import com.milkyhead.android.payconiq.presentation.error.ErrorHandler
import com.milkyhead.android.payconiq.presentation.event.SearchEvent
import com.milkyhead.android.payconiq.presentation.search.SearchUserViewModel.Companion.KEY_LAST_PAGE
import com.milkyhead.android.payconiq.presentation.search.SearchUserViewModel.Companion.KEY_LAST_QUERY
import com.milkyhead.android.payconiq.presentation.search.SearchUserViewModel.Companion.KEY_LAST_STATE
import com.milkyhead.android.payconiq.presentation.state.SearchViewState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

@ExperimentalCoroutinesApi
class SearchUserViewModelTest {

    private lateinit var searchUserViewModel: SearchUserViewModel
    private lateinit var searchUserUseCase: SearchUserUseCase
    private lateinit var errorHandler: ErrorHandler
    private lateinit var savedStateHandle: SavedStateHandle
    private val testCoroutineDispatcher = TestCoroutineDispatcher()

    @Before
    fun setUp() {
        searchUserUseCase = mock(SearchUserUseCase::class.java)
        errorHandler = mock(ErrorHandler::class.java)
        savedStateHandle = SavedStateHandle()
        searchUserViewModel = SearchUserViewModel(
            searchUserUseCase,
            errorHandler,
            savedStateHandle,
            testCoroutineDispatcher
        )
    }

    @Test
    fun `test current page must increment if need load more data`() =
        testCoroutineDispatcher.runBlockingTest {
            val event = SearchEvent(query = "test")
            searchUserViewModel.search(event)
            searchUserViewModel.search(event.copy(loadMore = true))

            verify(searchUserUseCase, times(1)).invoke("test", 1)
            verify(searchUserUseCase, times(1)).invoke("test", 2)
        }

    @Test
    fun `test current page must not increment if api need to retry`() =
        testCoroutineDispatcher.runBlockingTest {
            val event = SearchEvent(query = "test")
            searchUserViewModel.search(event)
            searchUserViewModel.search(event.copy(retry = true))

            verify(searchUserUseCase, times(2)).invoke("test", 1)
        }

    @Test
    fun `test current page must be one if api called with new query`() =
        testCoroutineDispatcher.runBlockingTest {
            val event = SearchEvent(query = "test")
            searchUserViewModel.search(event)
            searchUserViewModel.search(event.copy(query = "tes"))
            searchUserViewModel.search(event.copy(query = "te"))
            searchUserViewModel.search(event.copy(query = "t"))

            verify(searchUserUseCase, times(1)).invoke("test", 1)
            verify(searchUserUseCase, times(1)).invoke("tes", 1)
            verify(searchUserUseCase, times(1)).invoke("te", 1)
            verify(searchUserUseCase, times(1)).invoke("t", 1)
        }

    @Test
    fun `test api must not call if query is empty and all state must clear`() =
        testCoroutineDispatcher.runBlockingTest {
            val event = SearchEvent(query = "")
            searchUserViewModel.search(event)

            verify(searchUserUseCase, times(0)).invoke("", 1)

            val lastPage = savedStateHandle.get<Int>(KEY_LAST_PAGE)
            assertThat(lastPage).isEqualTo(1)
            val lastQuery = savedStateHandle.get<String>(KEY_LAST_QUERY)
            assertThat(lastQuery).isNull()
            val lastState = savedStateHandle.get<SearchViewState>(KEY_LAST_STATE)
            assertThat(lastState).isNull()
        }

    @Test
    fun `test api must not call if query is same and api dose not want to load more or retry`() =
        testCoroutineDispatcher.runBlockingTest {
            `when`(searchUserUseCase.invoke(anyString(), anyInt())).thenReturn(
                Either.Success(
                    UserListModel(
                        totalCount = 1,
                        users = listOf(
                            UserModel(
                                id = 10,
                                "test",
                                null
                            )
                        )
                    )
                )
            )

            val event = SearchEvent(query = "test")
            searchUserViewModel.search(event)
            searchUserViewModel.search(event)

            verify(searchUserUseCase, times(1)).invoke("test", 1)
        }

    @Test
    fun `test latest page must save in state handle`() = testCoroutineDispatcher.runBlockingTest {
        val event = SearchEvent(query = "test")
        searchUserViewModel.search(event)
        searchUserViewModel.search(event.copy(loadMore = true))
        searchUserViewModel.search(event.copy(loadMore = true))

        val lastOffset = savedStateHandle.get<Int>(KEY_LAST_PAGE)
        assertThat(lastOffset).isEqualTo(3)
    }

    @Test
    fun `test latest query must save in state handle if get success`() =
        testCoroutineDispatcher.runBlockingTest {
            val userListModel = UserListModel(
                totalCount = 1,
                users = listOf(
                    UserModel(
                        id = 100,
                        "test_user",
                        null
                    )
                )
            )

            `when`(searchUserUseCase.invoke(anyString(), anyInt())).thenReturn(
                Either.Success(userListModel)
            )

            val event = SearchEvent(query = "test")
            searchUserViewModel.search(event)

            val lastQuery = savedStateHandle.get<String>(KEY_LAST_QUERY)
            assertThat(lastQuery).isEqualTo("test")
        }

    @Test
    fun `test view state must updated with search result after getting success from api call`() =
        testCoroutineDispatcher.runBlockingTest {
            val userListModel = UserListModel(
                totalCount = 1,
                users = listOf(
                    UserModel(
                        id = 100,
                        "test_user",
                        null
                    )
                )
            )

            `when`(searchUserUseCase.invoke(anyString(), anyInt())).thenReturn(
                Either.Success(userListModel)
            )

            searchUserViewModel.search(SearchEvent(query = "test"))
            val state = searchUserViewModel.state.value

            assertThat(state.result).isNotNull()
            assertThat(state.result).isEqualTo(userListModel)
        }

    @Test
    fun `test last success view state must save in state handler`() =
        testCoroutineDispatcher.runBlockingTest {
            val userListModel = UserListModel(
                totalCount = 1,
                users = listOf(
                    UserModel(
                        id = 100,
                        "test_user",
                        null
                    )
                )
            )

            `when`(searchUserUseCase.invoke(anyString(), anyInt())).thenReturn(
                Either.Success(userListModel)
            )

            searchUserViewModel.search(SearchEvent(query = "test"))
            val state = searchUserViewModel.state.value
            val lastState = savedStateHandle.get<SearchViewState>(KEY_LAST_STATE)
            assertThat(state).isEqualTo(lastState)
        }

    @Test
    fun `test last success view state must clear before new sate save in state handler if query dose not same`() =
        testCoroutineDispatcher.runBlockingTest {
            val response1 = UserListModel(
                totalCount = 1,
                users = listOf(
                    UserModel(
                        id = 100,
                        "test_user",
                        null
                    )
                )
            )

            val response2 = UserListModel(
                totalCount = 2,
                users = listOf(
                    UserModel(
                        id = 333,
                        "new user",
                        null
                    ),
                    UserModel(
                        id = 555,
                        "another new user",
                        null
                    )
                )
            )

            `when`(searchUserUseCase.invoke("test", 1)).thenReturn(
                Either.Success(response1)
            )

            `when`(searchUserUseCase.invoke("new", 1)).thenReturn(
                Either.Success(response2)
            )

            searchUserViewModel.search(SearchEvent(query = "test"))
            assertThat(savedStateHandle.get<SearchViewState>(KEY_LAST_STATE)?.result).isEqualTo(
                response1
            )

            searchUserViewModel.search(SearchEvent(query = "new"))
            assertThat(savedStateHandle.get<SearchViewState>(KEY_LAST_STATE)?.result).isEqualTo(
                response2
            )
        }

    @Test
    fun `test last success view state must updated with all data if search query dose the same`() =
        testCoroutineDispatcher.runBlockingTest {
            val response1 = UserListModel(
                totalCount = 2,
                users = listOf(
                    UserModel(
                        id = 333,
                        "new user",
                        null
                    )
                )
            )

            val response2 = UserListModel(
                totalCount = 2,
                users = listOf(
                    UserModel(
                        id = 555,
                        "another new user",
                        null
                    )
                )
            )

            `when`(searchUserUseCase.invoke("new", 1)).thenReturn(
                Either.Success(response1)
            )

            `when`(searchUserUseCase.invoke("new", 2)).thenReturn(
                Either.Success(response2)
            )

            searchUserViewModel.search(SearchEvent(query = "new"))
            searchUserViewModel.search(SearchEvent(query = "new", loadMore = true))

            val expected = UserListModel(
                totalCount = 2,
                users = listOf(
                    UserModel(
                        id = 333,
                        "new user",
                        null
                    ),
                    UserModel(
                        id = 555,
                        "another new user",
                        null
                    )
                )
            )

            assertThat(savedStateHandle.get<SearchViewState>(KEY_LAST_STATE)?.result).isEqualTo(
                expected
            )
        }

    @Test
    fun `test view state must updated with error after getting error from api`() =
        testCoroutineDispatcher.runBlockingTest {
            `when`(searchUserUseCase(anyString(), anyInt())).thenReturn(
                Either.Error(ErrorEntity.NetworkError)
            )
            `when`(errorHandler.getErrorMessage(ErrorEntity.NetworkError)).thenReturn("network error!")

            searchUserViewModel.search(SearchEvent(query = "test"))
            val state = searchUserViewModel.state.value
            assertThat(state.error).isNotNull()
            assertThat(state.error).isEqualTo("network error!")
        }

    @Test
    fun `test view state must retrieve last success state after viewmodel recreate`() =
        testCoroutineDispatcher.runBlockingTest {
            val state = SearchViewState(
                result = UserListModel(
                    totalCount = 2,
                    users = listOf(
                        UserModel(
                            id = 333,
                            "new user",
                            null
                        )
                    )
                )
            )

            savedStateHandle.set(KEY_LAST_STATE, state)

            searchUserViewModel = SearchUserViewModel(
                searchUserUseCase,
                errorHandler,
                savedStateHandle,
                testCoroutineDispatcher
            )

            val currentStateAfterRecreate = searchUserViewModel.state.value
            assertThat(currentStateAfterRecreate).isEqualTo(state)
        }
}