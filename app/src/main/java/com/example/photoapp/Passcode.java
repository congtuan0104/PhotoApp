package com.example.photoapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Passcode extends AppCompatActivity {

    EditText edtOldPIN,edtNewPIN;
    Button btnVerify;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passcode);
        edtOldPIN=(EditText) findViewById(R.id.edtOldPIN);
        edtNewPIN=(EditText) findViewById(R.id.edtNewPIN);

        //Tạo PIN mặc định là 123456
        SharedPreferences.Editor editor = getSharedPreferences("my_pin_pref", MODE_PRIVATE).edit();
        SharedPreferences prefs = getSharedPreferences("my_pin_pref", MODE_PRIVATE);
        if(!prefs.contains("pin")){
            editor.putString("pin", "123456");
            editor.commit();
        }



        String mysavedpin = prefs.getString("pin", "");

        btnVerify = (Button) findViewById(R.id.btnVerify);
        btnVerify.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){

                if(edtOldPIN.getText().toString().equals(mysavedpin)){
                    if(edtNewPIN.getText().toString().length()<6)
                        Toast.makeText(Passcode.this, "Mã PIN phải gồm 6 ký tự", Toast.LENGTH_SHORT).show();
                    else{
                        prefs.edit().remove("pin").commit();
                        prefs.edit().putString("pin", edtNewPIN.getText().toString()).commit();

                        Toast.makeText(Passcode.this, "PIN has been changed", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                }
                else{
                    Toast.makeText(Passcode.this, "Nhập sai mã PIN", Toast.LENGTH_SHORT).show();
                }
            }});
    }
}