package com.example.new_groupproject;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.net.Inet4Address;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.widget.Toast;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SensorEventListener {
    /** PLEASE DELETE THE APP AND RE LAUNCH THE APP**/
    /** PLEASE DELETE THE APP AND RE LAUNCH THE APP**/
    /** PLEASE DELETE THE APP AND RE LAUNCH THE APP**/
    //database of sharedpreferences
    SharedPreferences sharedPreferences;
    // perform the sugar operation

    public static final String productinfo ="Product_info";


    public history_record recordClass =new history_record();

    private static final String DATA_LIST = "data_list";
    private static final String SHARED_PREFS = "shared_prefs";
    public String targetSugar=""; // for storing the scanned sugar of product
    public String global_Sugar = ""; // for updating the total scanned sugar of product
    public String targetBarcode=""; // for storing the scanned barcode of product
    // perform a key-value mapping - easy for searching
    public HashMap<String, String> codeToProductName =new HashMap<>();
    public HashMap<String, String> codeToSugar = new HashMap<>();
    //Sensor object
    private SensorManager sensorManager;
    private Sensor accelerometerSensor;

    private ConstraintLayout CL1;
    private List<ImageView> imageViews = new ArrayList<ImageView>();
    private float[] lastAccelerometer;
    private World world;

    // Object that needed for our app
    ImageButton ib_list[]=new ImageButton[3];

    int ib_id[] = {R.id.history_ib,R.id.setting_ib,R.id.qrscan_ib};

    TextView current_Date, standardValue, cubesOfSugar; // three text view for updating info in background

    //healthy message
    private String[] messages_too_many_sugar = {
            "sims like you just intake too many sugar today",
            "Maybe water is your best friend than sugar contained drinks",
            "A sugar away, Healthy on each day",
            "NO MORE ᕙ(`▽´)ᕗ SUGAR "
            // Add more messages as needed
    };


    private int messageIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Hide the title bar from this page
        //getSupportActionBar().hide();

        // init all the needed objects
        init_object();

        // Add date and time in the background
        Date currentTime = Calendar.getInstance().getTime();

        // set the format of time
        String format = DateFormat.getDateInstance(DateFormat.DEFAULT).format(currentTime);
        current_Date.setText(String.valueOf(format));

        //sensing object , Do not change unless inneeded
        initializeWorld();
        CL1 = findViewById(R.id.CL1);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);

        //CL1 .setBackgroundColor(Color.parseColor("#FF00FF"));


        //world
        CL1.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                CL1.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int width = CL1.getWidth();
                int height = CL1.getHeight();

                createScreenBounds(width, height); // Now that the dimensions are known, create the screen bounds
            }
        });

    }
    public void Generating_Sugars(String numOfSugar)
    {
        int n = Integer.parseInt(numOfSugar);
        Log.d("Vincent", numOfSugar + " Cubes of sugar");
        for(int i=0; i<n; i++)
        {
            Log.d("Vincent", i+" sugars.");
            addNewImageView();
        }

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

        // barcode maps to product name
        codeToProductName.put("4891513000122", "7 Up - 7 Up - Bottle 1.25L");
        codeToProductName.put("4890008100156", "Coca Cola - Coke - Bottle 1.25L");
        codeToProductName.put("4890008100293", "Coca Cola - Coke - Bottle 2L");
        codeToProductName.put("4890008100231", "Coca Cola - Coke - Bottle 500mL");
        codeToProductName.put("4890008101238", "Coca Cola - Coke Plus (Zero Sugar) - Bottle 500mL");
        codeToProductName.put("4890008109234", "Coca Cola - Coke Zero - Bottle 500mL");
        codeToProductName.put("4890008109159", "Coca Cola - Coke Zero - Bottle 1.25L");
        codeToProductName.put("4890008120291", "Fanta - Orange Drink - Bottle 2L");
        codeToProductName.put("4890008411238", "Schweppes - +C Lemon Flavoured Soda - Bottle 500mL");
        codeToProductName.put("4890008110155", "Sprite - Lemon-Lime Flavoured Soda - Bottle 1.25L");
        codeToProductName.put("4890008110230", "Sprite - Lemon-Lime Flavoured Soda - Bottle 500mL");
        codeToProductName.put("3179730013158", "Perrier - Sparkling Mineral Water 330mL");
        codeToProductName.put("3179730011154", "Perrier - Sparkling Mineral Water 750mL");

        // barcode maps to sugar
        codeToSugar.put("4891513000122", "24");
        codeToSugar.put("4890008100156", "33");
        codeToSugar.put("4890008100293", "53");
        codeToSugar.put("4890008100231", "13");
        codeToSugar.put("4890008101238", "0");
        codeToSugar.put("4890008109234", "0");
        codeToSugar.put("4890008109159", "0");
        codeToSugar.put("4890008120291", "65");
        codeToSugar.put("4890008411238", "9");
        codeToSugar.put("4890008110155", "15");
        codeToSugar.put("4890008110230", "6");
        codeToSugar.put("3179730013158", "0");
        codeToSugar.put("3179730011154", "0");

        // iterate over the key-value pairs in the map and store them in SharedPreferences
        for(Map.Entry<String, String> entry:  codeToProductName.entrySet())
        {
            String barCodeAsKey = entry.getKey();
            String name = entry.getValue();
            if(name instanceof String)
            {
                editor.putString(barCodeAsKey, name);
            }
        }
        for(Map.Entry<String, String> entry:  codeToSugar.entrySet())
        {
            String barCodeAsKey = entry.getKey();
            String sugar = entry.getValue();
            if(sugar instanceof String)
            {
                editor.putString(barCodeAsKey, sugar);
            }
        }

        editor.commit();
        editor.apply();

        // make a log
        Toast.makeText(getApplicationContext(), codeToProductName.size()+ " records are saved!" ,Toast.LENGTH_LONG).show();

    }

    @Override
    public void onClick(View view) {
        Class class_array[]={history_record.class,setting_page.class,scanning_barcode.class};
        for(int i = 0 ; i<ib_id.length;i++){
            if(view.getId()==ib_id[i]&&view.getId()!=ib_id[2]){
                switch (i) {
                    //history xml
                    case 0:
                        /*
                        if(recordClass.getClass()==null) StartNewActivity(history_record.class);
                        else recordClass.startHistoryRecordActivity(); // start the history_record activity
                        */
                        StartNewActivity(class_array[i]);

                        break;
                    //setting xml
                    case 1:
                        Intent intent =new Intent(MainActivity.this,setting_page.class);
                        startActivityForResult(intent,1);
                        break;

                }
                //StartNewActivity(class_array[i]);
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
    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setPositiveButton("Got it", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Do any additional actions if needed
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 999 && resultCode == saving.RESULT_OK) {
            if (data != null) {
                // getting a boolean value from saving class
                Boolean res = data.getBooleanExtra("result", false);
                // when user click save
                if (res) {

                    // Generate sugar
                    // the formula of Standard Value (1 cube = 4 gram)
                    // sugar consumed per day is 50 gram
                    double formulaForStandardValue = Double.parseDouble(global_Sugar)*4.0/50.0;
                    formulaForStandardValue*=100.0;
                    // updating the textview in the background
                    standardValue.setText(Double.toString(formulaForStandardValue)+"% of standard value");
                    cubesOfSugar.setText(global_Sugar);
                    Log.d("Vincent", "onActivityResult: generating sugar");
                    Generating_Sugars(targetSugar);
                    // putting the barcode to the database
                    // for history record to retrieve
                    SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    Gson gson = new Gson();
                    String json = sharedPreferences.getString(DATA_LIST, null);
                    Type type = new TypeToken<ArrayList<String>>() {}.getType();
                    List<String> dataList = gson.fromJson(json, type);

                    if (dataList == null) {
                        dataList = new ArrayList<>();
                    }

                    dataList.add(targetBarcode);

                    json = gson.toJson(dataList);
                    editor.putString(DATA_LIST, json);
                    editor.apply();


                    if(formulaForStandardValue>100){
                        // Show the alert with the current message
                        showAlert(messages_too_many_sugar[messageIndex]);
                        // Increment the message index and reset it if it exceeds the array size
                        messageIndex = (messageIndex + 1) % messages_too_many_sugar.length;
                    }

                }
                // when user click cancel -> nothing perform
                else{
                    Log.d("Vincent", "onActivityResult: Refresh");
                    //StartNewActivity(MainActivity.class);
                }
            }
        }
        Log.d("main_activity", "Run any code in get color code ?");
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            String color = data.getStringExtra("color");
            if (color != null) {
            Log.d("main_activity", "get the color code of : " + color);
            CL1.setBackgroundColor(Color.parseColor(color));}
        }
    }

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(),result ->
    {
        //String barcodeval = (sharedPreferences.getString("CodeKeyKEY",""));

        //Trying to get the value search in the sharedpreferences

        String targetCode = result.getContents();
        Boolean isExisted = false;

        // check if the database contains this barcode
        // if found -> performing the sugar adding
        if(codeToProductName.containsKey(targetCode)&&codeToSugar.containsKey(targetCode)){

            //builder.setTitle("Result");

            //builder.setMessage(result.getContents());
            String Product = codeToProductName.get(targetCode);
            String Sugar = codeToSugar.get(targetCode);
            Toast.makeText(this, "Product: "+Product + " Sugar: "+Sugar , Toast.LENGTH_SHORT).show();
            Log.d("Vincent", "The product name is " + Product + " and the sugar is " + Sugar );
            Log.d("Vincent", "Barcode is:  "+targetCode);
            isExisted = true;
            targetSugar=Sugar;
            // first time add sugar
            if(global_Sugar=="") {
                global_Sugar=targetSugar;
            }
            // if already have some sugar in main activity
            else {
                int a = Integer.parseInt(Sugar);
                int b = Integer.parseInt(global_Sugar);
                global_Sugar = String.valueOf(a + b);
            }

            targetBarcode=targetCode;

            if(!isExisted)
            {
                Log.d("Vincent", "The product is not searched!");
            }
            else{
                Log.d("Vincent", "The product is searched!");
            }
            String []arr = new String[2];
            arr[0] = Product;
            arr[1] = Sugar;
            Intent intent = new Intent(this, saving.class);
            intent.putExtra("target", arr);
            startActivityForResult(intent, 999);
            //builder.setMessage("get!\n" + "The barcode is " + result.getContents());
            // get the bool value from saving


            //isSave = getIntent().getStringExtra("Saving");

        /*builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();*/
        }
        // if the barcode is not found
        else{
            AlertDialog.Builder builder= new AlertDialog.Builder(this);
            builder.setMessage("The product seems new...\n Look forward on later update")
            .setPositiveButton("Got it", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // Do any additional actions if needed
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    });


///sugar moving AREA based on user phone

    // creating a sugar in the main activity page
    private void addNewImageView() {
        ImageView i = new ImageView(this);
        i.setImageResource(R.drawable.sugar); // Replace with your image resource

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(160, 160); // Adjust width and height as needed
        layoutParams.leftMargin = 50; // Adjust the initial position as needed
        layoutParams.topMargin = 50;

        // Set layout rules for the new ImageView
        //layoutParams.addRule(RelativeLayout.BELOW, R.id.button); // make the new ImageView appear below the button
        //layoutParams.addRule(RelativeLayout.ALIGN_LEFT, R.id.button); // align the new ImageView with the left edge of the button

        i.setLayoutParams(layoutParams);
        CL1.addView(i, 0); // Add the new ImageView at index 0

        // Create a JBox2D body for the new ImageView
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
        i.setTag(body); // Store the JBox2D body as a tag in the ImageView

        imageViews.add(i);
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

        float timeStep = 1.0f / 10.0f;  // Decrease the timeStep to increase speed
        int velocityIterations = 8;  // Decrease the iterations to increase speed
        int positionIterations = 3;  // Decrease the iterations to increase speed

        world.step(timeStep, velocityIterations, positionIterations);
        updateImageViews();
    }

    private void updateImageViews() {
        for (ImageView imageView : imageViews) {
            Body body = (Body) imageView.getTag();
            Vec2 position = body.getPosition();
            float angle = body.getAngle();

            // Adjust the position accounting for the ImageView dimensions within the screen border
            float adjustedX = position.x * 100.0f - (imageView.getWidth() / 2.0f);
            float adjustedY = position.y * 100.0f - (imageView.getHeight() / 2.0f);

            imageView.setX(adjustedX);
            imageView.setY(adjustedY);
            imageView.setRotation((float) Math.toDegrees(angle));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do nothing
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

    public static MainActivity getInstance() {
        return new MainActivity();
    }

}
