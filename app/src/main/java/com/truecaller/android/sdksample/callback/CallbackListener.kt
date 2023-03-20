package com.truecaller.android.sdksample.callback

import com.truecaller.android.sdk.common.TrueException

interface CallbackListener : BaseListener {
    fun initiatedMissedCall(ttl: String?)
    fun initiatedOtp(ttl: String?)
    fun receivedMissedCall()
    fun receivedOtp(otp: String?)
    fun verifiedBefore(name: String?)
    fun verificationComplete()
    fun requestFailed(e: TrueException)
}