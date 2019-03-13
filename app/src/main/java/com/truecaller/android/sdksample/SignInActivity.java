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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsMessage;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
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
import com.truecaller.android.sdk.TrueSDK;
import com.truecaller.android.sdk.TrueSdkScope;
import com.truecaller.android.sdk.clients.VerificationCallback;

import java.util.Locale;

public class SignInActivity extends Activity {

    private static final String TAG           = "SignInActivity";
    private static final int    REQUEST_PHONE = 0;
    private static final int    REQUEST_SMS   = 1;

    //constants for layouts
    private static final int LANDING_LAYOUT  = 1;
    private static final int PROFILE_LAYOUT  = 2;
    private static final int LOADER_LAYOUT   = 3;
    private static final int FORM_LAYOUT     = 4;
    private static final int SETTINGS_LAYOUT = 5;

    private BroadcastReceiver mTokenReceiver;
    private RadioGroup        titleSelector;
    private int               verificationCallbackType;

    private final ITrueCallback sdkCallback = new ITrueCallback() {
        @Override
        public void onSuccessProfileShared(@NonNull final TrueProfile trueProfile) {
            Toast.makeText(SignInActivity.this,
                    "Verified without " + getViaText() + " ! (Truecaller User): " + trueProfile.firstName,
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

    private final CountDownTimer countDownTimer = new CountDownTimer(240000, 1000) {

        public void onTick(long millisUntilFinished) {
            ((Button) findViewById(R.id.btnVerify)).setText("Verify (" + millisUntilFinished / 1000 + ")");
        }

        public void onFinish() {
            ((Button) findViewById(R.id.btnVerify)).setText("Retry");
            ((Button) findViewById(R.id.btnVerify)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    showLayout(FORM_LAYOUT);
                }
            });

        }
    };

    private final VerificationCallback apiCallback = new VerificationCallback() {

        @Override
        public void onRequestSuccess(final int requestCode, @Nullable String accessToken) {
            if (requestCode == VerificationCallback.TYPE_MISSED_CALL || requestCode == VerificationCallback.TYPE_OTP) {
                showLayout(PROFILE_LAYOUT);
                findViewById(R.id.btnVerify).setOnClickListener(verifyClickListener);
            } else {
                Toast.makeText(SignInActivity.this,
                        "Success: Verified with" + getViaText() + " with " + accessToken, Toast.LENGTH_SHORT).show();
                showLayout(LANDING_LAYOUT);
                startActivity(new Intent(SignInActivity.this, SignedInActivity.class));
            }
        }

        @Override
        public void onRequestFailure(final int requestCode, @NonNull final TrueException e) {
            Toast.makeText(SignInActivity.this, "OnFailureApiCallback: " + e.getExceptionMessage(),
                    Toast.LENGTH_SHORT).show();
            showLayout(FORM_LAYOUT);
        }
    };

    @NonNull
    private String getViaText() {
        String viaText = "Unknown";
        if (verificationCallbackType == VerificationCallback.TYPE_OTP) {
            viaText = "OTP";
        } else if (verificationCallbackType == VerificationCallback.TYPE_MISSED_CALL) {
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

            if (!TextUtils.isEmpty(otp) && verificationCallbackType == VerificationCallback.TYPE_OTP) {
                otp = otp.substring(0, 6);
                showLoader("Verifying profile...", false);
                TrueSDK.getInstance().verifyOtp(profile, otp, apiCallback);
            } else {
                TrueSDK.getInstance().verifyMissedCall(profile, apiCallback);
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

    private final View.OnClickListener proceedClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
            checkPhonePermission();
        }
    };

    private EditText   edtOtp;
    private TrueButton trueButton;
    private EditText   mPhoneField;

    @SuppressLint("NewApi")
    private View.OnClickListener btnGoClickListner = v -> {
        initTrueSdk();
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

        initTrueSdk();
        System.out.println("phone permission " + (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE) == PackageManager
                .PERMISSION_GRANTED));
        System.out.println("log permission " + (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_CALL_LOG) == PackageManager
                .PERMISSION_GRANTED));
        System.out.println("answer call permission " + isAnswerCallPermissionEnabled());
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initTrueSdk() {
        TrueSdkScope trueScope = new TrueSdkScope.Builder(this, sdkCallback)
                .consentMode(((Switch) findViewById(R.id.fullscreen)).isChecked() ? TrueSdkScope.CONSENT_MODE_FULLSCREEN
                        : TrueSdkScope.CONSENT_MODE_POPUP)
                .footerType(((Switch) findViewById(R.id.skip)).isChecked() ? TrueSdkScope.FOOTER_TYPE_SKIP
                        : TrueSdkScope.FOOTER_TYPE_CONTINUE)
                .consentTitleOption(titleSelector.getCheckedRadioButtonId() == ListView.INVALID_POSITION
                        ? TrueSdkScope.SDK_CONSENT_TITLE_LOG_IN
                        : resolveSelectedPosition(titleSelector.getCheckedRadioButtonId()))
                .build();
        TrueSDK.init(trueScope);


        EditText localeEt = findViewById(R.id.localeEt);
        String locale = "en";
        if (!TextUtils.isEmpty(localeEt.getText())) {
            locale = localeEt.getText().toString();
        }

        try {
            TrueSDK.getInstance().setLocale(new Locale(locale));
        } catch (Exception e) {
            //            ignored
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
                    verificationCallbackType == VerificationCallback.TYPE_MISSED_CALL);
            TrueSDK.getInstance().requestVerification("IN", phone, apiCallback);
        }
    }

