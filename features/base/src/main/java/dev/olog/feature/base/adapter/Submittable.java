package dev.olog.feature.base.adapter;

import androidx.annotation.Nullable;

import java.util.List;

/**
 * Same signature as {@link androidx.recyclerview.widget.ListAdapter}
 */
public interface Submittable<T> {

    void submitList(@Nullable List<T> list);

    void submitList(@Nullable List<T> list, @Nullable final Runnable commitCallback);

}
