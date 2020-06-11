package com.truecaller.android.sdksample

import android.content.Context
import android.graphics.drawable.AnimatedVectorDrawable
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
    lateinit var truecallerUserCallback: TruecallerUserCallback

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentListener = context as MainFragmentActivity
        truecallerUserCallback = TruecallerUserCallback(fragmentListener)
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

    fun showCallingMessage(view: AppCompatTextView? = null) {
        val textView = view ?: callMessage
        textView.visibility = View.VISIBLE
    }
}