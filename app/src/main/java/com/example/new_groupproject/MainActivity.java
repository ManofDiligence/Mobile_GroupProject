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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SensorEventListener {

    //database of sharedpreferences
    SharedPreferences sharedPreferences;
    SharedPreferences todaysugar_preferences;
    // perform the sugar operation
    public static final String productinfo ="Product_info";

    public history_record recordClass =new history_record();
    //////////////////////////////////////////////////////////////////////////////////////
    /** main shared prefernece **/
    private static final String DATA_LIST = "data_list";
    private static final String SHARED_PREFS = "shared_prefs";
    public String targetSugar=""; // for storing the scanned sugar of product
    public String global_Sugar = ""; // for updating the total scanned sugar of product
    public String targetBarcode=""; // for storing the scanned barcode of product
    // perform a key-value mapping - easy for searching
    public HashMap<String, String> codeToProductName =new HashMap<>();
    public HashMap<String, String> codeToSugar = new HashMap<>();
    /////////////////////////////////////////////////////////////////////////////////////
    /**today sugar value**/
    public static final  String todaysug = "today_sugar";
    public static final String no_sugar = "sugarKey";
    public static final String percent_value = "percent_valueKey";

    double today_sum_SD = 0;

    private boolean hasAppBuilt = false;

    /////////////////////////////////////////////////////////////////////////////////////
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
////////////////////////////////////////////////////////////////////////////////
    /**           healthy message                **/

    private String[] messages_too_many_sugar = {
            "sims like you just intake too many sugar today",
            "Maybe water is your best friend than sugar contained drinks",
            "A sugar away, Healthy on each day",
            "NO MORE ᕙ(`▽´)ᕗ SUGAR "
            // Add more messages as needed
    };

    //Every day health message
    private  String[] everyday_information = {
            "Avoid energy drinks and high-sugar sports drinks.",
            "Use natural sweeteners like honey or stevia instead of refined sugar.",
            "Check labels for hidden sugars in drinks and aim for lower sugar options.",
            "Choose unsweetened tea or coffee instead of sugary beverages.",
            "If you crave a sweet drink, try a small serving or share with a friend.",
            "Avoid adding sugar to coffee or tea, and choose plant-based milk alternatives.",
            "Be aware of the sugar content in alcoholic drinks and opt for lower sugar options.",
            "Consider carbonated water or sparkling water as a low-sugar alternative."
    };
    private int messageIndex = 0;
    private int messageIndex2 = 0;
/////////////////////////////////////////////////////////////////////////
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
        //////////////////////////////
        /** today shared preference **/
        todaysugar_preferences = getSharedPreferences(todaysug , Context.MODE_PRIVATE);
        int lastClearedDayOfYear = todaysugar_preferences.getInt("last_cleared_day_of_year", -1);
        int currentDayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);

        if (lastClearedDayOfYear != currentDayOfYear) {
            SharedPreferences.Editor editor = todaysugar_preferences.edit();
            editor.clear();
            editor.putInt("last_cleared_day_of_year", currentDayOfYear);
            editor.apply();


            // Show the alert with the current message
            showAlert(everyday_information[messageIndex2]);
            // Increment the message index and reset it if it exceeds the array size
            messageIndex2 = (messageIndex2 + 1) % everyday_information.length;

        }

        //init of the another today sugar saving
        cubesOfSugar.setText(todaysugar_preferences.getString( no_sugar,""));
        standardValue.setText(todaysugar_preferences.getString(percent_value,"")+"% of standard value");

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
    // rebuild the sugar cubes after app is build
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus && !hasAppBuilt) {
            // This code will be executed when the app has fully built and is ready for user
            String today_sugar = todaysugar_preferences.getString(no_sugar,"");
            if (today_sugar == "") {
                //newer of the app
                cubesOfSugar.setText("0");
                standardValue.setText("0 % of standard value");
                hasAppBuilt = true;
            }
            else{
                //rebuild sugar as there have record
                Log.d("main_activity", today_sugar);
                Generating_Sugars(today_sugar);
                hasAppBuilt = true;
            }

        }
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
        codeToProductName.put("8885012290272", "Ribena - Blackcurrant Fruit Drink 330mL");
        codeToProductName.put("4890008711246", "Healthworks - Self-Heal Spike 500mL");
        codeToProductName.put("4890008724246", "Healthworks - Sugarcane Sea Coconut Drink 500mL");
        codeToProductName.put("4892214250168", "Tao Ti - Mandarin Lemon Juice Drink 500mL");
        codeToProductName.put("4892214250915", "Tao Ti - Supreme Oolong Tea 500mL");
        codeToProductName.put("4892214250021", "Tao Ti - Honey Green Tea 500mL");
        codeToProductName.put("4892214250625", "Tao Ti - Apple Green Tea 500mL");
        codeToProductName.put("8885012291781", "Lucozade - Sport Drink - Orange - Bottle 450mL");
        codeToProductName.put("4895016008142", "Hung Fook Tong - Canton Love-pes Vine Drink (Low Sugar) 500mL");
        codeToProductName.put("4895016002140", "Hung Fook Tong - Chrysanthemum with Honey Drink (Low Sugar) 500mL");
        codeToProductName.put("4895016004144", "Hung Fook Tong - Ginseng with Honey 500mL");
        codeToProductName.put("4895016025361", "Hung Fook Tong - Mix Flower Tea (Low Sugar) 500mL");
        codeToProductName.put("4895016010145", "Hung Fook Tong - Selfheal Fruit-spike Drink (Low Sugar) 500mL");
        codeToProductName.put("4890008200511", "Minute Maid - Orange Juice Drink 420mL");
        codeToProductName.put("4711161766037", "Suntory - C.C. Lemon Vitamin Soft Drink - Bottle 500mL");
        codeToProductName.put("4895016023497", "Hung Fook Tong - Pear Tea Drink 500mL");
        codeToProductName.put("3057640100673", "Volvic - Natural Mineral Water (Eau Minérale Naturelle) 500mL");
        codeToProductName.put("8885012290036", "Lucozade - Energy Drink - Orange 300mL");
        codeToProductName.put("4891028711995", "Vita - HK Style Milk Tea - Bottle 480mL");
        codeToProductName.put("4890008330249", "Aquarius - Electrolytes Replenishment Drink - Bottle 500mL");
        codeToProductName.put("4890008515240", "Nestea - Ice Rush Lemon Tea 480mL");
        codeToProductName.put("6937761805022", "Pocari Sweat - Ion Supply Drink 500mL");
        codeToProductName.put("4890008411238", "Schweppes - +C Lemon Flavoured Soda - Bottle 500mL");
        codeToProductName.put("4890008333240", "Aquarius - Electrolytes Replenishment Drink Zero - Bottle 500mL");
        codeToProductName.put("4893342710203", "Cou-Su - Apple Vinegar Drink 350mL");
        codeToProductName.put("4891034024300", "Gatorade - Sport Drink - Lemon-Lime Flavor 600mL");
        codeToProductName.put("4897020730019", "Libogen - Tonic Drink 150mL");
        codeToProductName.put("4710035368704", "Weider - In Energy Jelly Drink (White Grape Flavour) 180g");
        codeToProductName.put("4890008200146", "Minute Maid - Orange Juice Drink 1.2L");
        codeToProductName.put("8885012290258", "Lucozade - Energy Drink - Orange 900mL");
        codeToProductName.put("4897036691489", "Monster - Ultra Carbonated Energy Drink 355mL");
        codeToProductName.put("4890008330140", "Aquarius - Electrolytes Replenishment Drink - Bottle 1.2L");
        codeToProductName.put("9311493000769", "Bundaberg - Diet Ginger Beer 375mL");
        codeToProductName.put("90162602", "Red Bull - Energy Drink 250mL");
        codeToProductName.put("90162800", "Red Bull - Sugarfree Energy Drink 250mL");
        codeToProductName.put("4897036691441", "Monster - Carbonated Energy Drink 355mL");
        codeToProductName.put("4901306017098", "Kagome - Mango Mixed Juice 720mL");
        codeToProductName.put("8801097250031", "Pocari Sweat - Ion Supply Drink 1.5L");
        codeToProductName.put("8888200615062", "F&N Fruit Tree - Apple & Aloe Vera Juice Drink 946mL");
        codeToProductName.put("4891944002160", "F&N Fruit Tree - Orange & Aloe Vera Juice Drink 946mL");
        codeToProductName.put("4891133364505", "Mr Juicy - 100% Orange Longlife Juice 1L");
        codeToProductName.put("8885012290494", "Ribena - Blackcurrant Cordial 1L");
        codeToProductName.put("31200449481", "Ocean Spray - Cranberry Juice 1L");
        codeToProductName.put("9421903084088", "Tree Top - Apple Juice 64oz");
        codeToProductName.put("8887333110116", "Del Monte - Prune Juice 32oz");
        codeToProductName.put("41800207503", "Welch's - 100% Grape Juice 64oz");
        codeToProductName.put("4893342711699", "Cou-do - Honey Citron Tea 1.15kg");
        codeToProductName.put("4004191014125", "Rabenhorst - Blueberry Juice No Sugar Added 330mL");
        codeToProductName.put("4893342712603", "Cou-do - Honey Citron Tea 2kg");
        codeToProductName.put("4890008400300", "Schweppes-CREAM SODA - 330ML");
        codeToProductName.put("4890008100309", "COCA-COLA-COKE 330ML");
        codeToProductName.put("4890008101306", "COCA-COLA COKE PLUS 330ML");
        codeToProductName.put("8935049501596", "SCHWEPPES(PARALLEL IMPORT) SODA WATER 320ML");
        codeToProductName.put("4890008110360", "SPRITE LEMON-LIME FLAVOURED SODA (MINI CANS) 200ML");
        codeToProductName.put("4890008120307", "FANTA ORANGE DRINK 330ML");
        codeToProductName.put("4891133332733", "WATSONS LIME FLAVOURED SODA WATER 330ML");
        codeToProductName.put("4891513202014", "7 UP SOFT DRINK 330ML");
        codeToProductName.put("4890008110308", "SPRITE LEMON-LIME FLAVOURED SODA 330ML");
        codeToProductName.put("6920180209694", "WATSONS TONIC WATER 330ML");
        codeToProductName.put("4890008661350", "OOHA PEACH & OOLONG TEA FLAVOURED SPARKING BEVERAGE 330ML");
        codeToProductName.put("4890008660353", "OOHA YUZU & SEA SALT FLAVORED SPARKLING BEVERAGE 330ML");
        codeToProductName.put("6920180212168", "WATSONS SPARKLING SARSAE DRINK 330ML");
        codeToProductName.put("4890008411306", "Schweppes +C LEMON SODA 330ML");
        codeToProductName.put("4890008110292", "SPRITE LEMON-LIME FLAVOURED SODA 2L");
        codeToProductName.put("4897120810123", "Mezzanine Makers Herbal Tonic Water 250ML");
        codeToProductName.put("4897120810178", "Mezzanine Makers Lemon Iron Buddha Sparkling Tea 330ML");
        codeToProductName.put("471028023022", "OCEANIC APPLE SIDRA 300ML");
        codeToProductName.put("4890008404360", "Schweppes DRY GINGER ALE MINI CAN 200ML");
        codeToProductName.put("4891133318232", "WATSONS SODA WATER-LIME FLAVOURED 420ML");
        codeToProductName.put("4897120810192", "Mezzanine Makers Spicy Ginger Soda 250ML");
        codeToProductName.put("4897120810147", "Mezzanine Makers Strawberry Yerba Mate Sparkling Tea 330ML");
        codeToProductName.put("4890008407309", "Schweppes GRAPEFRUIT SODA 330ML");
        codeToProductName.put("4890008123308", "FANTA GRAPE DRINK 330ML");
        codeToProductName.put("9311493001773", "BUNDABERG GUAVA SPARKLING DRINK 375ML");
        codeToProductName.put("9311493000769", "BUNDABERG DIET GINGER BEER 375ML");
        codeToProductName.put("4890008405367", "Schweppes SPICY GINGER BEER SODA (GINGER FLAVORED) MINI CAN 200ML");
        codeToProductName.put("6920180210775", "WATSONS LEMONGRASS FLAVOURED SODA WATER 330ML");
        codeToProductName.put("4891513000016", "EVERVESS SODA WATER 330ML");
        codeToProductName.put("4892214253039", "TAO TI KYOHO GRAPE JUICE DRINK WITH NATA DE COCO 340ML");
        codeToProductName.put("4892214055176", "MEKO KOREAN CITRON TEA 430ML");
        codeToProductName.put("4892214250168", "TAO TI MANDARIN LEMON 500ML");
        codeToProductName.put("4891028723646", "VITA Osmanthus Chrysanthemum Tea 500ML");
        codeToProductName.put("885012295574", "RIBENA RIBENA JELLY REG 160G");
        codeToProductName.put("4892214256375", "TAO TI SUPREME META TEA 900ML");
        codeToProductName.put("4897083330416", "MAMA WORKSHOP LEMON WITH ROCK SUGAR 350ML");
        codeToProductName.put("4892214250267", "TAO TI HONEY GREEN TEA 1.5L");
        codeToProductName.put("4890008531615", "NESTEA HONEY PEAR TEA 250ML");
        codeToProductName.put("4897083330485", "MAMA WORKSHOP STEWED LEMON WITH OSMANTHUS AND ROCK SUGAR 350ML");
        codeToProductName.put("4909411031787", "KIRIN AFTERNOON TEA LEMON TEA 1.5L");
        codeToProductName.put("6920180216173", "CRYSTAL SPRING LEMON TEA DRINK 330ML");
        codeToProductName.put("4890008571246", "AUTHENTIC TEA HOUSE DAHONGPAO OOLONG TEA 500ML");
        codeToProductName.put("4710421072871", "ROYAL TEA GARDEN JUICY LEMON TEA 500ML");
        codeToProductName.put("4890008711246", "HEALTHWORKS SELF-HEAL SPIKE DRINK 500ML");
        codeToProductName.put("4890008720248", "HEALTHWORKS HAWTHORN APPLE JUICE DRINK 500M");
        codeToProductName.put("4895016026436", "HUNG FOOK TONG SUGARCANE AND SEA COCONUT WITH CARROT DRINK 500ML");
        codeToProductName.put("4909411084639", "KIRIN RICH GREEN TEA 525ML");
        codeToProductName.put("4892214257525", "KITAGAWA LEGEND WHITE PEACH OOLONG TEA 430ML");
        codeToProductName.put("4892214054629", "MEKO PEACH TEA 430ML");
        codeToProductName.put("4892214054865", "MEKO LEMON TEA 430ML");
        codeToProductName.put("4892214055176", "MEKO KOREAN CITRON TEA 430ML");
        codeToProductName.put("4897009888953", "TAI HING HONG KONG STYLE MILK TEA 250ML");
        codeToProductName.put("4891118043432", "NESCAFE FULL ROAST-CASE 250ML");
        codeToProductName.put("4891444008600", "Kamcha HONG KONG STYLE MILK TEA 280ML");
        codeToProductName.put("4890008500314", "NESCAFE RTD COFFEE WITH MILK & SUGAR-CASE 250ML");
        codeToProductName.put("4897009880285", "TAI HING Coffee 250ML");
        codeToProductName.put("6957354240078", "STARBUCKS FRAPPUCCINO-MOCHA 281ML");
        codeToProductName.put("6957354240016", "STARBUCKS FRAPPUCCINO-COFFEE 281ML");
        codeToProductName.put("7394376614552", "OATLY OAT DRINK-CHOCOLATE 1L");
        codeToProductName.put("7394376616228", "OATLY OAT DRINK-BARISTA EDITION RADOM 1L");
        codeToProductName.put("7394376123337", "OALTY OAT DRINK-ORGANIC 1L");
        codeToProductName.put("4897036420768", "COW & GATE MILK+ 180ML");
        codeToProductName.put("5060278790558", "DAIONI ORGANIC ORGANIC SEMI-SKIMMED MILK-BANANA 200ML");
        codeToProductName.put("5060278790619", "DAIONI ORGANIC ORGANIC SEMI-SKIMMED MILK-STRAWBERRY 200ML");
        codeToProductName.put("5029674000722", "DAIONI ORGANIC ORGANIC WHOLE MILK 1L");
        codeToProductName.put("7640142777014", "FRED & CHLOE FULL CREAM MILK 1L");
        codeToProductName.put("7640142777007", "FRED & CHLOE SEMI-SKIMMED MILK 1L");
        codeToProductName.put("9311697101583", "HARVEY FRESH SKIM MILK 1L");
        codeToProductName.put("9311697100173", "HARVEY FRESH FULL CREAM MILK 1L");
        codeToProductName.put("8885012294911", "LUCOZADE ENERGY COLA 300ML");
        codeToProductName.put("8801097250031", "POCARI ION SUPPLY DRINK 1.5L");
        codeToProductName.put("8885012291781", "LUCOZADE SPORT ORANGE 450ML");
        codeToProductName.put("8885012290036", "LUCOZADE ENERGY ORANGE 300ML");
        codeToProductName.put("8885012290012", "LUCOZADE ENERGY REGULAR 300ML");
        codeToProductName.put("4716426130516", "POCARI ION SUPPLY DRINK 340ML");
        codeToProductName.put("899959002670", "ENERGY WATT SPORTS DRINK 800ML");
        codeToProductName.put("4891034024294", "GATORADE SPORT DRINK-ORANGE FLAVOUR 600ML");
        codeToProductName.put("4891034024300","GATORADE SPORT DRINK-LEMON LIME 600ML");
        codeToProductName.put("90448621","RED BULL ENERGY DRINK THE RED EDITION 250ML");

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
        codeToSugar.put("8885012290272", "9");
        codeToSugar.put("4890008711246", "8");
        codeToSugar.put("4890008724246", "8");
        codeToSugar.put("4892214250168", "10");
        codeToSugar.put("4892214250915", "0");
        codeToSugar.put("4892214250021", "4");
        codeToSugar.put("4892214250625", "7");
        codeToSugar.put("8885012291781", "6");
        codeToSugar.put("4895016008142", "6");
        codeToSugar.put("4895016002140", "6");
        codeToSugar.put("4895016004144", "8");
        codeToSugar.put("4895016025361", "6");
        codeToSugar.put("4895016010145", "6");
        codeToSugar.put("4890008200511", "9");
        codeToSugar.put("4711161766037", "12");
        codeToSugar.put("4895016023497", "8");
        codeToSugar.put("3057640100673", "0");
        codeToSugar.put("8885012290036", "10");
        codeToSugar.put("4891028711995", "7");
        codeToSugar.put("4890008330249", "5");
        codeToSugar.put("4890008515240", "14");
        codeToSugar.put("6937761805022", "8");
        codeToSugar.put("4890008411238", "9");
        codeToSugar.put("4890008333240", "0");
        codeToSugar.put("4893342710203", "7");
        codeToSugar.put("4891034024300", "9");
        codeToSugar.put("4897020730019", "7");
        codeToSugar.put("4710035368704", "3");
        codeToSugar.put("4890008200146", "26");
        codeToSugar.put("8885012290258", "10");
        codeToSugar.put("4897036691489", "0");
        codeToSugar.put("4890008330140", "13");
        codeToSugar.put("9311493000769", "1");
        codeToSugar.put("90162602", "7");
        codeToSugar.put("90162800", "0");
        codeToSugar.put("4897036691441", "10");
        codeToSugar.put("4901306017098", "15");
        codeToSugar.put("8801097250031", "25");
        codeToSugar.put("8888200615062", "24");
        codeToSugar.put("4891944002160", "24");
        codeToSugar.put("4891133364505", "26");
        codeToSugar.put("8885012290494", "26");
        codeToSugar.put("31200449481", "29");
        codeToSugar.put("9421903084088", "44");
        codeToSugar.put("8887333110116", "30");
        codeToSugar.put("41800207503", "28");
        codeToSugar.put("4893342711699", "164");
        codeToSugar.put("4004191014125", "7");
        codeToSugar.put("4893342712603", "164");
        codeToSugar.put("4890008400300", "9");
        codeToSugar.put("4890008100309", "9");
        codeToSugar.put("4890008101306", "0");
        codeToSugar.put("8935049501596", "0");
        codeToSugar.put("4890008110360", "2");
        codeToSugar.put("4890008120307", "11");
        codeToSugar.put("4891133332733", "4");
        codeToSugar.put("4891513202014", "6");
        codeToSugar.put("4890008110308", "4");
        codeToSugar.put("6920180209694", "7");
        codeToSugar.put("4890008661350", "0");
        codeToSugar.put("4890008660353", "0");
        codeToSugar.put("6920180212168", "8");
        codeToSugar.put("4890008411306", "6");
        codeToSugar.put("4890008110292", "54");
        codeToSugar.put("4897120810123", "5");
        codeToSugar.put("4897120810178", "3");
        codeToSugar.put("471028023022", "7");
        codeToSugar.put("4890008404360", "4");
        codeToSugar.put("4891133318232", "5");
        codeToSugar.put("4897120810192", "4");
        codeToSugar.put("4897120810147","3");
        codeToSugar.put("4890008407309","9");
        codeToSugar.put("4890008123308","10");
        codeToSugar.put("9311493001773","11");
        codeToSugar.put("9311493000769","1");
        codeToSugar.put("4890008405367","5");
        codeToSugar.put("6920180210775","7");
        codeToSugar.put("4891513000016","0");
        codeToSugar.put("4892214253039","8");
        codeToSugar.put("4892214055176","8");
        codeToSugar.put("4892214250168","8");
        codeToSugar.put("4891028723646","6");
        codeToSugar.put("885012295574","6");
        codeToSugar.put("4892214256375","0");
        codeToSugar.put("4897083330416","8");
        codeToSugar.put("4892214250267","12");
        codeToSugar.put("4890008531615","5");
        codeToSugar.put("4897083330485","7");
        codeToSugar.put("4909411031787","26");
        codeToSugar.put("6920180216173","8");
        codeToSugar.put("4890008571246","0");
        codeToSugar.put("4710421072871","16");
        codeToSugar.put("4890008711246","8");
        codeToSugar.put("4890008720248","10");
        codeToSugar.put("4895016026436","8");
        codeToSugar.put("4909411084639","0");
        codeToSugar.put("4892214257525","8");
        codeToSugar.put("4892214054629","8");
        codeToSugar.put("4892214054865","9");
        codeToSugar.put("4892214055176","8");
        codeToSugar.put("4897009888953","5");
        codeToSugar.put("4891118043432","4");
        codeToSugar.put("4891444008600","6");
        codeToSugar.put("4890008500314","4");
        codeToSugar.put("4897009880285","5");
        codeToSugar.put("6957354240078","7");
        codeToSugar.put("6957354240016","7");
        codeToSugar.put("7394376614552","19");
        codeToSugar.put("7394376616228","10");
        codeToSugar.put("7394376123337","10");
        codeToSugar.put("4897036420768","2");
        codeToSugar.put("5060278790558","5");
        codeToSugar.put("5060278790619","5");
        codeToSugar.put("5029674000722","12");
        codeToSugar.put("7640142777014","12");
        codeToSugar.put("7640142777007","4");
        codeToSugar.put("9311697101583","12");
        codeToSugar.put("9311697100173","12");
        codeToSugar.put("8885012294911","9");
        codeToSugar.put("8801097250031","25");
        codeToSugar.put("8885012291781","6");
        codeToSugar.put("8885012290036","10");
        codeToSugar.put("8885012290012","8");
        codeToSugar.put("4716426130516","6");
        codeToSugar.put("899959002670","13");
        codeToSugar.put("4891034024294","9");
        codeToSugar.put("4891034024300","9");
        codeToSugar.put("90448621","7");
        codeToSugar.put("4891034024294","0");

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

    }

    @Override
    public void onClick(View view) {
        Class class_array[]={history_record.class,setting_page.class,scanning_barcode.class};
        for(int i = 0 ; i<ib_id.length;i++){
            if(view.getId()==ib_id[i]&&view.getId()!=ib_id[2]){
                switch (i) {
                    //history xml
                    case 0:
                        StartNewActivity(class_array[i]);
                        break;
                    //setting xml
                    case 1:
                        Intent intent =new Intent(MainActivity.this,setting_page.class);
                        startActivityForResult(intent,1);
                        break;
                }
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

// Retrieve SharedPreferences data
                    SharedPreferences todaysugar_preferences = getSharedPreferences(todaysug, MODE_PRIVATE);
                    String global_Sugar1 = todaysugar_preferences.getString(no_sugar, "0");
                    today_sum_SD = Double.parseDouble(todaysugar_preferences.getString(percent_value, "0"));

// Add the new global_Sugar value received from user input
                    String new_global_Sugar = global_Sugar; // Replace this with the actual value from user input
                    int total_global_Sugar = Integer.parseInt(global_Sugar1) + Integer.parseInt(new_global_Sugar);

// Perform your calculations and update the today_sum_SD
                    double formulaForStandardValue = total_global_Sugar * 4.0 / 50.0;
                    formulaForStandardValue *= 100.0;

                    today_sum_SD = formulaForStandardValue;
                    Log.d("main_activity", "number of " + today_sum_SD);
                    standardValue.setText(Double.toString(today_sum_SD) + "% of standard value");
                    cubesOfSugar.setText(Integer.toString(total_global_Sugar));
                    Log.d("Vincent", "onActivityResult: generating sugar");
                    Generating_Sugars(targetSugar);

// Save the updated values to SharedPreferences
                    SharedPreferences.Editor sugar_editor = todaysugar_preferences.edit();
                    sugar_editor.putString(no_sugar, Integer.toString(total_global_Sugar));
                    sugar_editor.putString(percent_value, Double.toString(today_sum_SD));
                    sugar_editor.apply();


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
                    TimeZone t = TimeZone.getTimeZone("Asia/Hong_Kong") ;
                    Calendar c = Calendar.getInstance(t);
                    // get the current time in milliseconds
                    long currentTimeMillis = c.getTimeInMillis();

// create a date object with the current time
                    Date currentTime = new Date(currentTimeMillis);

// create a date format for displaying the time
                    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");

// set the time zone of the date format to the current time zone
                    dateFormat.setTimeZone(t);

// format the current time as a string
                    String currentTimeString = dateFormat.format(currentTime);

                    // update time and pass it

                    //current_Date.setText(time);

                    String partition = "";
                    String ml1 = "";
                    String ml2 = "";
                    partition+=codeToProductName.get(targetBarcode);

                    char d = partition.charAt(partition.length()-1);
                    if(d=='L')
                    {
                        int index = partition.length()-1;
                        while(partition.charAt(index)!=' ')
                        {
                            ml1+=partition.charAt(index);
                            index--;
                        }
                    }
                    int i = ml1.length()-1;
                    while(i>=0)
                    {
                        ml2 += ml1.charAt(i);
                        i--;
                    }
                    ml1 = "";
                    i=0;
                    while(partition.charAt(i)!=ml2.charAt(0))
                    {
                        ml1+=partition.charAt(i);
                        i++;
                    }
                    String s = codeToSugar.get(targetBarcode);
                    dataList.add(s+"  "+  ml1+  "("+ml2+")" + " "+ currentTimeString);

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

                }
            }
        }

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            String color = data.getStringExtra("color");
            if (color != null) {
                Log.d("main_activity", "get the color code of : " + color);
                CL1.setBackgroundColor(Color.parseColor(color));}
        }
    }

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(),result ->
    {


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
                global_Sugar = String.valueOf(a);
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

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(100, 100); // Adjust width and height as needed
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
        int velocityIterations = 8;  //Hiher the number will be more relastic
        int positionIterations = 3;  // but it will use more user resources

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



}
