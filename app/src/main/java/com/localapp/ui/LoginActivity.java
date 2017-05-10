package com.localapp.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.iid.FirebaseInstanceId;
import com.localapp.R;
import com.localapp.camera.CropImage;
import com.localapp.data.FbLoginError;
import com.localapp.data.LoginData;
import com.localapp.data.Profile;
import com.localapp.data.SignUpData;
import com.localapp.login_session.SessionManager;
import com.localapp.request.CommonRequest;
import com.localapp.request.FbLoginRequest;
import com.localapp.request.FbSignUpRequest;
import com.localapp.request.ForgetPasswordRequest;
import com.localapp.request.LoginRequest;
import com.localapp.request.UpdateProfileRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import static com.localapp.camera.CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE;
import static com.localapp.ui.ProfileFragment.SIGN_UP_REQUEST_CODE;
import static com.localapp.ui.SignUpActivity.PICK_IMAGE_REQUEST;

public class LoginActivity extends AppCompatActivity implements LoginRequest.LoginResponseCallback,ForgetPasswordRequest.ForgetPasswordRequestCallback,UpdateProfileRequest.UpdateProfileResponseCallback,
        com.facebook.GraphRequest.GraphJSONObjectCallback,FbLoginRequest.FbLoginResponseCallback,FbSignUpRequest.FbSignUpResponseCallback {
    private static final String TAG = "LoginActivity";
    private EditText _email, _password;
    private Button _loginBtn, _signupBtn;
    private TextView _forgotPass;
    private LoginButton _fbLoginButton;
    private File imgFile;
    private SignUpData tempSignUpData;

    CallbackManager fbCallbackManager;
    AccessTokenTracker fbTokenTracker;
    ProfileTracker fbProfileTracker;


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
        _fbLoginButton = (LoginButton) findViewById(R.id.fb_login_button);
        _fbLoginButton.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.com_facebook_button_login_logo_blue,0,0,0);

        _loginBtn.setOnClickListener(onClickListener);
        _signupBtn.setOnClickListener(onClickListener);
        _forgotPass.setOnClickListener(onClickListener);


        /***************** fb login *****************/
        List<String> fbPermissions = new ArrayList<>();
        fbPermissions.add("public_profile");
        fbPermissions.add("email");
        fbPermissions.add("user_about_me");
        fbPermissions.add("user_birthday");
        fbPermissions.add("user_location");
        fbPermissions.add("user_relationships");
        fbPermissions.add("user_work_history");
        _fbLoginButton.setReadPermissions(fbPermissions);

        fbCallbackManager = CallbackManager.Factory.create();
        _fbLoginButton.registerCallback(fbCallbackManager, fbCallback);
        LoginManager.getInstance().registerCallback(fbCallbackManager, fbCallback);
        fbTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                AccessToken.setCurrentAccessToken(currentAccessToken);
            }
        };

        fbProfileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(com.facebook.Profile oldProfile, com.facebook.Profile currentProfile) {
                com.facebook.Profile.setCurrentProfile(currentProfile);

            }
        };

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



    FacebookCallback<LoginResult> fbCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            Log.d("facebook","userId: "+loginResult.getAccessToken().getUserId());
            Log.d("facebook","token: "+loginResult.getAccessToken().getToken());

            if (!fbTokenTracker.isTracking()) {
                fbTokenTracker.startTracking();
            }

            onFbLogin(loginResult);



        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onError(FacebookException error) {
            Log.e(TAG,error.getMessage());
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
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Please wait...");
        mProgressDialog.show();
    }

    private void onFbLogin(LoginResult mLoginResult){
        LoginData data = new LoginData();
        data.setFbLoginResult(mLoginResult);
        FbLoginRequest fbLoginRequest = new FbLoginRequest(this,data,this);
        fbLoginRequest.executeRequest();
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
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
        mProgressDialog.setCancelable(false);
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

    private void onFbSignUpSuccess(SignUpData data) {
        //TODO: Implement signup success logic here
        HomeActivity.mLoginToken = data.getmToken();
        HomeActivity.mUserId = data.getmUserId();
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
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Please wait...");
            mProgressDialog.show();
        }else {
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            finish();
        }

    }

    private void fbSignUpRequest(SignUpData data) {

        FbSignUpRequest request = new FbSignUpRequest(this,data,this);
        request.executeRequest();
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Please wait...");
        mProgressDialog.show();
    }


    private void fbSignUpErrorDialog (){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("UPLOAD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getPicFromGallery();
            }
        });

        builder.setCancelable(false);

        builder.setNegativeButton("Cancel",null);
        builder.setView(R.layout.fb_signup_error_alert);
        builder.show();
    }

    void getPicFromGallery(){
        Intent intent = new Intent();
        // Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
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

            case PICK_IMAGE_REQUEST:
                if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                    Uri uri = data.getData();
                    startCropImageActivity(uri);
                }
                break;
            case CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                if (resultCode == RESULT_OK && data != null ) {

                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    Uri uri = result.getUri();
//                    String path = getRealPathFromURI(this,uri);//getRealPathFromURI_API19(this,uri);
                        imgFile = new File(uri.getPath());
                    tempSignUpData.setPicFile(imgFile);
                    fbSignUpRequest(tempSignUpData);
                }


        }
        fbCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Start crop image activity for the given image.
     */
    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .start(this);
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
        mProgressDialog.dismiss();
        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        finish();
    }

    //get facebook data from here
    @Override
    public void onCompleted(JSONObject object, GraphResponse response) {
        Log.d("LoginActivity",response.toString());
        mProgressDialog.dismiss();

        try {
            String fbName = object.getString("name");
            String fbEmail = object.getString("email");
            String fbId = AccessToken.getCurrentAccessToken().getUserId();
            String fbToken = AccessToken.getCurrentAccessToken().getToken();
//            String fbBirthaday = object.getString("birthday");
//                        String fbGender = object.getString("gender");
//                        String fbAbout = object.getString("about");
//                        String fbRelationship_status = object.getString("relationship_status");

            com.facebook.Profile fbProfile = null;
            try
            {
                fbProfile = com.facebook.Profile.getCurrentProfile();
                if (fbProfile == null){
                    Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    return;
                }
            }catch (Exception e){
                e.printStackTrace();
            }



            Uri picUrl = fbProfile.getProfilePictureUri(400,400);
            SignUpData signUpData = new SignUpData();
            signUpData.setFbId(fbId);
            signUpData.setFbToken(fbToken);
            signUpData.setmName(fbName);
            signUpData.setmEmail(fbEmail);

            new DownloadFileFromURL(signUpData).execute(picUrl.toString());


        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onFbLoginResponse(CommonRequest.ResponseCode responseCode, LoginData data) {
        mProgressDialog.dismiss();
        switch (responseCode) {
            case COMMON_RES_SUCCESS: onLoginSuccess(data);
                break;
            case COMMON_RES_SERVER_ERROR_WITH_MESSAGE:
                if (FbLoginError.ERROR_USER_NOT_FOUND == data.getFbLoginError().getStatusCode()) {
                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id, name, email, gender, birthday, location, about, relationship_status, work"); // Parámetros que pedimos a facebook
                    GraphRequest request = GraphRequest.newMeRequest(data.getFbLoginResult().getAccessToken(), LoginActivity.this);
                    request.setParameters(parameters);
                    request.executeAsync();
                    mProgressDialog = new ProgressDialog(this);
                    mProgressDialog.setCancelable(false);
                    mProgressDialog.setMessage("Please wait...");
                    mProgressDialog.show();
                }else {

                }
                break;
            case COMMON_RES_CONNECTION_TIMEOUT:
                LoginManager.getInstance().logOut();
                toast("Connection timeout");
                break;

            case COMMON_RES_FAILED_TO_CONNECT:
                LoginManager.getInstance().logOut();
                toast("No internet connection");
                break;
            case COMMON_RES_INTERNAL_ERROR:
                LoginManager.getInstance().logOut();
                break;
        }
    }

    @Override
    public void onFbSignUpResponse(CommonRequest.ResponseCode res, SignUpData data) {
        mProgressDialog.dismiss();
        switch (res) {
            case COMMON_RES_SUCCESS:
                onFbSignUpSuccess(data);
                break;
            case COMMON_RES_SERVER_ERROR_WITH_MESSAGE:
                if (FbLoginError.ERROR_FB_FACE_NOT_FOUND == data.getFbLoginError().getStatusCode()){
                    tempSignUpData = data;
                    fbSignUpErrorDialog();
                }
                break;
            case COMMON_RES_CONNECTION_TIMEOUT:
                LoginManager.getInstance().logOut();
                toast("Connection timeout");
                break;
            case COMMON_RES_FAILED_TO_CONNECT:
                LoginManager.getInstance().logOut();
                toast("No internet connection");
                break;
            case COMMON_RES_INTERNAL_ERROR:
                break;
        }
    }




    /**
     * Background Async Task to download file
     * */
    private class DownloadFileFromURL extends AsyncTask<String, String, String> {
        String fileName;

        private SignUpData mSignUpData;

        public DownloadFileFromURL(SignUpData mSignUpData) {
            this.mSignUpData = mSignUpData;
        }

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(LoginActivity.this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Getting data...");
            mProgressDialog.show();
            LoginManager.getInstance().logOut();
        }

        /**
         * Downloading file in background thread
         */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            fileName = System.currentTimeMillis() + "_fb.jpg";
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();
                // getting file length
                int lenghtOfFile = conection.getContentLength();

                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                // Output stream to write file
                OutputStream output =new FileOutputStream("/sdcard/downloadedfile.jpg");

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        /**
         * Updating progress bar
         */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            mProgressDialog.dismiss();
        }

        /**
         * After completing background task
         * Dismiss the progress dialog
         **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            mProgressDialog.dismiss();

            // Displaying downloaded image into image view
            // Reading image path from sdcard
            String imagePath = Environment.getExternalStorageDirectory().toString() + "/downloadedfile.jpg";
            imgFile = new File(imagePath);

            mSignUpData.setPicFile(imgFile);

            fbSignUpRequest(mSignUpData);


//            Toast.makeText(getContext(), ""+imagePath, Toast.LENGTH_SHORT).show();
            // setting downloaded into image view
//            my_image.setImageDrawable(Drawable.createFromPath(imagePath));
        }
    }


}
