package dev.olog.presentation.widgets.equalizer.croller;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import dev.olog.presentation.R;
import kotlin.jvm.JvmName;

public class Croller extends View {

    private float midx, midy;
    private Paint textPaint, circlePaint, circlePaint2, linePaint;
    private float currdeg = 0, deg = 3, downdeg = 0;

    private int backCircleColor = Color.parseColor("#222222");
    private int mainCircleColor = Color.parseColor("#000000");
    private int indicatorColor = Color.parseColor("#FFA036");
    private int progressPrimaryColor = Color.parseColor("#FFA036");
    private int progressSecondaryColor = Color.parseColor("#111111");

    private float progressPrimaryCircleSize = -1;
    private float progressSecondaryCircleSize = -1;

    private float progressPrimaryStrokeWidth = 25;
    private float progressSecondaryStrokeWidth = 10;

    private float mainCircleRadius = -1;
    private float backCircleRadius = -1;
    private float progressRadius = -1;

    protected int innerMax = 25;
    protected int innerMin = 1;

    private float indicatorWidth = 7;

    private String label = "Label";
    private String labelFont;
    private int labelStyle = 0;
    private float labelSize = 14;
    private int labelColor = Color.WHITE;

    private int labelDisabledColor = Color.BLACK;

    private int startOffset = 30;
    private int startOffset2 = 0;
    private int sweepAngle = -1;

    RectF oval;

    protected OnCrollerProgressChangedListener mProgressChangeListener;

    public Croller(Context context, AttributeSet attrs) {
        super(context, attrs);
        initXMLAttrs(context, attrs);
        init();
    }

