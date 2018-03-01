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
package dev.olog.msc.floating.window.service.api;

import android.content.Context;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import dev.olog.msc.R;

/**
 * Fullscreen View that provides an exit "drop zone" for users to exit the Hover Menu.
 */
class ExitView extends RelativeLayout {

    private static final String TAG = "ExitView";

    private int mExitRadiusInPx;
    private View mExitIcon;

    public ExitView(@NonNull Context context) {
        this(context, null);
    }

    public ExitView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_hover_menu_exit, this, true);

        mExitIcon = findViewById(R.id.view_exit);

        mExitRadiusInPx = getResources().getDimensionPixelSize(R.dimen.hover_exit_radius); // todo increase radius?

        drawUnderNavigationBar();
    }

    private void drawUnderNavigationBar(){
        // todo
    }

    public boolean isInExitZone(@NonNull Point position) {
        Point exitCenter = getExitZoneCenter();
        double distanceToExit = calculateDistance(position, exitCenter);
        Log.d(TAG, "Drop point: " + position + ", Exit center: " + exitCenter + ", Distance: " + distanceToExit);
        return distanceToExit <= mExitRadiusInPx;
    }

    private Point getExitZoneCenter() {
        return new Point(
                (int) (mExitIcon.getX() + (mExitIcon.getWidth() / 2)),
                (int) (mExitIcon.getY() + (mExitIcon.getHeight() / 2))
        );
    }

    public void show(){
        setVisibility(View.VISIBLE); // todo animation
//        setVisibility(View.GONE);
//        setScaleX(.7f);
//        setScaleY(.8f);
//
//        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.scale_out);
//        animation.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//                setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//
//            }
//        });
//        startAnimation(animation);
    }

    public void hide(){
        setVisibility(View.GONE); // todo animation
//        setVisibility(View.VISIBLE);
//        setScaleX(1f);
//        setScaleY(1f);
//
//        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.scale_in);
//        animation.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//
//            }
//        });
//        startAnimation(animation);
    }

    private double calculateDistance(@NonNull Point p1, @NonNull Point p2) {
        return Math.sqrt(
                Math.pow(p2.x - p1.x, 2) + Math.pow(p2.y - p1.y, 2)
        );
    }
}
