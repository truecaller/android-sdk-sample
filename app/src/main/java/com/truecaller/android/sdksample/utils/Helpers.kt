package com.truecaller.android.sdksample.utils

import android.widget.EditText

fun EditText.suppressSoftKeyboard() {
    showSoftInputOnFocus = false
}