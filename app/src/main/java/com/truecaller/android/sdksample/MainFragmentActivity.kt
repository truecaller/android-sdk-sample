package com.truecaller.android.sdksample

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.truecaller.android.sdk.TruecallerSDK
import com.truecaller.android.sdksample.callback.FragmentListener
import org.shadow.apache.commons.lang3.StringUtils
import java.util.Locale

const val FLOW1 = 1
const val FLOW2 = 2
const val FLOW3 = 3
const val FLOW4 = 4

class MainFragmentActivity : AppCompatActivity(), FragmentListener {

    private var flowType: Int = 0
    private lateinit var scope: Scope

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_fragment)
        addFragment(OptionsCustomizationFragment())
    }

    private fun addFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment, fragment.tag)
            .addToBackStack(null)
            .commit()
    }

    override fun getContext(): Context {
        return this
    }

    override fun getProfile(flowType: Int) {
        this.flowType = flowType
        initTruecallerSdk()
        TruecallerSDK.getInstance().getUserProfile(this)
    }

    override fun setScope(scope: Scope) {
        this.scope = scope
    }

    override fun loadFlowSelectionFragment() {
        addFragment(FlowSelectionFragment())
    }

    override fun onVerificationRequired() {
        when (flowType) {
            1 -> addFragment(Flow1Fragment())
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        TruecallerSDK.getInstance().onActivityResultObtained(this, resultCode, data)
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

    override fun closeFlow() {
        supportFragmentManager.popBackStack()
    }

    private fun getCurrentFragment(): Fragment? = supportFragmentManager.findFragmentById(R.id.fragment_container)

    override fun initVerification(phoneNumber: String) {
        if (phoneNumber.isBlank() || phoneNumber.length != 10) {
            Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Yo!", Toast.LENGTH_SHORT).show()
            getCurrentFragment()?.let {
                if (it is Flow1Fragment) {
                    it.showDialogProgress(true)
                }
            }
        }
    }
}
