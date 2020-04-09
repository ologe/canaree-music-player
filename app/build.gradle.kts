plugins {
    id(BuildPlugins.androidApplication)
    id(BuildPlugins.kotlinAndroid)
    id(BuildPlugins.kotlinKapt)
    id(BuildPlugins.kotlinAndroidExtensions)
    id(BuildPlugins.fabric)
}

android {
    applyDefaults()

    defaultConfig {
        applicationId = "dev.olog.msc"

        configField("AES_PASSWORD" to localProperties.aesPassword)
    }

    bundle {
        language.enableSplit = true
        density.enableSplit = true
        abi.enableSplit = true
    }

    buildTypes {
        val properties = localProperties

        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            configField("LAST_FM_KEY" to properties.lastFmKey)
            configField("LAST_FM_SECRET" to properties.lastFmSecret)
        }
        debug {
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
            multiDexEnabled = true

            configField("LAST_FM_KEY" to properties.lastFmKey)
            configField("LAST_FM_SECRET" to properties.lastFmSecret)
        }
    }


    lintOptions {
        isCheckReleaseBuilds = false
        disable("MissingTranslation")
    }

    packagingOptions {
        excludes = setOf(
            "META-INF/core.kotlin_module",
            "META-INF/MANIFEST.MF",
            "META-INF/proguard/coroutines.pro"
        )
    }

}

dependencies {
    lintChecks(project(":lint"))

    implementation(project(":core"))
    implementation(project(":domain"))

    implementation(project(":data"))
    implementation(project(":data-spotify"))

    // feature
    implementation(project(":feature-presentation-base")) // TODO is needed?

    // libs
    implementation(project(":lib.network")) // TODO rename to lib.network
    implementation(project(":lib.equalizer"))
    implementation(project(":lib.media"))
    implementation(project(":lib.offline-lyrics"))
    implementation(project(":lib.image-loader"))

    implementation(project(":analytics"))

    implementation(project(":feature-app-shortcuts"))
    implementation(project(":analytics"))


    implementation(project(":prefs-keys"))
    implementation(project(":presentation"))
    implementation(project(":shared-android"))
    implementation(project(":service-music"))
    implementation(project(":service-floating"))
    implementation(project(":intents"))
    implementation(project(":shared"))

    implementation(Libraries.kotlin)
    implementation(Libraries.Coroutines.core)

    implementation(Libraries.Dagger.core)
    kapt(Libraries.Dagger.kapt)
    implementation(Libraries.Dagger.android)
    implementation(Libraries.Dagger.androidSupport)
    kapt(Libraries.Dagger.androidKapt)

    implementation(Libraries.X.core)
    implementation(Libraries.X.appcompat)
    implementation(Libraries.X.browser)
    implementation(Libraries.X.preference)
    implementation(Libraries.X.media)
    implementation(Libraries.X.material)
    implementation(Libraries.X.Lifecycle.service)
    implementation(Libraries.X.Lifecycle.java8)

    implementation(Libraries.UX.blurKit)
    implementation(Libraries.UX.customTabs)
    implementation(Libraries.UX.glide)
    implementation(Libraries.UX.dialogs)

    implementation(Libraries.X.Room.core)
    implementation(Libraries.Network.okHttp)
    implementation(Libraries.Network.retrofit)

    implementation(Libraries.Utils.aesCrypto)

    debugImplementation(Libraries.Debug.leakCanary)
    debugImplementation(Libraries.Debug.stetho)
    implementation(Libraries.Debug.timber)

    implementation(Libraries.Firebase.analytics)
    implementation(Libraries.Firebase.crashlytics)

    testImplementation(Libraries.Test.junit)
    testImplementation(Libraries.Test.mockito)
    testImplementation(Libraries.Test.mockitoKotlin)
    testImplementation(Libraries.Test.android)
    testImplementation(Libraries.Test.robolectric)
    testImplementation(Libraries.Coroutines.test)
}

// leave at bottom
apply(plugin = BuildPlugins.gms)
