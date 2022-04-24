package dev.olog.presentation.widgets.fascroller;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import dev.olog.presentation.R;
import dev.olog.shared.TextUtils;
import dev.olog.ui.ThemeUtilsKt;

public class WaveSideBarView extends View {

    private static final double ANGLE = Math.PI * 45 / 180;
    private static final double ANGLE_R = Math.PI * 90 / 180;
    protected OnTouchLetterChangeListener listener;

    @NonNull
    protected static final List<String> LETTERS = Arrays.asList("#","A","B","C","D","E","F","G","H","I","J","K","L","M",
            "N","O","P","Q","R","S","T","U","V","W","X","Y","Z", "?");

    protected List<String> mLetters;

    private int mChoose = -1;

    @NonNull
    private Paint mLettersPaint = new Paint();
    @NonNull
    private Paint mSelectedLetterPaint = new Paint();

    @NonNull
    public String mSelectedLetter = "";

    // selected text paint
    @NonNull
    private Paint mTextPaint = new Paint();

    @NonNull
    private Paint mWavePaint = new Paint();

    private float mTextSize;
    private int mWidth;
    private int mHeight;
    private int mItemHeight;
    private int mPadding;

    @NonNull
    private Path mWavePath = new Path();

    @NonNull
    private Path mBallPath = new Path();

    private int mCenterY;

    private int mRadius;

    private int mBallRadius;

    ValueAnimator mRatioAnimator;

    private float mRatio;

    private float mPosX;

    private float mBallCentreX;

    private float letterBaseline = Math.abs(-mLettersPaint.getFontMetrics().bottom - mLettersPaint.getFontMetrics().top);

    public WaveSideBarView(@NonNull Context context) {
        this(context, null, 0);
    }

