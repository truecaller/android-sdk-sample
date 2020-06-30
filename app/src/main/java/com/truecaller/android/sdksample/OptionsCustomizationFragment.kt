package com.truecaller.android.sdksample

import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.RadioGroup
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.truecaller.android.sdk.SdkThemeOptions
import com.truecaller.android.sdk.TruecallerSdkScope
import kotlinx.android.synthetic.main.optionslayout.bottomsheet
import kotlinx.android.synthetic.main.optionslayout.buttonGo
import kotlinx.android.synthetic.main.optionslayout.editTextPp
import kotlinx.android.synthetic.main.optionslayout.editTextTnC
import kotlinx.android.synthetic.main.optionslayout.fullscreen
import kotlinx.android.synthetic.main.optionslayout.localeEt
import kotlinx.android.synthetic.main.optionslayout.sdkOptions
import kotlinx.android.synthetic.main.optionslayout.shapeOptions
import kotlinx.android.synthetic.main.optionslayout.themeOptions

class OptionsCustomizationFragment : BaseFragment() {

    private lateinit var titleSelector: RadioGroup
    private lateinit var additionalFooterSelector: RadioGroup
    private lateinit var colorSpinner: Spinner
    private lateinit var colorTextSpinner: Spinner
    private lateinit var ctaPrefixSpinner: Spinner
    private lateinit var prefixSpinner: Spinner
    private lateinit var suffixSpinner: Spinner
    private lateinit var scope: Scope

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_options_customization, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bottomsheet.isChecked = true
        buttonGo.setOnClickListener {
            initTruecallerSdkScope()
            fragmentListener.setScope(scope)
            fragmentListener.loadFlowSelectionFragment()
        }
        titleSelector = view.findViewById(R.id.sdkTitleOptions)
        additionalFooterSelector = view.findViewById(R.id.additionalFooters)

        colorSpinner = view.findViewById(R.id.color_spinner)
        colorTextSpinner = view.findViewById(R.id.color_text_spinner)
        ctaPrefixSpinner = view.findViewById(R.id.cta_prefix_spinner)
        prefixSpinner = view.findViewById(R.id.prefix_spinner)
        suffixSpinner = view.findViewById(R.id.suffix_spinner)

        setSpinnerAdapters()
    }

    private fun setSpinnerAdapters() {
        val adapterP = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.SdkPartnerLoginPrefixOptionsArray,
            android.R.layout.simple_spinner_item
        )
        adapterP.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        prefixSpinner.adapter = adapterP

        val adapterS = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.SdkPartnerLoginSuffixOptionsArray,
            android.R.layout.simple_spinner_item
        )
        adapterS.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        suffixSpinner.adapter = adapterS

        val adapterCP = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.SdkPartnerCTAPrefixOptionsArray,
            android.R.layout.simple_spinner_item
        )
        adapterCP.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        ctaPrefixSpinner.adapter = adapterCP

        val adapterColor = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.SdkPartnerSampleColors,
            android.R.layout.simple_spinner_item
        )
        adapterColor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        colorSpinner.adapter = adapterColor
        colorTextSpinner.adapter = adapterColor

        prefixSpinner.setSelection(0)
        suffixSpinner.setSelection(0)
        ctaPrefixSpinner.setSelection(0)
        colorSpinner.setSelection(2)
        colorTextSpinner.setSelection(1)
    }

    fun initTruecallerSdkScope() {
        val truecallerSdkScope = TruecallerSdkScope.Builder(requireContext(), truecallerUserCallback)
            .consentMode(
                when {
                    fullscreen.isChecked -> TruecallerSdkScope.CONSENT_MODE_FULLSCREEN
                    bottomsheet.isChecked -> TruecallerSdkScope.CONSENT_MODE_BOTTOMSHEET
                    else -> TruecallerSdkScope.CONSENT_MODE_POPUP
                }
            )
            .buttonColor(Color.parseColor(colorSpinner.selectedItem.toString())) //default TC blue
            .buttonTextColor(Color.parseColor(colorTextSpinner.selectedItem.toString())) //default white
            .loginTextPrefix(prefixSpinner.selectedItemPosition) //default 0
            .loginTextSuffix(suffixSpinner.selectedItemPosition) //default 0
            .ctaTextPrefix(ctaPrefixSpinner.selectedItemPosition) //default 0
            .buttonShapeOptions(
                when {
                    shapeOptions.isChecked -> TruecallerSdkScope.BUTTON_SHAPE_RECTANGLE
                    else -> TruecallerSdkScope.BUTTON_SHAPE_ROUNDED
                }
            ) //default ROUNDED
            .privacyPolicyUrl(editTextPp.text.toString()) //default NULL
            .termsOfServiceUrl(editTextTnC.text.toString()) //default NULL
            .footerType(
                when (additionalFooterSelector.checkedRadioButtonId) {
                    ListView.INVALID_POSITION -> TruecallerSdkScope.FOOTER_TYPE_NONE
                    else -> resolveAdditionalFooter(
                        additionalFooterSelector.checkedRadioButtonId
                    )
                }
            )
            .consentTitleOption(
                when (titleSelector.checkedRadioButtonId) {
                    ListView.INVALID_POSITION -> TruecallerSdkScope.SDK_CONSENT_TITLE_LOG_IN
                    else -> resolveSelectedPosition(
                        titleSelector.checkedRadioButtonId
                    )
                }
            )
            .sdkOptions(
                when {
                    sdkOptions.isChecked -> TruecallerSdkScope.SDK_OPTION_WITH_OTP
                    else -> TruecallerSdkScope.SDK_OPTION_WITHOUT_OTP
                }
            )
            .build()

        var locale: String? = null
        if (!TextUtils.isEmpty(localeEt.text)) {
            locale = localeEt.text.toString()
        }
        val sdkThemeOptions = when {
            themeOptions.isChecked -> SdkThemeOptions.DARK
            else -> SdkThemeOptions.LIGHT
        }
        scope = Scope(truecallerSdkScope, locale, sdkThemeOptions)
    }

    private fun resolveAdditionalFooter(checkedRadioButtonId: Int): Int {
        return when (checkedRadioButtonId) {
            R.id.skip -> TruecallerSdkScope.FOOTER_TYPE_SKIP
            R.id.uan -> TruecallerSdkScope.FOOTER_TYPE_CONTINUE
            R.id.uam -> TruecallerSdkScope.FOOTER_TYPE_ANOTHER_METHOD
            R.id.edm -> TruecallerSdkScope.FOOTER_TYPE_MANUALLY
            R.id.idl -> TruecallerSdkScope.FOOTER_TYPE_LATER
            else -> TruecallerSdkScope.FOOTER_TYPE_NONE
        }
    }

    private fun resolveSelectedPosition(checkedRadioButtonId: Int): Int {
        var pos = 0
        when (checkedRadioButtonId) {
            R.id.zero -> pos = 0
            R.id.one -> pos = 1
            R.id.two -> pos = 2
            R.id.three -> pos = 3
            R.id.four -> pos = 4
            R.id.five -> pos = 5
        }
        return pos
    }
}
