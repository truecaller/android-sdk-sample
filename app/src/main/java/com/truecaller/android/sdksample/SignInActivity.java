/**
 * True SDK Copyright notice and License
 * <p/>
 * Copyright(c)2015-present,True Software Scandinavia AB.All rights reserved.
 * <p/>
 * In accordance with the separate agreement executed between You and True Software Scandinavia AB You are granted a
 * limited,non-exclusive,
 * non-sublicensable,non-transferrable,royalty-free,license to use the True SDK Product in object code form only,
 * solely for the purpose of using the
 * True SDK Product with the applications and API’s provided by True Software Scandinavia AB.
 * <p/>
 * THE TRUE SDK PRODUCT IS PROVIDED WITHOUT WARRANTY OF ANY KIND,EXPRESS OR IMPLIED,INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE,SOFTWARE QUALITY,PERFORMANCE,DATA ACCURACY AND NON-INFRINGEMENT.IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM,DAMAGES OR OTHER LIABILITY,WHETHER IN AN ACTION OF CONTRACT,TORT OR OTHERWISE,ARISING
 * FROM,OUT OF OR IN CONNECTION WITH THE
 * TRUE SDK PRODUCT OR THE USE OR OTHER DEALINGS IN THE TRUE SDK PRODUCT.AS A RESULT,THE TRUE SDK PRODUCT IS
 * PROVIDED"AS IS"AND BY INTEGRATING THE TRUE
 * SDK PRODUCT YOU ARE ASSUMING THE ENTIRE RISK AS TO ITS QUALITY AND PERFORMANCE.
 **/
package com.truecaller.android.sdksample;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.truecaller.android.sdk.ITrueCallback;
import com.truecaller.android.sdk.TrueButton;
import com.truecaller.android.sdk.TrueError;
import com.truecaller.android.sdk.TrueException;
import com.truecaller.android.sdk.TrueProfile;
import com.truecaller.android.sdk.TruecallerSDK;
import com.truecaller.android.sdk.TruecallerSdkScope;
import com.truecaller.android.sdk.clients.VerificationCallback;
import com.truecaller.android.sdk.clients.VerificationDataBundle;

import java.util.Locale;

