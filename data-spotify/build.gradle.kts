import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id(buildPlugins.androidLibrary)
    id(buildPlugins.kotlinAndroid)
    id(buildPlugins.kotlinKapt)
    id(buildPlugins.hilt)
}

apply(from = rootProject.file("buildscripts/configure-android-defaults.gradle"))

android {

    defaultConfig {
        val localProperties = gradleLocalProperties(rootDir)
        buildConfigField("String", "SPOTIFY_ENCODED_CLIENT", localProperties.getProperty("spotify_encoded_client"))
    }

}

dependencies {
    lintChecks(project(":lint"))

    implementation(project(":domain"))
    implementation(project(":libraries:network"))
    implementation(project(":shared"))
    implementation(project(":shared-android"))

    implementation(libs.kotlin)
    implementation(libs.Coroutines.core)

    implementation(libs.dagger.core)
    kapt(libs.dagger.kapt)
    implementation(libs.dagger.hilt)
    kapt(libs.dagger.hiltKapt)

    implementation(libs.X.Room.core)
    implementation(libs.X.Room.coroutines)
    kapt(libs.X.Room.kapt)

    implementation(libs.Network.retrofit)
    implementation(libs.Utils.fuzzy)

    implementation(libs.Debug.timber)

    testImplementation(libs.Test.junit)
    testImplementation(libs.Test.mockito)
    testImplementation(libs.Test.mockitoKotlin)
    testImplementation(libs.Test.android)
    testImplementation(libs.Test.robolectric)
    testImplementation(libs.Coroutines.test)
}
