package dev.olog.msc.presentation.equalizer.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

public class InfiniteViewPager extends ViewPager {

    public InfiniteViewPager(Context context) {
        this(context, null);
    }

//    private ViewPager.OnPageChangeListener colorPageChangeListener;

    public InfiniteViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()){
//            colorPageChangeListener = new ColorfulOnPageChangeListener(this);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
//        if (!isInEditMode()){
//            addOnPageChangeListener(colorPageChangeListener);
//            addOnPageChangeListener(onPageChangeListener);
//
//            getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//                @Override
//                public boolean onPreDraw() {
//                    getViewTreeObserver().removeOnPreDrawListener(this);
//                    GradientDrawable drawable = new GradientDrawable(GradientDrawable.Orientation.BL_TR,
//                            CoverUtils.INSTANCE.getCOLORS().get(getCurrentItem()));
//                    setBackground(drawable);
//                    return false;
//                }
//            });
//        }

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        setAdapter(null);
//        removeOnPageChangeListener(colorPageChangeListener);
    }

    @Override
    public InfinitePagerAdapter getAdapter() {
        return (InfinitePagerAdapter) super.getAdapter();
    }


}
