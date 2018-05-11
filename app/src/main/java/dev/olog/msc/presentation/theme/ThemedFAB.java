package dev.olog.msc.presentation.theme;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import dev.olog.msc.R;

public class ThemedFAB extends FloatingActionButton {

    public ThemedFAB(Context context) {
        super(context);
        initialize(context);
    }

    public ThemedFAB(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public ThemedFAB(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    private void initialize(Context context){
        if (AppTheme.INSTANCE.isDarkTheme()){
            int background = ContextCompat.getColor(context, R.color.accent_secondary);
            int image = ContextCompat.getColor(context, R.color.dark_grey);
            setBackgroundTintList(ColorStateList.valueOf(background));
            setColorFilter(image);
        } else {
            int background = ContextCompat.getColor(context, R.color.dark_grey);
            int image = Color.WHITE;
            setBackgroundTintList(ColorStateList.valueOf(background));
            setColorFilter(image);
        }
    }

}
