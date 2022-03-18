package com.milkyhead.android.payconiq.domain.usecase

import com.milkyhead.android.payconiq.domain.repository.GithubUserRepository
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

class GetUserDetailsUseCaseTest {

    private lateinit var getUserDetailsUseCase: GetUserDetailsUseCase
    private lateinit var githubUserRepository: GithubUserRepository

    @Before
    fun setUp() {
        githubUserRepository = mock(GithubUserRepository::class.java)
        getUserDetailsUseCase = DefaultGetUserDetailsUseCase(githubUserRepository)
    }


    @Test
    fun `test get user details usecase must calls on repository`() = runBlocking<Unit> {
        getUserDetailsUseCase("test")
        verify(githubUserRepository, times(1)).getUserDetails("test")
    }
}