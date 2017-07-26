package com.localapp.ui.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.SwitchCompat;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.localapp.R;
import com.localapp.appcontroller.AppController;
import com.localapp.background.ConnectivityReceiver;
import com.localapp.camera.Camera2Activity;
import com.localapp.compressor.Compressor;
import com.localapp.models.LoginData;
import com.localapp.models.Profile;
import com.localapp.network.UpdateProfilePicRequest;
import com.localapp.preferences.AppPreferences;
import com.localapp.preferences.SessionManager;
import com.localapp.network.helper.CommonRequest;
import com.localapp.network.ForgetPasswordRequest;
import com.localapp.network.GetProfileRequest;
import com.localapp.network.LoginRequest;
import com.localapp.network.UpdateProfileRequest;
import com.localapp.ui.activities.HomeActivity;
import com.localapp.ui.activities.LoginActivity;
import com.localapp.ui.activities.SignUpActivity;
import com.localapp.ui.activities.UpdateActivity;
import com.localapp.utils.Utility;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;

import static com.localapp.ui.activities.SignUpActivity.CAMERA_PERMISSIONS;
import static com.localapp.ui.activities.SignUpActivity.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_AND_CAMERA;
import static com.localapp.ui.activities.SignUpActivity.PICK_IMAGE_REQUEST;
import static com.localapp.ui.activities.UpdateActivity.REQUEST_ABOUT;
import static com.localapp.ui.activities.UpdateActivity.REQUEST_ALL;
import static com.localapp.ui.activities.UpdateActivity.REQUEST_PERSONAL;


/**
 * Created by 4 way on 02-03-2017.
 */

