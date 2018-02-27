package dev.olog.msc.music.service.helper;

import android.os.Bundle;
import android.support.wearable.media.MediaControlConstants;

public class WearHelper {
    private static final String WEAR_APP_PACKAGE_NAME = "com.google.android.wearable.app";

    public static boolean isValidWearCompanionPackage(String packageName) {
        return WEAR_APP_PACKAGE_NAME.equals(packageName);
    }

    public static void setShowCustomActionOnWear(Bundle customActionExtras, boolean showOnWear) {
        if (showOnWear) {
            customActionExtras.putBoolean(
                    MediaControlConstants.EXTRA_CUSTOM_ACTION_SHOW_ON_WEAR, true);
        } else {
            customActionExtras.remove(MediaControlConstants.EXTRA_CUSTOM_ACTION_SHOW_ON_WEAR);
        }
    }

    public static void setUseBackgroundFromTheme(Bundle extras, boolean useBgFromTheme) {
        if (useBgFromTheme) {
            extras.putBoolean(MediaControlConstants.EXTRA_BACKGROUND_COLOR_FROM_THEME, true);
        } else {
            extras.remove(MediaControlConstants.EXTRA_BACKGROUND_COLOR_FROM_THEME);
        }
    }

    public static void setSlotReservationFlags(Bundle extras, boolean reserveSkipToNextSlot,
                                               boolean reserveSkipToPrevSlot) {
        if (reserveSkipToPrevSlot) {
            extras.putBoolean(MediaControlConstants.EXTRA_RESERVE_SLOT_SKIP_TO_PREVIOUS, true);
        } else {
            extras.remove(MediaControlConstants.EXTRA_RESERVE_SLOT_SKIP_TO_PREVIOUS);
        }
        if (reserveSkipToNextSlot) {
            extras.putBoolean(MediaControlConstants.EXTRA_RESERVE_SLOT_SKIP_TO_NEXT, true);
        } else {
            extras.remove(MediaControlConstants.EXTRA_RESERVE_SLOT_SKIP_TO_NEXT);
        }
    }
}
