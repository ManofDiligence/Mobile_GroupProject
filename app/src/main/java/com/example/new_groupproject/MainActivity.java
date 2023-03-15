package com.example.new_groupproject;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    ImageButton ib_list[]=new ImageButton[3];
    int ib_id[] = {R.id.history_ib,R.id.setting_ib,R.id.qrscan_ib};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init_object();

    }
    public void init_object(){
        for(int i = 0 ; i<ib_id.length;i++){
            ib_list[i] = findViewById(ib_id[i]);
            ib_list[i].setOnClickListener(this);
        }
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
    //New activity with no things pass thoruh, only start new activity
    public void StartNewActivity(Class i){
        Intent c = new Intent(getApplicationContext(),i);
        startActivity(c);
    }

    //just some scaninning codes
    private void scanCode(){
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setOrientationLocked(true);
        options.setCaptureActivity(scanning_barcode.class);
        barLauncher.launch(options);
    }
    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(),result -> {
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
}