    public void checkPhonePermission() {
        if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED
                || !isAnswerCallPermissionEnabled()) {
            requestPhonePermission();
        } else {
            verificationCallbackType = VerificationCallback.TYPE_MISSED_CALL;
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

        // BEGIN_INCLUDE(phone_permission_request)
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)
                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CALL_LOG)
                || shouldShowAnswerCallRequestPermissionRationale()) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            Log.i(TAG,
                    "Displaying camera permission rationale to provide additional context.");
            Snackbar.make(findViewById(R.id.activity_landing), "Give permission to identify device.",
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction("Allow", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            requestRequiredPhonePermissions();
                        }
                    })
                    .show();
        } else {
            // Phone permission has not been granted yet. Request it directly.
            requestRequiredPhonePermissions();
        }
        // END_INCLUDE(phone_permission_request)
    }

    public void checkSMSPermission() {
        // BEGIN_INCLUDE(sms_permission)
        // Check if the SMS permission is already available.
        verificationCallbackType = VerificationCallback.TYPE_OTP;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            // SMS permission has not been granted.
            requestSMSPermission();
        } else {
            waitForCode();
            requestVerification();
        }
        // END_INCLUDE(SMS_permission)
    }

    /**
     * Requests the SMS permission.
     * If the permission has been denied previously, a SnackBar will prompt the user to grant the
     * permission, otherwise it is requested directly.
     */
    private void requestSMSPermission() {
        Log.i(TAG, "SMS permission has NOT been granted. Requesting permission.");

        // BEGIN_INCLUDE(sms_permission_request)
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_SMS)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            Log.i(TAG,
                    "Displaying SMS permission rationale to provide additional context.");
            Snackbar.make(findViewById(R.id.activity_landing), "Give permission to identify device.",
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction("Allow", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(SignInActivity.this,
                                    new String[]{ Manifest.permission.READ_SMS },
                                    REQUEST_SMS);
                        }
                    })
                    .show();
        } else {
            // SMS permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.READ_SMS },
                    REQUEST_SMS);
        }
        // END_INCLUDE(sms_permission_request)
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        TrueSDK.getInstance().onActivityResultObtained(this, resultCode, data);
    }

    private void waitForCode() {
        //SmsUtils.register(this);
        countDownTimer.start();
        if (mTokenReceiver == null) {
            mTokenReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(final Context context, final Intent intent) {
                    if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
                        SmsMessage message = SmsUtils.getFirstReceivedMessage(intent);
                        if (message != null) {
                            final String token = SmsUtils.getVerificationCode(message.getMessageBody());
                            if (!TextUtils.isEmpty(token)) {
                                EditText editText = findViewById(R.id.edtOtpCode);
                                final String finalOtpText = getString(R.string.auto_fill_token, token);
                                editText.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(finalOtpText
                                        .length()) });
                                editText.setText(finalOtpText);
                            }
                            countDownTimer.cancel();
                            ((Button) findViewById(R.id.btnVerify)).setText(R.string.verify);
                            unregisterReceiver(mTokenReceiver);
                            mTokenReceiver = null;
                        }
                    }
                }
            };
        }


        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
        //        intentFilter.addAction(SmsUtils.ACTION_PROFILE_RESPONSE);
        registerReceiver(mTokenReceiver, intentFilter);
    }

    public void showLoader(String message, final boolean showSmsVerificationButton) {
        showLayout(LOADER_LAYOUT);
        ((TextView) findViewById(R.id.txtLoader)).setText(message);
        if (showSmsVerificationButton) {
            //            todo show sms button if number cant be resolved here
        }
    }

    @Override
    protected void onDestroy() {
        if (mTokenReceiver != null) {
            unregisterReceiver(mTokenReceiver);
        }
        super.onDestroy();
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
                verificationCallbackType = VerificationCallback.TYPE_MISSED_CALL;
                requestVerification();
            }
            //                if any of the phone permissions are not given, we would fallback to otp flow
            //                it would be a better place to request sms permission to auto-fill otp
            else {
                checkSMSPermission();
            }
        } else if (requestCode == REQUEST_SMS) {
            //            this will start sms verification
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
