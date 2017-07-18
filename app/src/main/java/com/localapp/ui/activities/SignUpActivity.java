package com.localapp.ui.activities;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.localapp.camera.Camera2Activity;
import com.localapp.compressor.Compressor;
import com.localapp.R;
import com.localapp.models.SignUpData;
import com.localapp.preferences.AppPreferences;
import com.localapp.preferences.SessionManager;
import com.localapp.network.helper.CommonRequest;
import com.localapp.network.SignUpRequest;
import com.localapp.ui.custom_views.CircularNetworkImageView;
import com.localapp.ui.adapters.ExpandableListAdapter;
import com.localapp.utils.Constants;
import com.squareup.picasso.Picasso;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity implements SignUpRequest.SignUpResponseCallback{
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_AND_CAMERA = 111;
    public static final int PICK_IMAGE_REQUEST = 100;

    SessionManager session;
    final static String[] CAMERA_PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA};

    EditText mNameView, mNumberView, mEmailView,
            mPasswordView, cPasswordView, mInfoView,
            mDetailView,mLocationTypeView,mProfessionView;
    boolean numberVisibility = true;

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;


    // Custom ImageView: with extended CircularImageView and customize like NetworkImageView
    CircularNetworkImageView profilePic;

    LoginButton fb_LoginButton;
    Button fb_FillBtn;
    CallbackManager fbCallbackManager;
    AccessTokenTracker fbTokenTracker;
    ProfileTracker fbProfileTracker;

    TextView mSignInView;

    Button signUpBtn, picUploadBtn;
    File imgFile;

    ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ActionBar actionBar = getActionBar();

        if (actionBar != null) {
            actionBar.hide();
        }


        setupView();
    }

    public void setupView(){
        signUpBtn = (Button) findViewById(R.id._signUp);

        session = new SessionManager(this);
        /*************** fb login *****************/
        List<String> fbPermissions = new ArrayList<>();
        fbPermissions.add(Constants.FB_PERMISSION_PROFILE);
        fbPermissions.add(Constants.FB_PERMISSION_EMAIL);
        /*fbPermissions.add(Constants.FB_PERMISSION_ABOUT);
        fbPermissions.add(Constants.FB_PERMISSION_BIRTHDAY);
        fbPermissions.add(Constants.FB_PERMISSION_LOCATION);
        fbPermissions.add(Constants.FB_PERMISSION_RELATIONSHIP);*/
        fbPermissions.add(Constants.FB_PERMISSION_WORK_HISTORY);


        fb_LoginButton = (LoginButton) findViewById(R.id.fb_login_button);
        fb_LoginButton.setReadPermissions(fbPermissions);
//        fb_LoginButton.setFragment(this);
        fbCallbackManager = CallbackManager.Factory.create();
        fb_LoginButton.registerCallback(fbCallbackManager, fbCallback);
        LoginManager.getInstance().registerCallback(fbCallbackManager, fbCallback);
        fbTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                AccessToken.setCurrentAccessToken(currentAccessToken);
            }
        };

        fbProfileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                com.facebook.Profile.setCurrentProfile(currentProfile);

            }
        };

        fb_FillBtn = (Button) findViewById(R.id.fb_fill_btn);
        fb_FillBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fb_LoginButton.performClick();
            }
        });

        picUploadBtn = (Button) findViewById(R.id.upload_btn);

        profilePic = (CircularNetworkImageView) findViewById(R.id.image_pic);

        mNameView = (EditText) findViewById(R.id.input_name);
        mNumberView = (EditText) findViewById(R.id.input_phoneNumber);
        mEmailView = (EditText) findViewById(R.id.input_email);
        mProfessionView = (EditText) findViewById(R.id.input_profession);
        mPasswordView = (EditText) findViewById(R.id.input_password);
        cPasswordView = (EditText) findViewById(R.id.input_password_c);
        mInfoView = (EditText) findViewById(R.id.input_brief_intro);
        mDetailView = (EditText) findViewById(R.id.input_details_des);
        mSignInView = (TextView) findViewById(R.id.sign_in_text);

