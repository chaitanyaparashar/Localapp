package com.fourway.localapp.ui;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.fourway.localapp.R;
import com.fourway.localapp.data.SignUpData;
import com.fourway.localapp.request.CommonRequest;
import com.fourway.localapp.request.SignUpRequest;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 */
public class SignUpFragment extends Fragment implements SignUpRequest.SignUpResponseCallback {

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 111;
    public static int PICK_IMAGE_REQUEST = 100;

    EditText mNameView, mNumberView, mEmailView,
            mPasswordView, cPasswordView, mInfoView,
            mDetailView,mLocationTypeView;

    Spinner spinner;
    CircularImageView profilePic;

    Button signUpBtn, picUploadBtn;
    File imgFile;

    ProgressDialog mProgressDialog;

    String[] professionString = {"Select Profession", "A", "B", "C", "D"};



    public SignUpFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        signUpBtn = (Button) view.findViewById(R.id._signUp);
        picUploadBtn = (Button) view.findViewById(R.id.upload_btn);

        profilePic = (CircularImageView) view.findViewById(R.id.image_pic);

        mNameView = (EditText) view.findViewById(R.id.input_name);
        mNumberView = (EditText) view.findViewById(R.id.input_phoneNumber);
        mEmailView = (EditText) view.findViewById(R.id.input_email);
        mPasswordView = (EditText) view.findViewById(R.id.input_password);
        cPasswordView = (EditText) view.findViewById(R.id.input_password_c);
        mInfoView = (EditText) view.findViewById(R.id.input_brief_intro);
        mDetailView = (EditText) view.findViewById(R.id.input_details_des);

        spinner = (Spinner) view.findViewById(R.id.spinner);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item,professionString);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });

        picUploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                // Show only images, no videos or anything else
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                // Always show the chooser (if there are multiple options available)
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        permissionsRequestReadExternalStorage();

        // Inflate the layout for this fragment
        return view;
    }

    void permissionsRequestReadExternalStorage()
    {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // No explanation needed, we can request the permission.

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

            // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();
            String path = getRealPathFromURI_API19(getContext(),uri);

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));
                imgFile = new File(path);


                profilePic.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    public String getRealPathFromURI_API19(Context context, Uri uri){


        String filePath = "";
        String wholeID = DocumentsContract.getDocumentId(uri);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = { MediaStore.Images.Media.DATA };

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor;


        cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{ id }, null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }

    }

    public boolean phone_val(String ph_number)
    {
        return android.util.Patterns.PHONE.matcher(ph_number).matches();
    }

    private boolean isValidMobile(String phone2)
    {
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
        String profession = mNameView.getText().toString();
        String password = mPasswordView.getText().toString();
        String cPassword = cPasswordView.getText().toString();
        String brifIntro = mInfoView.getText().toString();
        String detail = mDetailView.getText().toString();

        number = "+91"+number;

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


        if (password.isEmpty() || password.length() < 6 || password.length() > 16) {
            mPasswordView.setError("between 6 and 16 alphanumeric characters");
            valid = false;
            return valid;
        } else {
            mPasswordView.setError(null);
        }

        if (cPassword.equals(password) == false) {
            cPasswordView.setError("Password not matched");
            valid = false;
            return valid;
        } else {
            cPasswordView.setError(null);
        }

        if (brifIntro.isEmpty() || brifIntro.length() < 1) {
            mInfoView.setError("Field Required");
            valid = false;
            return valid;
        } else {
            mInfoView.setError(null);
        }

        return valid;
    }

    public void signUp() {
        if (!validate()) {
            onSignupFailed("Check input field");
            return;
        }

        if (imgFile == null) {
            Toast.makeText(getContext(), "Please select a pic", Toast.LENGTH_SHORT).show();
        }

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage("Please wait...");
        mProgressDialog.show();



        signUpBtn.setEnabled(false);

        String name = mNameView.getText().toString();
        String number = mNumberView.getText().toString();
        String email = mEmailView.getText().toString();
        String profession = mNameView.getText().toString();
        String password = mPasswordView.getText().toString();
        String brifIntro = mInfoView.getText().toString();
        String detail = mDetailView.getText().toString();


        SignUpData data = new SignUpData(name, email,password, number, brifIntro, detail, null,null, null,imgFile);
        SignUpRequest request = new SignUpRequest(getContext(),data,this);

        request.executeRequest();


    }

    private void onSignupFailed(String errorMsg) {
        signUpBtn.setEnabled(true);
        Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onSignUpResponse(CommonRequest.ResponseCode res) {
        mProgressDialog.dismiss();
        switch (res) {
            case COMMON_RES_SUCCESS:
                Toast.makeText(getActivity(), "Registration successfully", Toast.LENGTH_SHORT).show();
                onSignUpSuccess();
                break;
            case COMMON_RES_CONNECTION_TIMEOUT:
                onSignupFailed("Connection timeout");
                break;
            case COMMON_RES_FAILED_TO_CONNECT:
                onSignupFailed("No internet connection");
                break;
            case COMMON_RES_INTERNAL_ERROR:
                break;
            case COMMON_RES_SERVER_ERROR_WITH_MESSAGE:
                break;
        }

    }

    private void onSignUpSuccess() {
        //TODO: Implement signup success login here
    }
}
