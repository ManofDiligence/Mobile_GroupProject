package com.example.new_groupproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class setting_page extends AppCompatActivity {
    Class classarray[]={about_us.class,standard_value.class,change_bg_color.class};
    String setting_array[]={"about us","About standard value","Change background color"};
    ListView lv_setting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_page);
        // add title for this page
        setTitle("Settings");
        lv_setting = findViewById(R.id.lv_setting);
        ArrayAdapter<String> setting_array_list =
                new ArrayAdapter<String>(
                this,
                R.layout.activity_setting_page,
                R.id.tv_setting,
                setting_array);
        // for(int c = 0 ; c < class_array.length;c++){
        //  setting_array_list.add(setting_array[c]);
        // }
    lv_setting.setAdapter(setting_array_list);


    lv_setting.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Intent c = new Intent(setting_page.this,classarray[i]);
            startActivity(c);
        }
    });

    }
}