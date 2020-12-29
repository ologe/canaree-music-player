package dev.olog.feature.about.license

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import dagger.hilt.android.qualifiers.ApplicationContext

internal class LicensesFragmentViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val cachedLicenses = mutableMapOf<String, String>()

    private val aosp = LicenseFragmentModel(
        name = "The Android Open Source Project",
        url = "https://source.android.com",
        license = apache()
    )

    private val androidSupport = LicenseFragmentModel(
        name = "Android Support Libraries",
        url = "https://developer.android.com/topic/libraries/support-library/index.html",
        license = apache()
    )

    private val coroutines = LicenseFragmentModel(
        name = "kotlinx.coroutines",
        url = "https://github.com/Kotlin/kotlinx.coroutines",
        license = apache()
    )

    private val dagger = LicenseFragmentModel(
        name = "Dagger",
        url = "https://github.com/google/dagger",
        license = apache()
    )

    private val exoPlayer = LicenseFragmentModel(
        name = "ExoPlayer",
        url = "https://github.com/google/ExoPlayer",
        license = apache()
    )

    private val hover = LicenseFragmentModel(
        name = "Hover",
        url = "https://github.com/google/hover",
        license = apache()
    )

    private val lottie = LicenseFragmentModel(
        name = "Lottie",
        url = "https://github.com/airbnb/lottie-android",
        license = apache()
    )

    private val glide = LicenseFragmentModel(
        name = "Glide",
        url = "https://github.com/bumptech/glide",
        license = glide()
    )

    private val betterPickers = LicenseFragmentModel(
        name = "ScrollHmsPicker",
        url = "https://github.com/DeweyReed/ScrollHmsPicker",
        license = mit()
    )

    private val fuzzyWuzzy = LicenseFragmentModel(
        name = "JavaWuzzy",
        url = "https://github.com/xdrop/fuzzywuzzy",
        license = gnu()
    )

    private val gson = LicenseFragmentModel(
        name = "google-gson",
        url = "https://github.com/google/gson",
        license = apache()
    )

    private val retrofit = LicenseFragmentModel(
        name = "Retrofit",
        url = "https://github.com/square/retrofit",
        license = apache()
    )

    private val okHttp = LicenseFragmentModel(
        name = "OkHttp",
        url = "https://github.com/square/okhttp",
        license = apache()
    )

    private val jAudioTagger = LicenseFragmentModel(
        name = "JAudiotagger",
        url = "http://www.jthink.net/jaudiotagger/",
        license = jAudioTagger()
    )

    private val tapTargetView = LicenseFragmentModel(
        name = "TapTargetView",
        url = "https://github.com/KeepSafe/TapTargetView",
        license = apache()
    )

    private val aesCrypto = LicenseFragmentModel(
        name = "java-aes-crypto",
        url = "https://github.com/tozny/java-aes-crypto",
        license = mit()
    )

    private val lastFmBinding = LicenseFragmentModel(
        name = "Last.fm API Bindings for Java",
        url = "https://github.com/jkovacs/lastfm-java",
        license = lastFmBinding()
    )

    // TODO still needed?
    private val customTabs = LicenseFragmentModel(
        name = "Android CustomTabs",
        url = "https://github.com/saschpe/android-customtabs",
        license = apache()
    )

    private val leakCanary = LicenseFragmentModel(
        name = "LeakCanary",
        url = "https://github.com/square/leakcanary",
        license = apache()
    )

    private val materialDialogs = LicenseFragmentModel(
        name = "Material Dialogs",
        url = "https://github.com/afollestad/material-dialogs",
        license = apache()
    )

    private val libAvCodec = LicenseFragmentModel(
        name = "libavcodec",
        url = "http://git.videolan.org/?p=ffmpeg.git",
        license = gnu()
    )

    private val libAvResample = LicenseFragmentModel(
        name = "libavresample",
        url = "http://git.videolan.org/?p=ffmpeg.git",
        license = gnu()
    )

    private val libAvUtil = LicenseFragmentModel(
        name = "libavutil",
        url = "http://git.videolan.org/?p=ffmpeg.git",
        license = gnu()
    )

    private val scrollHelper = LicenseFragmentModel(
        name = "Scroll Helper",
        url = "https://github.com/ologe/scroll-helper",
        license = mit()
    )

    private val blurKit = LicenseFragmentModel(
        name = "BlurKit",
        url = "https://github.com/CameraKit/blurkit-android",
        license = mit()
    )

    private val colorDesaturation = LicenseFragmentModel(
        name = "Color desaturation",
        url = "https://github.com/ologe/color-desaturation",
        license = mit()
    )

    private val contentResolverSql = LicenseFragmentModel(
        name = "Content Resolver SQL",
        url = "https://github.com/ologe/android-content-resolver-SQL",
        license = mit()
    )

    private val croller = LicenseFragmentModel(
        name = "Croller",
        url = "https://github.com/harjot-oberai/Croller",
        license = mit()
    )

    val data: List<LicenseFragmentModel> = listOf(
        aosp,
        androidSupport,

        // core
        coroutines,
        dagger,

        // audio
        exoPlayer,
        jAudioTagger,
        libAvCodec,
        libAvResample,
        libAvUtil,

        // ui
        glide,
        lottie,
        customTabs,
        materialDialogs,
        scrollHelper,
        colorDesaturation,
        blurKit,
        tapTargetView,
        betterPickers,
        croller,

        // data
        contentResolverSql,

        // network
        okHttp,
        retrofit,
        gson,

        // utils
        lastFmBinding,
        aesCrypto,
        fuzzyWuzzy,
        hover,

        // debug
        leakCanary,
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