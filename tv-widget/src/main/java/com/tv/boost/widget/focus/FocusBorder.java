package com.tv.boost.widget.focus;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;

/**
 * Created by owen on 2017/7/20.
 */

public interface FocusBorder {

    void setVisible(boolean visible);

    boolean isVisible();

    void onFocus(@NonNull View focusView, Options options);
    
    void boundGlobalFocusListener(@NonNull OnFocusCallback callback);
    
    void unBoundGlobalFocusListener();

    interface OnFocusCallback {
        Options onFocus(View oldFocus, View newFocus);
    }
    
    abstract class Options {}
    
    abstract class Builder {
        public abstract FocusBorder build(Activity activity);
    }
    
    class BuilderFactory {
        
        public static <B extends Builder> B get(Class<B> builder) {
            try {
                return (B)builder.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
