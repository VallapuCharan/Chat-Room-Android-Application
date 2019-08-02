/*
 * Assignment : Homework 06
 * File name : Group15_HW06.zip
 * Full names : Manideep Reddy Nukala, Sai Charan Reddy Vallapureddy
 * */
package com.mad.group15_hw06;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import org.ocpsoft.prettytime.PrettyTime;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
public class MessageAdapter extends ArrayAdapter<Message> {
    Date date;
    String Uid;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference("messages");
    public MessageAdapter(@NonNull Context context, int resource, ArrayList<Message> objects,String Uid)
    {

        super(context, resource,objects);
        this.Uid = Uid;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Message message = getItem(position);
        PrettyTime p = new PrettyTime();
        SimpleDateFormat formatter=new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        try {
            date = formatter.parse(message.date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (convertView==null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_message,parent,false);
        }
        TextView message_text = convertView.findViewById(R.id.message);
        ImageView message_image = convertView.findViewById(R.id.message_image);
        TextView sender_name = convertView.findViewById(R.id.name);
        TextView date_time = convertView.findViewById(R.id.time);
        ImageView delete = convertView.findViewById(R.id.message_delete);
        if(!Uid.equals(message.sender_id)){

            delete.setVisibility(View.INVISIBLE);
        }
        else{
            delete.setVisibility(View.VISIBLE);
        }
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference.child(message.obj_key).removeValue();
            }
        });

        if (message.message_image_url.length() == 0){
            message_image.setVisibility(View.INVISIBLE);
            message_text.setText(message.message_text);
            sender_name.setText(message.sender_name);
            date_time.setText(p.format(date));
        }
        else{
            message_image.setVisibility(View.VISIBLE);
            message_text.setText(message.message_text);
            sender_name.setText(message.sender_name);
            date_time.setText(p.format(date));

            Picasso.get().load(message.message_image_url).into(message_image);
        }

        return convertView;
    }
}
