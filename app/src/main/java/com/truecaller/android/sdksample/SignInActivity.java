/**
 * Truecaller SDK Copyright notice and License
 * <p/>
 * Copyright(c)2015-present,True Software Scandinavia AB.All rights reserved.
 * <p/>
 * In accordance with the separate agreement executed between You and Your respective Truecaller entity, You are granted a limited,non-exclusive,
 * non-sublicensable,non-transferrable,royalty-free,license to use the Truecaller SDK Product in object code form only,solely for the purpose of using the
 * Truecaller SDK Product with the applications and API’s provided by Truecaller.
 * <p/>
 * THE TRUECALLER SDK PRODUCT IS PROVIDED WITHOUT WARRANTY OF ANY KIND,EXPRESS OR IMPLIED,INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE,SOFTWARE QUALITY,PERFORMANCE,DATA ACCURACY AND NON-INFRINGEMENT.IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM,DAMAGES OR OTHER LIABILITY,WHETHER IN AN ACTION OF CONTRACT,TORT OR OTHERWISE,ARISING FROM,OUT OF OR IN CONNECTION WITH THE
 * TRUECALLER SDK PRODUCT OR THE USE OR OTHER DEALINGS IN THE TRUE SDK PRODUCT.AS A RESULT,THE TRUECALLER SDK PRODUCT IS PROVIDED”AS IS”AND BY INTEGRATING
 * THE TRUECALLER
 * SDK PRODUCT YOU ARE ASSUMING THE ENTIRE RISK AS TO ITS QUALITY AND PERFORMANCE
 **/

package com.truecaller.android.sdksample;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.truecaller.android.sdk.ITrueCallback;
import com.truecaller.android.sdk.SdkThemeOptions;
import com.truecaller.android.sdk.TrueError;
import com.truecaller.android.sdk.TrueException;
import com.truecaller.android.sdk.TrueProfile;
import com.truecaller.android.sdk.TruecallerSDK;
import com.truecaller.android.sdk.TruecallerSdkScope;
import com.truecaller.android.sdk.clients.VerificationCallback;
import com.truecaller.android.sdk.clients.VerificationDataBundle;

