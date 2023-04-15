package com.example.new_groupproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class history_record extends AppCompatActivity {

    public ListView listRecords;
    public Layout LL1;
    private Intent myIntent;
    SharedPreferences sharedPreferences;
    private Class Curc;
    private Context context;
    public String []SavedProduct = new String[3];

    private ArrayList<String> recordArrayList = new ArrayList<String>();

    private ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_record);
        // since array can contain duplicate data
        // we will use a array to store user's records
        // convert it to arraylist

        sharedPreferences = getSharedPreferences("Product_info", MODE_PRIVATE);
        String newRecord = sharedPreferences.getString("SavedKey", "");

        listRecords = findViewById(R.id.listRecords);
        // get the saved data from MainActivity
        /*
        Bundle extras = getIntent().getExtras();
        if(extras!=null)
        {
            SavedProduct=extras.getStringArray("ListOfValue");
        }
        */
        // create a new list
        ArrayList<String> newList = new ArrayList<>();

        newList.add(newRecord);
        /*
        // add all the existing records
        newList.addAll(recordArrayList);
        for(int i=0; i<SavedProduct.length; i++)
            newList.add(SavedProduct[i]);
        */
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(
                this,
                R.layout.activity_history_record,
                R.id.records,
                newList
        );

        // updating
        recordArrayList.addAll(newList);
        adapter = adapter1;
        listRecords.setAdapter(adapter);

        // updating the array adapter after inserting
        adapter.notifyDataSetChanged();

        Log.d("Vincent", "***History record*** ");
        for(String element: recordArrayList)
        {
            Log.d("Vincent", element);

        }



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