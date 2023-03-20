package com.truecaller.android.sdksample

import com.truecaller.android.sdk.legacy.TruecallerSdkScope

data class Scope(
    val truecallerSdkScope: TruecallerSdkScope,
    val locale: String?,
    val themeOptions: Int
)