import org.shadow.apache.commons.lang3.StringUtils;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class SignInActivity extends AppCompatActivity {

    private static final String TAG           = "SignInActivity";
    private static final int    REQUEST_PHONE = 0;

    //constants for layouts
    private static final int LANDING_LAYOUT  = 1;
    private static final int PROFILE_LAYOUT  = 2;
    private static final int LOADER_LAYOUT   = 3;
    private static final int FORM_LAYOUT     = 4;
    private static final int SETTINGS_LAYOUT = 5;

    private RadioGroup titleSelector;
    private RadioGroup additionalFooterSelector;
    private int        verificationCallbackType;
    private Spinner    ctaPrefixSpinner, prefixSpinner, suffixSpinner;
    private Spinner colorSpinner, colorTextSpinner;
    private AppCompatTextView timerTextViewMissedCall, timerTextViewOTP;
    private CountDownTimer timer;

    private final ITrueCallback sdkCallback = new ITrueCallback() {
        @Override
        public void onSuccessProfileShared(@NonNull final TrueProfile trueProfile) {
            Toast.makeText(SignInActivity.this,
                    "Verified Truecaller User: " + trueProfile.firstName,
                    Toast.LENGTH_SHORT).show();
            showLayout(LANDING_LAYOUT);
            startActivity(new Intent(SignInActivity.this, SignedInActivity.class));
        }

        @Override
        public void onFailureProfileShared(@NonNull final TrueError trueError) {
            Toast.makeText(SignInActivity.this, "onFailureProfileShared: " + trueError.getErrorType(), Toast
                    .LENGTH_SHORT).show();
            showLayout(LANDING_LAYOUT);
        }

        @Override
        public void onVerificationRequired(final TrueError trueError) {
            Toast.makeText(SignInActivity.this,
                    "Verification Required",
                    Toast.LENGTH_SHORT).show();
            showLayout(FORM_LAYOUT);
            findViewById(R.id.btnProceed).setOnClickListener(proceedClickListener);
        }
    };

    private final VerificationCallback apiCallback = new VerificationCallback() {

        @Override
        public void onRequestSuccess(final int requestCode, @Nullable VerificationDataBundle bundle) {
            if (requestCode == VerificationCallback.TYPE_MISSED_CALL_INITIATED) {
                verificationCallbackType = VerificationCallback.TYPE_MISSED_CALL_INITIATED;
                String ttl = bundle.getString(VerificationDataBundle.KEY_TTL);
                if (ttl != null) {
                    Toast.makeText(SignInActivity.this,
                            "Missed call initiated with TTL : " + ttl,
                            Toast.LENGTH_SHORT).show();
                    showCountDownTimer(Double.parseDouble(ttl) * 1000);
                }
                showLoader("Waiting for call", false);
            } else if (requestCode == VerificationCallback.TYPE_MISSED_CALL_RECEIVED) {
                Toast.makeText(SignInActivity.this,
                        "Missed call received",
                        Toast.LENGTH_SHORT).show();
                showLayout(PROFILE_LAYOUT);
                findViewById(R.id.btnVerify).setOnClickListener(verifyClickListener);
            } else if (requestCode == VerificationCallback.TYPE_OTP_INITIATED) {
                verificationCallbackType = VerificationCallback.TYPE_OTP_INITIATED;
                String ttl = bundle.getString(VerificationDataBundle.KEY_TTL);
                if (ttl != null) {
                    Toast.makeText(SignInActivity.this,
                            "OTP initiated with TTL : " + bundle.getString(VerificationDataBundle.KEY_TTL),
                            Toast.LENGTH_SHORT).show();
                    showCountDownTimer(Double.parseDouble(ttl) * 1000);
                }
                showLayout(PROFILE_LAYOUT);
                findViewById(R.id.btnVerify).setOnClickListener(verifyClickListener);
            } else if (requestCode == VerificationCallback.TYPE_OTP_RECEIVED) {
                Toast.makeText(SignInActivity.this,
                        "OTP received",
                        Toast.LENGTH_SHORT).show();
                fillOtp(bundle.getString(VerificationDataBundle.KEY_OTP));
            } else if (requestCode == VerificationCallback.TYPE_PROFILE_VERIFIED_BEFORE) {
                Toast.makeText(SignInActivity.this,
                        "Profile verified for your app before: " + bundle.getProfile().firstName
                                + " and access token: " + bundle.getProfile().accessToken,
                        Toast.LENGTH_SHORT).show();
                showLayout(LANDING_LAYOUT);
                startActivity(new Intent(SignInActivity.this, SignedInActivity.class));
            } else {
                dismissCountDownTimer();
                Toast.makeText(SignInActivity.this,
                        "Success: Verified with" + getViaText() + " with " + bundle.getString(VerificationDataBundle.KEY_ACCESS_TOKEN),
                        Toast.LENGTH_SHORT).show();
                showLayout(LANDING_LAYOUT);
                startActivity(new Intent(SignInActivity.this, SignedInActivity.class));
            }
        }

        @Override
        public void onRequestFailure(final int requestCode, @NonNull final TrueException e) {
            Toast.makeText(
                    SignInActivity.this,
                    "OnFailureApiCallback: " + e.getExceptionType() + "\n" + e.getExceptionMessage(),
                    Toast.LENGTH_SHORT)
                    .show();
            showLayout(FORM_LAYOUT);
        }
    };

    private void fillOtp(final String otp) {
        edtOtp.setText(otp);
    }

    @NonNull
    private String getViaText() {
        String viaText = "Unknown";
        if (verificationCallbackType == VerificationCallback.TYPE_OTP_INITIATED) {
            viaText = "OTP";
        } else if (verificationCallbackType == VerificationCallback.TYPE_MISSED_CALL_INITIATED) {
            viaText = "MISSED CALL";
        }
        return viaText;
    }

    //**********Click listeners  *************//
    private final View.OnClickListener verifyClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
            String otp = edtOtp.getText().toString().trim();
            final String firstName = ((EditText) findViewById(R.id.edtFirstName)).getText().toString();
            final String lastName = ((EditText) findViewById(R.id.edtLastName)).getText().toString();
            final TrueProfile profile = new TrueProfile.Builder(firstName, lastName).build();

            if (verificationCallbackType == VerificationCallback.TYPE_OTP_INITIATED) {
                if (TextUtils.isEmpty(otp)) {
                    return;
                }
                showLoader("Verifying profile...", false);
                TruecallerSDK.getInstance().verifyOtp(profile, otp, apiCallback);
            } else {
                TruecallerSDK.getInstance().verifyMissedCall(profile, apiCallback);
            }
        }
    };

    private final View.OnClickListener startClickListener = view -> {
        try {
            TruecallerSDK.getInstance().getUserProfile(SignInActivity.this);
        } catch (Exception e) {
            Toast.makeText(SignInActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    private final View.OnClickListener proceedClickListener = view -> checkPhonePermission();

    private EditText edtOtp;
    private EditText mPhoneField;

    @SuppressLint("NewApi")
    private View.OnClickListener btnGoClickListner = v -> {
        initTruecallerSDK();
        showLayout(LANDING_LAYOUT);
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        edtOtp = findViewById(R.id.edtOtpCode);
        mPhoneField = findViewById(R.id.edtPhone);

        showLayout(LANDING_LAYOUT);

        findViewById(R.id.btnStart).setOnClickListener(startClickListener);
        findViewById(R.id.buttonGo).setOnClickListener(btnGoClickListner);
        findViewById(R.id.buttonGo).setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        findViewById(R.id.card_layout).setBackgroundColor(ContextCompat.getColor(this, R.color.card_layout_color));
        titleSelector = findViewById(R.id.sdkTitleOptions);
        additionalFooterSelector = findViewById(R.id.additionalFooters);

        colorSpinner = findViewById(R.id.color_spinner);
        colorTextSpinner = findViewById(R.id.color_text_spinner);
        ctaPrefixSpinner = findViewById(R.id.cta_prefix_spinner);
        prefixSpinner = findViewById(R.id.prefix_spinner);
        suffixSpinner = findViewById(R.id.suffix_spinner);

        timerTextViewMissedCall = findViewById(R.id.timerTextProgress);
        timerTextViewOTP = findViewById(R.id.timerText);
        setSpinnerAdapters();

        initTruecallerSDK();
        System.out.println("phone permission " + (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE) == PackageManager
                .PERMISSION_GRANTED));
        System.out.println("log permission " + (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_CALL_LOG) == PackageManager
                .PERMISSION_GRANTED));
        System.out.println("answer call permission " + isAnswerCallPermissionEnabled());
    }

    private void setSpinnerAdapters() {
        ArrayAdapter<CharSequence> adapterP =
                ArrayAdapter.createFromResource(this,
                        R.array.SdkPartnerLoginPrefixOptionsArray,
                        android.R.layout.simple_spinner_item);
        adapterP.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prefixSpinner.setAdapter(adapterP);

        ArrayAdapter<CharSequence> adapterS =
                ArrayAdapter.createFromResource(this,
                        R.array.SdkPartnerLoginSuffixOptionsArray,
                        android.R.layout.simple_spinner_item);
        adapterS.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        suffixSpinner.setAdapter(adapterS);

        ArrayAdapter<CharSequence> adapterCP =
                ArrayAdapter.createFromResource(this,
                        R.array.SdkPartnerCTAPrefixOptionsArray,
                        android.R.layout.simple_spinner_item);
        adapterCP.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ctaPrefixSpinner.setAdapter(adapterCP);

        ArrayAdapter<CharSequence> adapterColor =
                ArrayAdapter.createFromResource(this,
                        R.array.SdkPartnerSampleColors,
                        android.R.layout.simple_spinner_item);
        adapterColor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        colorSpinner.setAdapter(adapterColor);
        colorTextSpinner.setAdapter(adapterColor);

        prefixSpinner.setSelection(0);
        suffixSpinner.setSelection(0);
        ctaPrefixSpinner.setSelection(0);
        colorSpinner.setSelection(0);
        colorTextSpinner.setSelection(1);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initTruecallerSDK() {
        EditText editTextTnC = findViewById(R.id.editTextTnC);
        EditText editTextPp = findViewById(R.id.editTextPp);

        TruecallerSdkScope trueScope = new TruecallerSdkScope.Builder(this, sdkCallback)
                .consentMode(((Switch) findViewById(R.id.fullscreen)).isChecked() ?
                        TruecallerSdkScope.CONSENT_MODE_FULLSCREEN
                        : (((Switch) findViewById(R.id.bottomsheet)).isChecked() ?
                        TruecallerSdkScope.CONSENT_MODE_BOTTOMSHEET
                        : TruecallerSdkScope.CONSENT_MODE_POPUP))
                .buttonColor(Color.parseColor(colorSpinner.getSelectedItem().toString())) //default TC blue
                .buttonTextColor(Color.parseColor(colorTextSpinner.getSelectedItem().toString())) //default white
                .loginTextPrefix(prefixSpinner.getSelectedItemPosition()) //default 0
                .loginTextSuffix(suffixSpinner.getSelectedItemPosition()) //default 0
                .ctaTextPrefix(ctaPrefixSpinner.getSelectedItemPosition()) //default 0
                .buttonShapeOptions(((Switch) findViewById(R.id.shapeOptions)).isChecked() ?
                        TruecallerSdkScope.BUTTON_SHAPE_RECTANGLE
                        : TruecallerSdkScope.BUTTON_SHAPE_ROUNDED) //default ROUNDED
                .privacyPolicyUrl(editTextPp.getText().toString()) //default NULL
                .termsOfServiceUrl(editTextTnC.getText().toString()) //default NULL
                .footerType(additionalFooterSelector.getCheckedRadioButtonId() == ListView.INVALID_POSITION
                        ? TruecallerSdkScope.FOOTER_TYPE_NONE
                        : resolveAdditionalFooter(additionalFooterSelector.getCheckedRadioButtonId()))
                .consentTitleOption(titleSelector.getCheckedRadioButtonId() == ListView.INVALID_POSITION
                        ? TruecallerSdkScope.SDK_CONSENT_TITLE_LOG_IN
                        : resolveSelectedPosition(titleSelector.getCheckedRadioButtonId()))
                .sdkOptions(((Switch) findViewById(R.id.sdkOptions)).isChecked() ?
                        TruecallerSdkScope.SDK_OPTION_WITH_OTP
                        : TruecallerSdkScope.SDK_OPTION_WITHOUT_OTP)
                .build();
        TruecallerSDK.init(trueScope);
        if (TruecallerSDK.getInstance().isUsable()) {
            TruecallerSDK.getInstance().setTheme(((Switch) findViewById(R.id.themeOptions)).isChecked() ?
                    SdkThemeOptions.DARK
                    : SdkThemeOptions.LIGHT);
        }
        EditText localeEt = findViewById(R.id.localeEt);
        String locale = null;
        if (!TextUtils.isEmpty(localeEt.getText())) {
            locale = localeEt.getText().toString();
        }

        if (!StringUtils.isEmpty(locale)) {
            TruecallerSDK.getInstance().setLocale(new Locale(locale));
        }

        findViewById(R.id.btnStart).setVisibility(TruecallerSDK.getInstance().isUsable() ? View.VISIBLE : View.GONE);
    }

    private int resolveAdditionalFooter(final int checkedRadioButtonId) {
        switch (checkedRadioButtonId) {
            case R.id.skip:
                return TruecallerSdkScope.FOOTER_TYPE_SKIP;
            case R.id.uan:
                return TruecallerSdkScope.FOOTER_TYPE_CONTINUE;
            case R.id.uam:
                return TruecallerSdkScope.FOOTER_TYPE_ANOTHER_METHOD;
            case R.id.edm:
                return TruecallerSdkScope.FOOTER_TYPE_MANUALLY;
            case R.id.idl:
                return TruecallerSdkScope.FOOTER_TYPE_LATER;
            default:
                return TruecallerSdkScope.FOOTER_TYPE_NONE;
        }
    }

    private int resolveSelectedPosition(final int checkedRadioButtonId) {
        int pos = 0;
        switch (checkedRadioButtonId) {
            case R.id.zero:
                pos = 0;
                break;
            case R.id.one:
                pos = 1;
                break;
            case R.id.two:
                pos = 2;
                break;
            case R.id.three:
                pos = 3;
                break;
            case R.id.four:
                pos = 4;
                break;
            case R.id.five:
                pos = 5;
        }
        System.out.println("pos is " + pos);
        return pos;
    }

    public void requestVerification() {
        final String phone = mPhoneField.getText().toString().trim();
        if (!TextUtils.isEmpty(phone)) {
            showLoader("Trying " + getViaText() + "...",
                    verificationCallbackType == VerificationCallback.TYPE_MISSED_CALL_INITIATED);
            TruecallerSDK.getInstance().requestVerification("IN", phone, apiCallback, this);
        }
    }

    public void checkPhonePermission() {
        //commented the logic for asking permission because that became a part of SDK 2.0 itself
        /*if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED
                || !isAnswerCallPermissionEnabled()) {
            requestPhonePermission();
        } else {*/
        //            verificationCallbackType = VerificationCallback.TYPE_MISSED_CALL_INITIATED;
        requestVerification();
        //        }
    }

    /**
     * Requests the phone permission.
     * If the permission has been denied previously, a SnackBar will prompt the user to grant the
     * permission, otherwise it is requested directly.
     */
    private void requestPhonePermission() {
        Log.i(TAG, "PHONE permission has NOT been granted. Requesting permission.");

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)
                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CALL_LOG)
                || shouldShowAnswerCallRequestPermissionRationale()) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            Snackbar.make(findViewById(R.id.activity_landing), "Give permission to identify device.",
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction("Allow", view -> requestRequiredPhonePermissions())
                    .show();
        } else {
            // Phone permission has not been granted yet. Request it directly.
            requestRequiredPhonePermissions();
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        TruecallerSDK.getInstance().onActivityResultObtained(this, resultCode, data);
    }

    public void showLoader(String message, final boolean showSmsVerificationButton) {
        showLayout(LOADER_LAYOUT);
        ((TextView) findViewById(R.id.txtLoader)).setText(message);
    }

    private void showCountDownTimer(Double ttl) {
        if (verificationCallbackType == VerificationCallback.TYPE_MISSED_CALL_INITIATED) {
            timerTextViewMissedCall.setVisibility(View.VISIBLE);
        }
        timerTextViewOTP.setVisibility(View.VISIBLE);
        timer = new CountDownTimer(ttl.longValue(), 1000) {
            @Override
            public void onTick(final long millisUntilFinished) {
                if (verificationCallbackType == VerificationCallback.TYPE_MISSED_CALL_INITIATED) {
                    timerTextViewMissedCall.setPaintFlags(timerTextViewMissedCall.getPaintFlags() & ~Paint.UNDERLINE_TEXT_FLAG);
                    timerTextViewMissedCall.setText(String.format(getString(R.string.retry_timer), millisUntilFinished / 1000));
                }
                timerTextViewOTP.setPaintFlags(timerTextViewOTP.getPaintFlags() & ~Paint.UNDERLINE_TEXT_FLAG);
                timerTextViewOTP.setText(String.format(getString(R.string.retry_timer), millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                if (verificationCallbackType == VerificationCallback.TYPE_MISSED_CALL_INITIATED) {
                    timerTextViewMissedCall.setPaintFlags(timerTextViewMissedCall.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    timerTextViewMissedCall.setText(getString(R.string.retry_now));
                    timerTextViewMissedCall.setOnClickListener(v -> {
                        showLayout(FORM_LAYOUT);
                    });
                }

                timerTextViewOTP.setPaintFlags(timerTextViewOTP.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                timerTextViewOTP.setText(getString(R.string.retry_now));
                timerTextViewOTP.setOnClickListener(v -> {
                    showLayout(FORM_LAYOUT);
                });
            }
        };
        timer.start();
    }

    private void dismissCountDownTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        if (verificationCallbackType == VerificationCallback.TYPE_MISSED_CALL_INITIATED) {
            timerTextViewMissedCall.setVisibility(View.GONE);
        }
        timerTextViewOTP.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        if (findViewById(R.id.optionsMenu).getVisibility() != View.VISIBLE) {
            showLayout(SETTINGS_LAYOUT);
        } else {
            super.onBackPressed();
        }
    }

    public void showLayout(int id) {
        findViewById(R.id.landingLayout).setVisibility(id == LANDING_LAYOUT ? View.VISIBLE : View.GONE);
        findViewById(R.id.profileLayout).setVisibility(id == PROFILE_LAYOUT ? View.VISIBLE : View.GONE);
        findViewById(R.id.loaderLayout).setVisibility(id == LOADER_LAYOUT ? View.VISIBLE : View.GONE);
        findViewById(R.id.formLayout).setVisibility(id == FORM_LAYOUT ? View.VISIBLE : View.GONE);
        findViewById(R.id.optionsMenu).setVisibility(id == SETTINGS_LAYOUT ? View.VISIBLE : View.GONE);

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(findViewById(R.id.landingLayout).getWindowToken(), 0);
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions,
                                           @NonNull final int[] grantResults) {
        //       we continue whether we get required phone permissions or not
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        /*if (requestCode == REQUEST_PHONE) {
            boolean isPhonePermissionsGiven = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    isPhonePermissionsGiven = false;
                    break;
                }
            }
            if (isPhonePermissionsGiven) {
                //               this will start missed-call verification
                verificationCallbackType = VerificationCallback.TYPE_MISSED_CALL_INITIATED;
                requestVerification();
            }
            //                if any of the phone permissions are not given, we would fallback to otp flow
            //                it would be a better place to request sms permission to auto-fill otp
            else {
                verificationCallbackType = VerificationCallback.TYPE_OTP_INITIATED;
                requestVerification();
            }
        } else {
            //            this will start sms verification
            verificationCallbackType = VerificationCallback.TYPE_OTP_INITIATED;
            requestVerification();
        }*/
    }

    private boolean isAnswerCallPermissionEnabled() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.O
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ANSWER_PHONE_CALLS) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean shouldShowAnswerCallRequestPermissionRationale() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ANSWER_PHONE_CALLS);
    }

    private void requestRequiredPhonePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ActivityCompat.requestPermissions(SignInActivity.this,
                    new String[]{ Manifest.permission.ANSWER_PHONE_CALLS, Manifest.permission.READ_CALL_LOG,
                            Manifest.permission.READ_PHONE_STATE }, REQUEST_PHONE);
        } else {
            ActivityCompat.requestPermissions(SignInActivity.this,
                    new String[]{ Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_PHONE_STATE },
                    REQUEST_PHONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissCountDownTimer();
    }
}