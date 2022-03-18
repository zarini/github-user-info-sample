package com.milkyhead.android.payconiq.domain.usecase

import com.milkyhead.android.payconiq.domain.repository.GithubUserRepository
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

class SearchUserUseCaseTest {

    private lateinit var searchUserUseCase: SearchUserUseCase
    private lateinit var githubUserRepository: GithubUserRepository

    @Before
    fun setUp() {
        githubUserRepository = mock(GithubUserRepository::class.java)
        searchUserUseCase = DefaultSearchUserUseCase(githubUserRepository)
    }


    @Test
    fun `test search user usecase must calls on repository`() = runBlocking<Unit> {
        searchUserUseCase("test", 1)
        verify(githubUserRepository, times(1)).searchUser("test", 1)
    }
}