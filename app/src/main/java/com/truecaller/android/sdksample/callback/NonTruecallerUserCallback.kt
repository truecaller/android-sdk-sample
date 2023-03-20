package com.truecaller.android.sdksample.callback

import android.widget.Toast
import com.truecaller.android.sdk.common.TrueException
import com.truecaller.android.sdk.common.VerificationCallback
import com.truecaller.android.sdk.common.VerificationDataBundle

class NonTruecallerUserCallback(private val callbackListener: CallbackListener) : VerificationCallback {
    override fun onRequestSuccess(requestCode: Int, bundle: VerificationDataBundle?) {
        when (requestCode) {
            VerificationCallback.TYPE_MISSED_CALL_INITIATED -> {
                Toast.makeText(
                    callbackListener.getContext().get(),
                    "Missed call initiated with TTL : " + bundle?.getString(VerificationDataBundle.KEY_TTL),
                    Toast.LENGTH_SHORT
                ).show()
                Toast.makeText(
                    callbackListener.getContext().get(),
                    "Req Nonce : " + bundle?.getString(VerificationDataBundle.KEY_REQUEST_NONCE),
                    Toast.LENGTH_SHORT
                ).show()
                callbackListener.initiatedMissedCall(bundle?.getString(VerificationDataBundle.KEY_TTL))
            }
            VerificationCallback.TYPE_MISSED_CALL_RECEIVED -> {
                Toast.makeText(
                    callbackListener.getContext().get(),
                    "Missed call received",
                    Toast.LENGTH_SHORT
                ).show()
                callbackListener.receivedMissedCall()
            }
            VerificationCallback.TYPE_OTP_INITIATED -> {
                Toast.makeText(
                    callbackListener.getContext().get(),
                    "OTP initiated with TTL : " + bundle?.getString(VerificationDataBundle.KEY_TTL),
                    Toast.LENGTH_SHORT
                ).show()
                Toast.makeText(
                    callbackListener.getContext().get(),
                    "Req Nonce : " + bundle?.getString(VerificationDataBundle.KEY_REQUEST_NONCE),
                    Toast.LENGTH_SHORT
                ).show()
                callbackListener.initiatedOtp(bundle?.getString(VerificationDataBundle.KEY_TTL))
            }
            VerificationCallback.TYPE_OTP_RECEIVED -> {
                Toast.makeText(
                    callbackListener.getContext().get(),
                    "OTP received",
                    Toast.LENGTH_SHORT
                ).show()
                callbackListener.receivedOtp(bundle?.getString(VerificationDataBundle.KEY_OTP))
            }
            VerificationCallback.TYPE_PROFILE_VERIFIED_BEFORE -> {
                Toast.makeText(
                    callbackListener.getContext().get(),
                    "Profile verified for your app before: " + bundle?.profile?.firstName
                            + " and access token: " + bundle?.profile?.accessToken,
                    Toast.LENGTH_SHORT
                ).show()
                Toast.makeText(
                    callbackListener.getContext().get(),
                    "Req nonce: " + bundle?.profile?.requestNonce,
                    Toast.LENGTH_SHORT
                ).show()
                callbackListener.verifiedBefore(bundle?.profile?.firstName)
            }
            else -> {
                Toast.makeText(
                    callbackListener.getContext().get(),
                    "Success: Verified with " + bundle!!.getString(VerificationDataBundle.KEY_ACCESS_TOKEN),
                    Toast.LENGTH_SHORT
                ).show()
                Toast.makeText(
                    callbackListener.getContext().get(),
                    "Req Nonce : " + bundle.getString(VerificationDataBundle.KEY_REQUEST_NONCE),
                    Toast.LENGTH_SHORT
                ).show()
                callbackListener.verificationComplete()
            }
        }
    }

    override fun onRequestFailure(requestCode: Int, e: TrueException) {
        Toast.makeText(
            callbackListener.getContext().get(),
            "OnFailureApiCallback: " + e.exceptionType + "\n" + e.exceptionMessage,
            Toast.LENGTH_SHORT
        )
            .show()
        callbackListener.requestFailed(e)
    }
}
