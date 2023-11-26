package dev.olog.presentation.widgets.equalizer.bar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.content.ContextCompat;

import dev.olog.presentation.R;

public class BoxedVertical extends View {

    /**
     * The min value of progress value.
     */
    private float mMin = 0f;

    /**
     * The Maximum value that this SeekArc can be set to
     */
    private float mMax = 100f;

    /**
     * The increment/decrement value for each movement of progress.
     */
    private float mStep = 1f;

    /**
     * The corner radius of the view.
     */
    private int mCornerRadius = 0;

    /**
     * Text size in SP.
     */
    private float mTextSize = 26;

    /**
     * Text bottom padding in pixel.
     */
    private int mtextBottomPadding = 20;

    private float mPoints;

    /**
     * Enable or disable text .
     */
    private boolean mtextEnabled = true;

    /**
     * Enable or disable image .
     */
    private boolean mImageEnabled = false;

    /**
     * mTouchDisabled touches will not move the slider
     * only swipe motion will activate it
     */
    private boolean mTouchDisabled = true;

    private float mProgressSweep = 0;
    private Paint mProgressPaint;
    private Paint mTextPaint;
    private int scrWidth;
    private int scrHeight;
    private OnValuesChangeListener mOnValuesChangeListener;
    private int backgroundColor;
    private float mDefaultValue;
    private Bitmap mDefaultImage;
    private Bitmap mMinImage;
    private Bitmap mMaxImage;
    private Rect dRect = new Rect();
    private boolean firstRun = true;

    private Paint paint = new Paint();
    private Path mPath = new Path();
    private RectF rectF = new RectF();

    public BoxedVertical(Context context) {
        super(context);
        init(context, null);
    }

