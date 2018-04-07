package dev.olog.msc.presentation.dialog.sleep.timer;

import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.codetroopers.betterpickers.hmspicker.HmsPicker;

import dev.olog.msc.R;
import dev.olog.msc.presentation.base.BaseDialogFragment;

public abstract class TimePickerDialog extends BaseDialogFragment {

    protected static final String REFERENCE_KEY = "HmsPickerDialogFragment_ReferenceKey";
    protected static final String THEME_RES_ID_KEY = "HmsPickerDialogFragment_ThemeResIdKey";
    protected static final String PLUS_MINUS_VISIBILITY_KEY = "HmsPickerDialogFragment_PlusMinusVisibilityKey";

    protected HmsPicker mPicker;
    protected Button positiveButton;
    protected Button cancelButton;

    private int mReference = -1;
    private int mTheme = -1;
    @Nullable
    private ColorStateList mTextColor;
    private int mDialogBackgroundResId;
    private int mHours;
    private int mMinutes;
    private int mSeconds;
    private int mPlusMinusVisibility = View.INVISIBLE;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null && args.containsKey(REFERENCE_KEY)) {
            mReference = args.getInt(REFERENCE_KEY);
        }
        if (args != null && args.containsKey(THEME_RES_ID_KEY)) {
            mTheme = args.getInt(THEME_RES_ID_KEY);
        }
        if (args != null && args.containsKey(PLUS_MINUS_VISIBILITY_KEY)) {
            mPlusMinusVisibility = args.getInt(PLUS_MINUS_VISIBILITY_KEY);
        }

        setStyle(DialogFragment.STYLE_NO_TITLE, 0);

        // Init defaults
        mTextColor = getResources().getColorStateList(R.color.dialog_text_color_holo_dark);
        mDialogBackgroundResId = R.drawable.dialog_full_holo_dark;

        if (mTheme != -1) {
            TypedArray a = getActivity().getApplicationContext().obtainStyledAttributes(mTheme, R.styleable.BetterPickersDialogFragment);

            mTextColor = a.getColorStateList(R.styleable.BetterPickersDialogFragment_bpTextColor);
            mDialogBackgroundResId = a.getResourceId(R.styleable.BetterPickersDialogFragment_bpDialogBackground, mDialogBackgroundResId);

            a.recycle();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.hms_picker_dialog, container, false);

        positiveButton = view.findViewById(R.id.done_button);
        cancelButton = view.findViewById(R.id.cancel_button);

        cancelButton.setTextColor(mTextColor);
        cancelButton.setOnClickListener(view1 -> TimePickerDialog.this.dismiss());
        positiveButton.setTextColor(mTextColor);

        mPicker = view.findViewById(R.id.hms_picker);
        mPicker.setSetButton(positiveButton);
        mPicker.setTime(mHours, mMinutes, mSeconds);
        mPicker.setTheme(mTheme);
        mPicker.setPlusMinusVisibility(mPlusMinusVisibility);

        getDialog().getWindow().setBackgroundDrawableResource(mDialogBackgroundResId);

        return view;
    }

    public void setTime(int hours, int minutes, int seconds) {
        this.mHours = hours;
        this.mMinutes = minutes;
        this.mSeconds = seconds;
        if (mPicker != null) {
            mPicker.setTime(hours, minutes, seconds);
        }
    }
}

