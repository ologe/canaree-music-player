package dev.olog.service.music.helper

object WearHelper {
    private const val WEAR_APP_PACKAGE_NAME = "com.google.android.wearable.app"

    fun isValidWearCompanionPackage(packageName: String): Boolean {
        return WEAR_APP_PACKAGE_NAME == packageName
    }
}
