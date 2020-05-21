package com.truecaller.android.sdksample.callback

interface CallbackListener : BaseListener {
    fun receivedMissedCall()
    fun receivedOtp(otp: String?)
    fun verifiedBefore()
    fun verificationComplete()
}