package com.milkyhead.android.payconiq.data.remote.dto

import com.google.gson.annotations.SerializedName


data class UserDetailsDto(
    @SerializedName("id")
    val id: Long,
    @SerializedName("login")
    val login: String,
    @SerializedName("avatar_url")
    val avatar: String?,
    @SerializedName("url")
    val url: String,
    @SerializedName("type")
    val type: String?,
    @SerializedName("name")
    val fullName: String,
    @SerializedName("company")
    val company: String?,
    @SerializedName("blog")
    val blog: String?,
    @SerializedName("location")
    val location: String?,
    @SerializedName("email")
    val email: String?,
    @SerializedName("bio")
    val bio: String?,
    @SerializedName("twitter_username")
    val twitterUserName: String?,
    @SerializedName("public_repos")
    val publicRepoCount: Int?,
    @SerializedName("public_gists")
    val publicGistCount: Int?,
    @SerializedName("followers")
    val followers: Int,
    @SerializedName("following")
    val following: Int
)
