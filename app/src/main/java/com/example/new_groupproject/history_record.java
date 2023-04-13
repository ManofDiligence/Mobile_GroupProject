package com.example.new_groupproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class history_record extends AppCompatActivity {

    public ListView listRecords;
    private Intent myIntent;
    private Class Curc;
    private Context context;
    public String []SavedProduct = new String[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_record);
        // since array can contain duplicate data
        // we will use a array to store user's records
        // convert it to arraylist

        listRecords = findViewById(R.id.listRecords);
        Bundle extras = getIntent().getExtras();
        if(extras!=null)
        {
            SavedProduct=extras.getStringArray("ListOfValue");
        }

        ArrayList<String> recordArrayList = new ArrayList<String>();
        for(int i=0; i<SavedProduct.length; i++)
        {
            recordArrayList.add(SavedProduct[i]);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                    R.layout.activity_history_record,
                R.id.records,
                recordArrayList
        );
        listRecords.setAdapter(adapter);
        Log.d("Vincent", "history Record: "+SavedProduct[0] + SavedProduct[1] + SavedProduct[2]);

    }

    public void setMyIntent(Intent newRecordData) {
        myIntent = newRecordData;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void startHistoryRecordActivity() {
        if (context != null && myIntent != null) {
            context.startActivity(myIntent); // start the activity using the context reference
        }
    }

    public void setClass(Class c) {
        Curc = c;
    }
}