plugins {
    id(BuildPlugins.androidApplication)
    id(BuildPlugins.kotlinAndroid)
    id(BuildPlugins.kotlinKapt)
    id(BuildPlugins.kotlinAndroidExtensions)
    id(BuildPlugins.hilt)
    id(BuildPlugins.gms)
    id(BuildPlugins.fabric)
}

android {
    applyDefaults()

    defaultConfig {
        applicationId = "dev.olog.msc"

        configField("AES_PASSWORD" to localProperties.aesPassword)
        configField("LAST_FM_KEY" to localProperties.lastFmKey)
        configField("LAST_FM_SECRET" to localProperties.lastFmSecret)
    }

    bundle {
        language.enableSplit = true
        density.enableSplit = true
        abi.enableSplit = true
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
            multiDexEnabled = true
        }
    }

    flavorDimensions("scope")
    productFlavors {
        featureFlavors.forEach { (flavor, _) ->
            register(flavor) {
                dimension = "scope"
                if (flavor != "full"){
                    versionNameSuffix = ".$flavor"
                }
            }
        }
    }
    featureFlavors.forEach { (flavor, config) ->
        project.dependencies.add("${flavor}Implementation", project(config.entryModule))
    }

    packagingOptions {
        exclude("META-INF/equalizer_debug.kotlin_module")
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
    implementation(project(":features:presentation-base"))

    // libs
    implementation(project(":libraries:network"))
    implementation(project(":libraries:equalizer"))
    implementation(project(":libraries:media"))
    implementation(project(":libraries:offline-lyrics"))
    implementation(project(":libraries:image-loader"))
    implementation(project(":libraries:analytics"))
    implementation(project(":libraries:audio-tagger"))



    implementation(project(":prefs-keys"))
    implementation(project(":shared-android"))

    implementation(project(":intents"))
    implementation(project(":shared"))

    implementation(Libraries.kotlin)
    implementation(Libraries.Coroutines.core)

    implementation(Libraries.Dagger.core)
    kapt(Libraries.Dagger.kapt)
    implementation(Libraries.Dagger.hilt)
    kapt(Libraries.Dagger.hiltKapt)
    implementation(Libraries.Dagger.hiltX)
    kapt(Libraries.Dagger.hiltXKapt)

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
