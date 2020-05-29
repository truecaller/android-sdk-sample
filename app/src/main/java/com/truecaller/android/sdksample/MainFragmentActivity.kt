package com.truecaller.android.sdksample

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.truecaller.android.sdk.TrueProfile
import com.truecaller.android.sdk.TruecallerSDK
import com.truecaller.android.sdk.clients.VerificationCallback
import com.truecaller.android.sdksample.callback.CallbackListener
import com.truecaller.android.sdksample.callback.FragmentListener
import com.truecaller.android.sdksample.callback.NonTruecallerUserCallback
import org.shadow.apache.commons.lang3.StringUtils
import java.util.Locale

const val FLOW1 = 1
const val FLOW2 = 2
const val FLOW3 = 3
const val FLOW4 = 4

const val REQUEST_CODE = 1291

class MainFragmentActivity : AppCompatActivity(), FragmentListener, CallbackListener {

    //    private var flowType: Int = 0
    private lateinit var scope: Scope
    private var nonTruecallerUserCallback = NonTruecallerUserCallback(this)
    private var verificationCallbackType = 0
    private var otp: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_fragment)
        addFragment(OptionsCustomizationFragment(), false)
    }

    private fun addFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment, fragment.tag)
        if (addToBackStack) {
            fragmentTransaction.addToBackStack(null).commit()
        } else {
            fragmentTransaction.commit()
        }
    }

    override fun getContext(): Context {
        return this
    }

    override fun startFlow(flowType: Int) {
        //        this.flowType = flowType
        when (flowType) {
            1 -> addFragment(Flow1Fragment())
        }
    }

    override fun setScope(scope: Scope) {
        this.scope = scope
    }

    override fun getProfile() {
        initTruecallerSdk()
        if (TruecallerSDK.getInstance().isUsable) {
            TruecallerSDK.getInstance().getUserProfile(this)
        } else {
            Toast.makeText(this, "No compatible client available", Toast.LENGTH_SHORT).show()
        }
    }

    override fun loadFlowSelectionFragment() {
        addFragment(FlowSelectionFragment())
    }

    override fun onVerificationRequired() {
        getCurrentFragment()?.let {
            when (it) {
                is Flow1Fragment -> {
                    it.showDialog()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            recreate()
        } else {
            TruecallerSDK.getInstance().onActivityResultObtained(this, resultCode, data)
        }
    }

    private fun initTruecallerSdk() {
        TruecallerSDK.init(scope.truecallerSdkScope)
        if (TruecallerSDK.getInstance().isUsable) {
            TruecallerSDK.getInstance().setTheme(scope.themeOptions)
        }

        if (!StringUtils.isEmpty(scope.locale)) {
            TruecallerSDK.getInstance().setLocale(Locale(scope.locale))
        }
    }

    /*override fun closeFlow() {
        supportFragmentManager.popBackStack()
    }*/

    private fun getCurrentFragment(): Fragment? = supportFragmentManager.findFragmentById(R.id.fragment_container)

    override fun initVerification(phoneNumber: String) {
        if (phoneNumber.isBlank() || phoneNumber.length != 10) {
            Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show()
        } else {
            getCurrentFragment()?.let {
                when (it) {
                    is Flow1Fragment -> {
                        TruecallerSDK.getInstance().requestVerification("IN", phoneNumber, nonTruecallerUserCallback, this)
                        it.showInputNumberView(true)
                    }
                }
            }
        }
    }

    override fun validateOtp(otp: String) {
        if (otp.isBlank() || otp.length != 6) {
            Toast.makeText(this, "Please enter a valid otp", Toast.LENGTH_SHORT).show()
        } else {
            this.otp = otp
            getCurrentFragment()?.let {
                when (it) {
                    is Flow1Fragment -> {
                        it.showInputNameView(false)
                    }
                }
            }
        }
    }

    override fun verifyUser(trueProfile: TrueProfile) {
        if (trueProfile.firstName.isNullOrBlank()) {
            Toast.makeText(this, "Please enter a valid first name", Toast.LENGTH_SHORT).show()
            return
        }
        when (verificationCallbackType) {
            VerificationCallback.TYPE_MISSED_CALL_RECEIVED -> TruecallerSDK.getInstance().verifyMissedCall(trueProfile, nonTruecallerUserCallback)
            VerificationCallback.TYPE_OTP_RECEIVED ->
                otp?.let {
                    TruecallerSDK.getInstance().verifyOtp(trueProfile, it, nonTruecallerUserCallback)
                }
        }
    }

    override fun receivedMissedCall() {
        verificationCallbackType = VerificationCallback.TYPE_MISSED_CALL_RECEIVED
        getCurrentFragment()?.let {
            when (it) {
                is Flow1Fragment -> {
                    it.showInputNameView(false)
                }
            }
        }
    }

    override fun receivedOtp(otp: String?) {
        verificationCallbackType = VerificationCallback.TYPE_OTP_RECEIVED
        getCurrentFragment()?.let {
            when (it) {
                is Flow1Fragment -> it.showInputOtpView(false)
            }
        }
    }

    override fun success() {
        startActivityForResult(Intent(this, SignedInSuccessfulActivity::class.java), REQUEST_CODE)
    }

    override fun verifiedBefore() {
        getCurrentFragment()?.let {
            when (it) {
                is Flow1Fragment -> {
                    it.closeDialog()
                }
            }
        }
        resetValues()
        success()
    }

    override fun verificationComplete() {
        getCurrentFragment()?.let {
            when (it) {
                is Flow1Fragment -> {
                    it.closeDialog()
                }
            }
        }
        resetValues()
        success()
    }

    override fun requestFailed() {
        getCurrentFragment()?.let {
            when (it) {
                is Flow1Fragment -> {
                    when (verificationCallbackType) {
                        VerificationCallback.TYPE_MISSED_CALL_RECEIVED -> {
                            it.showInputNameView(false)
                        }
                        VerificationCallback.TYPE_OTP_RECEIVED -> {
                            it.showInputOtpView(false)
                        }
                        else -> {
                            it.showInputNumberView(false)
                        }
                    }
                }
            }
        }
    }

    private fun resetValues() {
        verificationCallbackType = 0
        otp = ""
    }
}
