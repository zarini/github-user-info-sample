package com.milkyhead.android.payconiq.data.remote

import com.milkyhead.android.payconiq.data.remote.dto.SearchUserDto
import com.milkyhead.android.payconiq.data.remote.dto.UserDetailsDto
import com.milkyhead.android.payconiq.domain.model.Either
import com.milkyhead.android.payconiq.domain.model.ErrorEntity


internal interface GithubRemoteDataSource {

    /**
     * search users with specific query
     */
    suspend fun searchUser(query: String, page: Int): Either<SearchUserDto, ErrorEntity>

    /**
     * get specific user account details
     */
    suspend fun getUserDetails(username: String): Either<UserDetailsDto, ErrorEntity>
}