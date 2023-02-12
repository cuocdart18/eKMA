package com.example.kmatool.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*

// Constants
const val KIT_URL = "https://www.facebook.com/kitclubKMA"
const val KEY_PASS_MINISTUDENT_ID = "ministudent_id"
const val KEY_PASS_STATISTIC_SUBJECT = "statistic_subject"
const val DATABASE_MINISTUDENT_NAME = "mini_student.db"
const val SCALE_LAYOUT_STATISTIC_SUBJECT_DIALOG_X = 0.80
const val SCALE_LAYOUT_STATISTIC_SUBJECT_DIALOG_Y = 0.25
const val SCALE_LAYOUT_SEARCH_DATA_DIALOG_X = 0.80
const val SCALE_LAYOUT_SEARCH_DATA_DIALOG_Y = 0.60

// Global method
fun EditText.textChanges(): Flow<CharSequence?> {
    return callbackFlow<CharSequence?> {
        val listener = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) = Unit
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                trySend(s)
            }
        }
        addTextChangedListener(listener)
        awaitClose { removeTextChangedListener(listener) }
    }.onStart { emit(text) }
}