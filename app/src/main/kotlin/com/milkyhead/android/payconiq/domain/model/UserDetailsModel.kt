package com.milkyhead.android.payconiq.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserDetailsModel(
    val id: Long,
    val username: String,
    val avatar: String?,
    val url: String?,
    val fullName: String?,
    val company: String?,
    val location: String?,
    val email: String?,
    val bio: String?,
    val twitterUserName: String?,
    val publicRepoCount: Int,
    val followers: Int,
    val following: Int
): Parcelable
