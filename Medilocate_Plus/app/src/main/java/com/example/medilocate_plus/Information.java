package com.example.medilocate_plus;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Information extends AppCompatActivity {

    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("description")
    @Expose
    public String description;
    @SerializedName("lat")
    @Expose
    public String lat;
    @SerializedName("lng")
    @Expose
    public String lng;
    @SerializedName("timestamp")
    @Expose
    public String timestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

    }
}