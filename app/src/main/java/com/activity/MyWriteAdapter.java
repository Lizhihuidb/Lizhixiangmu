package com.activity;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.bean.PagerItemInfo;
import com.yinghuanhang.pdf.parser.R;

import java.util.ArrayList;


public class MyWriteAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater inflater;
    private ArrayList<PagerItemInfo> data;

    PaletteView palt;
    private String TAG = "MyWriteAdapter";

    public MyWriteAdapter(Context context, ArrayList<PagerItemInfo> data) {
        mContext = context;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewGroup layout;
        if (convertView == null) {
            layout = (ViewGroup) inflater.inflate(R.layout.item_pager, null);
            Log.i(TAG, "***getView: layout");
            final PaletteView paletteView = (PaletteView) layout.findViewById(R.id.palette);
            paletteView.setCallback(new PaletteView.Callback() {
                @Override
                public void onUndoRedoStatusChanged() {
                    MyWriteActivity activity = (MyWriteActivity) mContext;
                    activity.mRedoView.setEnabled(paletteView.canRedo());
                    activity.mUndoView.setEnabled(paletteView.canUndo());
                    Log.i(TAG, "***onUndoRedoStatusChanged: ");
                }
            });
        } else {
            layout = (ViewGroup) convertView;
            Log.i(TAG, "***getView: convertView");
        }
        setViewContent(layout, position);
        return layout;
    }

    /**
     * @param group
     * @param position
     */
    private void setViewContent(View group, final int position) {
        palt = (PaletteView) group.findViewById(R.id.palette);

        if (position % 3 == 0) {
            palt.setBackgroundResource(R.drawable.x1);
        } else if (position % 3 == 1) {
            palt.setBackgroundResource(R.drawable.x2);
        } else if (position % 3 == 2) {
            palt.setBackgroundResource(R.drawable.x3);
        }
    }

}
