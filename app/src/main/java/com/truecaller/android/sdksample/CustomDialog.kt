package com.truecaller.android.sdksample

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.DisplayMetrics
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.LinearLayout

class CustomDialog(context: Context) : Dialog(context) {

    var proceedButton: Button
    private var progressBar: LinearLayout

    init {
        window?.requestFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setContentView(R.layout.form_dialog)
        val displayMetrics = DisplayMetrics()
        window?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        window?.setLayout(displayMetrics.widthPixels * 3 / 4, displayMetrics.heightPixels * 1 / 2)
        progressBar = findViewById(R.id.progress_bar)
        proceedButton = findViewById(R.id.btnProceed)
        proceedButton.tag = PHONE_LAYOUT
        setCanceledOnTouchOutside(false)
    }

    fun showInputNumberView(inProgress: Boolean) {
        progressBar.visibility = if (inProgress) View.VISIBLE else View.GONE
        findViewById<LinearLayout>(R.id.phone_layout).visibility = if (inProgress) View.GONE else View.VISIBLE
        proceedButton.visibility = if (inProgress) View.GONE else View.VISIBLE
        proceedButton.tag = PHONE_LAYOUT

        findViewById<LinearLayout>(R.id.name_layout).visibility = View.GONE
        findViewById<LinearLayout>(R.id.otp_layout).visibility = View.GONE
    }

    fun showInputNameView(inProgress: Boolean) {
        progressBar.visibility = if (inProgress) View.VISIBLE else View.GONE
        findViewById<LinearLayout>(R.id.name_layout).visibility = if (inProgress) View.GONE else View.VISIBLE
        proceedButton.visibility = if (inProgress) View.GONE else View.VISIBLE
        proceedButton.tag = NAME_LAYOUT

        findViewById<LinearLayout>(R.id.phone_layout).visibility = View.GONE
        findViewById<LinearLayout>(R.id.otp_layout).visibility = View.GONE
    }

    fun showInputOtpView(inProgress: Boolean) {
        progressBar.visibility = if (inProgress) View.VISIBLE else View.GONE
        findViewById<LinearLayout>(R.id.otp_layout).visibility = if (inProgress) View.GONE else View.VISIBLE
        proceedButton.visibility = if (inProgress) View.GONE else View.VISIBLE
        proceedButton.tag = OTP_LAYOUT

        findViewById<LinearLayout>(R.id.name_layout).visibility = View.GONE
        findViewById<LinearLayout>(R.id.phone_layout).visibility = View.GONE
    }
}