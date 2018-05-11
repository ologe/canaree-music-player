package dev.olog.msc.presentation.theme;

import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.util.AttributeSet;

public class ThemedTextInputLayout extends TextInputLayout {

    public ThemedTextInputLayout(Context context) {
        super(context);
        initialize(context);
    }

    public ThemedTextInputLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public ThemedTextInputLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    private void initialize(Context context){
//        if (AppTheme.INSTANCE.isDarkTheme()){
//            updateHintColor(Color.WHITE);
//        } else {
//            updateHintColor(ContextCompat.getColor(context, R.color.accent));
//        }
    }

//    private void updateHintColor(int color){
//        try {
//            Field field = getClass().getDeclaredField("mFocusedTextColor");
//            field.setAccessible(true);
//            int[][] states = new int[][] { new int[]{} };
//            int[] colors = new int[] { color };
//            ColorStateList stateList = new ColorStateList(states, colors);
//            field.set(this, stateList);
//
//            Method method = getClass().getDeclaredMethod("updateLabelState", boolean.class);
//            method.setAccessible(true);
//            method.invoke(this, true);
//
//        } catch (Exception ignored){ }
//    }

}
