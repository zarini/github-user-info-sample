package com.milkyhead.android.payconiq.core

import android.content.res.Resources
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


val Int.dp: Int get() = (this * Resources.getSystem().displayMetrics.density).roundToInt()


fun CoroutineScope.doAfterTextChanged(
    delay: Long = 500L,
    editText: EditText,
    onTextChanged: (text: String) -> Unit
) {
    var job: Job? = null
    val listener = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            job?.cancel()
            job = launch {
                delay(delay)
                onTextChanged(s?.toString() ?: "")
            }
        }
    }
    editText.addTextChangedListener(listener)
}