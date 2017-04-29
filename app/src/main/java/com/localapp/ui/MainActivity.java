package com.localapp.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.localapp.R;
import com.localapp.login_session.SessionManager;

public class MainActivity extends AppCompatActivity {
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        session = new SessionManager(this);
        if (session.isLoggedIn()) {
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        }else {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }
        finish();

    }
}
