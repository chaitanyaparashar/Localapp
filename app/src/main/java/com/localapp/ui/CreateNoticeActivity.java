package com.localapp.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.localapp.R;
import com.localapp.data.NoticeBoard;
import com.localapp.data.NoticeBoardMessage;
import com.localapp.request.CommonRequest;
import com.localapp.request.CreateNoticeBoardRequest;

import java.util.ArrayList;
import java.util.List;

public class CreateNoticeActivity extends AppCompatActivity implements CreateNoticeBoardRequest.CreateNoticeBoardResponseCallback{

    private EditText nameEditText,noticeEditText;
    private List<NoticeBoardMessage> messages;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_notice);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        nameEditText = (EditText) findViewById(R.id._input_name);
        noticeEditText = (EditText) findViewById(R.id._input_notice);

        messages = new ArrayList<>();
    }


    public void onBack(View view) {
        setResult(Activity.RESULT_CANCELED);
        onBackPressed();
    }

    public void onSubmit(View view) {
        if (isValid()){

            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Please wait...");
            mProgressDialog.show();
            createNotice(nameEditText.getText().toString().trim(),noticeEditText.getText().toString().trim());
        }
    }

    boolean isValid() {
        boolean valid = false;
        String mName = nameEditText.getText().toString().trim();
        String mNotice = noticeEditText.getText().toString().trim();

        if (mName.length() < 1) {
            nameEditText.setError("enter a valid name");
            valid = false;
            return valid;
        }else {
            valid = true;
        }

        if (mNotice.length() < 5) {
            noticeEditText.setError("minimum 2 word");
            valid = false;
            return valid;
        }else {
            valid = true;
        }

        return valid;
    }

    private void createNotice(String name, String notice) {
        NoticeBoard data = new NoticeBoard(HomeActivity.mUserId, name);
        if (messages.size() > 0) {
            messages.clear();
        }
        messages.add(new NoticeBoardMessage(notice));
        data.setMessagesList(messages);

        CreateNoticeBoardRequest noticeBoardRequest = new CreateNoticeBoardRequest(this,data,this);
        noticeBoardRequest.executeRequest();
    }

    @Override
    public void createNoticeBoardResponse(CommonRequest.ResponseCode responseCode, NoticeBoard data) {

        mProgressDialog.dismiss();
        if (responseCode == CommonRequest.ResponseCode.COMMON_RES_SUCCESS) {
            Toast.makeText(this, "Notice Board Created Successfully", Toast.LENGTH_SHORT).show();
            setResult(Activity.RESULT_OK);
            finish();
        }
    }
}
