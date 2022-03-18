package com.milkyhead.android.payconiq.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserListModel(
    val totalCount: Int,
    val users: List<UserModel>
) : Parcelable

@Parcelize
data class UserModel(
    val id: Long,
    val username: String,
    val avatar: String?
) : Parcelable