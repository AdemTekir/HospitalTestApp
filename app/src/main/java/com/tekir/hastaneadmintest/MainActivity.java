package com.tekir.hastaneadmintest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private EditText editEmail, editPassword;
    private String emailTxt, passwordTxt,sharedPreferencesGetUserEmail,sharedPreferencesGetUserPassword;
    private Button signBtn;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editEmail = (EditText) findViewById(R.id.editTextTextEmailAddress);
        editPassword = (EditText) findViewById(R.id.editTextTextPassword);
        signBtn = (Button) findViewById(R.id.signButton);

        mAuth = FirebaseAuth.getInstance();

        sharedPreferences = this.getSharedPreferences("com.tekir.hastaneadmintest", Context.MODE_PRIVATE);
        sharedPreferencesGetUserEmail = sharedPreferences.getString("Email",null);
        sharedPreferencesGetUserPassword = sharedPreferences.getString("Password",null);

        sharedPreferencesCheck();

        signBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sign();
            }
        });
    }
    @SuppressLint("SuspiciousIndentation")
    public void sharedPreferencesCheck(){
        if (!TextUtils.isEmpty(sharedPreferencesGetUserEmail) && !TextUtils.isEmpty(sharedPreferencesGetUserPassword))
            editEmail.setText(sharedPreferencesGetUserEmail);
            editPassword.setText(sharedPreferencesGetUserPassword);
            sign();
    }
    public void sign(){
        emailTxt = editEmail.getText().toString();
        passwordTxt = editPassword.getText().toString();

        if (!TextUtils.isEmpty(emailTxt) && !TextUtils.isEmpty(passwordTxt)){
            editor = sharedPreferences.edit();
            editor.putString("Email",emailTxt);
            editor.putString("Password",passwordTxt);
            editor.apply();

            mAuth.signInWithEmailAndPassword(emailTxt,passwordTxt)
                    .addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            mUser = mAuth.getCurrentUser();
                            Intent intent = new Intent(MainActivity.this,HomeActivity.class);
                            startActivity(intent);
                        }
                    }).addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
        }else
            Toast.makeText(this,"E-mail Ve Şifre Alanlarını Doldurunuz.",Toast.LENGTH_SHORT).show();
    }

}