public class ProfileFragment extends Fragment implements LoginRequest.LoginResponseCallback,GetProfileRequest.GetProfileRequestCallback,UpdateProfileRequest.UpdateProfileResponseCallback,
        ForgetPasswordRequest.ForgetPasswordRequestCallback, Target,ConnectivityReceiver.ConnectivityReceiverListener,UpdateProfilePicRequest.UpdateProfilePicResponseCallback{
    private LinearLayout profileLayout;
    private RelativeLayout loginLayout;
    private CircularImageView userPic;
    private ImageButton camButton;
    private ImageView editPersonal,editAbout;
    private TextView uNmaeTextView,uNumberTextView,
            uEmailTextView,uProdessionTextView,uBreifInfo,
            uDetailTextView;
    private Button logoutBtn,shareBtn;
    private SwitchCompat mBroadcastSwitch;

    private RelativeLayout backgroundLayout;

    //Login
    private EditText _email, _password;
    private Button _loginBtn, _signupBtn;
    private TextView _forgotPass;
    private ProgressBar mPicProgressBar;

    public static Profile myProfile;

    private File imgFile;



    ProgressDialog mProgressDialog;

    SessionManager session;

    public final static int SIGN_UP_REQUEST_CODE = 110;
    public final static int UPDATE_REQUEST_CODE = 111;


    public ProfileFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        session = new SessionManager(getActivity());

        setupView(view);

        if (session.isLoggedIn()) {
            profileLayout.setVisibility(View.VISIBLE);
            loginLayout.setVisibility(View.GONE);
            profileRequest();
        }else {
            myProfile = null;
            loginLayout.setVisibility(View.VISIBLE);
            profileLayout.setVisibility(View.GONE);
        }



        return view;
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (menuVisible && getContext()!=null && !session.isLoggedIn()) {
//            startActivityForResult(new Intent(getContext(),SignUpActivity.class),0);
        }

    }

    public void setupView(View view) {

        //layouts
        profileLayout = (LinearLayout) view.findViewById(R.id.profile_layout);
        loginLayout = (RelativeLayout) view.findViewById(R.id.login_layout);
        backgroundLayout = (RelativeLayout) view.findViewById(R.id.rl_fm_profile);



        //login
        _email = (EditText) view.findViewById(R.id.input_email);
        _password = (EditText) view.findViewById(R.id.input_password);
        _loginBtn = (Button)  view.findViewById(R.id.btn_login);
        _signupBtn = (Button) view.findViewById(R.id.link_signup);
        _forgotPass  = (TextView) view.findViewById(R.id.link_forgotPassword);
        _loginBtn.setOnClickListener(onClickListener);
        _signupBtn.setOnClickListener(onClickListener);
        _forgotPass.setOnClickListener(onClickListener);

        //user profile
        mPicProgressBar = (ProgressBar) view.findViewById(R.id.image_pic_progress);
        userPic = (CircularImageView) view.findViewById(R.id.image_pic);
        camButton = (ImageButton) view.findViewById(R.id.cam_btn);
        editPersonal = (ImageView) view.findViewById(R.id.edit_personal_info);
        editAbout = (ImageView) view.findViewById(R.id.edit_about);
        uNmaeTextView = (TextView) view.findViewById(R.id._name);
        uNumberTextView = (TextView) view.findViewById(R.id._phone);
        uEmailTextView = (TextView) view.findViewById(R.id._email);
        uProdessionTextView = (TextView) view.findViewById(R.id._profession);
        uBreifInfo = (TextView) view.findViewById(R.id._brief_intro);
        uDetailTextView = (TextView) view.findViewById(R.id._details);
        logoutBtn = (Button) view.findViewById(R.id._logout_btn);
        shareBtn = (Button) view.findViewById(R.id._share_btn);
        mBroadcastSwitch = (SwitchCompat) view.findViewById(R.id.set_broadcast_setting);
        camButton.setOnClickListener(onClickListener);
        logoutBtn.setOnClickListener(onClickListener);
        shareBtn.setOnClickListener(onClickListener);
        editAbout.setOnClickListener(onClickListener);
        editPersonal.setOnClickListener(onClickListener);


        if (AppPreferences.getInstance(AppController.getAppContext()).isBroadcastNotificationOn()) {
            mBroadcastSwitch.setChecked(true);
        }else {
            mBroadcastSwitch.setChecked(false);
        }
        mBroadcastSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    AppPreferences.getInstance(AppController.getAppContext()).setBroadcastNotificationOn();
                }else {
                    AppPreferences.getInstance(AppController.getAppContext()).setBroadcastNotificationOff();
                }
            }
        });
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.btn_login: onLogin();
                    break;
                case R.id.link_signup: startActivityForResult(new Intent(getContext(),SignUpActivity.class),SIGN_UP_REQUEST_CODE);
                    break;
                case R.id.link_forgotPassword: onForgetPassword();
                    break;
                case R.id.cam_btn: {
                    if (Utility.hasPermissionsGranted(getActivity(),CAMERA_PERMISSIONS)) {
                        openCamera();
                    }else {
                        requestPermissions(CAMERA_PERMISSIONS,MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_AND_CAMERA);
                    }
                }
                    break;
                case R.id.edit_personal_info: startActivityForResult(new Intent(getContext(),UpdateActivity.class).putExtra("request",REQUEST_PERSONAL),UPDATE_REQUEST_CODE);
                    break;
                case R.id.edit_about: startActivityForResult(new Intent(getContext(),UpdateActivity.class).putExtra("request",REQUEST_ABOUT),UPDATE_REQUEST_CODE);
                    break;
                case R.id._logout_btn: onLogout();
                    break;

                case R.id._share_btn: onAppShare();
                    break;
            }
        }
    };

    private void openCamera(){
        Intent intent = new Intent(getContext(),Camera2Activity.class);
        intent.putExtra("requestCode", PICK_IMAGE_REQUEST);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    public void setProfileData(Profile profile) {
        Picasso.with(AppController.getAppContext()).load(profile.getuPictureURL()).placeholder(R.drawable.ic_user).into(userPic);
        Picasso.with(AppController.getAppContext()).load(profile.getuPictureURL()).placeholder(R.drawable.ic_user).into(this);

        if (HomeActivity.mUserName == null || HomeActivity.mUserName.equals("")) {
            HomeActivity.mUserName = profile.getuName();
            session.createLoginSession(HomeActivity.mLoginToken,HomeActivity.mUserId, HomeActivity.mUserName, HomeActivity.mPicUrl, HomeActivity.mLastKnownLocation);
        }

        uNmaeTextView.setText(profile.getuName());
        uEmailTextView.setText(profile.getuEmail());


        if (profile.getuMobile() !=null && !profile.getuMobile().equals("null") && !profile.getuMobile().trim().isEmpty()) {
            uNumberTextView.setText(profile.getuMobile());
            uNumberTextView.setVisibility(View.VISIBLE);
        }else {
            uNumberTextView.setVisibility(View.GONE);
            if (AppPreferences.getInstance(AppController.getAppContext()).getLaunchCount() < 2) {
                HomeActivity.mViewPager.setCurrentItem(3);
                startActivityForResult(new Intent(getContext(), UpdateActivity.class).putExtra("request", REQUEST_ALL), UPDATE_REQUEST_CODE);
            }
        }



        if (profile.getProfession() !=null && !profile.getProfession().equals("null") && !profile.getProfession().trim().isEmpty()) {
            uProdessionTextView.setText(profile.getProfession());
            uProdessionTextView.setVisibility(View.VISIBLE);
        }else {
            uProdessionTextView.setVisibility(View.GONE);
        }


        if (profile.getuSpeciality() !=null && !profile.getuSpeciality().equals("null") && !profile.getuSpeciality().trim().isEmpty()) {
            uBreifInfo.setText(profile.getuSpeciality());
            uBreifInfo.setVisibility(View.VISIBLE);
        }else {
            uBreifInfo.setVisibility(View.GONE);
        }


        if (profile.getuNotes() !=null && !profile.getuNotes().equals("null") && !profile.getuNotes().trim().isEmpty()) {
            uDetailTextView.setText(profile.getuNotes());
            uDetailTextView.setVisibility(View.VISIBLE);
        }else {
            uDetailTextView.setVisibility(View.GONE);
        }

        fcmTokenUpdateRequest();

        myProfile = profile;

    }

    public void setProfileData(LoginData profileData) {
        Picasso.with(AppController.getAppContext()).load(profileData.getPicUrl()).placeholder(R.drawable.ic_user).into(userPic);
        Picasso.with(AppController.getAppContext()).load(profileData.getPicUrl()).placeholder(R.drawable.ic_user).into(this);

        uNmaeTextView.setText(profileData.getmName());
        uEmailTextView.setText(profileData.getEmail());

        if (profileData.getmMobile() !=null && !profileData.getmMobile().equals("null") && !profileData.getmMobile().trim().isEmpty()) {
            uNumberTextView.setText(profileData.getmMobile());
            uNumberTextView.setVisibility(View.VISIBLE);
        }else {
            uNumberTextView.setVisibility(View.GONE);
        }



        if (profileData.getmProfession() !=null && !profileData.getmProfession().equals("null") && !profileData.getmProfession().trim().isEmpty()) {
            uProdessionTextView.setText(profileData.getmProfession());
            uProdessionTextView.setVisibility(View.VISIBLE);
        }else {
            uProdessionTextView.setVisibility(View.GONE);
        }


        if (profileData.getmSpeciality() !=null && !profileData.getmSpeciality().equals("null") && !profileData.getmSpeciality().trim().isEmpty()) {
            uBreifInfo.setText(profileData.getmSpeciality());
            uBreifInfo.setVisibility(View.VISIBLE);
        }else {
            uBreifInfo.setVisibility(View.GONE);
        }


        if (profileData.getmNotes() !=null && !profileData.getmNotes().equals("null") && !profileData.getmNotes().trim().isEmpty()) {
            uDetailTextView.setText(profileData.getmNotes());
            uDetailTextView.setVisibility(View.VISIBLE);
        }else {
            uDetailTextView.setVisibility(View.GONE);
        }

        myProfile = new Profile(profileData.getUserId());
        myProfile.setuEmail(profileData.getEmail());
        myProfile.setuMobile(profileData.getmMobile());
    }


    private void setPallet(final Bitmap bitmap) {
        Palette.from(bitmap)
                .generate(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
                        Palette.Swatch textSwatch = palette.getVibrantSwatch();
                        if (textSwatch != null) {
                            backgroundLayout.setBackgroundColor(textSwatch.getRgb());
                        }/*else {
                            new PublicProfileActivity.DoInBackground().execute(bitmap);
                        }*/

                    }


                });
    }

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
        LoginRequest request = new LoginRequest(getActivity(),loginData,this);
        request.executeRequest();
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage("Please wait...");
        mProgressDialog.show();
    }

    private void onLogout() {
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage("Please wait...");
        mProgressDialog.show();
        session.logoutUser();
        LoginManager.getInstance().logOut();
        /*loginLayout.setVisibility(View.VISIBLE);
        profileLayout.setVisibility(View.GONE);
        userPic.setImageResource(R.drawable.ic_user);
        uNmaeTextView.setText("");
        uNumberTextView.setText("");
        uEmailTextView.setText("");
        uProdessionTextView.setText("");
        uBreifInfo.setText("");
        uDetailTextView.setText("");*/
        HomeActivity.mUserId = "";
        HomeActivity.mLoginToken = "";
        HomeActivity.mPicUrl = null;

        startActivity(new Intent(getContext(),LoginActivity.class));
        getActivity().finish();

        mProgressDialog.dismiss();
    }

    private void onForgetPassword() {
        String mEmail = _email.getText().toString();
        if (mEmail.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(mEmail).matches()) {
            _email.setError("enter a valid email address");
            return;
        }

        ForgetPasswordRequest request = new ForgetPasswordRequest(getContext(),mEmail,this);
        request.executeRequest();
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage("Please wait...");
        mProgressDialog.show();
    }


    private void onLoginSuccess(LoginData data) {
        //TODO: -----
        HomeActivity.mLoginToken = data.getAccessToken();
        HomeActivity.mUserId = data.getUserId();
        HomeActivity.mPicUrl = data.getPicUrl();
        HomeActivity.mUserName = data.getmName();
        session.createLoginSession(HomeActivity.mLoginToken,HomeActivity.mUserId, HomeActivity.mUserName, HomeActivity.mPicUrl, HomeActivity.mLastKnownLocation);

        setProfileData(data);
        fcmTokenUpdateRequest();

        profileLayout.setVisibility(View.VISIBLE);
        loginLayout.setVisibility(View.GONE);

    }


    private void onAppShare() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Check out \"Localapp\"");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.invite_text));
        startActivity(Intent.createChooser(sharingIntent, "Share \"Localapp\" via"));
    }

    private void toast(String msg) {
        try {
            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        }catch (NullPointerException ignore){

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SIGN_UP_REQUEST_CODE:
                if (resultCode != Activity.RESULT_CANCELED && data.getBooleanExtra("result",false)) {
                    profileRequest();
                }
                break;
            case UPDATE_REQUEST_CODE:
                if (resultCode != Activity.RESULT_CANCELED && data.getBooleanExtra("result",false)) {
                    profileRequest();
                }
                break;

            case PICK_IMAGE_REQUEST:
                if (resultCode == PICK_IMAGE_REQUEST) {

                    Uri resultData = Uri.parse(data.getStringExtra("result"));
                    imgFile = new File(resultData.getPath());

                    int file_size = Integer.parseInt(String.valueOf(imgFile.length()/1024));

                    if (file_size > 80) {
                        imgFile = Compressor.getDefault(getContext()).compressToFile(imgFile);
                    }

//                    Glide.with(this).load(imgFile).asBitmap().into(profilePic);
                    profilePicUpdateRequest(imgFile);

//                    startCropImageActivity(uri);


                }
                break;

        }
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
                    Toast.makeText(getContext(), getText(R.string.error_permission_denied), Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onLoginResponse(CommonRequest.ResponseCode responseCode, LoginData data) {
        mProgressDialog.dismiss();
        switch (responseCode) {
            case COMMON_RES_SUCCESS:
                _email.setText("");
                _password.setText("");
                onLoginSuccess(data);
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
                toast(data.getErrorMessage());
                break;
        }
    }

    private void profileRequest() {
        mPicProgressBar.setVisibility(View.VISIBLE);
        Profile mProfile = new Profile(HomeActivity.mUserId);
        mProfile.setuToken(HomeActivity.mLoginToken);

        GetProfileRequest request = new GetProfileRequest(getContext(),mProfile,this);
        request.executeRequest();
    }

    private void fcmTokenUpdateRequest() {
        String fcm_token = FirebaseInstanceId.getInstance().getToken();
        if (fcm_token != null) {
            Profile profile = new Profile(HomeActivity.mUserId);
            profile.setFcmToken(fcm_token);
            profile.setuToken(HomeActivity.mLoginToken);
            UpdateProfileRequest request = new UpdateProfileRequest(getContext(),profile,this);
            request.executeRequest();
        }

    }

    private void profilePicUpdateRequest(File imgFile) {
        UpdateProfilePicRequest request = new UpdateProfilePicRequest(getContext(),imgFile, this);
        request.executeRequest();
        mPicProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onProfileResponse(CommonRequest.ResponseCode responseCode, Profile mProfile) {
        switch (responseCode) {
            case COMMON_RES_SUCCESS:
                setProfileData(mProfile);
                profileLayout.setVisibility(View.VISIBLE);
                loginLayout.setVisibility(View.GONE);
                break;
            case COMMON_RES_CONNECTION_TIMEOUT:
                toast("Connection timeout");
                break;
            case COMMON_RES_FAILED_TO_CONNECT:
                break;
            case COMMON_RES_INTERNAL_ERROR:
                break;
            case COMMON_RES_SERVER_ERROR_WITH_MESSAGE:
                onLogout();
                break;
        }
    }

    @Override
    public void onUpdateProfileResponse(CommonRequest.ResponseCode responseCode) {

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
    public void onResume() {
        super.onResume();
        View view = getView();

        if (view != null) {
            view.setFocusableInTouchMode(true);
            view.requestFocus();
            view.setOnKeyListener(onKeyListener);
        }

        AppController.getInstance().addConnectivityListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        AppController.getInstance().removeConnectivityListener(this);
    }

    View.OnKeyListener onKeyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if( keyCode == KeyEvent.KEYCODE_BACK && event.getAction()!= KeyEvent.ACTION_DOWN ) {
                HomeActivity.mViewPager.setCurrentItem(0);
                return true;
            }
            return false;
        }
    };


    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        mPicProgressBar.setVisibility(View.GONE);
        setPallet(bitmap);
    }


    @Override
    public void onBitmapFailed(Drawable errorDrawable) {

    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {

    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isConnected) profileRequest();
    }

    @Override
    public void UpdateProfilePicResponse(CommonRequest.ResponseCode responseCode, String picUrl) {
        switch (responseCode) {
            case COMMON_RES_SUCCESS: {
                HomeActivity.mPicUrl = picUrl;
                Picasso.with(AppController.getAppContext()).load(HomeActivity.mPicUrl).placeholder(R.drawable.ic_user).into(userPic);
                Picasso.with(AppController.getAppContext()).load(HomeActivity.mPicUrl).placeholder(R.drawable.ic_user).into(this);


                if (HomeActivity.mUserName == null || HomeActivity.mUserName.equals("")) {
                    session.createLoginSession(HomeActivity.mLoginToken,HomeActivity.mUserId, HomeActivity.mUserName, HomeActivity.mPicUrl, HomeActivity.mLastKnownLocation);
                }
            }
            break;

            default:
                mPicProgressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Update failed", Toast.LENGTH_SHORT).show();


        }
    }
}
