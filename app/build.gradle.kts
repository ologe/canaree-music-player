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


    implementation(project(":app-shortcuts"))
    implementation(project(":analytics"))
    implementation(project(":domain"))
    implementation(project(":data"))
    implementation(project(":image-provider"))
    implementation(project(":injection"))
    implementation(project(":media"))
    implementation(project(":prefs-keys"))
    implementation(project(":presentation"))
    implementation(project(":shared-android"))
    implementation(project(":shared-widgets"))
    implementation(project(":service-music"))
    implementation(project(":service-floating"))
    implementation(project(":offline-lyrics"))
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

    implementation(Libraries.UX.blurKit)
    implementation(Libraries.UX.customTabs)

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
