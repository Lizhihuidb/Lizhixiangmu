package com.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.usbtest.uMainActivity;
import com.yinghuanhang.pdf.parser.R;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener{

    Button mRead,mWrite,mLook;
    ImageView mIread,mIwrite,mIlook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题
        setContentView(R.layout.activity_welcome);
        init();
    }

    void init(){
        mRead = (Button) findViewById(R.id.btn_read);
        mIread = (ImageView) findViewById(R.id.iv_read);
        mWrite = (Button) findViewById(R.id.btn_write);
        mIwrite = (ImageView) findViewById(R.id.iv_write);
        mRead.setOnClickListener(this);mIread.setOnClickListener(this);
        mWrite.setOnClickListener(this);mIwrite.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        //阅读
         if (v.getId()==R.id.iv_read||v.getId()==R.id.btn_read){
             intent = new Intent(this,uMainActivity.class);
             startActivity(intent);
         }
         //签名
         if (v.getId()==R.id.btn_write||v.getId()==R.id.iv_write){
             intent = new Intent(this,MyWriteActivity.class);
             startActivity(intent);
         }

    }
}
