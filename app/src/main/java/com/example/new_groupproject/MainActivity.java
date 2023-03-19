package com.example.new_groupproject;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Dimension;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import android.graphics.Rect;
import android.util.AttributeSet;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SensorEventListener {

    //database of sharedpreferences
    SharedPreferences sharedPreferences;
    public static final String productinfo ="Product_info";
    public  static  final  String Product_name = "product_nameKEY";
    public static final String gram_sugar = "sugarKEY";
    public static final String cube_sugar = "cube_sugarKEY";
    public static final String barCode = "CodeKeyKEY";


//Sensor oject
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private float xPosition, yPosition;
    private ImageView sugar;

    // Object that needed for our app
    ImageButton ib_list[]=new ImageButton[3];
    int ib_id[] = {R.id.history_ib,R.id.setting_ib,R.id.qrscan_ib};
    TextView current_Date, standardValue, cubesOfSugar;

    ConstraintLayout CL1 ;
    RelativeLayout RL1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Hide the title bar from this page
        getSupportActionBar().hide();
        // init all the needed objects
        init_object();
        // Add date and time in the background
        Date currentTime = Calendar.getInstance().getTime();
        // set the format of time
        String format = DateFormat.getDateInstance(DateFormat.DEFAULT).format(currentTime);
        current_Date.setText(String.valueOf(format));
        //set shared preferences file and made
        sharedPreferences = getSharedPreferences(productinfo, Context.MODE_PRIVATE);
        //sensing object
        sugar = findViewById(R.id.sugar);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        xPosition = sugar.getX();
        yPosition = sugar.getY();


    }
    public void init_object(){
        // init image Button objects
        for(int i = 0 ; i<ib_id.length;i++){
            ib_list[i] = findViewById(ib_id[i]);
            ib_list[i].setOnClickListener(this);
        }
        // init TextView object
        current_Date = findViewById(R.id.current_Date);
        standardValue = findViewById(R.id.standardValue);
        cubesOfSugar = findViewById(R.id.cubesOfSugar);
        CL1 = findViewById(R.id.CL1);
        RL1 = findViewById(R.id.RL1);


    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }
    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);

    }
    @Override
    public void onClick(View view) {
        Class class_array[]={history_record.class,setting_page.class,scanning_barcode.class};
        for(int i = 0 ; i<ib_id.length;i++){
            if(view.getId()==ib_id[i]&&view.getId()!=ib_id[2]){
                switch (i) {
                    //history
                    case 0:
                        break;
                    //setting
                    case 1:
                        break;



                }
                StartNewActivity(class_array[i]);
            }
        }
        if(view.getId()==ib_id[2]){
            scanCode();
        }
    }
    //New activity with no things pass through, only start new activity
    public void StartNewActivity(Class i){
        Intent c = new Intent(getApplicationContext(),i);
        startActivity(c);
    }

    //just some scanning codes
    private void scanCode(){
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setOrientationLocked(true);
        options.setCaptureActivity(scanning_barcode.class);
        barLauncher.launch(options);
    }

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(),result ->
    {
        if(result.getContents()!=null){
            AlertDialog.Builder builder= new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Result");
        builder.setMessage(result.getContents());
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();
    }});
///sugar moving AREA
    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];


        // calculate the new position of the ImageView based on accelerometer data : Warning , comment provided by GPT , please retype by own wording , adjust the calculation to run it smoothly
        xPosition -= x * 5;
        yPosition += y * 5;

        // make sure the ImageView stays within the view border : Warning , comment provided by GPT , please retype by own wording
        if (xPosition < 0) xPosition = 0;
        if (yPosition < 0) yPosition = 0;
        if(xPosition>getApplicationContext().getResources().getDisplayMetrics().widthPixels-sugar.getWidth()) xPosition = getApplicationContext().getResources().getDisplayMetrics().widthPixels - sugar.getWidth();
        if(yPosition>getApplicationContext().getResources().getDisplayMetrics().heightPixels-sugar.getHeight()) yPosition = getApplicationContext().getResources().getDisplayMetrics().heightPixels  - sugar.getHeight();


        // update the position of the ImageView on the screen : Warning , comment provided by GPT , please retype by own wording
        sugar.setX(xPosition);
        sugar.setY(yPosition);
        // Set the new position and rotation of the ImageView : Warning , comment provided by GPT , please retype by own wording
        sugar.setTranslationX(xPosition);
        sugar.setTranslationY(yPosition);
        //rotation of the sugar , configur it to run it smoothly
        sugar.setRotation(-xPosition * 3.0f);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
