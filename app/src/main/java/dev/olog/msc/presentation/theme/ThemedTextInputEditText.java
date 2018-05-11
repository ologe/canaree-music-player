package dev.olog.msc.presentation.theme;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import dev.olog.msc.R;

public class ThemedTextInputEditText extends TextInputEditText {

    public ThemedTextInputEditText(Context context) {
        super(context);
        initialize(context);
    }

    public ThemedTextInputEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public ThemedTextInputEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    private void initialize(Context context){
        if (AppTheme.INSTANCE.isDarkTheme()){
            setHintTextColor(Color.WHITE);
            setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
        } else {
            int color = ContextCompat.getColor(context, R.color.accent);
            setHintTextColor(color);
            setBackgroundTintList(ColorStateList.valueOf(color));
        }
    }

}
