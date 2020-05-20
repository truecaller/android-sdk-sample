package com.truecaller.android.sdksample.callback

import android.content.Context
import com.truecaller.android.sdksample.Scope

interface FragmentListener {
    fun getContext() : Context
    fun getProfile(flowType: Int)
    fun setScope(scope: Scope)
    fun loadFlowSelectionFragment()
    fun onVerificationRequired()
    fun closeFlow()
    fun initVerification(phoneNumber: String)
}