package com.milkyhead.android.payconiq.domain.usecase

import com.milkyhead.android.payconiq.domain.model.Either
import com.milkyhead.android.payconiq.domain.model.ErrorEntity
import com.milkyhead.android.payconiq.domain.model.UserListModel
import com.milkyhead.android.payconiq.domain.repository.GithubUserRepository
import javax.inject.Inject


internal interface SearchUserUseCase {
    /**
     *
     */
    suspend operator fun invoke(
        query: String,
        page: Int
    ): Either<UserListModel, ErrorEntity>
}


internal class DefaultSearchUserUseCase @Inject constructor(
    private val githubUserRepository: GithubUserRepository
) : SearchUserUseCase {

    override suspend fun invoke(
        query: String,
        page: Int
    ): Either<UserListModel, ErrorEntity> {
        return githubUserRepository.searchUser(query, page)
    }
}