package com.example.new_groupproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
            startActivityForResult(c,1);
        }
    });




    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            String color = data.getStringExtra("color");
            Log.d("setting_page", "Get the color code of  "+color);
            if (color != null) {
                Intent intent = new Intent();
                intent.putExtra("color", color);
                setResult(MainActivity.RESULT_OK, intent);
                Log.d("setting_page", "Passed  "+color +"to main_activity");

            }
        }
    }


}