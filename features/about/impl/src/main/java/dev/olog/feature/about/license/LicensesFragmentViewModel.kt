package dev.olog.feature.about.license

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class LicensesFragmentViewModel @Inject constructor(
    @ApplicationContext val context: Context
) : ViewModel() {

    private val cachedLicenses = mutableMapOf<String, String>()

    private val ANDROID_OPEN_SOURCE_PROJECT = LicenseItem(
        name = "The Android Open Source Project",
        url = "https://source.android.com",
        license = apache()
    )

    private val ANDROID_SUPPORT_LIBRARIES = LicenseItem(
        name = "Android Support Libraries",
        url = "https://developer.android.com/topic/libraries/support-library/index.html",
        license = apache()
    )

    private val KOTLIN_COROUTINES = LicenseItem(
        name = "kotlinx.coroutines",
        url = "https://github.com/Kotlin/kotlinx.coroutines",
        license = apache()
    )

    private val DAGGER = LicenseItem(
        name = "Dagger",
        url = "https://github.com/google/dagger",
        license = apache()
    )

    private val EXO_PLAYER = LicenseItem(
        name = "ExoPlayer",
        url = "https://github.com/google/ExoPlayer",
        license = apache()
    )

    private val HOVER = LicenseItem(
        name = "Hover",
        url = "https://github.com/google/hover",
        license = apache()
    )

    private val LOTTIE = LicenseItem(
        name = "Lottie",
        url = "https://github.com/airbnb/lottie-android",
        license = apache()
    )

    private val GLIDE = LicenseItem(
        name = "Glide",
        url = "https://github.com/bumptech/glide",
        license = glide()
    )

    private val BETTER_PICKERS = LicenseItem(
        name = "ScrollHmsPicker",
        url = "https://github.com/DeweyReed/ScrollHmsPicker",
        license = mit()
    )

    private val FUZZY_WUZZY = LicenseItem(
        name = "JavaWuzzy",
        url = "https://github.com/xdrop/fuzzywuzzy",
        license = gnu()
    )

    private val GSON = LicenseItem(
        name = "google-gson",
        url = "https://github.com/google/gson",
        license = apache()
    )

    private val RETROFIT = LicenseItem(
        name = "Retrofit",
        url = "https://github.com/square/retrofit",
        license = apache()
    )

    private val OK_HTTP = LicenseItem(
        name = "OkHttp",
        url = "https://github.com/square/okhttp",
        license = apache()
    )

    private val J_AUDIO_TAGGER = LicenseItem(
        name = "JAudiotagger",
        url = "http://www.jthink.net/jaudiotagger/",
        license = jAudioTagger()
    )

    private val TAP_TARGET_VIEW = LicenseItem(
        name = "TapTargetView",
        url = "https://github.com/KeepSafe/TapTargetView",
        license = apache()
    )

    private val AES_CRYPTO = LicenseItem(
        name = "java-aes-crypto",
        url = "https://github.com/tozny/java-aes-crypto",
        license = mit()
    )

    private val LAST_FM_BINDING = LicenseItem(
        name = "Last.fm API Bindings for Java",
        url = "https://github.com/jkovacs/lastfm-java",
        license = lastFmBinding()
    )

    private val CUSTOM_TABS = LicenseItem(
        name = "Android CustomTabs",
        url = "https://github.com/saschpe/android-customtabs",
        license = apache()
    )

    private val LEAK_CANARY = LicenseItem(
        name = "LeakCanary",
        url = "https://github.com/square/leakcanary",
        license = apache()
    )

    private val MATERIAL_DIALOGS = LicenseItem(
        name = "Material Dialogs",
        url = "https://github.com/afollestad/material-dialogs",
        license = apache()
    )

    private val LIBAVCODEC = LicenseItem(
        name = "libavcodec",
        url = "http://git.videolan.org/?p=ffmpeg.git",
        license = gnu()
    )

    private val LIBAVRESAMPLE = LicenseItem(
        name = "libavresample",
        url = "http://git.videolan.org/?p=ffmpeg.git",
        license = gnu()
    )

    private val LIBAVUTIL = LicenseItem(
        name = "libavutil",
        url = "http://git.videolan.org/?p=ffmpeg.git",
        license = gnu()
    )

    private val SCROLL_HELPER = LicenseItem(
        name = "Scroll Helper",
        url = "https://github.com/ologe/scroll-helper",
        license = mit()
    )

    private val BLUR_KIT = LicenseItem(
        name = "BlurKit",
        url = "https://github.com/CameraKit/blurkit-android",
        license = mit()
    )

    private val COLOR_DESATURATION = LicenseItem(
        name = "Color desaturation",
        url = "https://github.com/ologe/color-desaturation",
        license = mit()
    )

    private val CONTENT_RESOLVER_SQL = LicenseItem(
        name = "Content Resolver SQL",
        url = "https://github.com/ologe/android-content-resolver-SQL",
        license = mit()
    )

    private val STETHO = LicenseItem(
        name = "Stetho",
        url = "https://github.com/facebook/stetho",
        license = mit()
    )

    private val CROLLER = LicenseItem(
        name = "Croller",
        url = "https://github.com/harjot-oberai/Croller",
        license = mit()
    )

    val data: List<LicenseItem> = listOf(
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
        LAST_FM_BINDING,
        AES_CRYPTO,
        FUZZY_WUZZY,
        HOVER,

        // debug
        LEAK_CANARY,
        STETHO
    )

    private fun apache(): String {
        return cachedLicenses.getOrPut("apache") {
            context.assets
                .open("apache.txt")
                .bufferedReader()
                .use { it.readText() }
        }
    }

    private fun mit(): String {
        return cachedLicenses.getOrPut("mit") {
            context.assets
                .open("mit.txt")
                .bufferedReader()
                .use { it.readText() }
        }
    }

    private fun gnu(): String {
        return cachedLicenses.getOrPut("gnu") {
            context.assets
                .open("gnu.txt")
                .bufferedReader()
                .use { it.readText() }
        }
    }

    private fun jAudioTagger(): String {
        return cachedLicenses.getOrPut("jaudiotagger") {
            context.assets
                .open("jaudiotagger.txt")
                .bufferedReader()
                .use { it.readText() }
        }
    }


    private fun glide(): String {
        return cachedLicenses.getOrPut("glide") {
            context.assets
                .open("glide.txt")
                .bufferedReader()
                .use { it.readText() }
        }
    }

    private fun lastFmBinding(): String {
        return cachedLicenses.getOrPut("lastfm") {
            context.assets
                .open("lastfm.txt")
                .bufferedReader()
                .use { it.readText() }
        }
    }

}