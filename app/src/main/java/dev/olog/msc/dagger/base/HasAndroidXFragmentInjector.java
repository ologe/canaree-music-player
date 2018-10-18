package dev.olog.msc.dagger.base;

import androidx.fragment.app.Fragment;
import dagger.android.AndroidInjector;

public interface HasAndroidXFragmentInjector {

    /** Returns an {@link AndroidInjector} of {@link Fragment}s. */
    AndroidInjector<Fragment> supportFragmentInjector();
}

