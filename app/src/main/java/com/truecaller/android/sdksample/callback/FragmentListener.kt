package com.truecaller.android.sdksample.callback

import com.truecaller.android.sdksample.Scope

interface FragmentListener : BaseListener {
    fun getProfile(flowType: Int)
    fun setScope(scope: Scope)
    fun loadFlowSelectionFragment()
    fun onVerificationRequired()
    fun closeFlow()
    fun initVerification(phoneNumber: String)
}