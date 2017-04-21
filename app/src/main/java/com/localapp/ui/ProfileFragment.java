package com.localapp.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.siyamed.shapeimageview.CircularImageView;
import com.localapp.R;
import com.localapp.appcontroller.AppController;
import com.localapp.data.LoginData;
import com.localapp.data.Profile;
import com.localapp.login_session.SessionManager;
import com.localapp.request.CommonRequest;
import com.localapp.request.GetProfileRequest;
import com.localapp.request.LoginRequest;
import com.localapp.request.helper.VolleySingleton;
import com.squareup.picasso.Picasso;

import static com.localapp.ui.UpdateActivity.REQUEST_ABOUT;
import static com.localapp.ui.UpdateActivity.REQUEST_PERSONAL;


/**
 * Created by 4 way on 02-03-2017.
 */

public class ProfileFragment extends Fragment implements LoginRequest.LoginResponseCallback,GetProfileRequest.GetProfileRequestCallback{
    private LinearLayout profileLayout,loginLayout;
    private CircularImageView userPic;
    private ImageButton camButton;
    private ImageView editPersonal,editAbout;
    private TextView uNmaeTextView,uNumberTextView,
            uEmailTextView,uProdessionTextView,uBreifInfo,
            uDetailTextView;
    private Button logoutBtn,shareBtn;

    //Login
    private EditText _email, _password;
    private Button _loginBtn, _signupBtn;
    private TextView _forgotPass;



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
        loginLayout = (LinearLayout) view.findViewById(R.id.login_layout);



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
        logoutBtn.setOnClickListener(onClickListener);
        shareBtn.setOnClickListener(onClickListener);
        editAbout.setOnClickListener(onClickListener);
        editPersonal.setOnClickListener(onClickListener);
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
                case R.id.link_forgotPassword:
                    break;
                case R.id.cam_btn:
                    break;
                case R.id.edit_personal_info: startActivityForResult(new Intent(getContext(),UpdateActivity.class).putExtra("request",REQUEST_PERSONAL),UPDATE_REQUEST_CODE);
                    break;
                case R.id.edit_about: startActivityForResult(new Intent(getContext(),UpdateActivity.class).putExtra("request",REQUEST_ABOUT),UPDATE_REQUEST_CODE);
                    break;
                case R.id._logout_btn: onLogout();
                    break;

                case R.id._share_btn:
                    String shareBody = "https://play.google.com/store/apps/details?id=com.localapp";
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Check out \"Localapp\"");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                    startActivity(Intent.createChooser(sharingIntent, "Share \"Localapp\" via"));

                    break;
            }
        }
    };

    public void setProfileData(Profile profile) {
        Picasso.with(AppController.getAppContext()).load(profile.getuPictureURL()).placeholder(R.drawable.ic_user).into(userPic);
        uNmaeTextView.setText(profile.getuName());
        uNumberTextView.setText(profile.getuMobile());
        uEmailTextView.setText(profile.getuEmail());
        uProdessionTextView.setText(profile.getProfession());
        uBreifInfo.setText(profile.getuSpeciality());
        uDetailTextView.setText(profile.getuNotes());
    }

    public void setProfileData(LoginData profileData) {
        Picasso.with(AppController.getAppContext()).load(profileData.getPicUrl()).placeholder(R.drawable.ic_user).into(userPic);
        uNmaeTextView.setText(profileData.getmName());
        uNumberTextView.setText(profileData.getmMobile());
        uEmailTextView.setText(profileData.getEmail());
        uProdessionTextView.setText(profileData.getmProfession());
        uBreifInfo.setText(profileData.getmSpeciality());
        uDetailTextView.setText(profileData.getmNotes());
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
        session.logoutUser();
        loginLayout.setVisibility(View.VISIBLE);
        profileLayout.setVisibility(View.GONE);
        userPic.setImageResource(R.drawable.ic_user);
        uNmaeTextView.setText("");
        uNumberTextView.setText("");
        uEmailTextView.setText("");
        uProdessionTextView.setText("");
        uBreifInfo.setText("");
        uDetailTextView.setText("");
        HomeActivity.mUserId = "";
        HomeActivity.mLoginToken = "";
        HomeActivity.mPicUrl = null;
    }

    private void onLoginSuccess(LoginData data) {
        //TODO: -----
        HomeActivity.mLoginToken = data.getAccessToken();
        HomeActivity.mUserId = data.getUserId();
        HomeActivity.mPicUrl = data.getPicUrl();
        session.createLoginSession(HomeActivity.mLoginToken,HomeActivity.mUserId, HomeActivity.mPicUrl, HomeActivity.mLastKnownLocation);

        setProfileData(data);

        profileLayout.setVisibility(View.VISIBLE);
        loginLayout.setVisibility(View.GONE);

    }

    private void toast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
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
        Profile mProfile = new Profile(HomeActivity.mUserId);
        mProfile.setuToken(HomeActivity.mLoginToken);

        GetProfileRequest request = new GetProfileRequest(getContext(),mProfile,this);
        request.executeRequest();
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
                toast("No internet connection");
                break;
            case COMMON_RES_INTERNAL_ERROR:
                break;
            case COMMON_RES_SERVER_ERROR_WITH_MESSAGE:
                toast(mProfile.getErrorMsg());
                break;
        }
    }
}
