package dev.olog.msc.music.service.helper;

import android.os.Bundle;

import androidx.annotation.NonNull;

public class CarHelper {

    public static final String AUTO_APP_PACKAGE_NAME = "com.google.android.projection.gearhead";

    // Use these extras to reserve space for the corresponding actions, even when they are disabled
    // in the playbackstate, so the custom actions don't reflow.
    private static final String SLOT_RESERVATION_SKIP_TO_NEXT =
            "com.google.android.gms.car.media.ALWAYS_RESERVE_SPACE_FOR.ACTION_SKIP_TO_NEXT";
    private static final String SLOT_RESERVATION_SKIP_TO_PREV =
            "com.google.android.gms.car.media.ALWAYS_RESERVE_SPACE_FOR.ACTION_SKIP_TO_PREVIOUS";
    private static final String SLOT_RESERVATION_QUEUE =
            "com.google.android.gms.car.media.ALWAYS_RESERVE_SPACE_FOR.ACTION_QUEUE";

    /**
     * Action for an intent broadcast by Android Auto when a media app is connected or
     * disconnected. A "connected" media app is the one currently attached to the "media" facet
     * on Android Auto. So, this intent is sent by AA on:
     *
     * - connection: when the phone is projecting and at the moment the app is selected from the
     *       list of media apps
     * - disconnection: when another media app is selected from the list of media apps or when
     *       the phone stops projecting (when the user unplugs it, for example)
     *
     * The actual event (connected or disconnected) will come as an Intent extra,
     * with the key MEDIA_CONNECTION_STATUS (see below).
     */
    public static final String ACTION_MEDIA_STATUS = "com.google.android.gms.car.media.STATUS";

    /**
     * Key in Intent extras that contains the media connection event type (connected or disconnected)
     */
    public static final String MEDIA_CONNECTION_STATUS = "media_connection_status";

    /**
     * Value of the key MEDIA_CONNECTION_STATUS in Intent extras used when the current media app
     * is connected.
     */
    public static final String MEDIA_CONNECTED = "media_connected";


    public static boolean isValidCarPackage(String packageName) {
        return AUTO_APP_PACKAGE_NAME.equals(packageName);
    }

    public static void setSlotReservationFlags(@NonNull Bundle extras, boolean reservePlayingQueueSlot,
                                               boolean reserveSkipToNextSlot, boolean reserveSkipToPrevSlot) {
        if (reservePlayingQueueSlot) {
            extras.putBoolean(SLOT_RESERVATION_QUEUE, true);
        } else {
            extras.remove(SLOT_RESERVATION_QUEUE);
        }
        if (reserveSkipToPrevSlot) {
            extras.putBoolean(SLOT_RESERVATION_SKIP_TO_PREV, true);
        } else {
            extras.remove(SLOT_RESERVATION_SKIP_TO_PREV);
        }
        if (reserveSkipToNextSlot) {
            extras.putBoolean(SLOT_RESERVATION_SKIP_TO_NEXT, true);
        } else {
            extras.remove(SLOT_RESERVATION_SKIP_TO_NEXT);
        }
    }

}