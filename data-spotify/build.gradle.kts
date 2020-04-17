plugins {
    id(BuildPlugins.androidLibrary)
    id(BuildPlugins.kotlinAndroid)
    id(BuildPlugins.kotlinKapt)
}

android {
    applyDefaults()

    defaultConfig {
        val properties = localProperties
        configField("SPOTIFY_ENCODED_CLIENT" to properties.spotifyEncodedClient)
    }

}

dependencies {
    lintChecks(project(":lint"))

    implementation(project(":domain"))
    implementation(project(":libraries:lib-network"))
    implementation(project(":shared"))
    implementation(project(":shared-android"))

    implementation(Libraries.kotlin)
    implementation(Libraries.Coroutines.core)

    implementation(Libraries.Dagger.core)
    kapt(Libraries.Dagger.kapt)
    implementation(Libraries.Dagger.assisted)
    kapt(Libraries.Dagger.assistedKapt)

    implementation(Libraries.X.Room.core)
    implementation(Libraries.X.Room.coroutines)
    kapt(Libraries.X.Room.kapt)

    implementation(Libraries.X.Lifecycle.livedata)

    implementation(Libraries.X.WorkManager.core)

    implementation(Libraries.Network.retrofit)
    implementation(Libraries.Utils.fuzzy)

    implementation(Libraries.Debug.timber)

    testImplementation(Libraries.Test.junit)
    testImplementation(Libraries.Test.mockito)
    testImplementation(Libraries.Test.mockitoKotlin)
    testImplementation(Libraries.Test.android)
    testImplementation(Libraries.Test.robolectric)
    testImplementation(Libraries.Coroutines.test)
}
