package com.example.new_groupproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class change_bg_color extends AppCompatActivity implements View.OnClickListener{

    Button button_list[]=new Button[12];
    int button_int [] = {R.id.color_blue,R.id.color_blue2,R.id.color_green,R.id.color_yellolime,R.id.color_green3,
        R.id.color_green4,R.id.green_purple,R.id.color_pink,R.id.color_pink2,R.id.color_yellow,R.id.color_skin,
            R.id.color_default};
    TextView back_btn;

    Intent resultIntent=new Intent();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_bg_color);

        init_object();
    }

    private void init_object() {
        back_btn = findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        for(int i = 0 ; i < button_int.length;i++){
            button_list[i] = findViewById(button_int[i]);
            button_list[i].setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {


        for(int i = 0 ; i < button_int.length;i++){
            if(view.getId()==button_int[i]){
                switch (i){
                    case 0:

                        resultIntent.putExtra("color","#91eaf2");
                        setResult(Activity.RESULT_OK,resultIntent);
                        Log.d("color_chnges", "Color have been change to #91eaf2 " );
                        Toast.makeText(this, "Color changed!", Toast.LENGTH_SHORT).show();
                        break;

                    case 1:

                        resultIntent.putExtra("color","#010575");
                        setResult(Activity.RESULT_OK,resultIntent);
                        Toast.makeText(this, "Color changed!", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        resultIntent.putExtra("color","#73d9a8");
                        setResult(Activity.RESULT_OK,resultIntent);
                        Toast.makeText(this, "Color changed!", Toast.LENGTH_SHORT).show();
                        break;
                    case 3:

                        resultIntent.putExtra("color","#d9ff00");
                        setResult(Activity.RESULT_OK,resultIntent);
                        Toast.makeText(this, "Color changed!", Toast.LENGTH_SHORT).show();
                        break;
                        case 4:

                            resultIntent.putExtra("color","#117402");
                            setResult(Activity.RESULT_OK,resultIntent);
                            Toast.makeText(this, "Color changed!", Toast.LENGTH_SHORT).show();
                            break;
                    case 5 :

                        resultIntent.putExtra("color","#29d602");
                        setResult(Activity.RESULT_OK,resultIntent);
                        Toast.makeText(this, "Color changed!", Toast.LENGTH_SHORT).show();
                        break;
                    case 6 :

                        resultIntent.putExtra("color","#a691f2");
                        setResult(Activity.RESULT_OK,resultIntent);
                        Toast.makeText(this, "Color changed!", Toast.LENGTH_SHORT).show();
                        break;
                    case 7:

                        resultIntent.putExtra("color","#f26dad");
                        setResult(MainActivity.RESULT_OK,resultIntent);
                        break;
                    case 8 :

                        resultIntent.putExtra("color","#d40066");
                        setResult(MainActivity.RESULT_OK,resultIntent);
                        Toast.makeText(this, "Color changed!", Toast.LENGTH_SHORT).show();
                        break;
                    case 9:

                        resultIntent.putExtra("color","#e6e283");
                        setResult(MainActivity.RESULT_OK,resultIntent);
                        Toast.makeText(this, "Color changed!", Toast.LENGTH_SHORT).show();
                        break;
                    case 10:

                        resultIntent.putExtra("color","#ffdcc4");
                        setResult(MainActivity.RESULT_OK,resultIntent);
                        Toast.makeText(this, "Color changed!", Toast.LENGTH_SHORT).show();
                        break;
                    case 11:

                        resultIntent.putExtra("color","#6c6c6c");
                        setResult(MainActivity.RESULT_OK,resultIntent);
                        Toast.makeText(this, "Color changed!", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }

    }
}