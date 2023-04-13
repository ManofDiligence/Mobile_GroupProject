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
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("color","#91eaf2");
                        setResult(Activity.RESULT_OK,resultIntent);
                        Log.d("color_chnges", "Color have been change to #91eaf2 " );
                        Toast.makeText(this, "Color changed!", Toast.LENGTH_SHORT).show();
                        break;

                    case 1:
                        Intent resultIntent1 = new Intent();
                        resultIntent1.putExtra("color","#010575");
                        setResult(Activity.RESULT_OK,resultIntent1);
                        Toast.makeText(this, "Color changed!", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Intent resultIntent2 = new Intent();
                        resultIntent2.putExtra("color","#73d9a8");
                        setResult(Activity.RESULT_OK,resultIntent2);
                        Toast.makeText(this, "Color changed!", Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        Intent resultIntent3 = new Intent();
                        resultIntent3.putExtra("color","#d9ff00");
                        setResult(Activity.RESULT_OK,resultIntent3);
                        Toast.makeText(this, "Color changed!", Toast.LENGTH_SHORT).show();
                        break;
                        case 4:
                            Intent resultIntent4 = new Intent();
                            resultIntent4.putExtra("color","#117402");
                            setResult(Activity.RESULT_OK,resultIntent4);
                            Toast.makeText(this, "Color changed!", Toast.LENGTH_SHORT).show();
                            break;
                    case 5 :
                        Intent resultIntent5 = new Intent();
                        resultIntent5.putExtra("color","#29d602");
                        setResult(Activity.RESULT_OK,resultIntent5);
                        Toast.makeText(this, "Color changed!", Toast.LENGTH_SHORT).show();
                        break;
                    case 6 :
                        Intent resultIntent6 = new Intent();
                        resultIntent6.putExtra("color","#a691f2");
                        setResult(Activity.RESULT_OK,resultIntent6);
                        Toast.makeText(this, "Color changed!", Toast.LENGTH_SHORT).show();
                        break;
                    case 7:
                        Intent resultIntent7 = new Intent();
                        resultIntent7.putExtra("color","#f26dad");
                        setResult(MainActivity.RESULT_OK,resultIntent7);
                        break;
                    case 8 :
                        Intent resultIntent8 = new Intent();
                        resultIntent8.putExtra("color","#d40066");
                        setResult(MainActivity.RESULT_OK,resultIntent8);
                        Toast.makeText(this, "Color changed!", Toast.LENGTH_SHORT).show();
                        break;
                    case 9:
                        Intent resultIntent9 = new Intent();
                        resultIntent9.putExtra("color","#e6e283");
                        setResult(MainActivity.RESULT_OK,resultIntent9);
                        Toast.makeText(this, "Color changed!", Toast.LENGTH_SHORT).show();
                        break;
                    case 10:
                        Intent resultIntent10 = new Intent();
                        resultIntent10.putExtra("color","#ffdcc4");
                        setResult(MainActivity.RESULT_OK,resultIntent10);
                        Toast.makeText(this, "Color changed!", Toast.LENGTH_SHORT).show();
                        break;
                    case 11:
                        Intent resultIntent11 = new Intent();
                        resultIntent11.putExtra("color","#6c6c6c");
                        setResult(MainActivity.RESULT_OK,resultIntent11);
                        Toast.makeText(this, "Color changed!", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }

    }
}