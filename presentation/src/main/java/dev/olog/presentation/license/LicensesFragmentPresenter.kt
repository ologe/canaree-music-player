package dev.olog.presentation.license

import android.content.Context

// todo automatic this
class LicensesFragmentPresenter(private val context: Context) {

    private val cachedLicenses = mutableMapOf<String, String>()

    private val ANDROID_OPEN_SOURCE_PROJECT = LicenceItem(
        "The Android Open Source Project",
        "https://source.android.com",
        apache()
    )

    private val ANDROID_SUPPORT_LIBRARIES = LicenceItem(
        "Android Support Libraries",
        "https://developer.android.com/topic/libraries/support-library/index.html",
        apache()
    )

    private val KOTLIN_COROUTINES = LicenceItem(
        "kotlinx.coroutines",
        "https://github.com/Kotlin/kotlinx.coroutines",
        apache()
    )

    private val DAGGER = LicenceItem(
        "Dagger",
        "https://github.com/google/dagger",
        apache()
    )

    private val EXO_PLAYER = LicenceItem(
        "ExoPlayer",
        "https://github.com/google/ExoPlayer",
        apache()
    )

    private val HOVER = LicenceItem(
        "Hover",
        "https://github.com/google/hover",
        apache()
    )

    private val LOTTIE = LicenceItem(
        "Lottie",
        "https://github.com/airbnb/lottie-android",
        apache()
    )

    private val GLIDE = LicenceItem(
        "Glide", "https://github.com/bumptech/glide",
        glide()
    )

    private val BETTER_PICKERS = LicenceItem(
        "ScrollHmsPicker",
        "https://github.com/DeweyReed/ScrollHmsPicker",
        mit()
    )

    private val FUZZY_WUZZY = LicenceItem(
        "JavaWuzzy",
        "https://github.com/xdrop/fuzzywuzzy",
        gnu()
    )

    private val GSON = LicenceItem(
        "google-gson",
        "https://github.com/google/gson",
        apache()
    )

    private val RETROFIT = LicenceItem(
        "Retrofit",
        "https://github.com/square/retrofit",
        apache()
    )

    private val OK_HTTP = LicenceItem(
        "OkHttp",
        "https://github.com/square/okhttp",
        apache()
    )

    private val J_AUDIO_TAGGER = LicenceItem(
        "JAudiotagger",
        "http://www.jthink.net/jaudiotagger/",
        jAudioTagger()
    )

    private val TAP_TARGET_VIEW = LicenceItem(
        "TapTargetView",
        "https://github.com/KeepSafe/TapTargetView",
        apache()
    )

    private val CUSTOM_TABS = LicenceItem(
        "Android CustomTabs",
        "https://github.com/saschpe/android-customtabs",
        apache()
    )

    private val LEAK_CANARY = LicenceItem(
        "LeakCanary",
        "https://github.com/square/leakcanary",
        apache()
    )

    private val MATERIAL_DIALOGS = LicenceItem(
        "Material Dialogs",
        "https://github.com/afollestad/material-dialogs",
        apache()
    )

    private val LIBAVCODEC = LicenceItem(
        "libavcodec",
        "http://git.videolan.org/?p=ffmpeg.git",
        gnu()
    )

    private val LIBAVRESAMPLE = LicenceItem(
        "libavresample",
        "http://git.videolan.org/?p=ffmpeg.git",
        gnu()
    )

    private val LIBAVUTIL = LicenceItem(
        "libavutil",
        "http://git.videolan.org/?p=ffmpeg.git",
        gnu()
    )

    private val SCROLL_HELPER = LicenceItem(
        "Scroll Helper",
        "https://github.com/ologe/scroll-helper",
        mit()
    )

    private val BLUR_KIT = LicenceItem(
        "BlurKit",
        "https://github.com/CameraKit/blurkit-android",
        mit()
    )

    private val COLOR_DESATURATION = LicenceItem(
        "Color desaturation",
        "https://github.com/ologe/color-desaturation",
        mit()
    )

    private val CONTENT_RESOLVER_SQL = LicenceItem(
        "Content Resolver SQL",
        "https://github.com/ologe/android-content-resolver-SQL",
        mit()
    )

    private val STETHO = LicenceItem(
        "Stetho",
        "https://github.com/facebook/stetho",
        mit()
    )

    private val CROLLER = LicenceItem(
        "Croller",
        "https://github.com/harjot-oberai/Croller",
        mit()
    )

    val data: List<LicenceItem> = listOf(
        ANDROID_OPEN_SOURCE_PROJECT,
        ANDROID_SUPPORT_LIBRARIES,

        // core
        KOTLIN_COROUTINES,
        DAGGER,

        // audio
        EXO_PLAYER,
        J_AUDIO_TAGGER,
        LIBAVCODEC,
        LIBAVRESAMPLE,
        LIBAVUTIL,

        // ui
        GLIDE,
        LOTTIE,
        CUSTOM_TABS,
        MATERIAL_DIALOGS,
        SCROLL_HELPER,
        COLOR_DESATURATION,
        BLUR_KIT,
        TAP_TARGET_VIEW,
        BETTER_PICKERS,
        CROLLER,

        // data
        CONTENT_RESOLVER_SQL,

        // network
        OK_HTTP,
        RETROFIT,
        GSON,

        // utils
        FUZZY_WUZZY,
        HOVER,

        // debug
        LEAK_CANARY,
        STETHO
    )

    private fun apache(): String {
        return cachedLicenses.getOrPut("apache") {
            context.assets
                .open("licenses/apache.txt")
                .bufferedReader()
                .use { it.readText() }
        }
    }

    private fun mit(): String {
        return cachedLicenses.getOrPut("mit") {
            context.assets
                .open("licenses/mit.txt")
                .bufferedReader()
                .use { it.readText() }
        }
    }

    private fun gnu(): String {
        return cachedLicenses.getOrPut("gnu") {
            context.assets
                .open("licenses/gnu.txt")
                .bufferedReader()
                .use { it.readText() }
        }
    }

    private fun jAudioTagger(): String {
        return cachedLicenses.getOrPut("jaudiotagger") {
            context.assets
                .open("licenses/jaudiotagger.txt")
                .bufferedReader()
                .use { it.readText() }
        }
    }


    private fun glide(): String {
        return cachedLicenses.getOrPut("glide") {
            context.assets
                .open("licenses/glide.txt")
                .bufferedReader()
                .use { it.readText() }
        }
    }

}