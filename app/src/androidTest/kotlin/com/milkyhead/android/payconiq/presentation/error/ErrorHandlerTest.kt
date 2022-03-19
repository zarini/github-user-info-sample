package com.milkyhead.android.payconiq.presentation.error

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import com.milkyhead.android.payconiq.R
import com.milkyhead.android.payconiq.domain.model.ErrorEntity
import org.junit.Before
import org.junit.Test

class ErrorHandlerTest {

    private lateinit var errorHandler: ErrorHandler
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        errorHandler = DefaultErrorHandler(context)
    }


    @Test
    fun test_parse_error_entity_to_error_message() {
        val expectedUnknownMessage = context.getString(R.string.unknown_error)
        val unknownMessage = errorHandler.getErrorMessage(ErrorEntity.UnknownError)
        assertThat(unknownMessage).isEqualTo(expectedUnknownMessage)

        val expectedNetworkMessage = context.getString(R.string.check_internet_connection)
        val networkMessage = errorHandler.getErrorMessage(ErrorEntity.NetworkError)
        assertThat(networkMessage).isEqualTo(expectedNetworkMessage)

        val expectedHttpMessage = context.getString(R.string.error_occurred)
        val httpMessage = errorHandler.getErrorMessage(ErrorEntity.HttpError)
        assertThat(httpMessage).isEqualTo(expectedHttpMessage)
    }
}