package com.sdgm.map.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sdgm.map.LoginActivity;
import com.sdgm.map.R;

public class BootActivity extends AppCompatActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.boot);
        Thread myThread=new Thread(){
          public void run(){
              try{
                  sleep(3000);
                  Intent it =new Intent(getApplicationContext(), LoginActivity.class);
                  startActivity(it);
                  finish();//关闭当前活动
              }catch (Exception e){
                  e.printStackTrace();
              }
          }
        };
        myThread.start();
    }
}
