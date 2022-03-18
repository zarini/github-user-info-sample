package com.milkyhead.android.payconiq.domain.model


data class UserListModel(
    val totalCount: Int,
    val users: List<UserModel>
)

data class UserModel(
    val id: Long,
    val username: String,
    val avatar: String?
)