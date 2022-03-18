package com.milkyhead.android.payconiq.presentation.state

import android.os.Parcelable
import com.milkyhead.android.payconiq.domain.model.UserListModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class SearchViewState(
    val loading: Boolean = false,
    val error: String? = null,
    val result: UserListModel? = null,
    val clearOldData: Boolean = false
) : Parcelable
