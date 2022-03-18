package com.milkyhead.android.payconiq.data.mapper

import com.milkyhead.android.payconiq.domain.model.ErrorEntity
import okio.IOException
import retrofit2.HttpException


internal fun Exception.mapToErrorEntity(): ErrorEntity {
    return when (this) {
        is HttpException -> ErrorEntity.HttpError
        is IOException -> ErrorEntity.NetworkError
        else -> ErrorEntity.UnknownError
    }
}