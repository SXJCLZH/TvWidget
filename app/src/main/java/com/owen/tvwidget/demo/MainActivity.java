package com.owen.tvwidget.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import com.owen.focus.AbsFocusBorder;
import com.owen.focus.FocusBorder;
import com.owen.tab.TvTabLayout;
import com.owen.widget.TvHorizontalScrollView;


public class MainActivity extends AppCompatActivity {
    
    private TvHorizontalScrollView mHorizontalScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mHorizontalScrollView = (TvHorizontalScrollView) findViewById(R.id.smooth_horizontal_scrollview);
        mHorizontalScrollView.setScrollerDuration(700);

        TvTabLayout mTabLayout = (TvTabLayout) findViewById(R.id.tablayout);
        TvTabLayout mTabLayout2 = (TvTabLayout) findViewById(R.id.tablayout2);
        TvTabLayout mTabLayout3 = (TvTabLayout) findViewById(R.id.tablayout3);
        for(int i=0; i<15; i++) {
            mTabLayout.addTab(mTabLayout.newTab().setText("标题"+i), i == 0);
            mTabLayout2.addTab(mTabLayout2.newTab().setText("标题"+i));
//            mTabLayout3.addTab(mTabLayout3.newTab().setText("标题"+i));
        }
        
        
        initBorder(true);
    }

    //焦点框的使用-------------------------------------------------------------------------------------------------
    private AbsFocusBorder.Builder initBuilder(boolean isColorBorder) {
        if(isColorBorder) {
            return new FocusBorder.Builder().asColor()
//                    .shadowWidth(18f) //阴影宽度(方式一)
                    .shadowWidth(TypedValue.COMPLEX_UNIT_DIP, 20f) //阴影宽度(方式二)
                    .shadowColor(getResources().getColor(R.color.colorShadow)) //阴影颜色
//                    .borderWidth(2f) //边框宽度
                    .borderWidth(TypedValue.COMPLEX_UNIT_DIP, 2f) //边框宽度
                    .borderColor(getResources().getColor(R.color.colorBorder)) //边框颜色
            ;
        } else {
            return new FocusBorder.Builder().asDrawable()
                    .borderDrawableRes(R.drawable.focus)
//                    .borderDrawable(drawable)
            ;
        }
    }
    
    private void initBorder(final boolean isColorBorder) {
        
        final FocusBorder focusBorder = initBuilder(isColorBorder)//以下为公共配置项
//                        .animDuration(400) //动画时长
//                        .padding(10f) 
//                        .shimmerDuration(800) //闪光时长
//                        .shimmerColor(getResources().getColor(R.color.shimmerColor)) //闪光颜色(默认白色)
//                        .noShimmer() //不使用闪光
                        .build(this);

        //方式一：绑定整个页面的焦点监听
        focusBorder.boundGlobalFocusListener(new FocusBorder.OnFocusCallback() {
            @Override
            public FocusBorder.Options onFocus(View oldFocus, View newFocus) {
                switch (newFocus.getId()) {
                    case R.id.layout_rfl:
                        return isColorBorder ? FocusBorder.OptionsFactory.get(1f, 1f, getResources().getDimension(R.dimen.x30))
                                : FocusBorder.OptionsFactory.get(1f, 1f);

                    case R.id.img2:
                    case R.id.img1:
                        return isColorBorder ? FocusBorder.OptionsFactory.get(1f, 1f, getResources().getDimension(R.dimen.x90))
                                :FocusBorder.OptionsFactory.get(1f, 1f);
                }
                if(null != newFocus.getParent() && ((ViewGroup)newFocus.getParent()).getId() == R.id.scroll_child_layout) {
                    return isColorBorder ? FocusBorder.OptionsFactory.get(1f, 1f, 4f)
                            : FocusBorder.OptionsFactory.get(1f, 1f);
                }

                focusBorder.setVisible(false);

                return null; //返回null表示不使用焦点框框架
            }
        });


        //方式二：单独控制焦点监听
        /*ViewGroup viewGroup = (ViewGroup) findViewById(R.id.scroll_child_layout);
        for(int i=0; i<viewGroup.getChildCount(); i++) {
            View view = viewGroup.getChildAt(i);
            if(view.isFocusable()) {
                view.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if(hasFocus) {
                            switch (v.getId()) {
                                case R.id.layout_rfl:
                                    focusBorder.onFocus(v, isColorBorder ? 
                                            FocusBorder.OptionsFactory.get(1f, 1f, getResources().getDimension(R.dimen.x30)) 
                                            : FocusBorder.OptionsFactory.get(1f, 1f));
                                    break;

                                case R.id.img2:
                                case R.id.img1:
                                    focusBorder.onFocus(v, isColorBorder ? 
                                            FocusBorder.OptionsFactory.get(1f, 1f, getResources().getDimension(R.dimen.x90)) 
                                            : FocusBorder.OptionsFactory.get(1f, 1f));
                                    break;

                                default:
                                    focusBorder.onFocus(v, isColorBorder ? 
                                    FocusBorder.OptionsFactory.get(1f, 1f, 4f) 
                                    : FocusBorder.OptionsFactory.get(1f, 1f));
                                    break;
                            }
                        }
                    }
                });
            }
        }*/
    }
}
