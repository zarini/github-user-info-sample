package com.milkyhead.android.payconiq.presentation.state

import android.os.Parcelable
import com.milkyhead.android.payconiq.domain.model.UserDetailsModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class DetailsViewState(
    val loading: Boolean = false,
    val error: String? = null,
    val result: UserDetailsModel? = null
) : Parcelable
