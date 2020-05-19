package com.truecaller.android.sdksample

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.DisplayMetrics
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.ProgressBar

class CustomDialog(context: Context) : Dialog(context) {

    init {
        window?.requestFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setContentView(R.layout.form_dialog)
        val displayMetrics = DisplayMetrics()
        window?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        window?.setLayout(displayMetrics.widthPixels * 3/4, displayMetrics.heightPixels * 3/4)
        val closeLayoutView = findViewById<ImageView>(R.id.close_layout)
        closeLayoutView.visibility = View.VISIBLE
        closeLayoutView.setOnClickListener { dismiss() }
        val progressBar = findViewById<ProgressBar>(R.id.progress_bar)
        show()
    }

     /*fun showDialogView() {
        val constraintLayout = findViewById<ConstraintLayout>(R.id.constraint_layout)
        val constrainSet = ConstraintSet()
        constrainSet.clone(constraintLayout)
        constrainSet.setDimensionRatio(constraintLayout.getChildAt(0).id, "2:3")
        constrainSet.applyTo(constraintLayout)
        show()
    }*/
}