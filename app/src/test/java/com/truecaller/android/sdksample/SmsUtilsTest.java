package com.truecaller.android.sdksample;

import junit.framework.TestCase;

import org.junit.Test;

public class SmsUtilsTest extends TestCase {

    @Test
    public void testGetVerificationCode() throws Exception {

        String verificationCode = SmsUtils.getVerificationCode("Truecaller code is 744523.");
        assertEquals("744523", verificationCode);

    }

}