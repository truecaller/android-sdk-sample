package com.truecaller.android.sdksample.callback

import com.truecaller.android.sdk.TrueProfile
import com.truecaller.android.sdksample.Scope

interface FragmentListener : BaseListener {
    fun startFlow(flowType: Int)
    fun getProfile()
    fun setScope(scope: Scope)
    fun loadFlowSelectionFragment()
    fun onVerificationRequired()
    fun closeFlow()
    fun initVerification(phoneNumber: String)
    fun validateOtp(otp: String)
    fun verifyUser(trueProfile: TrueProfile)
    fun success(name: String?)
}