package dev.olog.msc.presentation.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import dev.olog.msc.presentation.theme.AppTheme;

public class ScrimView extends View {

    public ScrimView(Context context) {
        this(context, null);
    }

    public ScrimView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrimView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ScrimView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        int color = AppTheme.INSTANCE.isDarkTheme() ? 0xAA232323 : 0xDDCCCCCC;
        setBackgroundColor(color);
    }

}
