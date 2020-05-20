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
import android.widget.ProgressBar

class CustomDialog(context: Context) : Dialog(context) {

    init {
        window?.requestFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setContentView(R.layout.form_dialog)
        val displayMetrics = DisplayMetrics()
        window?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        window?.setLayout(displayMetrics.widthPixels * 3 / 4, displayMetrics.heightPixels * 3 / 4)
        show()
    }

    fun showInputNumberView(inProgress : Boolean) {
        findViewById<ProgressBar>(R.id.progress_bar).visibility = if (inProgress) View.VISIBLE else View.GONE
        findViewById<LinearLayout>(R.id.phone_layout).visibility = if (inProgress) View.GONE else View.VISIBLE
        findViewById<Button>(R.id.btnProceed).visibility = if (inProgress) View.GONE else View.VISIBLE
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