package dev.olog.msc.presentation.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import dev.olog.msc.R;

public class DottedSeparator extends View {

    public DottedSeparator(Context context) {
        this(context, null);
    }

    public DottedSeparator(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DottedSeparator(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public DottedSeparator(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        setAlpha(.1f);
        setBackgroundResource(R.drawable.dotted_line);
    }
}
