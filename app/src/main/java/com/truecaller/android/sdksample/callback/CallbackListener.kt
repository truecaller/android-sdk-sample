package com.truecaller.android.sdksample.callback

interface CallbackListener : BaseListener {
    fun initiatedMissedCall()
    fun initiatedOtp()
    fun receivedMissedCall()
    fun receivedOtp(otp: String?)
    fun verifiedBefore(name: String?)
    fun verificationComplete()
    fun requestFailed()
}