package com.truecaller.android.sdksample

import com.truecaller.android.sdk.TruecallerSdkScope

data class Scope(
    val truecallerSdkScope: TruecallerSdkScope,
    val locale: String?,
    val themeOptions: Int
)