//        mProfessionView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showPopup(SignUpActivity.this);
//            }
//        });

        mProfessionView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    showPopup(SignUpActivity.this);
                }
                return false;
            }
        });




        mNumberView.setTag("0");//privacy 0 means visible and 1 means hide
        mNumberView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (mNumberView.getRight() - mNumberView.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here
                        if (numberVisibility) {
//                            mNumberView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_phone, 0, R.drawable.ic_password_hidden, 0);
                            mNumberView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_phone,0,R.drawable.ic_password_hidden,0);
                            numberVisibility = false;
                            mNumberView.setTag("1");
                        }else {
//                            mNumberView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_phone, 0, R.drawable.ic_password_visible, 0);
                            mNumberView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_phone, 0, R.drawable.ic_password_visible, 0);
                            numberVisibility = true;
                            mNumberView.setTag("0");
                        }

                    }
                }
                return false;
            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*!session.isLoggedIn()*/
                if (true) {
                    signUp();
                }else {
                    session.logoutUser();
                    signUpBtn.setText(R.string.btn_sign_up);
                    mNameView.setEnabled(true);
                    mNumberView.setEnabled(true);
                    mEmailView.setEnabled(true);
                    mInfoView.setEnabled(true);
                    mDetailView.setEnabled(true);

                    mNameView.getText().clear();
                    mNumberView.getText().clear();
                    mEmailView.getText().clear();
                    mInfoView.getText().clear();
                    mDetailView.getText().clear();
                }
            }
        });

        mSignInView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        picUploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isStoragePermissionGranted()){
//                    getPicFromGallery();
                    openCamera();
                }else {
                    permissionsRequestReadExternalStorage();
                }
            }
        });



    }

    void openCamera(){
        Intent intent = new Intent(this,Camera2Activity.class);
        intent.putExtra("requestCode", PICK_IMAGE_REQUEST);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }


    // The method that displays the popup.
    private void showPopup(final Activity context) {

        // Inflate the popup_layout.xml
        LinearLayout viewGroup = (LinearLayout) context.findViewById(R.id.activity_select_profession);
        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.expendable_list, null);




        // get the listview
        expListView = (ExpandableListView) layout.findViewById(R.id.lvExp);

        // preparing list data
