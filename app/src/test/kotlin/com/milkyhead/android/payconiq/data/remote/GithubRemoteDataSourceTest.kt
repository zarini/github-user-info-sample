package com.milkyhead.android.payconiq.data.remote

import com.google.common.truth.Truth.assertThat
import com.milkyhead.android.payconiq.data.remote.api.GithubApi
import com.milkyhead.android.payconiq.data.remote.dto.SearchUserDto
import com.milkyhead.android.payconiq.data.remote.dto.UserDetailsDto
import com.milkyhead.android.payconiq.data.remote.dto.UserDto
import com.milkyhead.android.payconiq.domain.model.Either
import com.milkyhead.android.payconiq.domain.model.ErrorEntity
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import retrofit2.HttpException

class GithubRemoteDataSourceTest {

    private lateinit var githubRemoteDataSource: GithubRemoteDataSource
    private lateinit var githubApi: GithubApi

    @Before
    fun setUp() {
        githubApi = mock(GithubApi::class.java)
        githubRemoteDataSource = DefaultGithubRemoteDataSource(githubApi)
    }


    @Test
    fun `test search user must returns success if get success from api`() = runBlocking {
        `when`(githubApi.searchUser(anyString(), anyInt())).thenReturn(
            SearchUserDto(
                totalCount = 100,
                users = listOf(
                    UserDto(
                        id = 1,
                        login = "user_test",
                        avatar = null
                    )
                )
            )
        )

        val response = githubRemoteDataSource.searchUser("test", 1)
        verify(githubApi, times(1)).searchUser("test", 1)
        assertThat(response).isInstanceOf(Either.Success::class.java)
    }

    @Test
    fun `test get user details must returns success if get success from api`() = runBlocking {
        `when`(githubApi.getUserDetails(anyString())).thenReturn(
            UserDetailsDto(
                id = 100L,
                login = "test_user",
                avatar = null,
                url = "https://test_user_url",
                type = "User",
                fullName = "test",
                company = null,
                blog = null,
                location = "Tehran, Iran",
                email = null,
                bio = null,
                twitterUserName = null,
                publicRepoCount = 1,
                publicGistCount = 0,
                followers = 1,
                following = 0
            )
        )

        val response = githubRemoteDataSource.getUserDetails("test")
        verify(githubApi, times(1)).getUserDetails("test")
        assertThat(response).isInstanceOf(Either.Success::class.java)
    }

    @Test
    fun `test map service exception to error entity in api call`() = runBlocking {
        `when`(githubApi.searchUser(anyString(), anyInt())).thenThrow(HttpException::class.java)
        val searchResponse = githubRemoteDataSource.searchUser("test", 1)
        assertThat(searchResponse).isInstanceOf(Either.Error::class.java)
        assertThat((searchResponse as Either.Error).error).isEqualTo(ErrorEntity.HttpError)

        `when`(githubApi.getUserDetails(anyString())).thenThrow(RuntimeException())
        val getDetailsResponse = githubRemoteDataSource.getUserDetails("test")
        assertThat(getDetailsResponse).isInstanceOf(Either.Error::class.java)
        assertThat((getDetailsResponse as Either.Error).error).isEqualTo(ErrorEntity.UnknownError)
    }
}