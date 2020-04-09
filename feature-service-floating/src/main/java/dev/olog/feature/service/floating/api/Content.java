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
package dev.olog.feature.service.floating.api;

import android.view.View;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;


/**
 * Content to be displayed in a {@link HoverView}.
 */
public abstract class Content implements LifecycleOwner {

    private final LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);

    public Content() {
        lifecycleRegistry.setCurrentState(Lifecycle.State.INITIALIZED);
    }

    /**
     * Returns the visual display of this content.
     *
     * @return the visual representation of this content
     */
    @NonNull
    public abstract View getView();

    /**
     * @return true to fill all available space, false to wrap content height
     */
    public abstract boolean isFullscreen();

    /**
     * Called when this content is displayed to the user.
     */
    @CallSuper
    public void onShown() {
        lifecycleRegistry.setCurrentState(Lifecycle.State.STARTED);
        lifecycleRegistry.setCurrentState(Lifecycle.State.RESUMED);
    }

    /**
     * Called when this content is no longer displayed to the user.
     * <p>
     * Implementation Note: {@code Content} can be brought back due to user navigation so
     * this call must not release resources that are required to show this content again.
     */
    @CallSuper
    public void onHidden() {
        lifecycleRegistry.setCurrentState(Lifecycle.State.STARTED);
        lifecycleRegistry.setCurrentState(Lifecycle.State.CREATED);
    }

    @CallSuper
    public void onDispose(){
        lifecycleRegistry.setCurrentState(Lifecycle.State.DESTROYED);
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return lifecycleRegistry;
    }
}
