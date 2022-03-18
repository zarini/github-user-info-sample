package com.milkyhead.android.payconiq.data.remote.api

import com.milkyhead.android.payconiq.data.remote.dto.SearchUserDto
import com.milkyhead.android.payconiq.data.remote.dto.UserDetailsDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


internal interface GithubApi {


    @GET("/search/users")
    suspend fun searchUser(
        @Query("q") query: String,
        @Query("page") page: Int
    ): SearchUserDto


    @GET("users/{username}")
    suspend fun getUserDetails(
        @Path("username") username: String
    ): UserDetailsDto
}