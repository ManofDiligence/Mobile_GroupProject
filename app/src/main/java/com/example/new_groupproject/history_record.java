package com.example.new_groupproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class history_record extends AppCompatActivity {

    private Intent myIntent;
    public String []SavedProduct = new String[3];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_record);
        // since array can contain duplicate data
        // we will use a array to store user's records
        // convert it to arraylist]

        //Bundle extras = myIntent.getExtras();
        /*if(extras!=null)
        {
            SavedProduct=extras.getStringArray("ListOfValue");
        }*/

        Log.d("Vincent", "history Record: "+SavedProduct[0] + SavedProduct[1] + SavedProduct[2]);
    }

    public void setMyIntent(Intent newRecordData) {
        myIntent = newRecordData;
    }
}