package com.example.new_groupproject;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.TextView;

public class standard_value extends AppCompatActivity implements View.OnClickListener {
    private Button my_button;
    private TextView asv_back_btn;
    private int mColorIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_standard_value);

        my_button = findViewById(R.id.my_button);
        asv_back_btn = findViewById(R.id.asv_back_btn);
        asv_back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        my_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://www.who.int/news/item/04-03-2015-who-calls-on-countries-to-reduce-sugars-intake-among-adults-and-children";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });

        // Create a handler to run the color change and rotation tasks
        // Rotate the button with ObjectAnimator
        ObjectAnimator rotateAnimator = ObjectAnimator.ofFloat(my_button, "rotation", 0, 360);
        rotateAnimator.setDuration(3000); // Rotate in 3 seconds
        rotateAnimator.setInterpolator(new LinearInterpolator());
        rotateAnimator.setRepeatCount(ObjectAnimator.INFINITE); // Rotate continuously
        rotateAnimator.start();
    }


    @Override
    public void onClick(View view) {

    }
}