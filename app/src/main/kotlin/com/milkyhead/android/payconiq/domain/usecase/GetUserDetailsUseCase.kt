package com.milkyhead.android.payconiq.domain.usecase

import com.milkyhead.android.payconiq.domain.model.Either
import com.milkyhead.android.payconiq.domain.model.ErrorEntity
import com.milkyhead.android.payconiq.domain.model.UserDetailsModel
import com.milkyhead.android.payconiq.domain.repository.GithubUserRepository
import javax.inject.Inject


internal interface GetUserDetailsUseCase {
    /**
     *
     */
    suspend operator fun invoke(username: String): Either<UserDetailsModel, ErrorEntity>
}


internal class DefaultGetUserDetailsUseCase @Inject constructor(
    private val githubUserRepository: GithubUserRepository
) : GetUserDetailsUseCase {

    override suspend fun invoke(
        username: String
    ): Either<UserDetailsModel, ErrorEntity> {
        return githubUserRepository.getUserDetails(username)
    }
}