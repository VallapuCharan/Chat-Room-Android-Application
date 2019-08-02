/*
 * Assignment : Homework 06
 * File name : Group15_HW06.zip
 * Full names : Manideep Reddy Nukala, Sai Charan Reddy Vallapureddy
 * */
package com.mad.group15_hw06;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    Button LoginButton,SignUpButton;
    EditText email,password;
    private FirebaseAuth auth;
    DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.login);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        LoginButton=findViewById(R.id.login);
        SignUpButton=findViewById(R.id.signup);
        auth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("users");

        SignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isConnected()) {
                    startActivity(new Intent(MainActivity.this, SignUp.class));
                    finish();
                }
                else {
                    Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isConnected()){
                    Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(email.getText().toString())){
                    email.setError("Please enter email");
                }
                else if(TextUtils.isEmpty(password.getText().toString())){
                    password.setError("Enter Password of 6 digits");
                }
                else {
                    userLogin();
                }
            }
        });



    }
    private void userLogin() {
        String user_email = email.getText().toString().trim();
        String user_password = password.getText().toString().trim();

        auth.signInWithEmailAndPassword(user_email, user_password).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    mDatabaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                                String id=postSnapshot.child("userId").getValue()+"";
                                if(id.equals(auth.getCurrentUser().getUid())) {
                                    Intent i = new Intent(MainActivity.this, ChatRoom.class);
                                    i.putExtra("userId",id);
                                    startActivity(i);
                                    finish();
                                }

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isConnected() ) {

            return false;
        }
        return true;
    }

}