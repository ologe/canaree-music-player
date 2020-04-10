plugins {
    id(BuildPlugins.androidApplication)
    id(BuildPlugins.kotlinAndroid)
    id(BuildPlugins.kotlinKapt)
    id(BuildPlugins.kotlinAndroidExtensions)
    id(BuildPlugins.fabric)
    id("dynamic-flavor")
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

}

dependencies {
    lintChecks(project(":lint"))

    implementation(project(":core"))
    implementation(project(":domain"))

    implementation(project(":data"))
    implementation(project(":data-spotify"))

    // feature
    implementation(project(":navigation"))

    implementation(project(":feature-presentation-base")) // TODO is needed?
    api(project(":feature-app-shortcuts"))
    api(project(":feature-library"))
    api(project(":feature-search"))
    api(project(":feature-detail"))
    api(project(":feature-player"))
    api(project(":feature-player-mini"))
    api(project(":feature-queue"))
    api(project(":feature-settings"))
    api(project(":feature-about"))
    api(project(":feature-onboarding"))
    api(project(":feature-equalizer"))
    api(project(":feature-edit"))

    api(project(":feature-service-music"))
    api(project(":feature-service-floating"))
    api(project(":presentation"))

    // libs
    implementation(project(":lib.network"))
    implementation(project(":lib.equalizer"))
    implementation(project(":lib.media"))
    implementation(project(":lib.offline-lyrics"))
    implementation(project(":lib.image-loader"))
    implementation(project(":lib.analytics"))
    implementation(project(":lib-audio-tagger"))



    implementation(project(":prefs-keys"))
    implementation(project(":shared-android"))

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
