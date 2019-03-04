package com.truecaller.android.sdksample;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.telephony.SmsMessage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsUtils {

    private static final String EXTRA_PDUS      = "pdus";
    private static final String EXTRA_FORMAT    = "format";
    private static final String MESSAGE_PATTERN = "Truecaller code ";

    @Nullable
    public static SmsMessage getFirstReceivedMessage(final Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            final Object[] pdus = (Object[]) extras.get(EXTRA_PDUS);
            final String format = extras.getString(EXTRA_FORMAT);

            if (pdus.length > 0) {
                return smsMessageFromPduCompat((byte[]) pdus[0], format);
            }
        }

        return null;
    }

    @SuppressWarnings("deprecation")
    public static SmsMessage smsMessageFromPduCompat(final byte[] pdu, @Nullable final String format) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return SmsMessage.createFromPdu(pdu, format);
        } else {
            return SmsMessage.createFromPdu(pdu);
        }
    }

    @Nullable
    public static String getVerificationCode(@NonNull final String message) {
        if (message.startsWith(MESSAGE_PATTERN)) {

            Pattern pattern = Pattern.compile("[0-9]{6,}");
            Matcher matcher = pattern.matcher(message);
            if (matcher.find()) {
                return matcher.group();
            }
        }
        return null;
    }
}
