package com.truecaller.android.sdksample

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_signed_in_successful.successImage
import kotlinx.android.synthetic.main.activity_signed_in_successful.successText
import kotlinx.android.synthetic.main.activity_signed_in_successful.welcomeText

class SignedInSuccessfulActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signed_in_successful)
        val rootLayout = findViewById<LinearLayout>(R.id.rootLayout)
        val flowType = intent.getIntExtra("flow", 1)
        val name = intent.getStringExtra("name")
        name?.let { welcomeText.text = String.format("Welcome, %s!", it) }
        when (flowType) {
            FLOW1 -> rootLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.flow1))
            FLOW2 -> rootLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.flow2))
            FLOW3, FLOW4 -> {
                rootLayout.background = ContextCompat.getDrawable(this, R.drawable.success_bg_layer)
                welcomeText.setTextColor(ContextCompat.getColor(this, android.R.color.black))
                successText.setTextColor(ContextCompat.getColor(this, android.R.color.black))
                successImage.visibility = View.INVISIBLE
            }
        }
    }
}
