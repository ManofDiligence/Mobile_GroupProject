package com.example.new_groupproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;



public class saving extends AppCompatActivity implements  View.OnClickListener{
    Button save_btn , cancel_btn;
    Intent resultIntent = new Intent();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saving);

        View transparentView = findViewById(R.id.transparent_view);
        transparentView.setAlpha(0.5f);

        init_object();

    }

    private void init_object() {
        save_btn = findViewById(R.id.save_btn);
        cancel_btn = findViewById(R.id.cancel_btn);
        save_btn.setOnClickListener(this);
        cancel_btn.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.save_btn:

                Log.d("Adding_sugar", "sugar added");

                //if(context instanceof MainActivity) {
                // MainActivity mainActivity = (MainActivity) view.getContext();
                // MainActivity.addNewImageView();
                // }

                resultIntent.putExtra("result", true);
                setResult(MainActivity.RESULT_OK, resultIntent);
                finish();
                break;
            case R.id.cancel_btn:

                resultIntent.putExtra("result", false);
                setResult(MainActivity.RESULT_OK, resultIntent);
                finish();
                Log.d("Cacnel", "Data not saved");
                break;

        }
    }



}