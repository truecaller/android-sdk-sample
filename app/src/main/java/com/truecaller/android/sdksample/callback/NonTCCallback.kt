package com.truecaller.android.sdksample.callback

/*import android.content.Intent
import android.widget.Toast
import com.truecaller.android.sdk.TrueException
import com.truecaller.android.sdk.TrueProfile
import com.truecaller.android.sdk.TruecallerSDK
import com.truecaller.android.sdk.clients.VerificationCallback
import com.truecaller.android.sdk.clients.VerificationDataBundle
import com.truecaller.android.sdksample.SignedInActivity

private val apiCallback: VerificationCallback = object : VerificationCallback {
    override fun onRequestSuccess(requestCode: Int, bundle: VerificationDataBundle?) {
        when (requestCode) {
            VerificationCallback.TYPE_MISSED_CALL_INITIATED -> {
                Toast.makeText(
                    this@MainFragmentActivity,
                    "Missed call initiated",
                    Toast.LENGTH_SHORT
                ).show()
            }
            VerificationCallback.TYPE_MISSED_CALL_RECEIVED -> {
                Toast.makeText(
                    this@MainFragmentActivity,
                    "Missed call received",
                    Toast.LENGTH_SHORT
                ).show()
                val profile = TrueProfile.Builder("shubh", "").build()
                TruecallerSDK.getInstance().verifyMissedCall(profile, this)
            }
            VerificationCallback.TYPE_OTP_INITIATED -> {
                Toast.makeText(
                    this@MainFragmentActivity,
                    "OTP initiated",
                    Toast.LENGTH_SHORT
                ).show()
            }
            VerificationCallback.TYPE_OTP_RECEIVED -> {
                Toast.makeText(
                    this@MainFragmentActivity,
                    "OTP received",
                    Toast.LENGTH_SHORT
                ).show()
                val profile = TrueProfile.Builder("shubh", "").build()
                val otp = bundle!!.getString(VerificationDataBundle.KEY_OTP)
                TruecallerSDK.getInstance().verifyOtp(profile, otp!!, this)
            }
            VerificationCallback.TYPE_PROFILE_VERIFIED_BEFORE -> {
                Toast.makeText(
                    this@MainFragmentActivity,
                    "Profile verified for your app before: " + bundle!!.profile!!.firstName
                            + " and access token: " + bundle.profile!!.accessToken,
                    Toast.LENGTH_SHORT
                ).show()
                startActivity(Intent(this@MainFragmentActivity, SignedInActivity::class.java))
            }
            else -> {
                Toast.makeText(
                    this@MainFragmentActivity,
                    "Success: Verified with" + " with " + bundle!!.getString(VerificationDataBundle.KEY_ACCESS_TOKEN),
                    Toast.LENGTH_SHORT
                ).show()
                startActivity(Intent(this@MainFragmentActivity, SignedInActivity::class.java))
            }
        }
    }

    override fun onRequestFailure(requestCode: Int, e: TrueException) {
        Toast.makeText(
            this@MainFragmentActivity,
            "OnFailureApiCallback: " + e.exceptionType + "\n" + e.exceptionMessage,
            Toast.LENGTH_SHORT
        )
            .show()
    }
}*/
