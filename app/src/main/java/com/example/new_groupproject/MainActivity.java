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
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.Toast;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;

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
    //cube of sugar
    public Set<String> cubeOfsugar;


    //Sensor oject
    private SensorManager sensorManager;
    private Sensor accelerometerSensor;

    private RelativeLayout sugar_RL;
    private List<ImageView> imageViews = new ArrayList<>();
    private float[] lastAccelerometer;
    private World world;



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
        initializeWorld();
        sugar_RL = findViewById(R.id.sugar_RL);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);

        //world
        sugar_RL.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                sugar_RL.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int width = sugar_RL.getWidth();
                int height = sugar_RL.getHeight();

                createScreenBounds(width, height); // Now that the dimensions are known, create the screen bounds
            }
        });


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

        // init relative layout

        imageViews = new ArrayList<>();
        //set shared preferences file and made
        sharedPreferences = getSharedPreferences(productinfo, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //hard coding area of inputting the value into sharedpreference
        //please DELETE THE APP after adding new product items
        product_name= sharedPreferences.getStringSet(ProductKey,new HashSet<String >());
        cubeOfsugar = sharedPreferences.getStringSet(cube_sugar,new HashSet<String>());
        barcode_key = sharedPreferences.getStringSet(barCodeKey,new HashSet<String >());
        // add two data
        barcode_key.add("4891513000122"); //1
        barcode_key.add("4890008100156"); //2
        barcode_key.add("4890008100293"); //3
        barcode_key.add("4890008100231"); //4
        barcode_key.add("4890008101238"); //5
        barcode_key.add("4890008109234"); //6
        barcode_key.add("4890008109159"); //7
        barcode_key.add("4890008120291"); //8
        barcode_key.add("4890008411238"); //9
        barcode_key.add("4890008110155"); //10
        barcode_key.add("4890008110230"); //11
        barcode_key.add("3179730013158"); //12
        barcode_key.add("3179730011154"); //13

        product_name.add("7 Up - 7 Up - Bottle 1.25L");                             //1
        product_name.add("Coca Cola - Coke - Bottle 1.25L");                        //2
        product_name.add("Coca Cola - Coke - Bottle 2L");                           //3
        product_name.add("Coca Cola - Coke - Bottle 500mL");                        //4
        product_name.add("Coca Cola - Coke Plus (Zero Sugar) - Bottle 500mL");      //5
        product_name.add("Coca Cola - Coke Zero - Bottle 500mL");                   //6
        product_name.add("Coca Cola - Coke Zero - Bottle 1.25L");                   //7
        product_name.add("Fanta - Orange Drink - Bottle 2L");                       //8
        product_name.add("Schweppes - +C Lemon Flavoured Soda - Bottle 500mL");     //9
        product_name.add("Sprite - Lemon-Lime Flavoured Soda - Bottle 1.25L");      //10
        product_name.add("Sprite - Lemon-Lime Flavoured Soda - Bottle 500mL");      //11
        product_name.add("Perrier - Sparkling Mineral Water 330mL");                //12
        product_name.add("Perrier - Sparkling Mineral Water 750mL");                //13

        // I think no. of cubeOfsugar can use array to store it ? , and so the others ?
        cubeOfsugar.add("24"); //1
        cubeOfsugar.add("33"); //2
        cubeOfsugar.add("53"); //3
        cubeOfsugar.add("13"); //4
        cubeOfsugar.add("0");  //5
        cubeOfsugar.add("0");  //6
        cubeOfsugar.add("0");  //7
        cubeOfsugar.add("65"); //8
        cubeOfsugar.add("9");  //9
        cubeOfsugar.add("15"); //10
        cubeOfsugar.add("6");  //11
        cubeOfsugar.add("0");  //12
        cubeOfsugar.add("0");  //13



        editor.putStringSet(barCodeKey,barcode_key);
        editor.putStringSet(ProductKey,product_name);
        editor.putStringSet(cube_sugar,cubeOfsugar);
        editor.commit();
        editor.apply();

        // make a log
        Toast.makeText(getApplicationContext(), product_name.size()+ " records are saved!" ,Toast.LENGTH_LONG).show();


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
        Iterator<String> i = barcode_key.iterator();
        if(result.getContents()!=null){
            AlertDialog.Builder builder= new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Result");
        Boolean isSearched = false;
        //builder.setMessage(result.getContents());
            if (barcode_key != null) {
                while(i.hasNext()) {
                    if(result.getContents().equals(i.next())) {
                        builder.setMessage("get!\n" + "The barcode is " + result.getContents());
                        addNewImageView(); // simple adding one sugar.
                        isSearched = true;
                    }
                }
                if(!isSearched)
                {
                    Log.d("Vincent", "The product is not searched!");
                }
            }

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();
    }});

