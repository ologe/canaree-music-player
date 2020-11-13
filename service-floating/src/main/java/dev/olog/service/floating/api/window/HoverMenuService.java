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
package dev.olog.service.floating.api.window;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleService;

import dev.olog.service.floating.api.HoverView;
import dev.olog.service.floating.api.OnExitListener;
import dev.olog.service.floating.api.SideDock;
import dev.olog.service.floating.api.overlay.OverlayPermission;


/**
 * {@code Service} that presents a {@link HoverView} within a {@code Window}.
 *
 * The {@code HoverView} is displayed whenever any Intent is received by this {@code Service}. The
 * {@code HoverView} is removed and destroyed whenever this {@code Service} is destroyed.
 *
 * A {@link Service} is required for displaying a {@code HoverView} in a {@code Window} because there
 * is no {@code Activity} to associate with the {@code HoverView}'s UI. This {@code Service} is the
 * application's link to the device's {@code Window} to display the {@code HoverView}.
 */
public abstract class HoverMenuService extends LifecycleService {

    private static final String TAG = "HoverMenuService";

    private HoverView mHoverView;
    private boolean mIsRunning;
    @NonNull
    private OnExitListener mOnMenuOnExitListener = new OnExitListener() {
        @Override
        public void onExit() {
            Log.d(TAG, "Menu exit requested. Exiting.");
            mHoverView.removeFromWindow();
            onHoverMenuExitingByUserRequest();
            stopSelf();
        }
    };

    @Override
    @CallSuper
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");
        Notification foregroundNotification = getForegroundNotification();
        int notificationId = getForegroundNotificationId();
        startForeground(notificationId, foregroundNotification);
    }

    @Override
    @CallSuper
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        // Stop and return immediately if we don't have permission to display things above other
        // apps.
        if (!OverlayPermission.hasRuntimePermissionToDrawOverlay(getApplicationContext())) {
            Log.e(TAG, "Cannot display a Hover menu in a Window without the draw overlay permission.");
            stopSelf();
            return START_NOT_STICKY;
        }

        if (null == intent) {
            Log.e(TAG, "Received null Intent. Not creating Hover menu.");
            stopSelf();
            return START_NOT_STICKY;
        }

        if (!mIsRunning) {
            Log.d(TAG, "onStartCommand() - showing Hover menu.");
            mIsRunning = true;
            initHoverMenu(intent);
        }

        return START_STICKY;
    }

    @Override
    @CallSuper
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
        if (mIsRunning) {
            mHoverView.removeFromWindow();
            mIsRunning = false;
        }
    }

    private void initHoverMenu(@NonNull Intent intent) {
        mHoverView = HoverView.createForWindow(
                this,
                new WindowViewController((WindowManager) getSystemService(Context.WINDOW_SERVICE)),
                new SideDock.SidePosition(SideDock.SidePosition.RIGHT, 0.5f)
        );
        mHoverView.setOnExitListener(mOnMenuOnExitListener);
        mHoverView.addToWindow();

        onHoverMenuLaunched(intent, mHoverView);
    }

    @NonNull
    protected HoverView getHoverView() {
        return mHoverView;
    }

    protected abstract int getForegroundNotificationId();

    @NonNull
    protected abstract Notification getForegroundNotification();

    protected void onHoverMenuLaunched(@NonNull Intent intent, @NonNull HoverView hoverView) {
        // Hook for subclasses.
    }

    /**
     * Hook method for subclasses to take action when the user exits the HoverMenu. This method runs
     * just before this {@code HoverMenuService} calls {@code stopSelf()}.
     */
    protected void onHoverMenuExitingByUserRequest() {
        // Hook for subclasses.
    }
}
