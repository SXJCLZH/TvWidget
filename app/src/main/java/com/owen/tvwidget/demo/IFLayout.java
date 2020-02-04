package com.owen.tvwidget.demo;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by owen on 2017/6/21.
 */

public class IFLayout extends FrameLayout {

    public IFLayout(@NonNull Context context) {
        super(context);
    }

    public IFLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public IFLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected boolean verifyDrawable(@NonNull Drawable who) {
        Log.i("@@@", "verifyDrawable....");
        if(!hasFocus())
            return false;
//        if(getBackground() == who && (!hasFocus() || !isSelected())) {
//            return false;
//        }
        return super.verifyDrawable(who);
    }
}
