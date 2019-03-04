package com.truecaller.android.sdksample

import com.truecaller.android.sdksample.PhoneUtils.processPhoneNumber
import junit.framework.Assert
import junit.framework.TestCase

/**
 * Created by rgdixit on 25/10/17.
 */
class PhoneNumberUtilsKtTest : TestCase() {
    fun testProcessPhoneNumber() {
        val pair = processPhoneNumber("+919833942369")
        Assert.assertEquals("+91", pair.first)
        Assert.assertEquals("9833942369", pair.second)
    }

}