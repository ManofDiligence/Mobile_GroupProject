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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SensorEventListener {
    /** PLEASE DELETE THE APP AND RE LAUNCH THE APP**/
    /** PLEASE DELETE THE APP AND RE LAUNCH THE APP**/
    /** PLEASE DELETE THE APP AND RE LAUNCH THE APP**/
    //database of sharedpreferences
    SharedPreferences sharedPreferences;
    public static final String productinfo ="Product_info";
    public static final String ProductKey = "product_nameKEY";
    public static final String gram_sugar = "sugarKEY";
    public static final String cube_sugar = "cube_sugarKEY";
    public static final String barCodeKey = "CodeKeyKEY";
    //Product name data
    public Set<String> product_name;
    //Barcode data
    public Set<String> barcode_key;


    //Sensor oject
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private float xPosition, yPosition;
    private ImageView sugar;

    // Object that needed for our app
    ImageButton ib_list[]=new ImageButton[3];
    int ib_id[] = {R.id.history_ib,R.id.setting_ib,R.id.qrscan_ib};
    TextView current_Date, standardValue, cubesOfSugar;

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


        //sensing object , Do not change unless inneeded
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

        //set shared preferences file and made
        sharedPreferences = getSharedPreferences(productinfo, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //hard coding area of inputting the value into sharedpreference
        //please DELETE THE APP after adding new product items
        product_name= sharedPreferences.getStringSet(ProductKey,new HashSet<String >());

        barcode_key = sharedPreferences.getStringSet(barCodeKey,new HashSet<String >());
        // add two data
        barcode_key.add("4890008333240");
        barcode_key.add("8885012291781");

        product_name.add("葡萄適 Lucozade - Sport 運動飲料 - 橙味 - 樽裝 450毫升");
        product_name.add("水動樂 Aquarius - 零系電解質補充飲品 - 樽裝 500毫升");

        editor.putStringSet(barCodeKey,barcode_key);
        editor.putStringSet(ProductKey,product_name);
        editor.apply();
        // make a log
        Toast.makeText(getApplicationContext(), product_name.size()+ " records are saved!" ,Toast.LENGTH_LONG).show();

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
                    //history xml
                    case 0:
                        break;
                    //setting xml
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


    //just some scanning codes,current gradle using below.
    //https://github.com/journeyapps/zxing-android-embedded
    private void scanCode(){
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setOrientationLocked(true);
        options.setCaptureActivity(scanning_barcode.class);
        barLauncher.launch(options);
    }

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(),result ->
    {
        //String barcodeval = (sharedPreferences.getString("CodeKeyKEY",""));

        //Trying to get the value search in the sharedpreferences
        //Set<String> barcode_key = sharedPreferences.getStringSet("CodeKeyKEY",new HashSet<String >());
        //String[] nbarcode_key = (new String[barcode_key.size()]);
        if(result.getContents()!=null){
            AlertDialog.Builder builder= new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Result");
        //builder.setMessage(result.getContents());
            if (barcode_key != null) {
                for (String value : barcode_key) {
                    if(result.getContents().equals(value)){
                        builder.setMessage("get!\n"+"The barcode is "+ result.getContents());

                    }
                    else{
                        builder.setMessage("not get");
                    }
                }
            }

            // trying to get the barcode and compare to the shared preference
            //sample code for reference , pls don't delete first
            //if(result.getContents().equals(nbarcode_key)){
               // builder.setMessage("get");
           /* }
            else{
                builder.setMessage("not get");
            }*/

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();
    }});

///sugar moving AREA based on user phone
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
        //rotation of the sugar , configure it to run it smoothly
        sugar.setRotation(-xPosition * 3.0f);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //No action needed in this class
    }

}
