package com.truecaller.android.sdksample

import android.content.Context
import androidx.fragment.app.Fragment
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
}