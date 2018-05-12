package dev.olog.msc.presentation.widget;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;

import dev.olog.msc.R;
import dev.olog.msc.presentation.theme.AppTheme;

public class MiniPlayerConstraintLayout extends ConstraintLayout {

    public MiniPlayerConstraintLayout(Context context) {
        super(context);
        initialize(context);
    }

    public MiniPlayerConstraintLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public MiniPlayerConstraintLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    private void initialize(Context context){
        if (AppTheme.INSTANCE.isWhiteTheme()){
            setBackgroundResource(R.color.background);
        } else if (AppTheme.INSTANCE.isDarkMode()){
            setBackgroundResource(R.color.theme_dark_toolbar);
        } else {
            setBackgroundResource(R.color.theme_black_background);
        }
    }

}
