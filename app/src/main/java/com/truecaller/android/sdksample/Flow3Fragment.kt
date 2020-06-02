package com.truecaller.android.sdksample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.flow_home_page.getStartedBtn

/**
 * A simple [Fragment] subclass.
 */
class Flow3Fragment : BaseFragment() {

//    private lateinit var proceedButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_flow3, container, false)
//        proceedButton = view.findViewById(R.id.btnProceed)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getStartedBtn.setOnClickListener { fragmentListener.getProfile() }
        /*proceedButton.setOnClickListener {
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
                    val firstName = view.findViewById<EditText>(R.id.editFirstName).text.toString()
                    val trueProfile = TrueProfile.Builder(firstName, "").build()
                    fragmentListener.verifyUser(trueProfile)
                }
            }
        }*/
    }
}
