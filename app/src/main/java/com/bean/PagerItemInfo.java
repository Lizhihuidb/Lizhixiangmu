package com.bean;

import android.graphics.Path;
import android.view.View;

import com.activity.PaletteView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by think on 2018/2/4.
 */

public class PagerItemInfo {
    private Path path;
    private View view;
    private List<PaletteView.DrawingInfo> drawingInfos = new ArrayList<>();

    public PagerItemInfo() {

    }


    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public List<PaletteView.DrawingInfo> getDrawingInfos() {
        return drawingInfos;
    }

    public void setDrawingInfos(List<PaletteView.DrawingInfo> drawingInfos) {
        if (drawingInfos.size() > 0) {
            this.drawingInfos.clear();
        }
        this.drawingInfos.addAll(drawingInfos);
    }
}
