package com.milkyhead.android.payconiq.domain.repository

import com.milkyhead.android.payconiq.domain.model.Either
import com.milkyhead.android.payconiq.domain.model.ErrorEntity
import com.milkyhead.android.payconiq.domain.model.UserDetailsModel
import com.milkyhead.android.payconiq.domain.model.UserListModel


internal interface GithubUserRepository {

    /**
     * search users with specific query
     */
    suspend fun searchUser(query: String, page: Int): Either<UserListModel, ErrorEntity>

    /**
     * get specific user account details
     */
    suspend fun getUserDetails(username: String): Either<UserDetailsModel, ErrorEntity>
}