    private void init() {

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setFakeBoldText(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(labelSize);

        generateTypeface();

        circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setStrokeWidth(progressSecondaryStrokeWidth);
        circlePaint.setStyle(Paint.Style.FILL);

        circlePaint2 = new Paint();
        circlePaint2.setAntiAlias(true);
        circlePaint2.setStrokeWidth(progressPrimaryStrokeWidth);
        circlePaint2.setStyle(Paint.Style.FILL);

        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setStrokeWidth(indicatorWidth);

        circlePaint2.setColor(progressPrimaryColor);
        circlePaint.setColor(progressSecondaryColor);
        linePaint.setColor(indicatorColor);
        textPaint.setColor(labelColor);

        oval = new RectF();

    }

    private void generateTypeface() {
        Typeface plainLabel = Typeface.DEFAULT;
        if (getLabelFont() != null && !getLabelFont().isEmpty()) {
            AssetManager assetMgr = getContext().getAssets();
            plainLabel = Typeface.createFromAsset(assetMgr, getLabelFont());
        }

        switch (getLabelStyle()) {
            case 0:
                textPaint.setTypeface(plainLabel);
                break;
            case 1:
                textPaint.setTypeface(Typeface.create(plainLabel, Typeface.BOLD));
                break;
            case 2:
                textPaint.setTypeface(Typeface.create(plainLabel, Typeface.ITALIC));
                break;
            case 3:
                textPaint.setTypeface(Typeface.create(plainLabel, Typeface.BOLD_ITALIC));
                break;

        }

    }

    private void initXMLAttrs(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Croller);

        setInnerProgress(a.getInt(R.styleable.Croller_start_progress, 1));
        setLabel(a.getString(R.styleable.Croller_label));

        setBackCircleColor(a.getColor(R.styleable.Croller_back_circle_color, backCircleColor));
        setMainCircleColor(a.getColor(R.styleable.Croller_main_circle_color, mainCircleColor));
        setIndicatorColor(a.getColor(R.styleable.Croller_indicator_color, indicatorColor));
        setProgressPrimaryColor(a.getColor(R.styleable.Croller_progress_primary_color, progressPrimaryColor));
        setProgressSecondaryColor(a.getColor(R.styleable.Croller_progress_secondary_color, progressSecondaryColor));

        setLabelSize(a.getDimension(R.styleable.Croller_label_size, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                labelSize, getResources().getDisplayMetrics())));
        setLabelColor(a.getColor(R.styleable.Croller_label_color, labelColor));
        setLabelFont(a.getString(R.styleable.Croller_label_font));
        setLabelStyle(a.getInt(R.styleable.Croller_label_style, 0));
        setIndicatorWidth(a.getFloat(R.styleable.Croller_indicator_width, 7));
        setProgressPrimaryCircleSize(a.getFloat(R.styleable.Croller_progress_primary_circle_size, -1));
        setProgressSecondaryCircleSize(a.getFloat(R.styleable.Croller_progress_secondary_circle_size, -1));
        setProgressPrimaryStrokeWidth(a.getFloat(R.styleable.Croller_progress_primary_stroke_width, 25));
        setProgressSecondaryStrokeWidth(a.getFloat(R.styleable.Croller_progress_secondary_stroke_width, 10));
        setSweepAngle(a.getInt(R.styleable.Croller_sweep_angle, -1));
        setStartOffset(a.getInt(R.styleable.Croller_start_offset, 30));
        innerMax = a.getInt(R.styleable.Croller_max, 25);
        innerMin = a.getInt(R.styleable.Croller_min, 1);
        deg = innerMin + 2;
        setBackCircleRadius(a.getFloat(R.styleable.Croller_back_circle_radius, -1));
        setProgressRadius(a.getFloat(R.styleable.Croller_progress_radius, -1));
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int minWidth = (int) Utils.convertDpToPixel(160, getContext());
        int minHeight = (int) Utils.convertDpToPixel(160, getContext());

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(minWidth, widthSize);
        } else {
            // only in case of ScrollViews, otherwise MeasureSpec.UNSPECIFIED is never triggered
            // If width is wrap_content i.e. MeasureSpec.UNSPECIFIED, then make width equal to height
            width = heightSize;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(minHeight, heightSize);
        } else {
            // only in case of ScrollViews, otherwise MeasureSpec.UNSPECIFIED is never triggered
            // If height is wrap_content i.e. MeasureSpec.UNSPECIFIED, then make height equal to width
            height = widthSize;
        }

        if (widthMode == MeasureSpec.UNSPECIFIED && heightMode == MeasureSpec.UNSPECIFIED) {
            width = minWidth;
            height = minHeight;
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        midx = getWidth() / 2f;
        midy = getHeight() / 2f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mProgressChangeListener != null)
            mProgressChangeListener.onProgressChanged((int) (deg - 2));

        circlePaint2.setColor(progressPrimaryColor);
        circlePaint.setColor(progressSecondaryColor);
        linePaint.setColor(indicatorColor);
        textPaint.setColor(labelColor);

        startOffset2 = startOffset - 15;

        linePaint.setStrokeWidth(indicatorWidth);
        textPaint.setTextSize(labelSize);

        int radius = (int) (Math.min(midx, midy) * ((float) 14.5 / 16));

        if (sweepAngle == -1) {
            sweepAngle = 360 - (2 * startOffset2);
        }

        if (mainCircleRadius == -1) {
            mainCircleRadius = radius * ((float) 11 / 15);
        }
        if (backCircleRadius == -1) {
            backCircleRadius = radius * ((float) 13 / 15);
        }
        if (progressRadius == -1) {
            progressRadius = radius;
        }

        float x, y;
        float deg2 = Math.max(3, deg);
        float deg3 = Math.min(deg, innerMax + 2);
        for (int i = (int) (deg2); i < innerMax + 3; i++) {
            float tmp = ((float) startOffset2 / 360) + ((float) sweepAngle / 360) * (float) i / (innerMax + 5);

            x = midx + (float) (progressRadius * Math.sin(2 * Math.PI * (1.0 - tmp)));
            y = midy + (float) (progressRadius * Math.cos(2 * Math.PI * (1.0 - tmp)));
            if (progressSecondaryCircleSize == -1)
                canvas.drawCircle(x, y, ((float) radius / 30 * ((float) 20 / innerMax) * ((float) sweepAngle / 270)), circlePaint);
            else
                canvas.drawCircle(x, y, progressSecondaryCircleSize, circlePaint);
        }
        for (int i = 3; i <= deg3; i++) {
            float tmp = ((float) startOffset2 / 360) + ((float) sweepAngle / 360) * (float) i / (innerMax + 5);

            x = midx + (float) (progressRadius * Math.sin(2 * Math.PI * (1.0 - tmp)));
            y = midy + (float) (progressRadius * Math.cos(2 * Math.PI * (1.0 - tmp)));
            if (progressPrimaryCircleSize == -1)
                canvas.drawCircle(x, y, (progressRadius / 15 * ((float) 20 / innerMax) * ((float) sweepAngle / 270)), circlePaint2);
            else
                canvas.drawCircle(x, y, progressPrimaryCircleSize, circlePaint2);
        }

        float tmp2 = ((float) startOffset2 / 360) + ((float) sweepAngle / 360) * deg / (innerMax + 5);

        float x1 = midx + (float) (radius * ((float) 2 / 5) * Math.sin(2 * Math.PI * (1.0 - tmp2)));
        float y1 = midy + (float) (radius * ((float) 2 / 5) * Math.cos(2 * Math.PI * (1.0 - tmp2)));
        float x2 = midx + (float) (radius * ((float) 3 / 5) * Math.sin(2 * Math.PI * (1.0 - tmp2)));
        float y2 = midy + (float) (radius * ((float) 3 / 5) * Math.cos(2 * Math.PI * (1.0 - tmp2)));

        circlePaint.setColor(backCircleColor);
        canvas.drawCircle(midx, midy, backCircleRadius, circlePaint);
        circlePaint.setColor(mainCircleColor);
        canvas.drawCircle(midx, midy, mainCircleRadius, circlePaint);
        canvas.drawText(label, midx, midy + (float) (radius * 1.1)-textPaint.getFontMetrics().descent, textPaint);
        canvas.drawLine(x1, y1, x2, y2, linePaint);

    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {

        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            getParent().requestDisallowInterceptTouchEvent(true);

            float dx = e.getX() - midx;
            float dy = e.getY() - midy;
            downdeg = (float) ((Math.atan2(dy, dx) * 180) / Math.PI);
            downdeg -= 90;
            if (downdeg < 0) {
                downdeg += 360;
            }
            downdeg = (float) Math.floor((downdeg / 360) * (innerMax + 5));

            return true;
        }
        if (e.getAction() == MotionEvent.ACTION_MOVE) {
            float dx = e.getX() - midx;
            float dy = e.getY() - midy;
            currdeg = (float) ((Math.atan2(dy, dx) * 180) / Math.PI);
            currdeg -= 90;
            if (currdeg < 0) {
                currdeg += 360;
            }
            currdeg = (float) Math.floor((currdeg / 360) * (innerMax + 5));

            if ((currdeg / (innerMax + 4)) > 0.75f && ((downdeg - 0) / (innerMax + 4)) < 0.25f) {
                deg--;
                if (deg < (innerMin + 2)) {
                    deg = (innerMin + 2);
                    }
            } else if ((downdeg / (innerMax + 4)) > 0.75f && ((currdeg - 0) / (innerMax + 4)) < 0.25f) {
                deg++;
                if (deg > innerMax + 2) {
                    deg = innerMax + 2;
                }
            } else {
                deg += (currdeg - downdeg);
                if (deg > innerMax + 2) {
                    deg = innerMax + 2;
                }
                if (deg < (innerMin + 2)) {
                    deg = (innerMin + 2);
                }
            }

            downdeg = currdeg;

            invalidate();
            return true;

        }
        if (e.getAction() == MotionEvent.ACTION_UP) {
            getParent().requestDisallowInterceptTouchEvent(false);
            return true;
        }
        return super.onTouchEvent(e);
    }

    protected int getInnerProgress() {
        return (int) (deg - 2);
    }

    protected void setInnerProgress(int x) {
        deg = x + 2;
        invalidate();
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String txt) {
        label = txt;
        invalidate();
    }

    public int getBackCircleColor() {
        return backCircleColor;
    }

    public void setBackCircleColor(int backCircleColor) {
        this.backCircleColor = backCircleColor;
        invalidate();
    }

    public int getMainCircleColor() {
        return mainCircleColor;
    }

    public void setMainCircleColor(int mainCircleColor) {
        this.mainCircleColor = mainCircleColor;
        invalidate();
    }

    public int getIndicatorColor() {
        return indicatorColor;
    }

    public void setIndicatorColor(int indicatorColor) {
        this.indicatorColor = indicatorColor;
        invalidate();
    }

    public int getProgressPrimaryColor() {
        return progressPrimaryColor;
    }

    public void setProgressPrimaryColor(int progressPrimaryColor) {
        this.progressPrimaryColor = progressPrimaryColor;
        invalidate();
    }

    public int getProgressSecondaryColor() {
        return progressSecondaryColor;
    }

    public void setProgressSecondaryColor(int progressSecondaryColor) {
        this.progressSecondaryColor = progressSecondaryColor;
        invalidate();
    }

    public float getLabelSize() {
        return labelSize;
    }

    public void setLabelSize(float labelSize) {
        this.labelSize = labelSize;
        invalidate();
    }

    public int getLabelColor() {
        return labelColor;
    }

    public void setLabelColor(int labelColor) {
        this.labelColor = labelColor;
        invalidate();
    }

    public int getlabelDisabledColor() {
        return labelDisabledColor;
    }

    public void setlabelDisabledColor(int labelDisabledColor) {
        this.labelDisabledColor = labelDisabledColor;
        invalidate();
    }

    public String getLabelFont() {
        return labelFont;
    }

    public void setLabelFont(String labelFont) {
        this.labelFont = labelFont;
        if (textPaint != null)
            generateTypeface();
        invalidate();
    }

    public int getLabelStyle() {
        return labelStyle;
    }

    public void setLabelStyle(int labelStyle) {
        this.labelStyle = labelStyle;
        invalidate();
    }

    public float getIndicatorWidth() {
        return indicatorWidth;
    }

    public void setIndicatorWidth(float indicatorWidth) {
        this.indicatorWidth = indicatorWidth;
        invalidate();
    }

    public float getProgressPrimaryCircleSize() {
        return progressPrimaryCircleSize;
    }

    public void setProgressPrimaryCircleSize(float progressPrimaryCircleSize) {
        this.progressPrimaryCircleSize = progressPrimaryCircleSize;
        invalidate();
    }

    public float getProgressSecondaryCircleSize() {
        return progressSecondaryCircleSize;
    }

    public void setProgressSecondaryCircleSize(float progressSecondaryCircleSize) {
        this.progressSecondaryCircleSize = progressSecondaryCircleSize;
        invalidate();
    }

    public float getProgressPrimaryStrokeWidth() {
        return progressPrimaryStrokeWidth;
    }

    public void setProgressPrimaryStrokeWidth(float progressPrimaryStrokeWidth) {
        this.progressPrimaryStrokeWidth = progressPrimaryStrokeWidth;
        invalidate();
    }

    public float getProgressSecondaryStrokeWidth() {
        return progressSecondaryStrokeWidth;
    }

    public void setProgressSecondaryStrokeWidth(float progressSecondaryStrokeWidth) {
        this.progressSecondaryStrokeWidth = progressSecondaryStrokeWidth;
        invalidate();
    }

    public int getSweepAngle() {
        return sweepAngle;
    }

    public void setSweepAngle(int sweepAngle) {
        this.sweepAngle = sweepAngle;
        invalidate();
    }

    public int getStartOffset() {
        return startOffset;
    }

    public void setStartOffset(int startOffset) {
        this.startOffset = startOffset;
        invalidate();
    }

    public float getMainCircleRadius() {
        return mainCircleRadius;
    }

    public void setMainCircleRadius(float mainCircleRadius) {
        this.mainCircleRadius = mainCircleRadius;
        invalidate();
    }

    public float getBackCircleRadius() {
        return backCircleRadius;
    }

    public void setBackCircleRadius(float backCircleRadius) {
        this.backCircleRadius = backCircleRadius;
        invalidate();
    }

    public float getProgressRadius() {
        return progressRadius;
    }

    public void setProgressRadius(float progressRadius) {
        this.progressRadius = progressRadius;
        invalidate();
    }

}