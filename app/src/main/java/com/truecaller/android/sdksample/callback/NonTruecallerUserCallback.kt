package com.truecaller.android.sdksample.callback

import android.widget.Toast
import com.truecaller.android.sdk.TrueException
import com.truecaller.android.sdk.clients.VerificationCallback
import com.truecaller.android.sdk.clients.VerificationDataBundle

class NonTruecallerUserCallback(private val callbackListener: CallbackListener) : VerificationCallback {
    override fun onRequestSuccess(requestCode: Int, bundle: VerificationDataBundle?) {
        when (requestCode) {
            VerificationCallback.TYPE_MISSED_CALL_INITIATED -> {
                Toast.makeText(
                    callbackListener.getContext(),
                    "Missed call initiated",
                    Toast.LENGTH_SHORT
                ).show()
                callbackListener.initiatedMissedCall()
            }
            VerificationCallback.TYPE_MISSED_CALL_RECEIVED -> {
                Toast.makeText(
                    callbackListener.getContext(),
                    "Missed call received",
                    Toast.LENGTH_SHORT
                ).show()
                callbackListener.receivedMissedCall()
            }
            VerificationCallback.TYPE_OTP_INITIATED -> {
                Toast.makeText(
                    callbackListener.getContext(),
                    "OTP initiated",
                    Toast.LENGTH_SHORT
                ).show()
                callbackListener.initiatedOtp()
            }
            VerificationCallback.TYPE_OTP_RECEIVED -> {
                Toast.makeText(
                    callbackListener.getContext(),
                    "OTP received",
                    Toast.LENGTH_SHORT
                ).show()
                callbackListener.receivedOtp(bundle?.getString(VerificationDataBundle.KEY_OTP))
            }
            VerificationCallback.TYPE_PROFILE_VERIFIED_BEFORE -> {
                Toast.makeText(
                    callbackListener.getContext(),
                    "Profile verified for your app before: " + bundle?.profile?.firstName
                            + " and access token: " + bundle?.profile?.accessToken,
                    Toast.LENGTH_SHORT
                ).show()
                callbackListener.verifiedBefore()
            }
            else -> {
                Toast.makeText(
                    callbackListener.getContext(),
                    "Success: Verified with" + " with " + bundle!!.getString(VerificationDataBundle.KEY_ACCESS_TOKEN),
                    Toast.LENGTH_SHORT
                ).show()
                callbackListener.verificationComplete()
            }
        }
    }

    override fun onRequestFailure(requestCode: Int, e: TrueException) {
        Toast.makeText(
            callbackListener.getContext(),
            "OnFailureApiCallback: " + e.exceptionType + "\n" + e.exceptionMessage,
            Toast.LENGTH_SHORT
        )
            .show()
        callbackListener.requestFailed()
    }
}