public class SignInActivity extends Activity {

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
        public void onVerificationRequired() {
            showLayout(FORM_LAYOUT);
            findViewById(R.id.btnProceed).setOnClickListener(proceedClickListener);
        }
    };

    private final VerificationCallback apiCallback = new VerificationCallback() {

        @Override
        public void onRequestSuccess(final int requestCode, @Nullable VerificationDataBundle bundle) {
            if (requestCode == VerificationCallback.TYPE_MISSED_CALL_INITIATED) {
                showLoader("Waiting for call", false);
            } else if (requestCode == VerificationCallback.TYPE_MISSED_CALL_RECEIVED || requestCode == VerificationCallback.TYPE_OTP_INITIATED) {
                showLayout(PROFILE_LAYOUT);
                findViewById(R.id.btnVerify).setOnClickListener(verifyClickListener);
            } else if (requestCode == VerificationCallback.TYPE_OTP_RECEIVED) {
                fillOtp(bundle.getString(VerificationDataBundle.KEY_OTP));
            } else if (requestCode == VerificationCallback.TYPE_PROFILE_VERIFIED_BEFORE) {
                Toast.makeText(SignInActivity.this,
                        "Profile verified for your app before: " + bundle.getProfile().firstName
                                + " and access token: " + bundle.getProfile().accessToken,
                        Toast.LENGTH_SHORT).show();
                showLayout(LANDING_LAYOUT);
                startActivity(new Intent(SignInActivity.this, SignedInActivity.class));
            } else {
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
                    "OnFailureApiCallback: " + e.getExceptionMessage(),
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

            if (!TextUtils.isEmpty(otp) && verificationCallbackType == VerificationCallback.TYPE_OTP_RECEIVED
                    || verificationCallbackType == VerificationCallback.TYPE_OTP_INITIATED) {
                otp = otp.substring(0, 6);
                showLoader("Verifying profile...", false);
                TruecallerSDK.getInstance().verifyOtp(profile, otp, apiCallback);
            } else {
                TruecallerSDK.getInstance().verifyMissedCall(profile, apiCallback);
            }
        }
    };

    private final View.OnClickListener startClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
            try {
                trueButton.callOnClick();
            } catch (Exception e) {
                Toast.makeText(SignInActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    };

    private final View.OnClickListener proceedClickListener = view -> checkPhonePermission();

    private EditText   edtOtp;
    private TrueButton trueButton;
    private EditText   mPhoneField;

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
        trueButton = findViewById(R.id.com_truecaller_android_sdk_truebutton);

        showLayout(LANDING_LAYOUT);

        findViewById(R.id.btnStart).setOnClickListener(startClickListener);
        findViewById(R.id.buttonGo).setOnClickListener(btnGoClickListner);
        titleSelector = findViewById(R.id.sdkTitleOptions);
        additionalFooterSelector = findViewById(R.id.additionalFooters);

        initTruecallerSDK();
        System.out.println("phone permission " + (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE) == PackageManager
                .PERMISSION_GRANTED));
        System.out.println("log permission " + (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_CALL_LOG) == PackageManager
                .PERMISSION_GRANTED));
        System.out.println("answer call permission " + isAnswerCallPermissionEnabled());
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initTruecallerSDK() {
        TruecallerSdkScope trueScope = new TruecallerSdkScope.Builder(this, sdkCallback)
                .consentMode(((Switch) findViewById(R.id.fullscreen)).isChecked() ?
                        TruecallerSdkScope.CONSENT_MODE_FULLSCREEN
                        : TruecallerSdkScope.CONSENT_MODE_POPUP)
                .footerType(additionalFooterSelector.getCheckedRadioButtonId() == ListView.INVALID_POSITION
                        ? TruecallerSdkScope.FOOTER_TYPE_NONE
                        : resolveAdditionalFooter(additionalFooterSelector.getCheckedRadioButtonId()))
                .consentTitleOption(titleSelector.getCheckedRadioButtonId() == ListView.INVALID_POSITION
                        ? TruecallerSdkScope.SDK_CONSENT_TITLE_LOG_IN
                        : resolveSelectedPosition(titleSelector.getCheckedRadioButtonId()))
                .sdkOptions(((Switch) findViewById(R.id.sdkOptions)).isChecked() ?
                        TruecallerSdkScope.SDK_OPTION_WITH_OTP
                        : TruecallerSdkScope.SDK_OPTION_WIHTOUT_OTP)
                .build();
        TruecallerSDK.init(trueScope);


        EditText localeEt = findViewById(R.id.localeEt);
        String locale = "en";
        if (!TextUtils.isEmpty(localeEt.getText())) {
            locale = localeEt.getText().toString();
        }

        try {
            TruecallerSDK.getInstance().setLocale(new Locale(locale));
        } catch (Exception e) {
            //            ignored
        }
        findViewById(R.id.btnStart).setVisibility(TruecallerSDK.getInstance().isUsable() ? View.VISIBLE : View.GONE);
    }

    private int resolveAdditionalFooter(final int checkedRadioButtonId) {
        switch (checkedRadioButtonId) {
            case R.id.skip:
                return TruecallerSdkScope.FOOTER_TYPE_SKIP;
            case R.id.uan:
                return TruecallerSdkScope.FOOTER_TYPE_CONTINUE;
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
            TruecallerSDK.getInstance().requestVerification("IN", phone, apiCallback);
        }
    }

    public void checkPhonePermission() {
        if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED
                || !isAnswerCallPermissionEnabled()) {
            requestPhonePermission();
        } else {
            verificationCallbackType = VerificationCallback.TYPE_MISSED_CALL_INITIATED;
            requestVerification();
        }
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
        if (requestCode == REQUEST_PHONE) {
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
        }
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
}