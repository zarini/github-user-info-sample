package com.milkyhead.android.payconiq.presentation.error

import com.milkyhead.android.payconiq.domain.model.ErrorEntity


internal interface ErrorHandler {

    /**
     * parse error entity to error message
     */
    fun getErrorMessage(errorEntity: ErrorEntity): String
}