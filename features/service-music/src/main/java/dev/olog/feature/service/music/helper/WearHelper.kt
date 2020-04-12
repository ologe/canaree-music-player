package dev.olog.feature.service.music.helper

internal object WearHelper {
    private const val WEAR_APP_PACKAGE_NAME = "com.google.android.wearable.app"

    @JvmStatic
    fun isValidWearCompanionPackage(packageName: String): Boolean {
        return WEAR_APP_PACKAGE_NAME == packageName
    }
}
