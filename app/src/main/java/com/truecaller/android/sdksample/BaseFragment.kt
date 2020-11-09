package com.truecaller.android.sdksample

import android.content.Context
import android.graphics.Paint
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.truecaller.android.sdksample.callback.FragmentListener
import com.truecaller.android.sdksample.callback.TruecallerUserCallback
import kotlinx.android.synthetic.main.progress_layout.callMessage
import kotlinx.android.synthetic.main.progress_layout.loaderImageView

open class BaseFragment : Fragment() {

    lateinit var fragmentListener: FragmentListener
    var truecallerUserCallback: TruecallerUserCallback? = null
    private var timer: CountDownTimer? = null
    private var timerTextView: AppCompatTextView? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentListener = context as MainFragmentActivity
        truecallerUserCallback = TruecallerUserCallback(fragmentListener)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        timerTextView = if (this is Flow1Fragment) {
            this.customDialog?.findViewById(R.id.timerText)
        } else view.findViewById(R.id.timerText)
    }

    fun String.getFirstName(): String {
        return split(" ").first()
    }

    fun String.getLastName(): String {
        val splitName = split(" ")
        return if (splitName.size > 1) splitName.last() else ""
    }

    fun animateView(view: AppCompatImageView? = null, inProgress: Boolean) {
        val imageView = view ?: loaderImageView
        when (val drawable = imageView.drawable) {
            is AnimatedVectorDrawableCompat -> {
                if (inProgress) drawable.start()
                else drawable.stop()
            }
            is AnimatedVectorDrawable -> {
                if (inProgress) drawable.start()
                else drawable.stop()
            }
        }
    }

    fun showCallingMessage(callingMsgView: AppCompatTextView? = null, ttl: Double? = null, timerTextViewOther: AppCompatTextView? = null) {
        val textView = callingMsgView ?: callMessage
        textView.visibility = View.VISIBLE
        //if coming via missed call flow
        ttl?.let { showCountDownTimer(it, timerTextViewOther ?: view?.findViewById(R.id.timerTextProgress)) }
    }

    fun showCountDownTimer(ttl: Double, timerTextViewOther: AppCompatTextView? = null) {
        val textView = timerTextViewOther ?: timerTextView
        textView?.setOnClickListener(null)
        timer = object : CountDownTimer(ttl.toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                textView?.let {
                    it.paintFlags = it.paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv()
                    it.text = String.format(getString(R.string.retry_timer), millisUntilFinished / 1000)
                }
                //if coming via missed call flow, textview are different, hence set the timer to the other textview as well
                if (textView?.id != timerTextView?.id) {
                    timerTextView?.let {
                        it.paintFlags = it.paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv()
                        it.text = String.format(getString(R.string.retry_timer), millisUntilFinished / 1000)
                    }
                }
            }

            override fun onFinish() {
                textView?.let {
                    it.paintFlags = it.paintFlags or Paint.UNDERLINE_TEXT_FLAG
                    it.text = getString(R.string.retry_now)
                    it.setOnClickListener {
                        if (this@BaseFragment is FragmentPresenter) {
                            this@BaseFragment.showInputNumberView(false)
                        }
                    }
                }
                //if coming via missed call flow, textview are different, hence finish the timer of the other textview as well
                if (textView?.id != timerTextView?.id) {
                    timerTextView?.let {
                        it.paintFlags = it.paintFlags or Paint.UNDERLINE_TEXT_FLAG
                        it.text = getString(R.string.retry_now)
                        it.setOnClickListener {
                            if (this@BaseFragment is FragmentPresenter) {
                                this@BaseFragment.showInputNumberView(false)
                            }
                        }
                    }
                }
            }
        }
        textView?.visibility = View.VISIBLE
        timer?.start()
    }

    fun dismissCountDownTimer(timerTextViewOther: AppCompatTextView? = null) {
        timer?.cancel()
        timer = null
        hideCountDownTimerText(timerTextViewOther)
    }

    private fun hideCountDownTimerText(timerTextViewOther: AppCompatTextView? = null) {
        val textView = timerTextViewOther ?: timerTextView
        textView?.visibility = View.GONE
    }

    fun showCountDownTimerText(timerTextViewOther: AppCompatTextView? = null) {
        val textView = timerTextViewOther ?: timerTextView
        textView?.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timerTextView = null
    }

    override fun onDetach() {
        super.onDetach()
        truecallerUserCallback = null
        dismissCountDownTimer()
    }
}