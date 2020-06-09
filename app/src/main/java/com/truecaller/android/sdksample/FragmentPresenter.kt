package com.truecaller.android.sdksample

interface FragmentPresenter {
    fun showVerificationFlow()
    fun closeVerificationFlow()
    fun showInputNumberView(inProgress: Boolean)
    fun showInputNameView(inProgress: Boolean)
    fun showInputOtpView(inProgress: Boolean)
}