package com.milkyhead.android.payconiq.data.mapper

import com.milkyhead.android.payconiq.data.remote.dto.SearchUserDto
import com.milkyhead.android.payconiq.data.remote.dto.UserDetailsDto
import com.milkyhead.android.payconiq.data.remote.dto.UserDto
import com.milkyhead.android.payconiq.domain.model.UserDetailsModel
import com.milkyhead.android.payconiq.domain.model.UserListModel
import com.milkyhead.android.payconiq.domain.model.UserModel


internal fun SearchUserDto.mapToUserListModel(): UserListModel {
    return UserListModel(
        totalCount = totalCount,
        users = users.map { it.mapToUserModel() }
    )
}

internal fun UserDto.mapToUserModel(): UserModel {
    return UserModel(
        id = id,
        username = login,
        avatar = avatar
    )
}


internal fun UserDetailsDto.mapToUserDetailsModel(): UserDetailsModel {
    return UserDetailsModel(
        id = id,
        username = login,
        avatar = avatar,
        url = url,
        fullName = fullName,
        company = company,
        location = location,
        email = email,
        bio = bio,
        twitterUserName = twitterUserName,
        publicRepoCount = publicRepoCount ?: 0,
        followers = followers ?: 0,
        following = following ?: 0
    )
}