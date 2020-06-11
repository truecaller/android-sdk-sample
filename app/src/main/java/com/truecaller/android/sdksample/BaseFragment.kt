package com.truecaller.android.sdksample

import android.content.Context
import android.graphics.drawable.AnimatedVectorDrawable
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.truecaller.android.sdksample.callback.FragmentListener
import com.truecaller.android.sdksample.callback.TruecallerUserCallback

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

    fun animateView(view: AppCompatImageView, inProgress: Boolean) {
        when (val drawable = view.drawable) {
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
}