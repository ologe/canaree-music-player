package dev.olog.presentation.widgets;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class SwipeableImageView extends AppCompatImageView {

    public final static int DEFAULT_SWIPED_THRESHOLD = 100;

    private final int swipedThreshold;
    private float xDown, xUp;
    private float yDown, yUp;
    private SwipeListener swipeListener;

    public SwipeableImageView(Context context) {
        this(context, null, 0);
    }

    public SwipeableImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeableImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        swipedThreshold = DEFAULT_SWIPED_THRESHOLD;
    }

    public void setOnSwipeListener(SwipeListener swipeListener) {
        this.swipeListener = swipeListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: // user started touching the screen
                onActionDown(event);
                break;
            case MotionEvent.ACTION_UP:   // user stopped touching the screen
                onActionUp(event);
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    private void onActionDown(final MotionEvent event) {
        xDown = event.getX();
        yDown = event.getY();
    }

    private void onActionUp(final MotionEvent event) {
        xUp = event.getX();
        yUp = event.getY();
        final boolean swipedHorizontally = Math.abs(xUp - xDown) > swipedThreshold;
        final boolean swipedVertically = Math.abs(yUp - yDown) > swipedThreshold;

        boolean isHorizontalScroll = swipedHorizontally &&
                (Math.abs(xUp - xDown) > Math.abs(yUp - yDown));

        if(isHorizontalScroll){
            final boolean swipedRight = xUp > xDown;
            final boolean swipedLeft = xUp < xDown;

            if (swipedRight) {
                if (swipeListener != null) {
                    swipeListener.onSwipedRight();
                }
            }
            if (swipedLeft) {
                if (swipeListener != null) {
                    swipeListener.onSwipedLeft();
                }
            }
        }

        if(!swipedHorizontally && !swipedVertically){
            if (swipeListener != null) {
                swipeListener.onClick();
            }
        }
    }

    public interface SwipeListener {

        default void onSwipedLeft(){
        }

        default void onSwipedRight(){
        }

        default void onSwipedUp(){
        }

        default void onSwipedDown(){
        }

        default void onClick(){
        }
    }
}
