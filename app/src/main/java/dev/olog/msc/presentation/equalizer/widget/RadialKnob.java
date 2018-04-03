/*
 * Copyright (c) 2013, The Linux Foundation. All rights reserved.
 * Copyright (c) 2015, The CyanogenMod Project. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *       * Redistributions in binary form must reproduce the above
 *         copyright notice, this list of conditions and the following
 *         disclaimer in the documentation and/or other materials provided
 *         with the distribution.
 *       * Neither the name of The Linux Foundation nor the names of its
 *         contributors may be used to endorse or promote products derived
 *         from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN
 * IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package dev.olog.msc.presentation.equalizer.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import dev.olog.msc.R;

public class RadialKnob extends View {

    private static final String TAG = RadialKnob.class.getSimpleName();
    private static final boolean DEBUG = Log.isLoggable(TAG, Log.DEBUG);

    public static final float REGULAR_SCALE = 0.8f;

    public static final float TOUCHING_SCALE = 1f;
    private static final int DO_NOT_VIBRATE_THRESHOLD = 100;

    private static final int DEGREE_OFFSET = -225;
    private static final int START_ANGLE = 360 + DEGREE_OFFSET;
    private static final int MAX_DEGREES = 270;

    @NonNull
    private final Paint mPaint, mTextPaint;

    ValueAnimator mAnimator;
    float mOffProgress;
    boolean mAnimating = false;
    long mDownTime;
    long mUpTime;
    @Nullable
    private OnKnobChangeListener mOnKnobChangeListener = null;
    private float mProgress = 0.0f;
    private float mTouchProgress = 0.0f;
    private int mMax = 100;
    private boolean mOn = false;
    private boolean mEnabled = true;
    private float mLastX;
    private float mLastY;
    private boolean mMoved;
    private int mWidth = 0;
    @NonNull
    private RectF mRectF, mOuterRect = new RectF(), mInnerRect = new RectF();
    private float mLastAngle;
    private Long mLastVibrateTime;
    private int mHighlightColor;
    private int mBackgroundArcColor;
    private int mBackgroundArcColorDisabled;
    private int mRectPadding;
    private int mStrokeWidth;
    private float mHandleWidth; // little square indicator where user touches
    private float mTextOffset;

    @NonNull
    Path mPath = new Path();
    @NonNull
    PathMeasure mPathMeasure = new PathMeasure();
    @NonNull
    float[] mTmp = new float[2];
    float mStartX, mStopX, mStartY, mStopY;

    public RadialKnob(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        Resources res = getResources();
        mBackgroundArcColor = res.getColor(R.color.radial_knob_arc_bg);
        mBackgroundArcColorDisabled = res.getColor(R.color.radial_knob_arc_bg_disabled);
        mHighlightColor = res.getColor(R.color.highlight);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setElegantTextHeight(true);
        mTextPaint.setFakeBoldText(true);
        mTextPaint.setTextSize(res.getDimension(R.dimen.radial_text_size));
        mTextPaint.setColor(Color.BLACK);

        mTextOffset = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2,
                getResources().getDisplayMetrics());

        mHandleWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5,
                getResources().getDisplayMetrics());

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(mHighlightColor);
        mPaint.setStrokeWidth(mStrokeWidth = res.getDimensionPixelSize(R.dimen.radial_knob_stroke));
        mPaint.setStrokeCap(Paint.Cap.BUTT);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setShadowLayer(2, 1, -2, Color.BLACK);

        setScaleX(REGULAR_SCALE);
        setScaleY(REGULAR_SCALE);

        mRectPadding = res.getDimensionPixelSize(R.dimen.radial_rect_padding);
        invalidate();
    }

    public RadialKnob(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RadialKnob(Context context) {
        this(context, null);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setOnTouchListener(onKnobTouchListener);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        setOnTouchListener(null);
    }

    @NonNull
    private OnTouchListener onKnobTouchListener = (v, event) -> {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                resize(true);
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                resize(false);
                return true;
        }

        return false;
    };

    public void setValue(int value) {
        if (mMax != 0) {
            setProgress(((float) value) / mMax);
            mTouchProgress = mProgress;
            mLastAngle = mProgress * MAX_DEGREES;
        }
    }

    public void setProgress(float progress, boolean fromUser) {
        if (progress > 1.0f) {
            progress = 1.0f;
        }
        if (progress < 0.0f) {
            progress = 0.0f;
        }

        mProgress = progress;

        invalidate();

        if (mOnKnobChangeListener != null) {
            mOnKnobChangeListener.onValueChanged(this, (int) (progress * mMax), fromUser);
        }
    }

    public void setMax(int max) {
        mMax = max;
    }

    public float getProgress() {
        return mProgress;
    }

    public void setProgress(float progress) {
        setProgress(progress, false);
    }

    @Override
    public boolean isEnabled() {
        return mEnabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        mEnabled = enabled;
        if (enabled) {
            setOn(mOn);
        }
        invalidate();
    }

    public void setOn(final boolean on) {
        mOn = on;
        if (mAnimator != null) {
            mAnimator.cancel();
        }
        invalidate();
    }

    public void setHighlightColor(int color) {
        mPaint.setColor(mHighlightColor = color);
        invalidate();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        mPaint.setStrokeWidth(mStrokeWidth);

        mPaint.setColor(mEnabled ? mBackgroundArcColor : mBackgroundArcColorDisabled);
        canvas.drawArc(mRectF, START_ANGLE, MAX_DEGREES, false, mPaint);

        final float sweepAngle = mEnabled ? mProgress * MAX_DEGREES : 0;
        if (mOn) {
            mPaint.setColor(mHighlightColor);
            canvas.drawArc(mRectF, START_ANGLE, sweepAngle, false, mPaint);
        }

        final float indicatorSweepAngle = Math.max(1f, sweepAngle);

        // render the indicator
        mPath.reset();
        mPath.arcTo(mInnerRect, START_ANGLE, indicatorSweepAngle, true);

        mPathMeasure.setPath(mPath, false);
        mPathMeasure.getPosTan(mPathMeasure.getLength(), mTmp, null);

        mStartX = mTmp[0];
        mStartY = mTmp[1];

        mPath.reset();
        mPath.arcTo(mOuterRect, START_ANGLE, indicatorSweepAngle, true);

        mPathMeasure.setPath(mPath, false);
        mPathMeasure.getPosTan(mPathMeasure.getLength(), mTmp, null);

        mStopX = mTmp[0];
        mStopY = mTmp[1];

        mPaint.setStrokeWidth(mHandleWidth);
        mPaint.setColor(Color.WHITE);
        canvas.drawLine(mStartX, mStartY, mStopX, mStopY, mPaint);

        canvas.drawText(getProgressText(),
                mOuterRect.centerX(),
                mOuterRect.centerY() + (mTextPaint.getTextSize() / 2.f) - mTextOffset,
                mTextPaint);
    }

    private String getProgressText() {
        if (mEnabled) {
            return ((int) (mProgress * 100)) + "%";
        } else {
            return "--";
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);

        int size = w > h ? h : w;
        mWidth = size;
        int diff;
        if (w > h) {
            diff = (w - h) / 2;
            mRectF = new RectF(mRectPadding + diff, mRectPadding,
                    w - mRectPadding - diff, h - mRectPadding);
        } else {
            diff = (h - w) / 2;
            mRectF = new RectF(mRectPadding, mRectPadding + diff,
                    w - mRectPadding, h - mRectPadding - diff);
        }
        mOuterRect.set(mRectF);
        mOuterRect.inset(-mRectPadding, -mRectPadding);
        mInnerRect.set(mRectF);
        mInnerRect.inset(mRectPadding, mRectPadding);
    }

    private boolean isUserSelected() {
        return getScaleX() == TOUCHING_SCALE && getScaleY() == TOUCHING_SCALE;
    }

    private void animateTo(float progress) {
        if (DEBUG) Log.w(TAG, "animateTo(" + progress + ")");
        if (mAnimator != null) {
            mAnimator.cancel();
        }
        mAnimator = ValueAnimator.ofFloat(mProgress, progress);
        mAnimator.setDuration(100);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mAnimating = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimating = false;
                postInvalidate();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mAnimating = false;
                postInvalidate();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(@NonNull ValueAnimator animation) {
                float progress = (Float) animation.getAnimatedValue();
                mProgress = progress;
                mLastAngle = mProgress * MAX_DEGREES;
                if (DEBUG) Log.i(TAG, "onAnimationUpdate(): mProgress: "
                        + mProgress + ", mLastAngle: " + mLastAngle);

                setProgress(mProgress);
                if (mOnKnobChangeListener != null) {
                    mOnKnobChangeListener.onValueChanged(RadialKnob.this,
                            (int) (progress * mMax), true);
                }
                postInvalidate();
            }
        });
        mAnimator.start();
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        final float x = event.getX();
        final float y = event.getY();

        if (!mEnabled) {
            return false;
        }

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mDownTime = System.currentTimeMillis();
                mOffProgress = 0;

                getParent().requestDisallowInterceptTouchEvent(true);
                vibrate();
                mLastX = event.getX();
                mLastY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                // we can be animating while moving
                if (mAnimating) {
                    return true;
                }
                final float center = getWidth() / 2;
                final float radius = (center / 2) - (mRectPadding * 2);

                final boolean inDeadzone = inCircle(x, y, center, center, radius);
                final boolean inOuterCircle = inCircle(x, y, center, center, radius + 70);
                if (DEBUG)
                    Log.d(TAG, "inOuterCircle: " + inOuterCircle + ", inDeadzone: " + inDeadzone);
                final float delta = getDelta(x, y);
                final float angle = angleWithOffset(x, y, DEGREE_OFFSET);

                if (mOn) {
                    if (isUserSelected() && (!inDeadzone)) {
                        float angleDiff = Math.abs(mLastAngle - angle);
                        if (mProgress == 1 && angle < (MAX_DEGREES / 2)) {
                            // oh jeez. no jumping from 100!
                            return true;
                        }
                        if (angleDiff < 90) {
                            // jump!
                            //Log.w(TAG, "using angle");
                            mLastAngle = angle;
                            mTouchProgress = angle / MAX_DEGREES;
                            mMoved = true;
                            if (DEBUG) Log.v(TAG, "ANGLE setProgress: " + mTouchProgress);
                            setProgress(mTouchProgress, true);
                        } else if (angle > 0 && angle < MAX_DEGREES) {
                            if (DEBUG) Log.v(TAG, "ANGLE animateTo: " + angle);
                            mMoved = true;
                            animateTo(angle / MAX_DEGREES);
                        }
                    }
                    // if it's less than one degree, turn it off
                    // 1% ~= 2.7 degrees, pick something slightly higher
                    if (mTouchProgress < (2.71f / MAX_DEGREES) && mOn && mMoved) {
                        mTouchProgress = (2.71f / MAX_DEGREES);
                        if (mOnKnobChangeListener != null) {
                            mOnKnobChangeListener.onSwitchChanged(this, !mOn);
                        }
                        setOn(!mOn);
                    }
                } else {
                    // off
                    if (isUserSelected() && (!inDeadzone)) {
                        if (delta > 0) {
                            mOffProgress += delta;
                        } else if (angle > 90) {
                            mOffProgress = 0;
                        }
                        if (DEBUG)
                            Log.d(TAG, "OFF, touching angle: " + angle +
                                    ", mOffProgress: " + mOffProgress + ", delta " + delta);
                        // we want at least 1%, how many degrees = 1%? + a little padding
                        final float onePercentInDegrees = (MAX_DEGREES / 100) + 1f;
                        if (mOffProgress > 15 && angle < MAX_DEGREES
                                && angle >= onePercentInDegrees) {
                            if (DEBUG) Log.w(TAG, "delta: " + delta);
                            if (angle <= MAX_DEGREES) {
                                if (mOnKnobChangeListener != null) {
                                    mOnKnobChangeListener.onSwitchChanged(this, !mOn);
                                }

                                setOn(!mOn);
                                if (angle > 30) {
                                    animateTo(angle / MAX_DEGREES);
                                } else {
                                    setProgress(angle / MAX_DEGREES, true);
                                }
                                mLastAngle = angle;
                                mMoved = false;
                            } else {
                                if (DEBUG) Log.w(TAG, "off, angle > 300, ignoring");
                            }
                        }
                    }
                    mLastX = x;
                    mLastY = y;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mUpTime = System.currentTimeMillis();
                final float finalAngle = angleWithOffset(x, y, DEGREE_OFFSET);
                if (DEBUG) Log.d(TAG, "angle at death: " + finalAngle);
                if (mUpTime - mDownTime < 100 && mMoved && finalAngle < MAX_DEGREES) {
                    if (mOn) {
                        animateTo(finalAngle / MAX_DEGREES);
                    } else {
                        if (mOnKnobChangeListener != null) {
                            mOnKnobChangeListener.onSwitchChanged(this, !mOn);
                        }

                        setOn(!mOn);
                    }
                }
                mLastX = -1;
                mLastY = -1;
                mOffProgress = 0;
                mMoved = false;
                break;
            default:
                break;
        }
        return true;
    }

    @SuppressLint("MissingPermission")
    private void vibrate() {
        if (mLastVibrateTime == null || System.currentTimeMillis() - mLastVibrateTime
                > DO_NOT_VIBRATE_THRESHOLD) {
            Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(40);
            mLastVibrateTime = System.currentTimeMillis();
        }
    }

    public void resize(boolean selected) {
        if (!mEnabled) {
            return;
        }
        if (selected) {
            animate()
                    .scaleY(RadialKnob.TOUCHING_SCALE)
                    .scaleX(RadialKnob.TOUCHING_SCALE)
                    .setDuration(100);
        } else {
            animate()
                    .scaleY(RadialKnob.REGULAR_SCALE)
                    .scaleX(RadialKnob.REGULAR_SCALE)
                    .setDuration(100);
        }
    }

    private float getDelta(float x, float y) {
        float angle = angle(x, y);
        float oldAngle = angle(mLastX, mLastY);
        float delta = angle - oldAngle;
        if (delta >= 180.0f) {
            delta = -oldAngle;
        } else if (delta <= -180.0f) {
            delta = 360 - oldAngle;
        }
        return delta;
    }

    private float angle(float x, float y) {
        float center = mWidth / 2.0f;
        x -= center;
        y -= center;

        if (x == 0.0f) {
            if (y > 0.0f) {
                return 180.0f;
            } else {
                return 0.0f;
            }
        }

        float angle = (float) (Math.atan(y / x) / Math.PI * 180.0);
        if (x > 0.0f) {
            angle += 90;
        } else {
            angle += 270;
        }
        return angle;
    }

    private float angleWithOffset(float x, float y, int degreeOffset) {
        float angle = angle(x, y);
        if (angle > 180) {
            angle += degreeOffset;
        } else {
            angle += (360 + degreeOffset);
        }
        return angle;
    }


    private static boolean inCircle(float x, float y, float circleCenterX, float circleCenterY,
                                    float circleRadius) {
        double dx = Math.pow(x - circleCenterX, 2);
        double dy = Math.pow(y - circleCenterY, 2);

        return (dx + dy) < Math.pow(circleRadius, 2);
    }

    @Override
    public boolean hasOverlappingRendering() {
        return false;
    }

    public void setOnKnobChangeListener(OnKnobChangeListener l) {
        mOnKnobChangeListener = l;
    }

    public interface OnKnobChangeListener {
        void onValueChanged(RadialKnob knob, int value, boolean fromUser);

        boolean onSwitchChanged(RadialKnob knob, boolean on);
    }
}
