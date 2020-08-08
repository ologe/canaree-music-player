package dev.olog.feature.about.license

import android.content.Context
import dev.olog.feature.about.R
import dev.olog.feature.about.model.LicenseModel
import dev.olog.feature.presentation.base.model.PresentationId.Companion.headerId

internal class LicensesFragmentPresenter(private val context: Context) {

    private val cachedLicenses = mutableMapOf<String, String>()

    private val ANDROID_OPEN_SOURCE_PROJECT = LicenseModel(
        R.layout.item_license,
        headerId("android"),
        "The Android Open Source Project", "https://source.android.com",
        apache()
    )

    private val ANDROID_SUPPORT_LIBRARIES = LicenseModel(
        R.layout.item_license,
        headerId("android support"),
        "Android Support Libraries",
        "https://developer.android.com/topic/libraries/support-library/index.html",
        apache()
    )

    private val KOTLIN_COROUTINES = LicenseModel(
        R.layout.item_license,
        headerId("coroutines"),
        "kotlinx.coroutines", "https://github.com/Kotlin/kotlinx.coroutines",
        apache()
    )

    private val DAGGER = LicenseModel(
        R.layout.item_license,
        headerId("dagger"),
        "Dagger", "https://github.com/google/dagger",
        apache()
    )

    private val EXO_PLAYER = LicenseModel(
        R.layout.item_license,
        headerId("exo player"),
        "ExoPlayer", "https://github.com/google/ExoPlayer",
        apache()
    )

    private val HOVER = LicenseModel(
        R.layout.item_license,
        headerId("hover"),
        "Hover", "https://github.com/google/hover",
        apache()
    )

    private val LOTTIE = LicenseModel(
        R.layout.item_license,
        headerId("lottie"),
        "Lottie", "https://github.com/airbnb/lottie-android",
        apache()
    )

    private val GLIDE = LicenseModel(
        R.layout.item_license,
        headerId("glide"),
        "Glide", "https://github.com/bumptech/glide",
        glide()
    )

    private val BETTER_PICKERS = LicenseModel(
        R.layout.item_license,
        headerId("ScrollHmsPicker"),
        "ScrollHmsPicker", "https://github.com/DeweyReed/ScrollHmsPicker",
        mit()
    )

    private val FUZZY_WUZZY = LicenseModel(
        R.layout.item_license,
        headerId("fuzzywuzzy"),
        "JavaWuzzy", "https://github.com/xdrop/fuzzywuzzy",
        gnu()
    )

    private val GSON = LicenseModel(
        R.layout.item_license,
        headerId("gson"),
        "google-gson", "https://github.com/google/gson",
        apache()
    )

    private val RETROFIT = LicenseModel(
        R.layout.item_license,
        headerId("retrofit"),
        "Retrofit", "https://github.com/square/retrofit",
        apache()
    )

    private val OK_HTTP = LicenseModel(
        R.layout.item_license,
        headerId("OkHttp"),
        "OkHttp", "https://github.com/square/okhttp",
        apache()
    )

    private val J_AUDIO_TAGGER = LicenseModel(
        R.layout.item_license,
        headerId("jaudiotagger"),
        "JAudiotagger", "http://www.jthink.net/jaudiotagger/",
        jAudioTagger()
    )

    private val TAP_TARGET_VIEW = LicenseModel(
        R.layout.item_license,
        headerId("TapTargetView"),
        "TapTargetView", "https://github.com/KeepSafe/TapTargetView",
        apache()
    )

    private val AES_CRYPTO = LicenseModel(
        R.layout.item_license,
        headerId("java-aes-crypto"),
        "java-aes-crypto", "https://github.com/tozny/java-aes-crypto",
        mit()
    )

    private val LAST_FM_BINDING = LicenseModel(
        R.layout.item_license,
        headerId("last_fm_binding"),
        "Last.fm API Bindings for Java", "https://github.com/jkovacs/lastfm-java",
        lastFmBinding()
    )

    private val CUSTOM_TABS = LicenseModel(
        R.layout.item_license,
        headerId("custom tabs"),
        "Android CustomTabs", "https://github.com/saschpe/android-customtabs",
        apache()
    )

    private val LEAK_CANARY = LicenseModel(
        R.layout.item_license,
        headerId("leakCanary"),
        "LeakCanary", "https://github.com/square/leakcanary",
        apache()
    )

    private val MATERIAL_DIALOGS = LicenseModel(
        R.layout.item_license,
        headerId("Material Dialogs"),
        "Material Dialogs", "https://github.com/afollestad/material-dialogs",
        apache()
    )

    private val LIBAVCODEC = LicenseModel(
        R.layout.item_license,
        headerId("libavcodec"),
        "libavcodec", "http://git.videolan.org/?p=ffmpeg.git",
        gnu()
    )

    private val LIBAVRESAMPLE = LicenseModel(
        R.layout.item_license,
        headerId("libavresample"),
        "libavresample", "http://git.videolan.org/?p=ffmpeg.git",
        gnu()
    )

    private val LIBAVUTIL = LicenseModel(
        R.layout.item_license,
        headerId("libavutil"),
        "libavutil", "http://git.videolan.org/?p=ffmpeg.git",
        gnu()
    )

    private val SCROLL_HELPER = LicenseModel(
        R.layout.item_license,
        headerId("scroll helper"),
        "Scroll Helper", "https://github.com/ologe/scroll-helper",
        mit()
    )

    private val BLUR_KIT = LicenseModel(
        R.layout.item_license,
        headerId("blurkit"),
        "BlurKit", "https://github.com/CameraKit/blurkit-android",
        mit()
    )

    private val COLOR_DESATURATION = LicenseModel(
        R.layout.item_license,
        headerId("color desaturation"),
        "Color desaturation", "https://github.com/ologe/color-desaturation",
        mit()
    )

    private val CONTENT_RESOLVER_SQL = LicenseModel(
        R.layout.item_license,
        headerId("content resolver sql"),
        "Content Resolver SQL", "https://github.com/ologe/android-content-resolver-SQL",
        mit()
    )

    private val CROLLER = LicenseModel(
        R.layout.item_license,
        headerId("croller"),
        "Croller", "https://github.com/harjot-oberai/Croller",
        mit()
    )

    val data: List<LicenseModel> = listOf(
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
        LEAK_CANARY
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

    private fun lastFmBinding(): String {
        return cachedLicenses.getOrPut("lastfm") {
            context.assets
                .open("licenses/lastfm.txt")
                .bufferedReader()
                .use { it.readText() }
        }
    }

}