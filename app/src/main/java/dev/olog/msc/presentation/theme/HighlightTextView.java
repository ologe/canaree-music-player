package dev.olog.msc.presentation.theme;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import dev.olog.msc.R;

public class HighlightTextView extends AppCompatTextView {

    public HighlightTextView(Context context) {
        super(context);
        initialize(context);
    }

    public HighlightTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public HighlightTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    private void initialize(Context context){
        if (AppTheme.INSTANCE.isDarkTheme()){
            int color = ContextCompat.getColor(context, R.color.accent_secondary);
            setTextColor(color);
        } else {
            setTextColor(ContextCompat.getColor(context, R.color.accent));
        }
    }


}
