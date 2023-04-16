package com.example.new_groupproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class weight_record extends AppCompatActivity {

    private EditText editTextNumber;
    private Button saveButton;
    private TextView savedNumberTextView;
    private TextView savedDateTextView;

    private SharedPreferences sharedPreferences;
    private static final String SHARED_PREF_NAME = "UserData";
    private static final String KEY_NUMBER = "number";
    private static final String KEY_DATE = "date";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_record);

        editTextNumber = findViewById(R.id.weight_et);
        saveButton = findViewById(R.id.weight_save_btn);
        savedNumberTextView = findViewById(R.id.weight_tv);
        savedDateTextView = findViewById(R.id.date_tv);

        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });

        loadData();
    }

    private void saveData() {
        String userInput = editTextNumber.getText().toString();
        if (userInput.isEmpty()) {
            Toast.makeText(this, "Please enter a number", Toast.LENGTH_SHORT).show();
            return;
        }

        int number = Integer.parseInt(userInput);
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_NUMBER, number);
        editor.putString(KEY_DATE, currentDate);
        editor.apply();

        savedNumberTextView.setText("Your weight: " + number +" KG");
        savedDateTextView.setText("Previous saved date: " + currentDate);

        Toast.makeText(this, "Data saved", Toast.LENGTH_SHORT).show();
    }

    private void loadData() {
        int savedNumber = sharedPreferences.getInt(KEY_NUMBER, -1);
        String savedDate = sharedPreferences.getString(KEY_DATE, "");

        if (savedNumber != -1) {
            savedNumberTextView.setText("Your weight: " + savedNumber +"");
        }

        if (!savedDate.isEmpty()) {
            savedDateTextView.setText("Previous saved date: " + savedDate);
        }
    }
}