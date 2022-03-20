package com.milkyhead.android.payconiq.presentation.details

import androidx.lifecycle.SavedStateHandle
import com.google.common.truth.Truth.assertThat
import com.milkyhead.android.payconiq.domain.model.Either
import com.milkyhead.android.payconiq.domain.model.ErrorEntity
import com.milkyhead.android.payconiq.domain.model.UserDetailsModel
import com.milkyhead.android.payconiq.domain.usecase.GetUserDetailsUseCase
import com.milkyhead.android.payconiq.presentation.details.UserDetailsViewModel.Companion.KEY_LAST_STATE
import com.milkyhead.android.payconiq.presentation.error.ErrorHandler
import com.milkyhead.android.payconiq.presentation.state.DetailsViewState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

@ExperimentalCoroutinesApi
class UserDetailsViewModelTest {

    private lateinit var userDetailsViewModel: UserDetailsViewModel
    private lateinit var getUserDetailsUseCase: GetUserDetailsUseCase
    private lateinit var errorHandler: ErrorHandler
    private lateinit var savedStateHandle: SavedStateHandle
    private val testCoroutineDispatcher = TestCoroutineDispatcher()

    @Before
    fun setUp() {
        getUserDetailsUseCase = mock(GetUserDetailsUseCase::class.java)
        errorHandler = mock(ErrorHandler::class.java)
        savedStateHandle = SavedStateHandle()
        userDetailsViewModel = UserDetailsViewModel(
            getUserDetailsUseCase,
            errorHandler,
            savedStateHandle,
            testCoroutineDispatcher
        )
    }

    @Test
    fun `test view state must updated with user info after getting success from api call`() =
        testCoroutineDispatcher.runBlockingTest {
            val userDetailsModel = getTestUserDetailsModel()

            `when`(getUserDetailsUseCase.invoke(anyString())).thenReturn(
                Either.Success(userDetailsModel)
            )

            userDetailsViewModel.getUserDetails("username")
            val state = userDetailsViewModel.state.value

            assertThat(state.result).isNotNull()
            assertThat(state.result).isEqualTo(userDetailsModel)
        }

    @Test
    fun `test last success view state must save in state handler`() =
        testCoroutineDispatcher.runBlockingTest {
            val userDetailsModel = getTestUserDetailsModel()

            `when`(getUserDetailsUseCase.invoke(anyString())).thenReturn(
                Either.Success(userDetailsModel)
            )

            userDetailsViewModel.getUserDetails("username")
            val state = userDetailsViewModel.state.value
            val lastState = savedStateHandle.get<DetailsViewState>(KEY_LAST_STATE)
            assertThat(state).isEqualTo(lastState)
        }

    @Test
    fun `test view state must updated with error after getting error from api`() =
        testCoroutineDispatcher.runBlockingTest {
            `when`(getUserDetailsUseCase(anyString())).thenReturn(
                Either.Error(ErrorEntity.UnknownError)
            )
            `when`(errorHandler.getErrorMessage(ErrorEntity.UnknownError)).thenReturn("unknown error!")

            userDetailsViewModel.getUserDetails("username")
            val state = userDetailsViewModel.state.value
            assertThat(state.error).isNotNull()
            assertThat(state.error).isEqualTo("unknown error!")
        }

    @Test
    fun `test view state must retrieve last success state after viewmodel recreate`() =
        testCoroutineDispatcher.runBlockingTest {
            val state = DetailsViewState(
                result = getTestUserDetailsModel()
            )

            savedStateHandle.set(KEY_LAST_STATE, state)

            userDetailsViewModel = UserDetailsViewModel(
                getUserDetailsUseCase,
                errorHandler,
                savedStateHandle,
                testCoroutineDispatcher
            )

            val currentStateAfterRecreate = userDetailsViewModel.state.value
            assertThat(currentStateAfterRecreate).isEqualTo(state)
        }

    private fun getTestUserDetailsModel() = UserDetailsModel(
        id = 1,
        username = "test_username",
        avatar = null,
        url = "https://github.com",
        fullName = "test_full_name",
        company = "test_company",
        location = null,
        email = null,
        bio = "test user bio",
        twitterUserName = null,
        publicRepoCount = 100,
        following = 1000,
        followers = 10000
    )
}