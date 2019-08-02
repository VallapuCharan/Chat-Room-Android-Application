/*
 * Assignment : Homework 06
 * File name : Group15_HW06.zip
 * Full names : Manideep Reddy Nukala, Sai Charan Reddy Vallapureddy
 * */
package com.mad.group15_hw06;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatRoom extends AppCompatActivity {
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference;
    DatabaseReference user_data;
    static int Image_request_code = 0;
    Uri filepath;
    Uri image_url;
    Bitmap bitmap;
    EditText message;
    ArrayList<Message> messages = new ArrayList<Message>();
    ListView listView;
    MessageAdapter messageAdapter;
    Message message1;
    String Uid;
    String name1;
    TextView name;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        message = findViewById(R.id.editText);
        final ImageView addimage = findViewById(R.id.selectImage);
        final ImageView send_message = findViewById(R.id.sendMessage);
        listView = findViewById(R.id.listView);
        name = findViewById(R.id.textView);
        Uid = getIntent().getStringExtra("userId");
        final ImageView logout = findViewById(R.id.logout);
        logout.setVisibility(View.VISIBLE);
        auth = FirebaseAuth.getInstance();
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                Intent intent = new Intent(ChatRoom.this,MainActivity.class);
                startActivity(intent);
                finish();

            }
        });

        user_data = firebaseDatabase.getReference("users");
        databaseReference = firebaseDatabase.getReference("messages");
        user_details();
        name.setText(name1);
        display_results();
        addimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takepictureintent = new Intent(Intent.ACTION_PICK);
                File Directory_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                String picture_path = Directory_path.getPath();
                Uri data = Uri.parse(picture_path);
                takepictureintent.setDataAndType(data, "image/*");
                startActivityForResult(takepictureintent, Image_request_code);


            }
        });
        send_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                message1 = new Message();
                String obj_key = databaseReference.push().getKey();
                message1.obj_key = obj_key;
                message1.sender_name = name1;
                message1.sender_id = Uid;
                DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
                Date date = new Date();
                message1.date = df.format(date);
                if(bitmap!=null){
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    byte[] data = baos.toByteArray();
                    String path = message1.obj_key + ".png";
                    final StorageReference storageReference = firebaseStorage.getReference(path);
                    StorageMetadata metadata = new StorageMetadata.Builder()
                            .setCustomMetadata("text", message1.obj_key)
                            .build();
                    UploadTask uploadTask = storageReference.putBytes(data, metadata);
                    uploadTask.addOnSuccessListener(ChatRoom.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageReference.getDownloadUrl();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getBaseContext(), "Upload unsuccessful", Toast.LENGTH_SHORT).show();
                        }
                    });
                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return storageReference.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                image_url = task.getResult();
                                message1.message_image_url = image_url.toString();
                                message1.message_text = message.getText().toString();
                                databaseReference.child(message1.obj_key).setValue(message1);
                            } else {
                                Toast.makeText(getBaseContext(), "task is not successful", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
                else {
                    message1.message_text = message.getText().toString();
                    databaseReference.child(obj_key).setValue(message1);

                }
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == Image_request_code) {
                filepath = data.getData();
                InputStream inputStream;

                try {
                    inputStream = this.getContentResolver().openInputStream(filepath);
                    bitmap = BitmapFactory.decodeStream(inputStream);


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "No image found", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    public void display_results(){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messages.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    Message e1 = (Message) child.getValue(Message.class);
                    messages.add(e1);


                }
                messageAdapter = new MessageAdapter(getBaseContext(),R.layout.activity_message,messages,Uid);
                listView.setAdapter(messageAdapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
    public void user_details(){
        user_data = user_data.child(Uid);
        user_data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                name1 = user.firstName;
                name.setText(name1);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
