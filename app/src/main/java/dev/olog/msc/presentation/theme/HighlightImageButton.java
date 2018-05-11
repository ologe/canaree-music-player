package dev.olog.msc.presentation.theme;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;

import dev.olog.msc.R;

public class HighlightImageButton extends AppCompatImageButton {

    public HighlightImageButton(Context context) {
        super(context);
        initialize(context);
    }

    public HighlightImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public HighlightImageButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    private void initialize(Context context){
        if (AppTheme.INSTANCE.isDarkTheme()){
            setColorFilter(Color.WHITE);
        } else {
            int color = ContextCompat.getColor(context, R.color.accent);
            setColorFilter(color);
        }
    }
}
