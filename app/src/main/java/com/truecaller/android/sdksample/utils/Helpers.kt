package com.truecaller.android.sdksample.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

fun EditText.suppressSoftKeyboard() {
    showSoftInputOnFocus = false
    /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        showSoftInputOnFocus = false
    } else {
        inputType = InputType.TYPE_NULL;
    }*/
}

fun EditText.showSoftKeyboard() {
    requestFocus()
    showSoftInputOnFocus = true
}

fun Context.hideKeyboard(view: View) {
    (getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.apply {
        hideSoftInputFromWindow(view.windowToken, 0)
    }
}

fun Context.showKeyboard(view: View) {
    view.requestFocus()
    (getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.apply {
        toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }
}