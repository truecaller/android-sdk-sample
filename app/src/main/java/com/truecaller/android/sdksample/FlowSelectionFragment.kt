package com.truecaller.android.sdksample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_flow_selection.flow1
import kotlinx.android.synthetic.main.fragment_flow_selection.flow2
import kotlinx.android.synthetic.main.fragment_flow_selection.flow3
import kotlinx.android.synthetic.main.fragment_flow_selection.flow4
import kotlinx.android.synthetic.main.fragment_flow_selection.flow5

class FlowSelectionFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_flow_selection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        flow1.setOnClickListener { startFlow(FLOW1) }
        flow2.setOnClickListener { startFlow(FLOW2) }
        flow3.setOnClickListener { startFlow(FLOW3) }
        flow4.setOnClickListener { startFlow(FLOW4) }
        flow5.setOnClickListener { startFlow(FLOW5) }
    }

    private fun startFlow(flowType: Int) {
        fragmentListener.startFlow(flowType)
    }
}
