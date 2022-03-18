package com.milkyhead.android.payconiq.domain.model


sealed class ErrorEntity {
    object NetworkError : ErrorEntity()
    object UnknownError : ErrorEntity()
    object HttpError : ErrorEntity()
}
