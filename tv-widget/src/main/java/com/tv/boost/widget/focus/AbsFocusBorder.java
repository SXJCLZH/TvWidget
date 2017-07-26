package com.tv.boost.widget.focus;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by owen on 2017/7/20.
 */

public abstract class AbsFocusBorder extends View implements FocusBorder, ViewTreeObserver.OnGlobalFocusChangeListener{
    private static final long DEFAULT_ANIM_DURATION_TIME = 300;
    
    protected long mAnimDuration = DEFAULT_ANIM_DURATION_TIME;
    protected RectF mFrameRectF = new RectF();
    protected RectF mPaddingRectF = new RectF();
    protected RectF mTempRectF = new RectF();

    private LinearGradient mShimmerLinearGradient;
    private Matrix mShimmerGradientMatrix;
    private Paint mShimmerPaint;
    private int mShimmerColor = 0xffffffff;
    private float mShimmerTranslate = 0;
    private boolean mShimmerAnimating = false;
    private boolean mIsShimmerAnim = true;

    private ObjectAnimator mTranslationXAnimator;
    private ObjectAnimator mTranslationYAnimator;
    private ObjectAnimator mWidthAnimator;
    private ObjectAnimator mHeightAnimator;
    private ObjectAnimator mShimmerAnimator;
    private AnimatorSet mAnimatorSet;

    private WeakReference<View> mOldFocusView;
    private OnFocusCallback mOnFocusCallback;
    private boolean mIsVisible = false;
    
    protected AbsFocusBorder(Context context, int shimmerColor, boolean isShimmerAnim, long animDuration, RectF paddingRectF) {
        super(context);
        
        this.mShimmerColor = shimmerColor;
        this.mIsShimmerAnim = isShimmerAnim;
        this.mAnimDuration = animDuration;
        if(null != paddingRectF)
            this.mPaddingRectF = paddingRectF;
        setLayerType(View.LAYER_TYPE_SOFTWARE, null); //关闭硬件加速
        setVisibility(INVISIBLE);
        
        mShimmerPaint = new Paint();
        mShimmerGradientMatrix = new Matrix();
    }
    
    protected void appendPadding(float left, float top, float right, float bottom) {
        mPaddingRectF.left += left;
        mPaddingRectF.top += top;
        mPaddingRectF.right += right;
        mPaddingRectF.bottom += bottom;
    }

    @Override
    public boolean isInEditMode() {
        return true;
    }

