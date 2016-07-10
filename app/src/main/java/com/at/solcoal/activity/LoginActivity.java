package com.at.solcoal.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.at.solcoal.R;


import com.sinch.android.rtc.SinchError;

public class LoginActivity extends BaseActivity implements SinchService.StartFailedListener {

    private Button mLoginButton;

    private EditText mLoginName;
    private ProgressDialog mSpinner;
    private String seller_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        mLoginButton = (Button) findViewById(R.id.loginButton);
        mLoginButton.setEnabled(false);
        mLoginName = (EditText) findViewById(R.id.loginName);
        Bundle bundle = getIntent().getExtras();
        mLoginName.setText(bundle.getString("userId"));
        seller_id = bundle.getString("sellerId");
        mLoginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                loginClicked();
            }
        });
    }

    @Override
    protected void onServiceConnected() {
        mLoginButton.setEnabled(true);
        getSinchServiceInterface().setStartListener(this);
        /* mimicking a click from software to avoid seeing this page */
        mLoginButton.performClick();
    }

    @Override
    protected void onPause() {
        if (mSpinner != null) {
            mSpinner.dismiss();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        finish();
    }

    @Override
    public void onStartFailed(SinchError error) {
        Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show();
        if (mSpinner != null) {
            mSpinner.dismiss();
        }
    }

    @Override
    public void onStarted() {
        openMessagingActivity();
    }

    private void loginClicked() {
        String userName = mLoginName.getText().toString();

        if (userName.isEmpty()) {
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_LONG).show();
            return;
        }

        if (!getSinchServiceInterface().isStarted()) {
            getSinchServiceInterface().startClient(userName);
            showSpinner();
        } else {
            openMessagingActivity();
        }
    }

    private void openMessagingActivity() {
        Intent messagingActivity = new Intent(this, MessagingActivity.class);
        messagingActivity.putExtra("sellerId", seller_id);
        startActivity(messagingActivity);
    }

    private void showSpinner() {
        mSpinner = new ProgressDialog(this);
        mSpinner.setTitle("Logging in");
        mSpinner.setMessage("Please wait...");
        mSpinner.show();
    }
}
