package com.truecaller.android.sdksample;

import android.support.annotation.NonNull;
import android.util.Pair;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by rgdixit on 27/10/17.
 */

/*package*/ class PhoneUtils {

    @NonNull
    /*package*/ static Pair<String, String> processPhoneNumber(@NonNull String phoneNumber) {
        Pattern compile = Pattern.compile("^\\+\\d\\d");
        Matcher matcher = compile.matcher(phoneNumber);
        if (matcher.find()) {
            int start = matcher.toMatchResult().start();
            int end = matcher.toMatchResult().end();
            return new Pair<>(phoneNumber.substring(start, end), phoneNumber.substring(end));
        }
        return new Pair<>("NONE", phoneNumber);
    }
}
