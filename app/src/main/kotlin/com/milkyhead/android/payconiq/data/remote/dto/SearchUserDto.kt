package com.milkyhead.android.payconiq.data.remote.dto

import com.google.gson.annotations.SerializedName


data class SearchUserDto(
    @SerializedName("total_count")
    val totalCount: Int,
    @SerializedName("items")
    val users: List<UserDto>
)

data class UserDto(
    @SerializedName("id")
    val id: Long,
    @SerializedName("login")
    val login: String,
    @SerializedName("avatar_url")
    val avatar: String?
)