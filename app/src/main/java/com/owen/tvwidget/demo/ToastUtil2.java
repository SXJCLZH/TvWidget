package com.owen.tvwidget.demo;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 *
 * @author owen
 * @date 16/8/23
 */
public class ToastUtil2 {
    
    private static ToastUtil2 mToastUtil2;

    private Toast toast;
    private LinearLayout toastView;

    public static ToastUtil2 getInstance(Context context){
        if(null == mToastUtil2) {
            mToastUtil2 = new ToastUtil2(context);
        }
        return mToastUtil2;
    }

    /**
     * 完全自定义布局Toast
     * @param context
     */
    private ToastUtil2(Context context){
        toast = new Toast(context);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_toast, null);
        toast.setView(view);
        toast.setGravity(Gravity.CENTER, 0, 0);
    }

    /**
     * 向Toast中添加自定义view
     * @param view
     * @param postion
     * @return
     */
    public ToastUtil2 addView(View view, int postion) {
        toastView = (LinearLayout) toast.getView();
        toastView.addView(view, postion);

        return this;
    }

    /**
     * 设置Toast字体及背景颜色
     * @param messageColor
     * @param backgroundColor
     * @return
     */
    public ToastUtil2 setToastColor(int messageColor, int backgroundColor) {
        View view = toast.getView();
        if(view!=null){
            TextView message=((TextView) view.findViewById(android.R.id.message));
            message.setBackgroundColor(backgroundColor);
            message.setTextColor(messageColor);
        }
        return this;
    }

    /**
     * 设置Toast字体及背景
     * @param messageColor
     * @param background
     * @return
     */
    public ToastUtil2 setToastBackground(int messageColor, int background) {
        View view = toast.getView();
        if(view!=null){
            TextView message=((TextView) view.findViewById(android.R.id.message));
            message.setBackgroundResource(background);
            message.setTextColor(messageColor);
        }
        return this;
    }
    /**
     * 设置Toast字体及背景
     * @param messageColor
     * @param backgroundResid
     * @param textPxSize px
     * @return
     */
    public ToastUtil2 setToastBackground(int messageColor, int textPxSize, int backgroundResid) {
        View view = toast.getView();
        if(view!=null){
            TextView message=((TextView) view.findViewById(android.R.id.message));
            message.setBackgroundResource(backgroundResid);
            message.setTextColor(messageColor);
            message.setTextSize(TypedValue.COMPLEX_UNIT_PX, textPxSize);
        }
        return this;
    }

    /**
     * 短时间显示Toast
     */
    public ToastUtil2 Short(CharSequence message){
        if(toast != null) {
            toast.setText(message);
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        return this;
    }

    /**
     * 短时间显示Toast
     */
    public ToastUtil2 Short(int messageResid) {
        if(toast != null) {
            toast.setText(messageResid);
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        return this;
    }

    /**
     * 长时间显示Toast
     */
    public ToastUtil2 Long(CharSequence message){
        if(toast != null) {
            toast.setText(message);
            toast.setDuration(Toast.LENGTH_LONG);
        }
        return this;
    }

    /**
     * 长时间显示Toast
     *
     * @param messageResid
     */
    public ToastUtil2 Long(int messageResid) {
        if(toast != null) {
            toast.setText(messageResid);
            toast.setDuration(Toast.LENGTH_LONG);
        }
        return this;
    }

    /**
     * 自定义显示Toast时间
     *
     * @param message
     * @param duration
     */
    public ToastUtil2 Indefinite(CharSequence message, int duration) {
        if(toast != null) {
            toast.setText(message);
            toast.setDuration(duration);
        }
        return this;
    }

    /**
     * 自定义显示Toast时间
     *
     * @param message
     * @param duration
     */
    public ToastUtil2 Indefinite(int message, int duration) {
        if(toast != null) {
            toast.setText(message);
            toast.setDuration(duration);
        }
        return this;
    }

    /**
     * 显示Toast
     * @return
     */
    public ToastUtil2 show (){
        toast.show();

        return this;
    }

    /**
     * 获取Toast
     * @return
     */
    public Toast getToast(){
        return toast;
    }
}
