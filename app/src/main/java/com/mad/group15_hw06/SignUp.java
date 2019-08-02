/*
 * Assignment : Homework 06
 * File name : Group15_HW06.zip
 * Full names : Manideep Reddy Nukala, Sai Charan Reddy Vallapureddy
 * */
package com.mad.group15_hw06;

import android.content.Intent;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {

    EditText first_name;
    EditText last_name;
    EditText Email;
    EditText Password;
    EditText reEnterPassword;
    Button signUp;
    Button cancel;
    private FirebaseAuth auth;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReference("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        first_name = findViewById(R.id.firstName);
        last_name = findViewById(R.id.lastName);
        Email = findViewById(R.id.email);
        Password = findViewById(R.id.choose);
        reEnterPassword = findViewById(R.id.repeat);
        signUp = findViewById(R.id.signup);
        cancel = findViewById(R.id.cancel);
        auth = FirebaseAuth.getInstance();
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(SignUp.this,MainActivity.class);
                startActivity(intent);
            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(first_name.getText().toString())){
                    first_name.setError("Enter the first name");
                }
                else if(TextUtils.isEmpty(last_name.getText().toString())){
                    last_name.setError("Enter the last name");
                }
                else if (TextUtils.isEmpty(Email.getText().toString())){
                    Email.setError("Enter the email");
                }
                else if(TextUtils.isEmpty(Password.getText().toString())){
                    Password.setError("Enter the password");
                }

                else if (!(reEnterPassword.getText().toString()).equals(Password.getText().toString())){
                    reEnterPassword.setError("Passwords doesn't match");
                }
                else{
                    register_user();

                }

            }
        });


    }
    public void register_user(){
        String email = Email.getText().toString();
        String password =Password.getText().toString();
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    User user = new User(first_name.getText().toString(),last_name.getText().toString(),firebaseUser.getUid());
                    String key = databaseReference.push().getKey();
                    user.userKey=key;
                    databaseReference.child(firebaseUser.getUid()).setValue(user);
                    Toast.makeText(getBaseContext(),"User created successfully"+firebaseUser.getUid(),Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(SignUp.this,ChatRoom.class);
                    intent.putExtra("userId",firebaseUser.getUid());
                    startActivity(intent);

                }
            }
        });
    }

}
