package dev.olog.msc.presentation.licenses

import android.content.Context
import dev.olog.msc.R
import dev.olog.msc.dagger.qualifier.ApplicationContext
import dev.olog.msc.utils.MediaId
import javax.inject.Inject

@Suppress("PrivatePropertyName")
class LicensesFragmentPresenter @Inject constructor(
        @ApplicationContext private val context: Context

) {

    private val ANDROID_OPEN_SOURCE_PROJECT = LicenseModel(
            R.layout.item_license,
            MediaId.headerId("android"),
            "The Android Open Source Project", "https://source.android.com",
            apache())

    private val ANDROID_SUPPORT_LIBRARIES = LicenseModel(
            R.layout.item_license,
            MediaId.headerId("android support"),
            "Android Support Libraries", "https://developer.android.com/topic/libraries/support-library/index.html",
            apache())

    private val DAGGER = LicenseModel(
            R.layout.item_license,
            MediaId.headerId("dagger"),
            "Dagger", "https://github.com/google/dagger",
            apache())

    private val RX_JAVA = LicenseModel(
            R.layout.item_license,
            MediaId.headerId("rxJava"),
            "RxJava", "https://github.com/ReactiveX/RxJava",
            apache())

    private val RX_ANDROID = LicenseModel(
            R.layout.item_license,
            MediaId.headerId("rxAndroid"),
            "RxAndroid", "https://github.com/ReactiveX/RxAndroid",
            apache())

    private val RX_BINDING = LicenseModel(
            R.layout.item_license,
            MediaId.headerId("rxBinding"),
            "RxBinding", "https://github.com/JakeWharton/RxBinding",
            apache())

    private val RX_PREFERENCES = LicenseModel(
            R.layout.item_license,
            MediaId.headerId("rxPreferences"),
            "Rx Preferences", "https://github.com/f2prateek/rx-preferences",
            apache())

    private val RX_NETWORK = LicenseModel(
            R.layout.item_license,
            MediaId.headerId("rxNetwork"),
            "ReactiveNetwork", "https://github.com/pwittchen/ReactiveNetwork",
            apache())

    private val SQL_BRITE = LicenseModel(
            R.layout.item_license,
            MediaId.headerId("sqlbrite"),
            "sqlbrite", "https://github.com/square/sqlbrite",
            apache())

    private val SLIDING_PANEL = LicenseModel(
            R.layout.item_license,
            MediaId.headerId("sliding panel"),
            "Android Sliding Up Panel", "https://github.com/umano/AndroidSlidingUpPanel",
            apache())

    private val EXO_PLAYER = LicenseModel(
            R.layout.item_license,
            MediaId.headerId("exo player"),
            "ExoPlayer", "https://github.com/google/ExoPlayer",
            apache())

    private val HOVER = LicenseModel(
            R.layout.item_license,
            MediaId.headerId("hover"),
            "Hover", "https://github.com/google/hover",
            apache())

    private val LOTTIE = LicenseModel(
            R.layout.item_license,
            MediaId.headerId("lottie"),
            "Lottie", "https://github.com/airbnb/lottie-android",
            apache())

    private val GLIDE = LicenseModel(
            R.layout.item_license,
            MediaId.headerId("glide"),
            "Glide", "https://github.com/bumptech/glide",
            glide())

    private val BETTER_PICKERS = LicenseModel(
            R.layout.item_license,
            MediaId.headerId("ScrollHmsPicker"),
            "ScrollHmsPicker", "https://github.com/DeweyReed/ScrollHmsPicker",
            mit())

    private val ANDROID_KTX = LicenseModel(
            R.layout.item_license,
            MediaId.headerId("android ktx"),
            "Android KTX", "https://github.com/android/android-ktx",
            apache())

    private val FUZZY_WUZZY = LicenseModel(
            R.layout.item_license,
            MediaId.headerId("fuzzywuzzy"),
            "JavaWuzzy", "https://github.com/xdrop/fuzzywuzzy",
            gnu())

    private val GSON = LicenseModel(
            R.layout.item_license,
            MediaId.headerId("gson"),
            "google-gson", "https://github.com/google/gson",
            apache())

    private val RETROFIT = LicenseModel(
            R.layout.item_license,
            MediaId.headerId("retrofit"),
            "Retrofit", "https://github.com/square/retrofit",
            apache())

    private val OK_HTTP = LicenseModel(
            R.layout.item_license,
            MediaId.headerId("OkHttp"),
            "OkHttp", "https://github.com/square/okhttp",
            apache())

    private val J_AUDIO_TAGGER = LicenseModel(
            R.layout.item_license,
            MediaId.headerId("jaudiotagger"),
            "JAudiotagger", "http://www.jthink.net/jaudiotagger/",
            jAudioTagger()
    )

    private val OPTIONAL = LicenseModel(
            R.layout.item_license,
            MediaId.headerId("optional"),
            "support-optional", "https://github.com/dmstocking/support-optional",
            apache()
    )

    val data : List<LicenseModel> = listOf(
            ANDROID_OPEN_SOURCE_PROJECT,
            ANDROID_SUPPORT_LIBRARIES,
            DAGGER,
            RX_JAVA,
            RX_ANDROID,
            RX_PREFERENCES,
            RX_BINDING,
            RX_NETWORK,
            SQL_BRITE,
            SLIDING_PANEL,
            EXO_PLAYER,
            HOVER,
            LOTTIE,
            GLIDE,
            BETTER_PICKERS,
            ANDROID_KTX,
            FUZZY_WUZZY,
            GSON,
            RETROFIT,
            OK_HTTP,
            J_AUDIO_TAGGER,
            OPTIONAL
    )

    private fun apache(): String {
        return context.assets
                .open("licenses/apache.txt")
                .bufferedReader()
                .use { it.readText() }
//                .replace("{year}", year)
    }

    private fun mit(): String {
        return context.assets
                .open("licenses/mit.txt")
                .bufferedReader()
                .use { it.readText() }
//                .replace("{year}", year)
    }

    private fun gnu(): String {
        return context.assets
                .open("licenses/gnu.txt")
                .bufferedReader()
                .use { it.readText() }
//                .replace("{year}", year)
    }

    private fun jAudioTagger(): String {
        return context.assets
                .open("licenses/glide.txt")
                .bufferedReader()
                .use { it.readText() }
    }


    private fun glide(): String{
        return context.assets
                .open("licenses/glide.txt")
                .bufferedReader()
                .use { it.readText() }
    }
}