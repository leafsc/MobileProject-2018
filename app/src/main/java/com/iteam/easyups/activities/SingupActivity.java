package com.iteam.easyups.activities;


import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.iteam.easyups.R;
import com.iteam.easyups.communication.BDDRoutes;
import com.iteam.easyups.communication.DatabaseConnection;

import java.util.HashMap;
import java.util.Map;


public class SingupActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth auth;
    ProgressBar progressBar;
    EditText emailText,pswdText;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        progressBar = findViewById(R.id.progressbar);
        emailText = findViewById(R.id.textEmail);
        pswdText = findViewById(R.id.textPassword);
        auth = FirebaseAuth.getInstance();
        ApplicationInfo applicationInfo = this.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        String result = stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : this.getString(stringId);
        this.setTitle(result);
        findViewById(R.id.buttonSignUp).setOnClickListener(this);
        findViewById(R.id.textLogin).setOnClickListener(this);
    }



    private void registerUser() {
        String email = emailText.getText().toString().trim();
        String password = pswdText.getText().toString().trim();

        if (email.isEmpty()) {
            emailText.setError("Email est obligatoire");
            pswdText.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError("Veuillez entrer un email valide");
            emailText.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            pswdText.setError("Mot de passe est obligatoire");
            pswdText.requestFocus();
            return;
        }

        if (password.length() < 6) {
            pswdText.setError("La longueur minimale du mot de passe est 6");
            pswdText.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    DatabaseReference mData= FirebaseDatabase.getInstance().getReference().child(BDDRoutes.USERS_PATH);
                    DatabaseReference currentUserId = mData.child(auth.getCurrentUser().getUid());
                    currentUserId.child("name").setValue("default");
                    startActivity(new Intent(SingupActivity.this, SingInActivity.class));
                    finish();
                } else {

                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(getApplicationContext(), "Vous êtes déjà inscrit", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonSignUp:
                registerUser();
                break;

            case R.id.textLogin:
                startActivity(new Intent(this, SingInActivity.class));
                finish();
                break;
        }
    }

    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;
    }

}



