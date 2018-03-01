package com.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
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
import android.widget.Toast;

import com.bean.PagerItemInfo;
import com.yinghuanhang.pdf.parser.R;
import com.yinghuanhang.pdf.parser.view.MyPageWidget;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MyWriteActivity extends AppCompatActivity
        implements View.OnClickListener/*, PaletteView.Callback*/, Handler.Callback {

    public View mUndoView;
    public View mRedoView;
    private View mPenView;
    private View mEraserView;
    private View mClearView;
    private ProgressDialog mSaveProgressDlg;
    private static final int MSG_SAVE_SUCCESS = 1;
    private static final int MSG_SAVE_FAILED = 2;
    //private Handler mHandle;
    private Handler mHandler;
    //    private PageWidget page = null;
    private MyPageWidget page = null;
    private int width = -1;
    private int hight = -1;
    private MyWriteAdapter adapter;
    private String TAG = "WriteActivity";
    private ArrayList<PagerItemInfo> mItemInfos;
    private int lastPos;
    private int mCurrentPos;
    //    private PaletteView currentPaletteView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//设置横屏切换
        setContentView(R.layout.item_one);

        mItemInfos = new ArrayList<>();
        for (int i = 0; i < 500; i++) {
            mItemInfos.add(new PagerItemInfo());
        }
//        page = (PageWidget) findViewById(R.id.write_pageWidget);
        page = (MyPageWidget) findViewById(R.id.write_pageWidget);
        width = getWindowManager().getDefaultDisplay().getWidth(); // 屏幕宽（像素，如：720px）
        hight = getWindowManager().getDefaultDisplay().getHeight(); // 屏幕高（像素，如：1280px）

        adapter = new MyWriteAdapter(this, mItemInfos);
        /*adapter.setOnDrawingChangeListener(new MyWriteAdapter.OnDrawingChangeListener() {
            @Override
            public void onDrawingChange(PaletteView paletteView) {
                mUndoView.setEnabled(paletteView.canUndo());
                mRedoView.setEnabled(paletteView.canRedo());
            }
        });*/
        page.setAdapter(adapter);
        page.setOnPageTurnListener(new MyPageWidget.OnPageTurnListener() {
            @Override
            public void onTurn(int count, int currentPosition) {
                Log.i(TAG, "onTurn: " + "翻页到" + currentPosition + "/" + count + "---ChildCount" + page.getChildCount());
                mCurrentPos = currentPosition;
                /*if (lastPos < currentPosition) {
                    //递增翻页,
                    Log.i(TAG, "***onTurn: " + "递增翻页" + currentPosition + "/" + count);

                } else {
                    //递减翻页
                    Log.i(TAG, "***onTurn: " + "递减翻页" + currentPosition + "/" + count);
                }*/
                PaletteView lastPaletteView = (PaletteView) page.getChildAt(0).findViewById(R.id.palette);
                PagerItemInfo pagerItemInfo = mItemInfos.get(lastPos);
                //备份刚划过的页面数据
                pagerItemInfo.setDrawingInfos(lastPaletteView.backups());
                //清除刚划过的页面数据
                lastPaletteView.clear();
                lastPos = currentPosition;

                //获取当前页面的view,这只tag.
                PaletteView currentPaletteView = (PaletteView) page.getChildAt(0).findViewById(R.id.palette);
                /*currentPaletteView.setCallback(new PaletteView.Callback() {
                    @Override
                    public void onUndoRedoStatusChanged() {
                        mUndoView.setEnabled(currentPaletteView.canUndo());
                        mRedoView.setEnabled(currentPaletteView.canRedo());
                        Log.i(TAG, "***onUndoRedoStatusChanged: -->currentPaletteView");
                    }
                });*/
                //恢复当前页面数据
                if (null != mItemInfos.get(currentPosition).getDrawingInfos()
                        && 0 != mItemInfos.get(currentPosition).getDrawingInfos().size()) {
                    currentPaletteView.recover(mItemInfos.get(currentPosition).getDrawingInfos());
                    Log.i(TAG, "***recover: " + currentPosition);
                }
            }
        });

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

        findViewById(R.id.btn_last).setOnClickListener(this);
        findViewById(R.id.btn_next).setOnClickListener(this);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(MSG_SAVE_FAILED);
        mHandler.removeMessages(MSG_SAVE_SUCCESS);
    }

    private void initSaveProgressDlg() {
        if (null == mSaveProgressDlg) {
            mSaveProgressDlg = new ProgressDialog(this);
        }
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
//                        Bitmap bm = mPaletteView.buildBitmap();
                        PaletteView currentPaletteView = (PaletteView) page.getChildAt(0).findViewById(R.id.palette);
                        Bitmap bm = currentPaletteView.buildBitmap();
                        String savedFile = saveImage(bm, 100);
                        if (savedFile != null) {
                            scanFile(MyWriteActivity.this, savedFile);
                            mHandler.obtainMessage(MSG_SAVE_SUCCESS).sendToTarget();
                        } else {
                            mHandler.obtainMessage(MSG_SAVE_FAILED).sendToTarget();
                        }
                    }
                }).start();
                break;
        }
        return true;
    }

   /* @Override
    public void onUndoRedoStatusChanged() {
        Log.i(TAG, "*****onUndoRedoStatusChanged: ");
        mUndoView.setEnabled(mPaletteView.canUndo());
        mRedoView.setEnabled(mPaletteView.canRedo());
    }*/

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.undo://回退,
               /* mPaletteView.undo();*/
                PaletteView currentPaletteView = (PaletteView) page.getChildAt(0).findViewById(R.id.palette);
                currentPaletteView.undo();
                break;
            case R.id.redo://前进
                /*mPaletteView.redo();*/
                PaletteView currentPaletteView1 = (PaletteView) page.getChildAt(0).findViewById(R.id.palette);
                currentPaletteView1.redo();
                break;
            case R.id.pen://画笔
                v.setSelected(true);
                mEraserView.setSelected(false);
                /*mPaletteView.setMode(PaletteView.Mode.DRAW);*/
                PaletteView currentPaletteView2 = (PaletteView) page.getChildAt(0).findViewById(R.id.palette);
                currentPaletteView2.setMode(PaletteView.Mode.DRAW);
                break;
            case R.id.eraser://橡皮
                v.setSelected(true);
                mPenView.setSelected(false);
                /*mPaletteView.setMode(PaletteView.Mode.ERASER);*/
                PaletteView currentPaletteView3 = (PaletteView) page.getChildAt(0).findViewById(R.id.palette);
                currentPaletteView3.setMode(PaletteView.Mode.ERASER);
                break;
            case R.id.clear://清除
               /* mPaletteView.clear();*/
                PaletteView currentPaletteView4 = (PaletteView) page.getChildAt(0).findViewById(R.id.palette);
                currentPaletteView4.clear();
                break;

            case R.id.btn_last:
                //Toast.makeText(this, "last", Toast.LENGTH_SHORT).show();
                int lastPosition = mCurrentPos - 1;
                if (lastPosition < 0) {
                    Toast.makeText(this, "没有上一页拉!", Toast.LENGTH_SHORT).show();
                    return;
                }
                this.page.setCurrentPosition(lastPosition);
                break;
            case R.id.btn_next:
                int nextPosition = mCurrentPos + 1;
                if (nextPosition > adapter.getCount()-1) {
                    Toast.makeText(this, "没有下一页拉!", Toast.LENGTH_SHORT).show();
                    return;
                }
                this.page.setCurrentPosition(nextPosition);
                break;
        }
    }
}