    public BoxedVertical(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        paint.setAlpha(255);

        float density = getResources().getDisplayMetrics().density;

        // Defaults, may need to link this into theme settings
        int progressColor = ContextCompat.getColor(context, R.color.boxed_color_progress);
        backgroundColor = ContextCompat.getColor(context, R.color.boxed_color_background);
        backgroundColor = ContextCompat.getColor(context, R.color.boxed_color_background);

        int textColor = ContextCompat.getColor(context, R.color.boxed_color_text);
        mTextSize = (int) (mTextSize * density);
        mDefaultValue = mMax / 2;

        if (attrs != null) {
            final TypedArray a = context.obtainStyledAttributes(attrs,
                    R.styleable.BoxedVertical, 0, 0);

            mPoints = a.getFloat(R.styleable.BoxedVertical_boxed_points, mPoints);
            mMax = a.getFloat(R.styleable.BoxedVertical_boxed_max, mMax);
            mMin = a.getFloat(R.styleable.BoxedVertical_boxed_min, mMin);
            mStep = a.getFloat(R.styleable.BoxedVertical_boxed_step, mStep);
            mDefaultValue = a.getFloat(R.styleable.BoxedVertical_boxed_defaultValue, mDefaultValue);
            mCornerRadius = a.getInteger(R.styleable.BoxedVertical_boxed_libCornerRadius, mCornerRadius);
            mtextBottomPadding = a.getInteger(R.styleable.BoxedVertical_boxed_textBottomPadding, mtextBottomPadding);
            //Images
            mImageEnabled = a.getBoolean(R.styleable.BoxedVertical_boxed_imageEnabled, mImageEnabled);

            if (mImageEnabled) {
                mDefaultImage = ((BitmapDrawable) a.getDrawable(R.styleable.BoxedVertical_boxed_defaultImage)).getBitmap();
                mMinImage = ((BitmapDrawable) a.getDrawable(R.styleable.BoxedVertical_boxed_minImage)).getBitmap();
                mMaxImage = ((BitmapDrawable) a.getDrawable(R.styleable.BoxedVertical_boxed_maxImage)).getBitmap();
            }

            progressColor = a.getColor(R.styleable.BoxedVertical_boxed_progressColor, progressColor);
            backgroundColor = a.getColor(R.styleable.BoxedVertical_boxed_backgroundColor, backgroundColor);

            mTextSize = (int) a.getDimension(R.styleable.BoxedVertical_boxed_textSize, mTextSize);
            textColor = a.getColor(R.styleable.BoxedVertical_boxed_textColor, textColor);

            mTouchDisabled = a.getBoolean(R.styleable.BoxedVertical_boxed_touchDisabled, mTouchDisabled);
            mtextEnabled = a.getBoolean(R.styleable.BoxedVertical_boxed_textEnabled, mtextEnabled);

            mPoints = mDefaultValue;

            a.recycle();
        }

        // range check
        mPoints = (mPoints > mMax) ? mMax : mPoints;
        mPoints = (mPoints < mMin) ? mMin : mPoints;

        mProgressPaint = new Paint();
        mProgressPaint.setColor(progressColor);
        mProgressPaint.setAntiAlias(true);
        mProgressPaint.setStyle(Paint.Style.STROKE);

        mTextPaint = new Paint();
        mTextPaint.setColor(textColor);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextSize(mTextSize);

        scrHeight = context.getResources().getDisplayMetrics().heightPixels;

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        scrWidth = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        scrHeight = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        mProgressPaint.setStrokeWidth(scrWidth);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPath.reset();

        canvas.translate(0, 0);
        rectF.set(0, 0, scrWidth, scrHeight);
        mPath.addRoundRect(rectF, mCornerRadius, mCornerRadius, Path.Direction.CCW);
        canvas.clipPath(mPath, Region.Op.INTERSECT);
        paint.setColor(backgroundColor);
        paint.setAntiAlias(true);
        canvas.drawRect(0, 0, scrWidth, scrHeight, paint);

        canvas.drawLine(getWidth() / 2, getHeight(), getWidth() / 2, mProgressSweep, mProgressPaint);

        if (mImageEnabled && mDefaultImage != null && mMinImage != null && mMaxImage != null) {
            //If image is enabled, text will not be shown
            if (mPoints == mMax) {
                drawIcon(mMaxImage, canvas);
            } else if (mPoints == mMin) {
                drawIcon(mMinImage, canvas);
            } else {
                drawIcon(mDefaultImage, canvas);
            }
        } else {
            //If image is disabled and text is enabled show text
            if (mtextEnabled) {
                String strPoint = String.valueOf(mPoints);
                drawText(canvas, mTextPaint, strPoint);
            }
        }

        if (firstRun) {
            firstRun = false;
            setValue(mPoints);
        }
    }

    private void drawText(Canvas canvas, Paint paint, String text) {
        canvas.getClipBounds(dRect);
        int cWidth = dRect.width();
        paint.setTextAlign(Paint.Align.LEFT);
        paint.getTextBounds(text, 0, text.length(), dRect);
        float x = cWidth / 2f - dRect.width() / 2f - dRect.left;
        canvas.drawText(text, x, canvas.getHeight() - mtextBottomPadding, paint);
    }

    private void drawIcon(Bitmap bitmap, Canvas canvas) {
        bitmap = getResizedBitmap(bitmap, canvas.getWidth() / 2, canvas.getWidth() / 2);
        canvas.drawBitmap(bitmap, null, new RectF((canvas.getWidth() / 2) - (bitmap.getWidth() / 2), canvas.getHeight() - bitmap.getHeight(), (canvas.getWidth() / 3) + bitmap.getWidth(), canvas.getHeight()), null);
    }

