package com.truecaller.android.sdksample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView

class Flow1Fragment : BaseFragment() {

    private lateinit var customDialog: CustomDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_flow1, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        customDialog = CustomDialog(requireContext())
        customDialog.findViewById<ImageView>(R.id.close_layout).setOnClickListener {
            customDialog.dismiss()
            fragmentListener.closeFlow()
        }
        customDialog.findViewById<Button>(R.id.btnProceed).setOnClickListener {
            val phoneNumber = customDialog.findViewById<EditText>(R.id.editPhone).text.toString()
            fragmentListener.initVerification(phoneNumber)
        }
    }

    fun showDialogProgress(inProgress : Boolean) {
        customDialog.showInputNumberView(inProgress)
    }
}
