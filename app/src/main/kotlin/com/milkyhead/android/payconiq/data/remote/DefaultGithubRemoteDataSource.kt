package com.milkyhead.android.payconiq.data.remote

import com.milkyhead.android.payconiq.data.mapper.mapToErrorEntity
import com.milkyhead.android.payconiq.data.remote.api.GithubApi
import com.milkyhead.android.payconiq.data.remote.dto.SearchUserDto
import com.milkyhead.android.payconiq.data.remote.dto.UserDetailsDto
import com.milkyhead.android.payconiq.domain.model.Either
import com.milkyhead.android.payconiq.domain.model.ErrorEntity
import javax.inject.Inject


internal class DefaultGithubRemoteDataSource @Inject constructor(
    private val githubApi: GithubApi
) : GithubRemoteDataSource {

    override suspend fun searchUser(
        query: String,
        page: Int
    ): Either<SearchUserDto, ErrorEntity> = runWithErrorMapper {
        githubApi.searchUser(query, page)
    }

    override suspend fun getUserDetails(
        username: String
    ): Either<UserDetailsDto, ErrorEntity> = runWithErrorMapper {
        githubApi.getUserDetails(username)
    }


    private inline fun <T> runWithErrorMapper(block: () -> T): Either<T, ErrorEntity> {
        return try {
            Either.Success(block())
        } catch (ex: Exception) {
            Either.Error(ex.mapToErrorEntity())
        }
    }
}