    private Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        //Thanks Piyush
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);
        // recreate the new Bitmap
        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.getParent().requestDisallowInterceptTouchEvent(true);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mOnValuesChangeListener != null)
                    mOnValuesChangeListener.onStartTrackingTouch(this);

                if (!mTouchDisabled)
                    updateOnTouch(event);
                break;
            case MotionEvent.ACTION_MOVE:
                updateOnTouch(event);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mOnValuesChangeListener != null)
                    mOnValuesChangeListener.onStopTrackingTouch(this);
                setPressed(false);
                this.getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }
        return true;
    }

    /**
     * Update the UI components on touch events.
     *
     * @param event MotionEvent
     */
    private void updateOnTouch(MotionEvent event) {
        setPressed(true);
        double mTouch = convertTouchEventPoint(event.getY());
        int progress = (int) Math.round(mTouch);
        updateProgress(progress);
    }

    private double convertTouchEventPoint(float yPos) {
        float wReturn;

        if (yPos > (scrHeight * 2)) {
            wReturn = scrHeight * 2;
            return wReturn;
        } else if (yPos < 0) {
            wReturn = 0;
        } else {
            wReturn = yPos;
        }

        return wReturn;
    }

    private void updateProgress(int progress) {
        mProgressSweep = progress;

        progress = (progress > scrHeight) ? scrHeight : progress;
        progress = (progress < 0) ? 0 : progress;

        //convert progress to min-max range
        mPoints = progress * (mMax - mMin) / scrHeight + mMin;
        //reverse value because progress is descending
        mPoints = mMax + mMin - mPoints;
        //if value is not max or min, apply step
        if (mPoints != mMax && mPoints != mMin) {
            mPoints = mPoints - (mPoints % mStep) + (mMin % mStep);
        }

        if (mOnValuesChangeListener != null) {
            mOnValuesChangeListener
                    .onPointsChanged(this, mPoints);
        }

        invalidate();
    }

    /**
     * Gets a value, converts it to progress for the seekBar and updates it.
     *
     * @param value The value given
     */
    private void updateProgressByValue(float value) {
        mPoints = value;

        mPoints = (mPoints > mMax) ? mMax : mPoints;
        mPoints = (mPoints < mMin) ? mMin : mPoints;

        //convert min-max range to progress
        mProgressSweep = (mPoints - mMin) * scrHeight / (mMax - mMin);
        //reverse value because progress is descending
        mProgressSweep = scrHeight - mProgressSweep;

        if (mOnValuesChangeListener != null) {
            mOnValuesChangeListener
                    .onPointsChanged(this, mPoints);
        }

        invalidate();
    }

    public interface OnValuesChangeListener {
        /**
         * Notification that the point value has changed.
         *
         * @param boxedPoints The SwagPoints view whose value has changed
         * @param points      The current point value.
         */
        void onPointsChanged(BoxedVertical boxedPoints, float points);

        void onStartTrackingTouch(BoxedVertical boxedPoints);

        void onStopTrackingTouch(BoxedVertical boxedPoints);
    }

    public void setValue(float points) {
        points = points > mMax ? mMax : points;
        points = points < mMin ? mMin : points;

        updateProgressByValue(points);
    }

    public float getValue() {
        return mPoints;
    }

    public float getMax() {
        return mMax;
    }

    public float getMin() {
        return mMin;
    }

    public void setMax(float mMax) {
        if (mMax <= mMin)
            throw new IllegalArgumentException("Max should not be less than min");
        this.mMax = mMax;
    }

    public void setMin(float mMin) {
        if (mMin >= mMax)
            throw new IllegalArgumentException("Min should not be less than max");
        this.mMin = mMin;
    }

    public void setCornerRadius(int mRadius) {
        this.mCornerRadius = mRadius;
        invalidate();
    }

    public int getCornerRadius() {
        return mCornerRadius;
    }

    public float getDefaultValue() {
        return mDefaultValue;
    }

    public void setDefaultValue(float mDefaultValue) {
        if (mDefaultValue > mMax)
            throw new IllegalArgumentException("Default value should not be bigger than max value.");
        this.mDefaultValue = mDefaultValue;

    }

    public float getStep() {
        return mStep;
    }

    public void setStep(float step) {
        mStep = step;
    }

    public boolean isImageEnabled() {
        return mImageEnabled;
    }

    public void setImageEnabled(boolean mImageEnabled) {
        this.mImageEnabled = mImageEnabled;
    }

    public void setOnBoxedPointsChangeListener(OnValuesChangeListener onValuesChangeListener) {
        mOnValuesChangeListener = onValuesChangeListener;
    }
}
