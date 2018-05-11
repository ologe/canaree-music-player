package dev.olog.msc.presentation.theme;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import dev.olog.msc.R;

public class ThemedProgressBar extends ProgressBar {

    public ThemedProgressBar(Context context) {
        super(context);
        initialize(context);
    }

    public ThemedProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public ThemedProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    public ThemedProgressBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize(context);
    }

    private void initialize(Context context){


        if (AppTheme.INSTANCE.isDarkTheme()){
            int progress = ContextCompat.getColor(context, R.color.accent_secondary);
            setProgressTintList(ColorStateList.valueOf(progress));
            int secondaryProgress = ContextCompat.getColor(context, R.color.progress_bar_secondary_tint);
            setProgressBackgroundTintList(ColorStateList.valueOf(secondaryProgress));
        } else {
            int progress = ContextCompat.getColor(context, R.color.accent);
            setProgressTintList(ColorStateList.valueOf(progress));
            setProgressBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
        }
    }

}
