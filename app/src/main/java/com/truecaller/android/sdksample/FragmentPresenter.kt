package com.truecaller.android.sdksample

interface FragmentPresenter {
    fun showVerificationFlow()
    fun closeVerificationFlow()
    fun showCallingMessageInLoader(ttl: Double?)
    fun showInputNumberView(inProgress: Boolean)
    fun showInputNameView(inProgress: Boolean)
    fun showInputOtpView(inProgress: Boolean, otp: String? = null, ttl: Double? = null)
}