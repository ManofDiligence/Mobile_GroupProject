package com.example.new_groupproject;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class history_record extends AppCompatActivity implements View.OnClickListener{

    SharedPreferences sharedPreferences;
    public ListView listRecords;
    private ArrayAdapter<String> adapter;

    public Button clearRecord;
    private ArrayList<String> dataList;
    private static final String DATA_LIST = "data_list";
    private static final String SHARED_PREFS = "shared_prefs";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_record);

        clearRecord = findViewById(R.id.clearRecord);
        listRecords = findViewById(R.id.listRecords);
        dataList = new ArrayList<>();

        // Use android.R.layout.simple_list_item_1 instead of dataList
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataList);
        listRecords.setAdapter(adapter);

        updateDataListFromSharedPreferences();

        // Removed unnecessary newList and adapter1

        // Updating the array adapter after inserting
        adapter.notifyDataSetChanged();

        Log.d("Vincent", "***History record*** ");
        for (String element : dataList) {
            Log.d("Vincent", element);
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.clearRecord)
        {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
        }
    }

    private void updateDataListFromSharedPreferences() {
        sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(DATA_LIST, null);
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        List<String> dataListFromPrefs = gson.fromJson(json, type);

        if (dataListFromPrefs != null) {
            dataList.clear();
            dataList.addAll(dataListFromPrefs);
            adapter.notifyDataSetChanged();
        }
    }
}