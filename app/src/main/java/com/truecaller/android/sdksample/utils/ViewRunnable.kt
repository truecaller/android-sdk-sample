package com.truecaller.android.sdksample.utils

import android.view.View

internal abstract class ViewRunnable : Runnable {
    var view: View? = null
}