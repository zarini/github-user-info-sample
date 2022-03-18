package com.milkyhead.android.payconiq.data.repository

import com.google.common.truth.Truth.assertThat
import com.milkyhead.android.payconiq.data.remote.GithubRemoteDataSource
import com.milkyhead.android.payconiq.data.remote.dto.SearchUserDto
import com.milkyhead.android.payconiq.data.remote.dto.UserDetailsDto
import com.milkyhead.android.payconiq.data.remote.dto.UserDto
import com.milkyhead.android.payconiq.domain.model.*
import com.milkyhead.android.payconiq.domain.repository.GithubUserRepository
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

class GithubUserRepositoryTest {

    private lateinit var githubUserRepository: GithubUserRepository
    private lateinit var githubRemoteDataSource: GithubRemoteDataSource

    @Before
    fun setUp() {
        githubRemoteDataSource = mock(GithubRemoteDataSource::class.java)
        githubUserRepository = DefaultGithubUserRepository(githubRemoteDataSource)
    }

    @Test
    fun `test search must map dto to domain model and returns success`() = runBlocking {
        `when`(githubRemoteDataSource.searchUser(anyString(), anyInt())).thenReturn(
            Either.Success(
                SearchUserDto(
                    totalCount = 100,
                    users = listOf(
                        UserDto(
                            id = 1,
                            login = "test_user",
                            avatar = null
                        )
                    )
                )
            )
        )

        val response = githubUserRepository.searchUser("test", 1)
        verify(githubRemoteDataSource, times(1)).searchUser("test", 1)
        assertThat(response).isInstanceOf(Either.Success::class.java)

        val success: Either.Success<UserListModel, ErrorEntity> = response as Either.Success
        val userListModel = UserListModel(
            totalCount = 100,
            users = listOf(
                UserModel(
                    id = 1,
                    username = "test_user",
                    avatar = null
                )
            )
        )

        assertThat(success.data).isEqualTo(userListModel)
    }

    @Test
    fun `test get user details must map dto to domain model and returns success`() = runBlocking {
        `when`(githubRemoteDataSource.getUserDetails(anyString())).thenReturn(
            Either.Success(
                UserDetailsDto(
                    id = 100L,
                    login = "test_user",
                    avatar = "https://test_user_url/avatar",
                    url = "https://test_user_url",
                    type = "User",
                    fullName = "test",
                    company = "Great company",
                    blog = null,
                    location = "Tehran, Iran",
                    email = "test@email.com",
                    bio = null,
                    twitterUserName = null,
                    publicRepoCount = 10,
                    publicGistCount = 2,
                    followers = 100,
                    following = 50
                )
            )
        )

        val response = githubUserRepository.getUserDetails("test")
        verify(githubRemoteDataSource, times(1)).getUserDetails("test")
        assertThat(response).isInstanceOf(Either.Success::class.java)

        val success: Either.Success<UserDetailsModel, ErrorEntity> = response as Either.Success
        val userDetailsModel = UserDetailsModel(
            id = 100L,
            username = "test_user",
            avatar = "https://test_user_url/avatar",
            url = "https://test_user_url",
            fullName = "test",
            company = "Great company",
            location = "Tehran, Iran",
            email = "test@email.com",
            bio = null,
            twitterUserName = null,
            publicRepoCount = 10,
            followers = 100,
            following = 50
        )

        assertThat(success.data).isEqualTo(userDetailsModel)
    }
}