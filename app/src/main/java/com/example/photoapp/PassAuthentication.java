package com.example.photoapp;

import androidx.appcompat.app.AppCompatActivity;
import com.hanks.passcodeview.PasscodeView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

public class PassAuthentication extends AppCompatActivity {
    PasscodeView passcodeView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass_authentication);
        passcodeView = findViewById(R.id.passcodeview);

        SharedPreferences prefs = getSharedPreferences("my_pin_pref", MODE_PRIVATE);
        String mysavedpin = prefs.getString("pin", "");

        passcodeView.setPasscodeLength(6)
                .setLocalPasscode(mysavedpin)
                .setListener(new PasscodeView.PasscodeViewListener() {
                    @Override
                    public void onFail() {
                        Toast.makeText(PassAuthentication.this, "Password is wrong!", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onSuccess(String number) {
                        finish();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(PassAuthentication.this, "Bạn phải nhập password trước", Toast.LENGTH_SHORT).show();
    }
}