    public WaveSideBarView(@NonNull Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveSideBarView(@NonNull Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {

        Resources res = context.getResources();

        int waveColor = ContextCompat.getColor(context, R.color.side_view_wave_color);
        mTextSize = res.getDimensionPixelSize(R.dimen.side_view_textSize);
        float largeTextSize = res.getDimensionPixelSize(R.dimen.side_view_large_text_size);
        mPadding = res.getDimensionPixelSize(R.dimen.side_view_text_size_padding);

        mRadius = (int) context.getResources().getDimension(R.dimen.side_view_radius);
        mBallRadius = (int) context.getResources().getDimension(R.dimen.side_view_ball_radius);

        mWavePaint.setAntiAlias(true);
        mWavePaint.setStyle(Paint.Style.FILL);
        mWavePaint.setColor(waveColor);

        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextSize(largeTextSize);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        mLettersPaint.setAntiAlias(true);
        mLettersPaint.setTextSize(mTextSize);
        mLettersPaint.setTextAlign(Paint.Align.CENTER);
        mLettersPaint.setColor(ThemeUtilsKt.textColorPrimary(getContext()));

        mSelectedLetterPaint.setAntiAlias(true);
        mSelectedLetterPaint.setTextSize(mTextSize);
        mSelectedLetterPaint.setTextAlign(Paint.Align.CENTER);
        mSelectedLetterPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mSelectedLetterPaint.setTextSize(res.getDimensionPixelSize(R.dimen.side_view_selected_text_size));

        if(!isInEditMode()){
            mLetters = new ArrayList<>(LETTERS.size());
            for (String ignored : LETTERS){
                mLetters.add("\u00B7");
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(@NonNull MotionEvent event) {
        final float y = event.getY();
        final float x = event.getX();

        final int oldChoose = mChoose;
        final int newChoose = (int) (y / mHeight * mLetters.size());

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                if (x < mWidth - 2 * mRadius) {
                    return false;
                }
                startAnimator(mRatio, 1.0f);
                break;
            case MotionEvent.ACTION_MOVE:

                mCenterY = (int) y;
                if (oldChoose != newChoose) {
                    if (newChoose >= 0 && newChoose < mLetters.size()) {
                        mChoose = newChoose;
                        if (listener != null) {
                            listener.onLetterChange(mLetters.get(newChoose));
                        }
                        invalidate();
                    }

                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:

                startAnimator(mRatio, 0f);
                mChoose = -1;
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        mWidth = getMeasuredWidth();

        if (isInEditMode()){
            mItemHeight = (mHeight - mPadding) / LETTERS.size();
        } else {
            mItemHeight = (mHeight - mPadding) / mLetters.size();
        }
        mPosX = mWidth - 1.6f * mTextSize;
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        drawLetters(canvas);

        if (!isInEditMode()){
            drawWavePath(canvas);

            drawBallPath(canvas);

            drawChooseText(canvas);
        }
    }

    private void drawLetters(@NonNull Canvas canvas) {
        List<String> letters;
        if (isInEditMode()){
            letters = LETTERS;
        } else {
            letters = mLetters;
        }

        for (int i = 0; i < letters.size(); i++) {
            float posY = mItemHeight * i + letterBaseline / 2 + mPadding;

            String letter = letters.get(i);
            canvas.drawText(letter, mPosX, posY,
                    mSelectedLetter.equals(letter) ? mSelectedLetterPaint : mLettersPaint);

        }
    }

    private void drawChooseText(@NonNull Canvas canvas) {
        if (mChoose != -1 && mRatio >= 0.9f) {

            String target = mLetters.get(mChoose);
            if(target.equals(TextUtils.MIDDLE_DOT)){
                target = getClosestLetter(mChoose);
            }
            Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
            float baseline = Math.abs(-fontMetrics.bottom - fontMetrics.top);
            float x = mBallCentreX;
            float y = mCenterY + baseline / 2;
            canvas.drawText(target, x, y, mTextPaint);
        }
    }

    private String getClosestLetter(int pos){
        ListIterator<String> forward = mLetters.listIterator(pos);
        ListIterator<String> backward = mLetters.listIterator(pos);

        while (forward.hasNext() || backward.hasPrevious()){

            if(backward.hasPrevious()){
                String tmp = backward.previous();
                if(!tmp.equals(TextUtils.MIDDLE_DOT)) return tmp;
            }

            if(forward.hasNext()){
                String tmp = forward.next();
                if(!tmp.equals(TextUtils.MIDDLE_DOT)) return tmp;
            }
        }

        return mLetters.get(pos);
    }

    private void drawWavePath(Canvas canvas) {
        mWavePath.reset();

        mWavePath.moveTo(mWidth, mCenterY - 3 * mRadius);

        int controlTopY = mCenterY - 2 * mRadius;

        int endTopX = (int) (mWidth - mRadius * Math.cos(ANGLE) * mRatio);
        int endTopY = (int) (controlTopY + mRadius * Math.sin(ANGLE));
        mWavePath.quadTo(mWidth, controlTopY, endTopX, endTopY);

        int controlCenterX = (int) (mWidth - 1.8f * mRadius * Math.sin(ANGLE_R) * mRatio);
        int controlCenterY = mCenterY;

        int controlBottomY = mCenterY + 2 * mRadius;
        int endBottomX = endTopX;
        int endBottomY = (int) (controlBottomY - mRadius * Math.cos(ANGLE));
        mWavePath.quadTo(controlCenterX, controlCenterY, endBottomX, endBottomY);

        mWavePath.quadTo(mWidth, controlBottomY, mWidth, controlBottomY + mRadius);

        mWavePath.close();
        canvas.drawPath(mWavePath, mWavePaint);
    }

    private void drawBallPath(Canvas canvas) {
        mBallCentreX = (mWidth + mBallRadius) - (2.0f * mRadius + 2.0f * mBallRadius) * mRatio;

        mBallPath.reset();
        mBallPath.addCircle(mBallCentreX, mCenterY, mBallRadius, Path.Direction.CW);
        mBallPath.op(mWavePath, Path.Op.DIFFERENCE);

        mBallPath.close();
        canvas.drawPath(mBallPath, mWavePaint);
    }

    private void startAnimator(float... value) {
        if (mRatioAnimator == null) {
            mRatioAnimator = new ValueAnimator();
        }
        mRatioAnimator.cancel();
        mRatioAnimator.setFloatValues(value);
        mRatioAnimator.addUpdateListener(value1 -> {
            float oldRatio = mRatio;
            mRatio = (float) value1.getAnimatedValue();
            if(oldRatio != mRatio) invalidate();
        });
        mRatioAnimator.start();
    }

    public interface OnTouchLetterChangeListener {
        void onLetterChange(String letter);
    }
}
