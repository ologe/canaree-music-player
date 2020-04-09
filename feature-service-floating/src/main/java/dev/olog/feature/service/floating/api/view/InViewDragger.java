/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.olog.feature.service.floating.api.view;

import android.graphics.Point;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import dev.olog.feature.service.floating.R;
import dev.olog.feature.service.floating.api.Dragger;
import timber.log.Timber;

/**
 * {@link Dragger} implementation that works within a {@link ViewGroup}.
 */
public class InViewDragger implements Dragger {

    private static final String TAG = "InViewDragger";

    @NonNull
    private final ViewGroup mContainer;
    private final int mTouchAreaDiameter;
    private final int mTapTouchSlop;
    private boolean mIsActivated;
    private boolean mIsDragging;
    private boolean mIsDebugMode = false;
    @Nullable
    private View mDragView;
    private DragListener mDragListener;
    @NonNull
    private PointF mOriginalViewPosition = new PointF();
    @NonNull
    private PointF mCurrentViewPosition = new PointF();
    @NonNull
    private PointF mOriginalTouchPosition = new PointF();

    private final View.OnTouchListener mDragTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Timber.d(TAG + "ACTION_DOWN");
                    mIsDragging = false;

                    mOriginalViewPosition = getDragViewCenterPosition();
                    mCurrentViewPosition = new PointF(mOriginalViewPosition.x, mOriginalViewPosition.y);
                    mOriginalTouchPosition.set(motionEvent.getRawX(), motionEvent.getRawY());

                    mDragListener.onPress(mCurrentViewPosition.x, mCurrentViewPosition.y);

                    return true;
                case MotionEvent.ACTION_MOVE:
                    Timber.v(TAG + "ACTION_MOVE. motionX: " + motionEvent.getRawX() + ", motionY: " + motionEvent.getRawY());
                    float dragDeltaX = motionEvent.getRawX() - mOriginalTouchPosition.x;
                    float dragDeltaY = motionEvent.getRawY() - mOriginalTouchPosition.y;
                    mCurrentViewPosition = new PointF(
                            mOriginalViewPosition.x + dragDeltaX,
                            mOriginalViewPosition.y + dragDeltaY
                    );

                    if (mIsDragging || !isTouchWithinSlopOfOriginalTouch(dragDeltaX, dragDeltaY)) {
                        if (!mIsDragging) {
                            // Dragging just started
                            Timber.d(TAG + "MOVE Start Drag.");
                            mIsDragging = true;
                            mDragListener.onDragStart(mCurrentViewPosition.x, mCurrentViewPosition.y);
                        } else {
                            moveDragViewTo(mCurrentViewPosition);
                            mDragListener.onDragTo(mCurrentViewPosition.x, mCurrentViewPosition.y);
                        }
                    }

                    return true;
                case MotionEvent.ACTION_UP:
                    if (!mIsDragging) {
                        Timber.d(TAG + "ACTION_UP: Tap.");
                        mDragListener.onTap();
                    } else {
                        Timber.d(TAG + "ACTION_UP: Released from dragging.");
                        mDragListener.onReleasedAt(mCurrentViewPosition.x, mCurrentViewPosition.y);
                    }

                    return true;
                default:
                    return false;
            }
        }
    };

    public InViewDragger(@NonNull ViewGroup container, int touchAreaDiameter, int touchSlop) {
        mContainer = container;
        mTouchAreaDiameter = touchAreaDiameter;
        mTapTouchSlop = touchSlop;
    }

    @Override
    public void enableDebugMode(boolean isDebugMode) {
        mIsDebugMode = isDebugMode;
        updateTouchControlViewAppearance();
    }

    @Override
    public void activate(@NonNull DragListener dragListener, @NonNull Point dragStartCenterPosition) {
        if (!mIsActivated) {
            Timber.d(TAG + "Activating.");
            mIsActivated = true;
            mDragListener = dragListener;
            createTouchControlView(dragStartCenterPosition);
        }
    }

    @Override
    public void deactivate() {
        if (mIsActivated) {
            Timber.d(TAG + "Deactivating.");
            mIsActivated = false;
            destroyTouchControlView();
        }
    }

    private void createTouchControlView(@NonNull Point dragStartCenterPosition) {
        mDragView = new View(mContainer.getContext());
        mDragView.setId(R.id.hover_drag_view);
        mDragView.setLayoutParams(new ViewGroup.LayoutParams(mTouchAreaDiameter, mTouchAreaDiameter));
        mDragView.setOnTouchListener(mDragTouchListener);
        mContainer.addView(mDragView);

        moveDragViewTo(new PointF(dragStartCenterPosition.x, dragStartCenterPosition.y));
        updateTouchControlViewAppearance();
    }

    private void destroyTouchControlView() {
        mContainer.removeView(mDragView);

        mDragView.setOnTouchListener(null);
        mDragView = null;
    }

    private void updateTouchControlViewAppearance() {
        if (null != mDragView) {
            if (mIsDebugMode) {
                Timber.d(TAG + "Making mDragView red: " + mDragView.hashCode());
                mDragView.setBackgroundColor(0x44FF0000);
            } else {
                mDragView.setBackgroundColor(0x00000000);
            }
        }
    }

    private boolean isTouchWithinSlopOfOriginalTouch(float dx, float dy) {
        double distance = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
        return distance < mTapTouchSlop;
    }

    private PointF getDragViewCenterPosition() {
        return convertCornerToCenter(new PointF(
                mDragView.getX(),
                mDragView.getY()
        ));
    }

    private void moveDragViewTo(@NonNull PointF centerPosition) {
        Timber.d(TAG + "Moving drag view (" + mDragView.hashCode() + ") to: " + centerPosition);
        PointF cornerPosition = convertCenterToCorner(centerPosition);
        mDragView.setX(cornerPosition.x);
        mDragView.setY(cornerPosition.y);
    }

    private PointF convertCornerToCenter(@NonNull PointF cornerPosition) {
        return new PointF(
                cornerPosition.x + (mTouchAreaDiameter / 2),
                cornerPosition.y + (mTouchAreaDiameter / 2)
        );
    }

    private PointF convertCenterToCorner(@NonNull PointF centerPosition) {
        return new PointF(
                centerPosition.x - (mTouchAreaDiameter / 2),
                centerPosition.y - (mTouchAreaDiameter / 2)
        );
    }
}
