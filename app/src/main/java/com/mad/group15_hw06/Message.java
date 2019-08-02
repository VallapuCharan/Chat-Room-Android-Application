/*
 * Assignment : Homework 06
 * File name : Group15_HW06.zip
 * Full names : Manideep Reddy Nukala, Sai Charan Reddy Vallapureddy
 * */
package com.mad.group15_hw06;

import java.io.Serializable;

public class Message implements Serializable {
    public String message_text="";
    public String message_image_url="";
    public String obj_key;
    public String sender_name;
    public String date;
    public String sender_id;

    public Message() {
    }
}
