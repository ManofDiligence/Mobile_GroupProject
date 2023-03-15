package com.example.new_groupproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class setting_page extends AppCompatActivity {
    String setting_array[]={"about us","your mom gay","so gay"};
    ListView lv_setting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_page);

        lv_setting = findViewById(R.id.lv_setting);
        ArrayAdapter<String> setting_array_list =
                new ArrayAdapter<String>(
                this,
                R.layout.activity_setting_page,
                R.id.tv_setting,
                setting_array);
       // for(int i = 0 ; i < setting_array.length;i++){
         //  setting_array_list.add(setting_array[i]);
       // }

    lv_setting.setAdapter(setting_array_list);


    }
}