    /**
     * 绘制闪光
     * @param canvas
     */
    protected void onDrawShimmer(Canvas canvas) {
        if (mShimmerAnimating) {
            canvas.save();
            mTempRectF.set(mFrameRectF);
            float shimmerTranslateX = mTempRectF.width() * mShimmerTranslate;
            float shimmerTranslateY = mTempRectF.height() * mShimmerTranslate;
            mShimmerGradientMatrix.setTranslate(shimmerTranslateX, shimmerTranslateY);
            mShimmerLinearGradient.setLocalMatrix(mShimmerGradientMatrix);
            canvas.drawRoundRect(mTempRectF, getRoundRadius(), getRoundRadius(), mShimmerPaint);
            canvas.restore();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if(w != oldw || h != oldh) {
            mFrameRectF.set(mPaddingRectF.left, mPaddingRectF.top, w - mPaddingRectF.right, h - mPaddingRectF.bottom);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        onDrawShimmer(canvas);
    }
    
    @Override
    protected void onDetachedFromWindow() {
        unBoundGlobalFocusListener();
        super.onDetachedFromWindow();
    }

    private void setShimmerAnimating(boolean shimmerAnimating) {
        mShimmerAnimating = shimmerAnimating;
        if(mShimmerAnimating) {
            mShimmerLinearGradient = new LinearGradient(
                    -mFrameRectF.width(), 0, 0, mFrameRectF.height(),
                    new int[]{0x00ffffff, 0x33ffffff, mShimmerColor, 0x33ffffff, 0x00ffffff},
                    new float[]{0, 0.2f, 0.5f, 0.8f, 1}, Shader.TileMode.CLAMP);
            mShimmerPaint.setShader(mShimmerLinearGradient);
        }
    }

    protected void setShimmerTranslate(float shimmerTranslate) {
        if(mIsShimmerAnim && mShimmerTranslate != shimmerTranslate) {
            mShimmerTranslate = shimmerTranslate;
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    protected float getShimmerTranslate() {
        return mShimmerTranslate;
    }

    protected void setWidth(int width) {
        if(getLayoutParams().width != width) {
            getLayoutParams().width = width;
            requestLayout();
        }
    }

    protected void setHeight(int height) {
        if(getLayoutParams().height != height) {
            getLayoutParams().height = height;
            requestLayout();
        }
    }

    @Override
    public void setVisible(boolean visible) {
        if(mIsVisible != visible) {
            mIsVisible = visible;
            setVisibility(visible ? VISIBLE : INVISIBLE);
            
            if(!visible && null != mOldFocusView && null != mOldFocusView.get()) {
                runFocusScaleAnimation(mOldFocusView.get(), Options.get(1f, 1f));
                mOldFocusView.clear();
                mOldFocusView = null;
            }
        }
    }

    @Override
    public boolean isVisible() {
        return mIsVisible;
    }

    protected Rect findLocationWithView(View view) {
        ViewGroup root = (ViewGroup) getParent();
        Rect rect = new Rect();
        root.offsetDescendantRectToMyCoords(view, rect);
        return rect;
    }

    @Override
    public void onFocus(@NonNull View focusView, FocusBorder.Options options) {
        if(null != mOldFocusView && null != mOldFocusView.get()) {
            runFocusScaleAnimation(mOldFocusView.get(), Options.get(1f, 1f));
            mOldFocusView.clear();
        }
        
        if(options instanceof Options) {
            final Options baseOptions = (Options) options;
            if (baseOptions.isScale()) {
                mOldFocusView = new WeakReference<>(focusView);
            }
            runFocusAnimation(focusView, baseOptions);
        }
    }
    
    @Override
    public void boundGlobalFocusListener(@NonNull OnFocusCallback callback) {
        mOnFocusCallback = callback;
        getViewTreeObserver().addOnGlobalFocusChangeListener(this);
    }
    
    @Override
    public void unBoundGlobalFocusListener() {
        if(null != mOnFocusCallback) {
            mOnFocusCallback = null;
            getViewTreeObserver().removeOnGlobalFocusChangeListener(this);
        }
    }

    @Override
    public void onGlobalFocusChanged(View oldFocus, View newFocus) {
        runFocusScaleAnimation(oldFocus, Options.get(1f, 1f));
        
        final Options options = null != mOnFocusCallback ? (Options) mOnFocusCallback.onFocus(oldFocus, newFocus) : null;
        if(null != options) {
            runFocusAnimation(newFocus, options);
        }
    }

    private void runFocusAnimation(View focusView, Options options) {
        setVisible(true);
        runFocusScaleAnimation(focusView, options); // 焦点缩放动画
        runBorderAnimation(focusView, options); // 移动边框的动画。
    }
    
    protected void runBorderAnimation(View focusView, Options options) {
        if(null == focusView)
            return;

        if(null != mAnimatorSet) {
            mAnimatorSet.cancel();
        }
        
        createBorderAnimation(focusView, options);

        mAnimatorSet.start();
    }

    /**
     * 焦点VIEW缩放动画
     * @param oldOrNewFocusView
     * @param options
     */
    protected void runFocusScaleAnimation(@Nullable View oldOrNewFocusView, @NonNull Options options) {
        if(null == oldOrNewFocusView)
            return;
        if(!options.equalsScale(oldOrNewFocusView)) {
            oldOrNewFocusView.animate().scaleX(options.scaleX).scaleY(options.scaleY).setDuration(mAnimDuration).start();
        }
    }

    protected void createBorderAnimation(View focusView, Options options) {

        final int newWidth = (int) (focusView.getMeasuredWidth() * options.scaleX + mPaddingRectF.left + mPaddingRectF.right);
        final int newHeight = (int) (focusView.getMeasuredHeight() * options.scaleY + mPaddingRectF.top + mPaddingRectF.bottom);
        final Rect fromRect = findLocationWithView(this);
        final Rect toRect = findLocationWithView(focusView);
        final int x = toRect.left - fromRect.left;
        final int y = toRect.top - fromRect.top;
        final float newX = x - Math.abs(focusView.getMeasuredWidth() - newWidth) / 2f;
        final float newY = y - Math.abs(focusView.getMeasuredHeight() - newHeight) / 2f;

        final List<Animator> together = new ArrayList<>();
        final List<Animator> appendTogether = getTogetherAnimators(newX, newY, newWidth, newHeight, options);
        together.add(getTranslationXAnimator(newX));
        together.add(getTranslationYAnimator(newY));
        together.add(getWidthAnimator(newWidth));
        together.add(getHeightAnimator(newHeight));
        if(null != appendTogether && !appendTogether.isEmpty()) {
            together.addAll(appendTogether);
        }

        final List<Animator> sequentially = new ArrayList<>();
        final List<Animator> appendSequentially = getSequentiallyAnimators(newX, newY, newWidth, newHeight, options);
        if(mIsShimmerAnim) { 
            sequentially.add(getShimmerAnimator());
        } 
        if(null != appendSequentially && !appendSequentially.isEmpty()) {
            sequentially.addAll(appendSequentially);
        }

        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.setInterpolator(new DecelerateInterpolator(1));
        mAnimatorSet.playTogether(together);
        mAnimatorSet.playSequentially(sequentially);
    }
    
    private ObjectAnimator getTranslationXAnimator(float x) {
        if(null == mTranslationXAnimator) {
            mTranslationXAnimator = ObjectAnimator.ofFloat(this, "translationX", x)
                    .setDuration(mAnimDuration);
        } else {
            mTranslationXAnimator.setFloatValues(x);
        }
        return mTranslationXAnimator;
    }

    private ObjectAnimator getTranslationYAnimator(float y) {
        if(null == mTranslationYAnimator) {
            mTranslationYAnimator = ObjectAnimator.ofFloat(this, "translationY", y)
                    .setDuration(mAnimDuration);
        } else {
            mTranslationYAnimator.setFloatValues(y);
        }
        return mTranslationYAnimator;
    }

    private ObjectAnimator getHeightAnimator(int height) {
        if(null == mHeightAnimator) {
            mHeightAnimator = ObjectAnimator.ofInt(this, "height", getMeasuredHeight(), height)
                    .setDuration(mAnimDuration);
        } else {
            mHeightAnimator.setIntValues(getMeasuredHeight(), height);
        }
        return mHeightAnimator;
    }
    
    private ObjectAnimator getWidthAnimator(int width) {
        if(null == mWidthAnimator) {
            mWidthAnimator = ObjectAnimator.ofInt(this, "width", getMeasuredWidth(), width)
                    .setDuration(mAnimDuration);
        } else {
            mWidthAnimator.setIntValues(getMeasuredWidth(), width);
        }
        return mWidthAnimator;
    }
    
    private ObjectAnimator getShimmerAnimator() {
        if(null == mShimmerAnimator) {
            mShimmerAnimator = ObjectAnimator.ofFloat(this, "shimmerTranslate", -1f, 1f);
            mShimmerAnimator.setInterpolator(new DecelerateInterpolator(1));
            mShimmerAnimator.setDuration(DEFAULT_ANIM_DURATION_TIME);
            mShimmerAnimator.setStartDelay(400);
            mShimmerAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    setShimmerAnimating(true);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    setShimmerAnimating(false);
                }
            });
        }
        return mShimmerAnimator;
    }

    abstract float getRoundRadius();

    abstract List<Animator> getTogetherAnimators(float newX, float newY, int newWidth, int newHeight, Options options);

    abstract List<Animator> getSequentiallyAnimators(float newX, float newY, int newWidth, int newHeight, Options options);
    
    public static class Options extends FocusBorder.Options{
        protected float scaleX = 1f, scaleY = 1f;

        Options() {
        }

        private static class OptionsHolder {
            private static final Options INSTANCE = new Options();
        }
        
        public static Options get(float scaleX, float scaleY) {
            OptionsHolder.INSTANCE.scaleX = scaleX;
            OptionsHolder.INSTANCE.scaleY = scaleY;
            return OptionsHolder.INSTANCE;
        }
        
        public boolean isScale() {
            return scaleX != 1f || scaleY != 1f;
        }
        
        public boolean equalsScale(View view) {
            return scaleX == view.getScaleX() && scaleY == view.getScaleY();
        }
    }
    
    public static abstract class Builder extends FocusBorder.Builder{
        protected int mShimmerColor = 0xffffffff;
        protected boolean mIsShimmerAnim = true;
        protected long mAnimDuration = AbsFocusBorder.DEFAULT_ANIM_DURATION_TIME;
        protected RectF mPaddingRectF = new RectF();

        public Builder shimmerColor(int color) {
            mShimmerColor = color;
            return this;
        }

        public Builder noShimmer() {
            mIsShimmerAnim = false;
            return this;
        }
        
        public Builder animDuration(long duration) {
            mAnimDuration = duration;
            return this;
        }
        
        public Builder padding(float padding) {
            return padding(padding, padding, padding, padding);
        }
        
        public Builder padding(float left, float top, float right, float bottom) {
            mPaddingRectF.left = left;
            mPaddingRectF.top = top;
            mPaddingRectF.right = right;
            mPaddingRectF.bottom = bottom;
            return this;
        }
    }
}