///sugar moving AREA based on user phone
private void addNewImageView() {
    ImageView imageView = new ImageView(this);
    imageView.setImageResource(R.drawable.sugar); // Replace with your image resource

    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(100, 100); // Adjust width and height as needed
    layoutParams.leftMargin = 100; // Adjust the initial position as needed
    layoutParams.topMargin = 100;

    imageView.setLayoutParams(layoutParams);
    sugar_RL.addView(imageView);

    BodyDef bodyDef = new BodyDef();
    bodyDef.type = BodyType.DYNAMIC;
    bodyDef.position.set(layoutParams.leftMargin / 100.0f, layoutParams.topMargin / 100.0f); // Divide by 100 to convert pixels to meters

    // Set allowSleep to false to prevent the body from going to sleep
    bodyDef.allowSleep = false;

    PolygonShape shape = new PolygonShape();
    float halfWidth = layoutParams.width / 200.0f; // Divide by 200 since we need half-width and half-height
    float halfHeight = layoutParams.height / 200.0f;
    shape.setAsBox(halfWidth, halfHeight);

    Body body = world.createBody(bodyDef);
    body.createFixture(shape, 1.0f);
    imageView.setTag(body); // Store the JBox2D body as a tag in the ImageView

    imageViews.add(imageView);
}

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            lastAccelerometer = event.values.clone();
            updateImageViewPositions();
        }
    }

    private void updateImageViewPositions() {
        Vec2 gravity = new Vec2(-lastAccelerometer[0], lastAccelerometer[1]);
        world.setGravity(gravity);

        float timeStep = 1.0f / 60.0f;
        int velocityIterations = 15;
        int positionIterations = 8;

        world.step(timeStep, velocityIterations, positionIterations);
        updateImageViews();
    }

    private void updateImageViews() {
        for (ImageView imageView : imageViews) {
            Body body = (Body) imageView.getTag();
            Vec2 position = body.getPosition();
            float angle = body.getAngle();

            // Adjust the position accounting for the ImageView dimensions
            float adjustedX = position.x * 100.0f - (imageView.getWidth() / 2.0f);
            float adjustedY = position.y * 100.0f - (imageView.getHeight() / 2.0f);

            imageView.setX(adjustedX);
            imageView.setY(adjustedY);
            imageView.setRotation((float) Math.toDegrees(angle));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do nothing in this example
    }

    private void createScreenBounds(int width, int height) {
        float screenWidth = width / 100.0f;
        float screenHeight = height / 100.0f;
        float wallThickness = 0.1f;

        createWall(new Vec2(screenWidth / 2, -wallThickness), screenWidth / 2, wallThickness); // Top
        createWall(new Vec2(screenWidth / 2, screenHeight + wallThickness), screenWidth / 2, wallThickness); // Bottom
        createWall(new Vec2(-wallThickness, screenHeight / 2), wallThickness, screenHeight / 2); // Left
        createWall(new Vec2(screenWidth + wallThickness, screenHeight / 2), wallThickness, screenHeight / 2); // Right
    }

    private void createWall(Vec2 position, float halfWidth, float halfHeight) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.STATIC;
        bodyDef.position.set(position);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(halfWidth, halfHeight);

        Body body = world.createBody(bodyDef);
        body.createFixture(shape, 0.0f);
    }

    private void initializeWorld() {
        world = new World(new Vec2(0, -9.81f)); // Create a new world with gravity

    }








}
