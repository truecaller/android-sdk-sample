package com.truecaller.android.sdksample.callback

import com.truecaller.android.sdk.TrueException

interface CallbackListener : BaseListener {
    fun initiatedMissedCall()
    fun initiatedOtp()
    fun receivedMissedCall()
    fun receivedOtp(otp: String?)
    fun verifiedBefore(name: String?)
    fun verificationComplete()
    fun requestFailed(e: TrueException)
}