package com.truecaller.android.sdksample.callback

import android.widget.Toast
import com.truecaller.android.sdk.ITrueCallback
import com.truecaller.android.sdk.TrueError
import com.truecaller.android.sdk.TrueProfile

class TruecallerUserCallback(private val fragmentListener: FragmentListener) : ITrueCallback {

    override fun onSuccessProfileShared(trueProfile: TrueProfile) {
        Toast.makeText(
            fragmentListener.getContext().get(),
            "Verified Truecaller User: " + trueProfile.firstName
            + "\nand Business profile: " + trueProfile.isBusiness,
            Toast.LENGTH_SHORT
        ).show()
        fragmentListener.success(trueProfile.firstName)
    }

    override fun onFailureProfileShared(trueError: TrueError) {
        Toast.makeText(
            fragmentListener.getContext().get(),
            "onFailureProfileShared: " + trueError.errorType,
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onVerificationRequired(trueError: TrueError?) {
        Toast.makeText(
            fragmentListener.getContext().get(),
            "Verification Required: " + trueError?.errorType,
            Toast.LENGTH_SHORT
        ).show()
        fragmentListener.onVerificationRequired()
    }
}