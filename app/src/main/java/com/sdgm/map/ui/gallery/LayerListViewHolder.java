package com.sdgm.map.ui.gallery;

import android.widget.CheckBox;
import android.widget.TextView;

public class LayerListViewHolder {
    private CheckBox checkBox ;
    private TextView textView ;
    public LayerListViewHolder() {}
    public LayerListViewHolder( TextView textView, CheckBox checkBox ) {
        this.checkBox = checkBox ;
        this.textView = textView ;
    }
    public CheckBox getCheckBox() {
        return checkBox;
    }
    public void setCheckBox(CheckBox checkBox) {
        this.checkBox = checkBox;
    }
    public TextView getTextView() {
        return textView;
    }
    public void setTextView(TextView textView) {
        this.textView = textView;
    }
}
