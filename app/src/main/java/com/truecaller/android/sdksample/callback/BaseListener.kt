package com.truecaller.android.sdksample.callback

import android.content.Context
import java.lang.ref.WeakReference

interface BaseListener {
    fun getContext() : WeakReference<Context>
}