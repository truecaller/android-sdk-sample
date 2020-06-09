package com.truecaller.android.sdksample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import com.truecaller.android.sdk.TrueProfile
import kotlinx.android.synthetic.main.flow4_layouts.editName
import kotlinx.android.synthetic.main.flow4_layouts.editOtp
import kotlinx.android.synthetic.main.flow4_layouts.editPhone
import kotlinx.android.synthetic.main.flow4_layouts.header
import kotlinx.android.synthetic.main.flow4_layouts.parentLayout
import kotlinx.android.synthetic.main.flow4_layouts.subHeader
import kotlinx.android.synthetic.main.flow4_layouts.verificationLayout
import kotlinx.android.synthetic.main.fragment_flow4.getStartedBtn
import kotlinx.android.synthetic.main.fragment_flow4.homeLayout

/**
 * A simple [Fragment] subclass.
 */
class Flow4Fragment : BaseFragment() {

    private lateinit var proceedButton: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_flow4, container, false)
        proceedButton = view.findViewById(R.id.btnProceed)
        progressBar = view.findViewById(R.id.progress_bar)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getStartedBtn.setOnClickListener { fragmentListener.getProfile() }
        proceedButton.setOnClickListener {
            when (proceedButton.tag) {
                PHONE_LAYOUT -> {
                    val phoneNumber = view.findViewById<EditText>(R.id.editPhone).text.toString()
                    fragmentListener.initVerification(phoneNumber)
                }
                OTP_LAYOUT -> {
                    val otp = view.findViewById<EditText>(R.id.editOtp).text.toString()
                    fragmentListener.validateOtp(otp)
                }
                NAME_LAYOUT -> {
                    val fullName = view.findViewById<EditText>(R.id.editName).text.toString()
                    val trueProfile = TrueProfile.Builder(fullName.getFirstName(), fullName.getLastName()).build()
                    fragmentListener.verifyUser(trueProfile)
                }
            }
        }
    }

    fun showVerificationFlow() {
        homeLayout.visibility = View.GONE
        verificationLayout.visibility = View.VISIBLE
        showInputNumberView(false)
    }

    fun closeVerificationFlow() {
        homeLayout.visibility = View.VISIBLE
        verificationLayout.visibility = View.GONE
    }

    fun showInputNumberView(inProgress: Boolean) {
        proceedButton.tag = PHONE_LAYOUT
        progressBar.visibility = if (inProgress) View.VISIBLE else View.GONE
        parentLayout.visibility = if (inProgress) View.GONE else View.VISIBLE
        header.visibility = if (inProgress) View.GONE else View.VISIBLE
        proceedButton.visibility = if (inProgress) View.GONE else View.VISIBLE

        editPhone.visibility = View.VISIBLE
        editOtp.visibility = View.GONE
        editName.visibility = View.GONE

        header.text = "Please tell us your\nMobile Number"
        subHeader.text = "Mobile number"
    }

    fun showInputOtpView(inProgress: Boolean) {
        proceedButton.tag = OTP_LAYOUT
        progressBar.visibility = if (inProgress) View.VISIBLE else View.GONE
        parentLayout.visibility = if (inProgress) View.GONE else View.VISIBLE
        header.visibility = if (inProgress) View.GONE else View.VISIBLE
        proceedButton.visibility = if (inProgress) View.GONE else View.VISIBLE

        editOtp.visibility = View.VISIBLE
        editPhone.visibility = View.GONE
        editName.visibility = View.GONE

        header.text = "OTP Verification"
        subHeader.text = "OTP"
    }

    fun showInputNameView(inProgress: Boolean) {
        proceedButton.tag = NAME_LAYOUT
        progressBar.visibility = if (inProgress) View.VISIBLE else View.GONE
        parentLayout.visibility = if (inProgress) View.GONE else View.VISIBLE
        header.visibility = if (inProgress) View.GONE else View.VISIBLE
        proceedButton.visibility = if (inProgress) View.GONE else View.VISIBLE

        editName.visibility = View.VISIBLE
        editPhone.visibility = View.GONE
        editOtp.visibility = View.GONE

        header.text = "We\'d like to know\nmore about you"
        subHeader.text = "Full Name"
    }
}
