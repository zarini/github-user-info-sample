package com.milkyhead.android.payconiq.presentation.error

import android.content.Context
import com.milkyhead.android.payconiq.R
import com.milkyhead.android.payconiq.domain.model.ErrorEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


internal class DefaultErrorHandler @Inject constructor(
    @ApplicationContext private val context: Context
) : ErrorHandler {

    override fun getErrorMessage(errorEntity: ErrorEntity): String {
        return when (errorEntity) {
            is ErrorEntity.NetworkError -> context.getString(R.string.check_internet_connection)
            is ErrorEntity.UnknownError -> context.getString(R.string.unknown_error)
            is ErrorEntity.HttpError -> context.getString(R.string.error_occurred)
        }
    }
}