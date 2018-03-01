package com.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Path;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.bean.PagerItemInfo;
import com.yinghuanhang.pdf.parser.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class WriteActivity extends AppCompatActivity
        implements View.OnClickListener, PaletteView.Callback, Handler.Callback {

    private View mUndoView;
    private View mRedoView;
    private View mPenView;
    private View mEraserView;
    private View mClearView;
    //    private PaletteView mPaletteView;
    private ProgressDialog mSaveProgressDlg;
    private static final int MSG_SAVE_SUCCESS = 1;
    private static final int MSG_SAVE_FAILED = 2;
    private Handler mHandler;

    /*private ArrayList<String> mdata = new ArrayList<String>();*/
    private PageWidget page = null;
    private int width = -1;
    private int hight = -1;
    private BaseAdapter adapter;
    private String TAG = "WriteActivity";
    private ArrayList<PagerItemInfo> mItemInfos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//设置横屏切换
        setContentView(R.layout.item_one);
       /* mdata = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            mdata.add(i + "");
        }*/

        mItemInfos = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            mItemInfos.add(new PagerItemInfo() {
                {
                    setPath(new Path());
                    setView(View.inflate(WriteActivity.this, R.layout.item_pager, null));
                }
            });
        }
        page = (PageWidget) findViewById(R.id.write_pageWidget);
        width = getWindowManager().getDefaultDisplay().getWidth(); // 屏幕宽（像素，如：720px）
        hight = getWindowManager().getDefaultDisplay().getHeight(); // 屏幕高（像素，如：1280px）

        /*adapter = new WriteAdapter(this, mdata);*/
        //adapter = new WriteAdapter(this, mItemInfos);
        adapter = new MyWriteAdapter(this, mItemInfos);
        page.setAdapter(adapter);
        page.setOnPageTurnListener(new PageWidget.OnPageTurnListener() {
            @Override
            public void onTurn(int count, int currentPosition) {
                Log.i(TAG, "onTurn: " + "翻页到" + currentPosition + "/" + count);
            }
        });

        //mdata.clear();


//        mPaletteView = (PaletteView) findViewById(R.id.palette);
//        mPaletteView.setCallback(this);

        mUndoView = findViewById(R.id.undo);
        mRedoView = findViewById(R.id.redo);
        mPenView = findViewById(R.id.pen);
        mPenView.setSelected(true);
        mEraserView = findViewById(R.id.eraser);
        mClearView = findViewById(R.id.clear);

        mUndoView.setOnClickListener(this);
        mRedoView.setOnClickListener(this);
        mPenView.setOnClickListener(this);
        mEraserView.setOnClickListener(this);
        mClearView.setOnClickListener(this);

        mUndoView.setEnabled(false);
        mRedoView.setEnabled(false);

        mHandler = new Handler(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(MSG_SAVE_FAILED);
        mHandler.removeMessages(MSG_SAVE_SUCCESS);
    }

    private void initSaveProgressDlg() {
        mSaveProgressDlg = new ProgressDialog(this);
        mSaveProgressDlg.setMessage("正在保存,请稍候...");
        mSaveProgressDlg.setCancelable(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_SAVE_FAILED:
                mSaveProgressDlg.dismiss();
                Toast.makeText(this, "保存失败", Toast.LENGTH_SHORT).show();
                break;
            case MSG_SAVE_SUCCESS:
                mSaveProgressDlg.dismiss();
                Toast.makeText(this, "画板已保存", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    private static void scanFile(Context context, String filePath) {
        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        scanIntent.setData(Uri.fromFile(new File(filePath)));
        context.sendBroadcast(scanIntent);
    }

    private static String saveImage(Bitmap bmp, int quality) {
        if (bmp == null) {
            return null;
        }
        File appDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (appDir == null) {
            return null;
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, quality, fos);
            fos.flush();
            return file.getAbsolutePath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                if (mSaveProgressDlg == null) {
                    initSaveProgressDlg();
                }
                mSaveProgressDlg.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        /*Bitmap bm = mPaletteView.buildBitmap();
                        String savedFile = saveImage(bm, 100);
                        if (savedFile != null) {
                            scanFile(WriteActivity.this, savedFile);
                            mHandler.obtainMessage(MSG_SAVE_SUCCESS).sendToTarget();
                        } else {
                            mHandler.obtainMessage(MSG_SAVE_FAILED).sendToTarget();
                        }*/
                    }
                }).start();
                break;
        }
        return true;
    }

    @Override
    public void onUndoRedoStatusChanged() {
        Log.i(TAG, "*****onUndoRedoStatusChanged: ");
       /* mUndoView.setEnabled(mPaletteView.canUndo());
        mRedoView.setEnabled(mPaletteView.canRedo());*/
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.undo://回退,
               /* mPaletteView.undo();*/
                break;
            case R.id.redo://前进
                /*mPaletteView.redo();*/
                break;
            case R.id.pen://画笔
                v.setSelected(true);
                mEraserView.setSelected(false);
                /*mPaletteView.setMode(PaletteView.Mode.DRAW);*/
                break;
            case R.id.eraser://橡皮
                v.setSelected(true);
                mPenView.setSelected(false);
               /* mPaletteView.setMode(PaletteView.Mode.ERASER);*/
                break;
            case R.id.clear://清除
               /* mPaletteView.clear();*/
                break;
        }
    }
}
