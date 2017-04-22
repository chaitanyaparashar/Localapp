package com.localapp.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.localapp.R;
import com.localapp.data.Profile;
import com.localapp.request.CommonRequest;
import com.localapp.request.GetProfileRequest;
import com.localapp.request.UpdateProfileRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

public class UpdateActivity extends AppCompatActivity implements GetProfileRequest.GetProfileRequestCallback,UpdateProfileRequest.UpdateProfileResponseCallback {

    LinearLayout personalLayout, aboutLayout;

    EditText mNameView, mNumberView,mProfessionView, mEmailView, mInfoView, mDetailView;
    private int whichUpdate;
    //The "x" and "y" position of the "Show Button" on screen.
    Point p;
    public static final int REQUEST_PERSONAL = 0;
    public static final int REQUEST_ABOUT = 1;

    boolean numberVisibility = true;
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    ArrayList<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    HashMap<String, List<Drawable>> listIconChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        whichUpdate = getIntent().getIntExtra("request",REQUEST_PERSONAL);


        personalLayout = (LinearLayout) findViewById(R.id._personal);
        aboutLayout = (LinearLayout) findViewById(R.id._about);

        mNameView = (EditText) findViewById(R.id.input_name);
        mNumberView = (EditText) findViewById(R.id.input_phoneNumber);
        mEmailView = (EditText) findViewById(R.id.input_email);
        mProfessionView = (EditText) findViewById(R.id.input_profession);
        mInfoView = (EditText) findViewById(R.id.input_brief_intro);
        mDetailView = (EditText) findViewById(R.id.input_details_des);

        mProfessionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(UpdateActivity.this);
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
                            mNumberView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_phone, 0, R.drawable.ic_password_hidden, 0);
                            numberVisibility = false;
                            mNumberView.setTag("1");
                        }else {
                            mNumberView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_phone, 0, R.drawable.ic_password_visible, 0);
                            numberVisibility = true;
                            mNumberView.setTag("0");
                        }

                    }
                }
                return false;
            }
        });



        if (whichUpdate == REQUEST_PERSONAL) {
            personalLayout.setVisibility(View.VISIBLE);
            aboutLayout.setVisibility(View.GONE);
        }else {
            personalLayout.setVisibility(View.GONE);
            aboutLayout.setVisibility(View.VISIBLE);
        }

        profileRequest();
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


        // Listview on child click listener
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                // TODO Auto-generated method stub
                Toast.makeText(
                        getApplicationContext(),
                        listDataHeader.get(groupPosition)
                                + " : "
                                + listDataChild.get(
                                listDataHeader.get(groupPosition)).get(
                                childPosition), Toast.LENGTH_SHORT)
                        .show();
                return false;
            }
        });



        // Creating the PopupWindow
        final PopupWindow popup = new PopupWindow(context);
        popup.setContentView(layout);
        popup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                String items = "";
                for(int mGroupPosition =0; mGroupPosition < listAdapter.getGroupCount(); mGroupPosition++)
                {
                    items = items +  listAdapter.getItemAtPostion(mGroupPosition);

                }
                if (items.length() > 2) {
                    mProfessionView.setText(items.substring(0,items.length()-1));
                }

            }
        });


        popup.setFocusable(true);


        // Clear the default translucent background
        popup.setBackgroundDrawable(new BitmapDrawable());


        popup.showAsDropDown(mProfessionView);

    }


    @Override
    protected void onPause() {
        super.onPause();
        LinearLayout mainLayout;
        mainLayout = (LinearLayout)findViewById(R.id.activity_update);
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mainLayout.getWindowToken(), 0);
    }

    public void onBack(View view) {
        onBackPressed();
    }

    public void onUpdate(View v) {
        if (!validate()) {
            Toast.makeText(this, "Check input field", Toast.LENGTH_SHORT).show();
            return;
        }

        profileUpdateRequest();
    }

    private void profileRequest() {
        Profile mProfile = new Profile(HomeActivity.mUserId);
        mProfile.setuToken(HomeActivity.mLoginToken);

        GetProfileRequest request = new GetProfileRequest(this,mProfile,this);
        request.executeRequest();
    }

    private void profileUpdateRequest() {
        Profile mProfile = new Profile(HomeActivity.mUserId);
        mProfile.setuToken(HomeActivity.mLoginToken);

        if (whichUpdate == REQUEST_PERSONAL) {
            mProfile.setuName(mNameView.getText().toString().trim());
            mProfile.setuMobile(mNumberView.getText().toString().trim());
            mProfile.setuEmail(mEmailView.getText().toString().trim());
            mProfile.setuPrivacy(mNumberView.getTag().toString());
        }else {
            mProfile.setProfession(mProfessionView.getText().toString().trim());
            mProfile.setuSpeciality(mInfoView.getText().toString().trim());
            mProfile.setuNotes(mDetailView.getText().toString().trim());
        }

        UpdateProfileRequest request = new UpdateProfileRequest(this,mProfile,this);
        request.executeRequest();

    }

    private void setProfileData(Profile mProfileData) {
        mNameView.setText(mProfileData.getuName());
        mNumberView.setText(mProfileData.getuMobile());
        mEmailView.setText(mProfileData.getuEmail());
        mProfessionView.setText(mProfileData.getProfession());
        mInfoView.setText(mProfileData.getuSpeciality());
        mDetailView.setText(mProfileData.getuNotes());

        if (mProfileData.getuPrivacy().equals("0")) {
            mNumberView.setTag("0");
            mNumberView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_phone, 0, R.drawable.ic_password_visible, 0);
            numberVisibility = true;
        }else {
            mNumberView.setTag("1");
            mNumberView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_phone, 0, R.drawable.ic_password_hidden, 0);
            numberVisibility = false;
        }
    }

    @Override
    public void onProfileResponse(CommonRequest.ResponseCode responseCode, Profile mProfile) {
        switch (responseCode) {
            case COMMON_RES_SUCCESS:
                setProfileData(mProfile);
                break;
        }
    }

    @Override
    public void onUpdateProfileResponse(CommonRequest.ResponseCode responseCode) {
        if (responseCode == CommonRequest.ResponseCode.COMMON_RES_SUCCESS) {
            Toast.makeText(this, "update success", Toast.LENGTH_SHORT).show();
            Intent returnIntent = new Intent();
            returnIntent.putExtra("result",true);
            setResult(10,returnIntent);
            finish();
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
        String profession = mProfessionView.getText().toString();
        String brifIntro = mInfoView.getText().toString();
        String detail = mDetailView.getText().toString();

        number = "+91"+number;

        if (whichUpdate == REQUEST_PERSONAL) {
            if (name.isEmpty() || name.length() < 3) {
                mNameView.setError("enter a valid name");
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

                valid = false;
                System.err.println("NumberParseException was thrown: " + e.toString());
            }

            valid_num = phone_val(number);

            if (valid_num) {
                mNumberView.setError(null);
            }
            else {
                mNumberView.setError("Enter a valid Mobile Number");
                valid =false;
            }
            valid_num = isValidMobile(number);
            if (valid_num) {
                mNumberView.setError(null);
            }
            else {
                mNumberView.setError("Enter a valid Mobile Number");
                valid =false;
            }



            if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                mEmailView.setError("enter a valid email address");
                valid = false;
                return valid;
            } else {
                mEmailView.setError(null);
            }
        }else {

            if (profession.isEmpty()) {
                mProfessionView.setError("Please select your profession");
                valid =  false;
                return valid;
            }else {
                mProfessionView.setError(null);
            }

            if (brifIntro.isEmpty() || brifIntro.length() < 1) {
                mInfoView.setError("Field Required");
                valid = false;
                return valid;
            } else {
                mInfoView.setError(null);
            }
        }

        /*if (spinner.getSelectedItemPosition() == 0){
            onSignUpFailed("Please select your profession");
            valid = false;
            return valid;
        }*/


        /*if (password.isEmpty() || password.length() < 6 || password.length() > 16) {
            mPasswordView.setError("between 6 and 16 alphanumeric characters");
            valid = false;
            return valid;
        } else {
            mPasswordView.setError(null);
        }

        if (!cPassword.equals(password)) {
            cPasswordView.setError("Password not matched");
            valid = false;
            return valid;
        } else {
            cPasswordView.setError(null);
        }*/



        return valid;
    }

}
