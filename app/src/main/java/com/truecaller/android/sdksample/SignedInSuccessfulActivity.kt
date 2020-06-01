package com.truecaller.android.sdksample

import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class SignedInSuccessfulActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signed_in_successful)
        val rootLayout = findViewById<LinearLayout>(R.id.rootLayout)
        val flowType = intent.getIntExtra("flow", 1)
        when (flowType) {
            FLOW1 -> rootLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.flow1))
            FLOW2 -> rootLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.flow2))
        }
    }
}
