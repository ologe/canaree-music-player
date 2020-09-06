plugins {
    id(buildPlugins.androidApplication)
    id(buildPlugins.kotlinAndroid)
    id(buildPlugins.kotlinKapt)
    id(buildPlugins.kotlinAndroidExtensions)
    id(buildPlugins.hilt)
    id(buildPlugins.gms)
    id(buildPlugins.crashlytics)
}

android {
    applyDefaults(compose = true)

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

    dagger()
    compose()
    coroutines()

    implementation(libs.X.core)
    implementation(libs.X.appcompat)
    implementation(libs.X.browser)
    implementation(libs.X.preference)
    implementation(libs.X.media)
    implementation(libs.X.material)
    implementation(libs.X.Lifecycle.service)
    implementation(libs.X.Lifecycle.java8)

    implementation(libs.UX.blurKit)
    implementation(libs.UX.customTabs)
    implementation(libs.UX.glide)
    implementation(libs.UX.dialogs)

    implementation(libs.X.Room.core)
    implementation(libs.Network.okHttp)
    implementation(libs.Network.retrofit)

    implementation(libs.Utils.aesCrypto)

    debugImplementation(libs.Debug.leakCanary)
    implementation(libs.Debug.timber)

    implementation(libs.Firebase.analytics)
    implementation(libs.Firebase.crashlytics)

    testImplementation(libs.Test.junit)
    testImplementation(libs.Test.mockito)
    testImplementation(libs.Test.mockitoKotlin)
    testImplementation(libs.Test.android)
    testImplementation(libs.Test.robolectric)
}
