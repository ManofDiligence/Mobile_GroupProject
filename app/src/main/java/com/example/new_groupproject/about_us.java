package com.example.new_groupproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URI;

public class about_us extends AppCompatActivity {

    public TextView intro;
    private TextView about_title;
    public Button linkToGithub;
    private float dX, dY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        intro = findViewById(R.id.intro);
        linkToGithub = (Button) findViewById(R.id.linkToGithub);
        about_title = findViewById(R.id.about_title);

        //intro.setText("");
        linkToGithub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Toast.makeText(about_us.this, "Clicked", Toast.LENGTH_SHORT).show();
                    Log.d("Vincent", "onClick: clicked!");
                    Intent i = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("https://github.com/ManofDiligence/Mobile_GroupProject"));
                    startActivity(i);


            }
        });
        // Apply onTouchListener to about_title TextView
        about_title.setOnTouchListener(onTouchListener);

        // Add more TextView elements and apply onTouchListener to them as needed
        //pull again
    }




    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    dX = v.getX() - event.getRawX();
                    dY = v.getY() - event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    v.animate()
                            .x(event.getRawX() + dX)
                            .y(event.getRawY() + dY)
                            .setDuration(0)
                            .start();
                    break;
                default:
                    return false;
            }
            return true;
        }
    };




}


