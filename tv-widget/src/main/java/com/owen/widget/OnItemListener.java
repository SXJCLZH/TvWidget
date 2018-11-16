package com.owen.widget;

import android.view.View;
import android.view.ViewGroup;

/**
 *
 * @author owen
 * @date 16/2/28
 */
public interface OnItemListener {
    void onItemSelected(ViewGroup parent, View itemView, int position);
    void onItemClick(ViewGroup parent, View itemView, int position);
}
