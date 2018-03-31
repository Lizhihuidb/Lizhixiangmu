package com.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.yinghuanhang.pdf.parser.R;

//导出导入
public class ExportImportActivity extends AppCompatActivity implements OnClickListener{

    private Button mExport,mImport,mBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_import);
        init();
    }

    void init(){
        mBack = (Button) findViewById(R.id.back);
        mExport = (Button) findViewById(R.id.btn_export);
        mImport = (Button) findViewById(R.id.btn_import);
        mExport.setOnClickListener(this);
        mImport.setOnClickListener(this);
        mBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()){
            case R.id.btn_export:
                intent = new Intent(ExportImportActivity.this,ExportActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_import:

                break;
            case R.id.back:
                finish();
                break;
        }
    }
}
