package dev.olog.msc.dagger.base;

import android.app.Activity;
import android.util.Log;

import androidx.fragment.app.Fragment;
import dagger.android.AndroidInjector;

import static android.util.Log.DEBUG;
import static dagger.internal.Preconditions.checkNotNull;

public final class AndroidXInjection {

    private static final String TAG = "dagger.androidx";

    private AndroidXInjection(){}

    public static void inject(Fragment fragment) {
        checkNotNull(fragment, "fragment");
        HasAndroidXFragmentInjector hasSupportFragmentInjector = findHasFragmentInjector(fragment);
        if (Log.isLoggable(TAG, DEBUG)) {
            Log.d(
                    TAG,
                    String.format(
                            "An injector for %s was found in %s",
                            fragment.getClass().getCanonicalName(),
                            hasSupportFragmentInjector.getClass().getCanonicalName()));
        }

        AndroidInjector<Fragment> fragmentInjector =
                hasSupportFragmentInjector.supportFragmentInjector();
        checkNotNull(
                fragmentInjector,
                "%s.supportFragmentInjector() returned null",
                hasSupportFragmentInjector.getClass());

        fragmentInjector.inject(fragment);
    }

    private static HasAndroidXFragmentInjector findHasFragmentInjector(Fragment fragment) {
        Fragment parentFragment = fragment;
        while ((parentFragment = parentFragment.getParentFragment()) != null) {
            if (parentFragment instanceof HasAndroidXFragmentInjector) {
                return (HasAndroidXFragmentInjector) parentFragment;
            }
        }
        Activity activity = fragment.getActivity();
        if (activity instanceof HasAndroidXFragmentInjector) {
            return (HasAndroidXFragmentInjector) activity;
        }
        if (activity.getApplication() instanceof HasAndroidXFragmentInjector) {
            return (HasAndroidXFragmentInjector) activity.getApplication();
        }
        throw new IllegalArgumentException(
                String.format("No injector was found for %s", fragment.getClass().getCanonicalName()));
    }

}
