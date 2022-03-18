package com.milkyhead.android.payconiq.data.repository

import com.milkyhead.android.payconiq.data.mapper.mapToUserDetailsModel
import com.milkyhead.android.payconiq.data.mapper.mapToUserListModel
import com.milkyhead.android.payconiq.data.remote.GithubRemoteDataSource
import com.milkyhead.android.payconiq.domain.model.Either
import com.milkyhead.android.payconiq.domain.model.ErrorEntity
import com.milkyhead.android.payconiq.domain.model.UserDetailsModel
import com.milkyhead.android.payconiq.domain.model.UserListModel
import com.milkyhead.android.payconiq.domain.repository.GithubUserRepository
import javax.inject.Inject


internal class DefaultGithubUserRepository @Inject constructor(
    private val githubRemoteDataSource: GithubRemoteDataSource
) : GithubUserRepository {


    override suspend fun searchUser(
        query: String,
        page: Int
    ): Either<UserListModel, ErrorEntity> {
        return when (val response = githubRemoteDataSource.searchUser(query, page)) {
            is Either.Success -> Either.Success(response.data.mapToUserListModel())
            is Either.Error -> Either.Error(response.error)
        }
    }

    override suspend fun getUserDetails(
        username: String
    ): Either<UserDetailsModel, ErrorEntity> {
        return when (val response = githubRemoteDataSource.getUserDetails(username)) {
            is Either.Success -> Either.Success(response.data.mapToUserDetailsModel())
            is Either.Error -> Either.Error(response.error)
        }
    }
}