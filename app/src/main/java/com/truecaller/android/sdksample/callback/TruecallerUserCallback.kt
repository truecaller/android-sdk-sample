package com.truecaller.android.sdksample.callback

import android.widget.Toast
import com.truecaller.android.sdk.ITrueCallback
import com.truecaller.android.sdk.TrueError
import com.truecaller.android.sdk.TrueProfile

class TruecallerUserCallback(private val fragmentListener: FragmentListener) : ITrueCallback {

    override fun onSuccessProfileShared(trueProfile: TrueProfile) {
        Toast.makeText(
            fragmentListener.getContext(),
            "Verified Truecaller User: " + trueProfile.firstName,
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onFailureProfileShared(trueError: TrueError) {
        Toast.makeText(
            fragmentListener.getContext(),
            "onFailureProfileShared: " + trueError.errorType,
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onVerificationRequired() {
        Toast.makeText(
            fragmentListener.getContext(),
            "Verification Required",
            Toast.LENGTH_SHORT
        ).show()
        fragmentListener.onVerificationRequired()
    }
}