package com.example.donogear.actionpages;

import androidx.appcompat.app.AppCompatActivity;
import com.example.donogear.R;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;

public class NotificationsActivity extends AppCompatActivity {

    public Switch sw55;
    public Switch sw82;
    public Switch sw91;
    public Switch sw90;
    public Switch sw10;
    public Switch sw87;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);


        //Switch Start
        sw55 = (Switch) findViewById(R.id.switch55);
        //shared prefs
        SharedPreferences sharedPreferences=getSharedPreferences("save",MODE_PRIVATE);
        sw55.setChecked(sharedPreferences.getBoolean("value",true));

        sw55.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Clicked");
                if(sw55.isChecked())
                {
                    System.out.println("abc");
                    SharedPreferences.Editor editor=getSharedPreferences("save",MODE_PRIVATE).edit();
                    editor.putBoolean("value",true);
                    editor.apply();
                    sw55.setChecked(true);
                }
                else
                {
                    System.out.println("dfg");
                    SharedPreferences.Editor editor=getSharedPreferences("save",MODE_PRIVATE).edit();
                    editor.putBoolean("value",false);
                    editor.apply();
                    sw55.setChecked(false);
                }
            }
        });




//        //Switch End
//
//        sw82 = (Switch) findViewById(R.id.switch82);
//        sw82.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
//
//        sw91 = (Switch) findViewById(R.id.switch91);
//        sw91.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
//
//        sw90 = (Switch) findViewById(R.id.switch90);
//        sw90.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
//
//        sw10 = (Switch) findViewById(R.id.switch10);
//        sw10.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
//
//        sw87 = (Switch) findViewById(R.id.switch87);
//        sw87.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });

    }
}
