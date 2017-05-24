package com.localapp.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.localapp.R;
import com.localapp.request.CommonRequest;
import com.localapp.request.LocalappInviteRequest;

import java.util.ArrayList;

public class InviteActivity extends AppCompatActivity implements LocalappInviteRequest.LocalappInviteRequestCallback{
    private static final int REQUEST_PICK_CONTACT = 222;

    public static ArrayList<String> emailValueArr = new ArrayList<String>();
    private ArrayAdapter<String> adapter;
    private ProgressDialog mProgressDialog;
    AutoCompleteTextView textView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.titleColor));
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Initialize AutoCompleteTextView values

        textView = (AutoCompleteTextView) findViewById(R.id._input_email);
        textView.setThreshold(1);

        //Create adapter
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, emailValueArr);
        //Set adapter to AutoCompleteTextView
        textView.setAdapter(adapter);

//        getPermissionToReadUserContacts();

    }


    public void onClicked(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.invite_text_l: inviteViaMessage();break;
            case R.id.invite_whtasapp_l:  inviteViaWhtasapp();break;
        }

    }

    public ArrayList<String> ShowContact() {

        ArrayList<String> nameList = new ArrayList<String>();
        ArrayList<String> phoneList = new ArrayList<String>();
        ArrayList<String> emailList = new ArrayList<String>();

        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
                null, null, null);
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur
                        .getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur
                        .getString(cur
                                .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (Integer
                        .parseInt(cur.getString(cur
                                .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    // Query phone here. Covered next

                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                    + " = ?", new String[] { id }, null);
                    while (pCur.moveToNext()) {
                        // Do something with phones
                        String phoneNo = pCur
                                .getString(pCur
                                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                        nameList.add(name); // Here you can list of contact.
                        phoneList.add(phoneNo); // Here you will get list of phone number.


                        Cursor emailCur = cr.query(
                                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                                new String[]{id}, null);
                        while (emailCur.moveToNext()) {
                            String email = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));

                            emailList.add(email); // Here you will get list of email

                        }
                        emailCur.close();
                    }
                    pCur.close();
                }
            }
        }

        emailValueArr.addAll(emailList);
        adapter.notifyDataSetChanged();

        return nameList; // here you can return whatever you want.
    }





    public void getPermissionToReadUserContacts() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
            ShowContact();
        }else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.READ_CONTACTS},
                        REQUEST_PICK_CONTACT);
        }


    }


    private void inviteViaMessage() {
        try {
            Intent localIntent = new Intent(Intent.ACTION_SEND);
            localIntent.putExtra(Intent.EXTRA_TEXT,getString(R.string.invite_text));
            String defApp = Settings.Secure.getString(getContentResolver(), "sms_default_application");
//        localIntent.setPackage("com.google.android.apps.messaging");
            localIntent.setPackage(defApp);
            localIntent.setType("text/plain");
            startActivity(localIntent);
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void inviteViaWhtasapp() {
        try {
            Intent localIntent = new Intent(Intent.ACTION_SEND);
            localIntent.putExtra(Intent.EXTRA_TEXT,getString(R.string.invite_text));
            localIntent.setPackage("com.whatsapp");
            localIntent.setType("text/plain");
            startActivity(localIntent);
        }catch (Exception e) {
            Toast.makeText(this, "Please install WhatsApp", Toast.LENGTH_SHORT).show();
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.whatsapp")));
            }catch (Exception e1){
                Toast.makeText(this, "Please install play store", Toast.LENGTH_SHORT).show();
            }

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_PICK_CONTACT:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ShowContact();
                }
        }


    }

    @Override
    public void InviteResponse(CommonRequest.ResponseCode responseCode, String errorMsg) {
        mProgressDialog.dismiss();
        if (responseCode == CommonRequest.ResponseCode.COMMON_RES_SUCCESS) {
            Toast.makeText(this, "Email send", Toast.LENGTH_SHORT).show();
            textView.setText("");
        }else {
            Toast.makeText(this, "something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validate() {
        boolean valid = true;
        String email = textView.getText().toString();
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            textView.setError("enter a valid email address");
            valid = false;
            return valid;
        } else {
            textView.setError(null);
        }

        return valid;
    }

    public void sendMail(View view) {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Please wait");

        String email = textView.getText().toString();

        if (validate()) {
            LocalappInviteRequest inviteRequest = new LocalappInviteRequest(this, email,this);
            inviteRequest.executeRequest();
            mProgressDialog.show();
        }
    }
}
