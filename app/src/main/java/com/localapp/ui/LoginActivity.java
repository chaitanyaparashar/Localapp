package com.localapp.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.localapp.R;
import com.localapp.data.LoginData;
import com.localapp.data.Profile;
import com.localapp.login_session.SessionManager;
import com.localapp.request.CommonRequest;
import com.localapp.request.ForgetPasswordRequest;
import com.localapp.request.LoginRequest;
import com.localapp.request.UpdateProfileRequest;

import static com.localapp.ui.ProfileFragment.SIGN_UP_REQUEST_CODE;

public class LoginActivity extends AppCompatActivity implements LoginRequest.LoginResponseCallback,ForgetPasswordRequest.ForgetPasswordRequestCallback,UpdateProfileRequest.UpdateProfileResponseCallback {
    private EditText _email, _password;
    private Button _loginBtn, _signupBtn;
    private TextView _forgotPass;

    SessionManager session;

    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        try {
            getSupportActionBar().hide();
        }catch (NullPointerException e){
            e.printStackTrace();
        }



        session = new SessionManager(this);

        setupView();
    }

    public void setupView() {
        _email = (EditText) findViewById(R.id.input_email);
        _password = (EditText) findViewById(R.id.input_password);
        _loginBtn = (Button)  findViewById(R.id.btn_login);
        _signupBtn = (Button) findViewById(R.id.link_signup);
        _forgotPass  = (TextView) findViewById(R.id.link_forgotPassword);
        _loginBtn.setOnClickListener(onClickListener);
        _signupBtn.setOnClickListener(onClickListener);
        _forgotPass.setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.btn_login: onLogin();
                    break;
                case R.id.link_signup: startActivityForResult(new Intent(LoginActivity.this,SignUpActivity.class),SIGN_UP_REQUEST_CODE);
                    break;
                case R.id.link_forgotPassword: onForgetPassword();
                    break;
            }
        }
    };


    private void onLogin() {
        String mEmail = _email.getText().toString();
        String mPassword = _password.getText().toString();

        if (mEmail.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(mEmail).matches()) {
            _email.setError("enter a valid email address");
            return;
        }else if (mPassword.isEmpty() || mPassword.length() <6 || mPassword.length() >16) {
            _password.setError("enter a valid password");
            return;
        }


        LoginData loginData = new LoginData(mEmail, mPassword);
        LoginRequest request = new LoginRequest(this,loginData,this);
        request.executeRequest();
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Please wait...");
        mProgressDialog.show();
    }

    private void onForgetPassword() {
        String mEmail = _email.getText().toString();
        if (mEmail.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(mEmail).matches()) {
            _email.setError("enter a valid email address");
            return;
        }

        ForgetPasswordRequest request = new ForgetPasswordRequest(LoginActivity.this,mEmail,this);
        request.executeRequest();
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Please wait...");
        mProgressDialog.show();
    }

    private void onLoginSuccess(LoginData data) {
        //TODO: -----
        HomeActivity.mLoginToken = data.getAccessToken();
        HomeActivity.mUserId = data.getUserId();
        HomeActivity.mPicUrl = data.getPicUrl();
        session.createLoginSession(HomeActivity.mLoginToken,HomeActivity.mUserId, HomeActivity.mPicUrl, HomeActivity.mLastKnownLocation);
        fcmTokenUpdateRequest();


    }

    private void fcmTokenUpdateRequest() {
        String fcm_token = FirebaseInstanceId.getInstance().getToken();
        if (fcm_token != null) {
            Profile profile = new Profile(HomeActivity.mUserId);
            profile.setFcmToken(fcm_token);
            profile.setuToken(HomeActivity.mLoginToken);
            UpdateProfileRequest request = new UpdateProfileRequest(this,profile,this);
            request.executeRequest();
        }

    }


    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SIGN_UP_REQUEST_CODE:
                if (resultCode != Activity.RESULT_CANCELED && data.getBooleanExtra("result",false)) {
                    startActivity(new Intent(this,HomeActivity.class));
                    finish();
                }
                break;


        }
    }

    @Override
    public void ForgetPasswordResponse(CommonRequest.ResponseCode responseCode, String msg) {
        mProgressDialog.dismiss();
        switch (responseCode) {
            case COMMON_RES_SUCCESS:
                toast("Password reset successfully, check your email!");
                break;
            case COMMON_RES_CONNECTION_TIMEOUT:
                toast("Connection timeout");
                break;
            case COMMON_RES_FAILED_TO_CONNECT:
                toast("No internet connection");
                break;
            case COMMON_RES_INTERNAL_ERROR:
                break;
            case COMMON_RES_SERVER_ERROR_WITH_MESSAGE:
                toast(msg);
                break;
        }
    }

    @Override
    public void onLoginResponse(CommonRequest.ResponseCode responseCode, LoginData data) {
        switch (responseCode) {
            case COMMON_RES_SUCCESS:
                _email.setText("");
                _password.setText("");
                onLoginSuccess(data);
                break;
            case COMMON_RES_CONNECTION_TIMEOUT:
                toast("Connection timeout");
                mProgressDialog.dismiss();
                break;
            case COMMON_RES_FAILED_TO_CONNECT:
                toast("No internet connection");
                mProgressDialog.dismiss();
                break;
            case COMMON_RES_INTERNAL_ERROR:
                break;
            case COMMON_RES_SERVER_ERROR_WITH_MESSAGE:
                toast(data.getErrorMessage());
                mProgressDialog.dismiss();
                break;
        }
    }

    @Override
    public void onUpdateProfileResponse(CommonRequest.ResponseCode responseCode) {
        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        finish();
    }
}