//        prepareListData();

        listAdapter = new ExpandableListAdapter(this);

        // setting list adapter
        expListView.setAdapter(listAdapter);



        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(layout);
        builder.setTitle(R.string.title_select_profession);
        builder.setIcon(R.drawable.ic_profession);
        builder.setPositiveButton(R.string.btn_done, null);

        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                String items = "";
                for(int mGroupPosition =0; mGroupPosition < listAdapter.getGroupCount(); mGroupPosition++) {
                    items = items +  listAdapter.getItemAtPostion(mGroupPosition);

                }
                if (items.length() > 2) {
                    mProfessionView.setText(items.substring(0,items.length()-1));
                }
            }
        });
        builder.show();


    }



    void permissionsRequestReadExternalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(CAMERA_PERMISSIONS,MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_AND_CAMERA);
        }
    }


    boolean isStoragePermissionGranted() {
       return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
               ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        switch (requestCode){
            case PICK_IMAGE_REQUEST:
                if (resultCode == PICK_IMAGE_REQUEST) {

                    Uri resultData = Uri.parse(data.getStringExtra("result"));
                    imgFile = new File(resultData.getPath());

                    int file_size = Integer.parseInt(String.valueOf(imgFile.length()/1024));

                    if (file_size > 80) {
                        imgFile = Compressor.getDefault(this).compressToFile(imgFile);
                    }

                    Glide.with(this).load(imgFile).asBitmap().into(profilePic);


//                    startCropImageActivity(uri);


                }
                break;

        }



        fbCallbackManager.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_AND_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 1
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    openCamera();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, getText(R.string.error_permission_denied), Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public boolean phone_val(String ph_number) {
        return android.util.Patterns.PHONE.matcher(ph_number).matches();
    }

    private boolean isValidMobile(String phone2) {
        boolean check=false;
        if(!Pattern.matches("[a-zA-Z]+", phone2))
        {
            if(phone2.length() < 13 || phone2.length() > 13)
            {
                check = false;

            }
            else
            {
                check = true;
            }
        }
        else
        {
            check=false;
        }
        return check;
    }

    public boolean validate() {
        boolean valid = true;
        boolean valid_num = true;

        String name = mNameView.getText().toString();
        String number = mNumberView.getText().toString();
        String email = mEmailView.getText().toString();
        String profession = mProfessionView.getText().toString().trim();
        String password = mPasswordView.getText().toString();
        String cPassword = cPasswordView.getText().toString();
        String brifIntro = mInfoView.getText().toString();
        String detail = mDetailView.getText().toString();

        number = "+91"+number;

        if (name.isEmpty() || name.length() < 3) {
            mNameView.setError(getString(R.string.error_enter_valid_name));
            mNameView.requestFocus();
            valid = false;
            return valid;
        } else {
            mNameView.setError(null);
        }


        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            // phone must begin with '+'
            Phonenumber.PhoneNumber numberProto = phoneUtil.parse(number, "");
            mNumberView.setError(null);
        } catch (NumberParseException e) {
            mNumberView.setError("Enter a valid Mobile re Exception");
            mNumberView.requestFocus();

            valid = false;
            System.err.println("NumberParseException was thrown: " + e.toString());
        }

        valid_num = phone_val(number);

        if (valid_num) {
            mNumberView.setError(null);
        }
        else {
            mNumberView.setError(getString(R.string.error_enter_valid_mobile));
            mNumberView.requestFocus();
            valid =false;
            return valid;
        }
        valid_num = isValidMobile(number);
        if (valid_num) {
            mNumberView.setError(null);
        }
        else {
            mNumberView.setError(getString(R.string.error_enter_valid_mobile));
            mNumberView.requestFocus();
            valid =false;
            return valid;
        }



        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmailView.setError(getString(R.string.error_enter_valid_email));
            mEmailView.requestFocus();
            valid = false;
            return valid;
        } else {
            mEmailView.setError(null);
        }

        if (profession.isEmpty() || profession.length() <1) {
            mProfessionView.setError(getString(R.string.error_select_profession));
            mProfessionView.requestFocus();
            valid =  false;
            return valid;
        }else {
            mProfessionView.setError(null);
        }


        if (password.isEmpty() || password.length() < 6 || password.length() > 16) {
            mPasswordView.setError(getString(R.string.error_password_between));
            mPasswordView.requestFocus();
            valid = false;
            return valid;
        } else {
            mPasswordView.setError(null);
        }

        if (!cPassword.equals(password)) {
            cPasswordView.setError(getString(R.string.error_password_not_match));
            cPasswordView.requestFocus();
            valid = false;
            return valid;
        } else {
            cPasswordView.setError(null);
        }

        if (brifIntro.isEmpty() || brifIntro.length() < 1) {
            mInfoView.setError(getString(R.string.error_field_required));
            mInfoView.requestFocus();
            valid = false;
            return valid;
        } else {
            mInfoView.setError(null);
        }

        return valid;
    }

    public void signUp() {
        if (!validate()) {
            return;
        }

        if (imgFile == null) {
            Toast.makeText(this, getText(R.string.error_select_pic), Toast.LENGTH_SHORT).show();
            return;
        }

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.message_please_wait));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();



        signUpBtn.setEnabled(false);

        String name = mNameView.getText().toString();
        String number = mNumberView.getText().toString();
        String email = mEmailView.getText().toString();
        String profession = mProfessionView.getText().toString().trim();
        String password = mPasswordView.getText().toString();
        String brifIntro = mInfoView.getText().toString();
        String detail = mDetailView.getText().toString();
        String privacy = mNumberView.getTag().toString();


        SignUpData data = new SignUpData(name, email,profession,password, number,privacy, brifIntro, detail, null,null, null,imgFile);
        SignUpRequest request = new SignUpRequest(this,data,this);

        request.executeRequest();


    }

    private void onSignUpFailed(String errorMsg) {
        signUpBtn.setEnabled(true);
        Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onSignUpResponse(CommonRequest.ResponseCode res, SignUpData data) {
        mProgressDialog.dismiss();
        switch (res) {
            case COMMON_RES_SUCCESS:
                Toast.makeText(this, getText(R.string.message_registration_successfully), Toast.LENGTH_SHORT).show();
                onSignUpSuccess(data);
                break;
            case COMMON_RES_CONNECTION_TIMEOUT:
                onSignUpFailed("Connection timeout");
                break;
            case COMMON_RES_FAILED_TO_CONNECT:
                onSignUpFailed("No internet connection");
                break;
            case COMMON_RES_INTERNAL_ERROR:
                break;
            case COMMON_RES_SERVER_ERROR_WITH_MESSAGE:
                try {
                    JSONObject errorObject = new JSONObject(data.getmErrorMessage());
                    if (errorObject.getInt("status") == 2) {
                        onSignUpFailed(getString(R.string.error_face_image));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
//                    onSignUpFailed("Something went wrong");
                    onSignUpFailed(data.getmErrorMessage());
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
    }

    private void onSignUpSuccess(SignUpData data) {
        //TODO: Implement signup success logic here
        HomeActivity.mLoginToken = data.getmToken();
        HomeActivity.mUserId = data.getmUserId();
        HomeActivity.mPicUrl = data.getPicUrl();
        HomeActivity.mUserName = data.getmName();
        session.createLoginSession(HomeActivity.mLoginToken,HomeActivity.mUserId, HomeActivity.mUserName, HomeActivity.mPicUrl, HomeActivity.mLastKnownLocation);

        AppPreferences.getInstance(this).setMobiruckSignupPostback(true);//postback true

        Intent returnIntent = new Intent();
        returnIntent.putExtra("result",true);
        setResult(10,returnIntent);
        finish();
    }


    FacebookCallback<LoginResult> fbCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {

            Log.d("facebook","userId: "+loginResult.getAccessToken().getUserId());
            Log.d("facebook","token: "+loginResult.getAccessToken().getToken());


            if (!fbTokenTracker.isTracking()) {
                fbTokenTracker.startTracking();
            }

            GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                @Override
                public void onCompleted(JSONObject object, GraphResponse response) {
                    // Get facebook data from login
                    try {
                        String fbName = object.getString("name");
                        String fbEmail = object.getString("email");
                        String companyName = null;
                        String workLocation;
                        String workPosition = null;
//                        String fbGender = object.getString("gender");
//                        String fbAbout = object.getString("about");
//                        String fbRelationship_status = object.getString("relationship_status");
//                        String fbBirthaday = object.getString("birthday");

                        try {
                            JSONArray workArray = object.getJSONArray("work");
                            if (workArray.length() != 0){
                                companyName = workArray.getJSONObject(0).getJSONObject("employer").getString("name");
                                workLocation = workArray.getJSONObject(0).getJSONObject("location").getString("name");
                                workPosition = workArray.getJSONObject(0).getJSONObject("position").getString("name");

                                mInfoView.setText("I am "+ workPosition + " at " + companyName + ".");
                                mProfessionView.append(workPosition);
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                        }




                        com.facebook.Profile fbProfile = Profile.getCurrentProfile();
                        if (fbProfile == null){
                            Toast.makeText(SignUpActivity.this, getText(R.string.error_something_went_wrong_try_again), Toast.LENGTH_SHORT).show();
                            return;
                        }


                        Uri picUrl = fbProfile.getProfilePictureUri(200,150);
                        URL url = null;
                        try {
                            url = new URL(picUrl.toString());
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }



                        mNameView.setText(fbName);
                        mEmailView.setText(fbEmail);

//                        mDetailView.setText(fbAbout);
                        profilePic.setImageResource(0);
                        Picasso.with(SignUpActivity.this).load(picUrl).placeholder(R.drawable.ic_user).error(R.drawable.ic_user).into(profilePic);
//                        profilePic.setImageUrl(picUrl.toString(), VolleySingleton.getInstance(AppController.getAppContext()).getImageLoader());


                        new DownloadFileFromURL().execute(picUrl.toString());

//                        profilePic.setImageBitmap(profilePicBitmap);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }


            });

            Bundle parameters = new Bundle();
            parameters.putString("fields", "id, name, email, gender, birthday, location, about, relationship_status, work"); // Parámetros que pedimos a facebook
            request.setParameters(parameters);
            request.executeAsync();




        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onError(FacebookException error) {


            Toast.makeText(SignUpActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            Log.d("fbError",error.getMessage());


        }
    };


    /**
     * Background Async Task to download file
     * */
    private class DownloadFileFromURL extends AsyncTask<String, String, String> {
        String rootDir = getFilesDir().getAbsolutePath();
        String fileName;
        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(SignUpActivity.this);
            mProgressDialog.setMessage(getString(R.string.message_please_wait));
            mProgressDialog.show();
            LoginManager.getInstance().logOut();
        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            fileName = System.currentTimeMillis()+"_fb.jpg";
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();
                // getting file length
                int lenghtOfFile = conection.getContentLength();

                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                // Output stream to write file
                OutputStream output = openFileOutput(fileName,Context.MODE_PRIVATE);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress(""+(int)((total*100)/lenghtOfFile));

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
         * */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            mProgressDialog.dismiss();
        }

        /**
         * After completing background task
         * Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            mProgressDialog.dismiss();

            // Displaying downloaded image into image view
            // Reading image path from sdcard
            String imagePath = rootDir+fileName;
            imgFile = new File(imagePath);


//            Toast.makeText(getContext(), ""+imagePath, Toast.LENGTH_SHORT).show();
            // setting downloaded into image view
//            my_image.setImageDrawable(Drawable.createFromPath(imagePath));
        }

    }
}
