import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id(buildPlugins.androidLibrary)
    id(buildPlugins.kotlinAndroid)
    id(buildPlugins.kotlinKapt)
    id(buildPlugins.kotlinAndroidExtensions)
    id(buildPlugins.hilt)
}

apply(from = rootProject.file("buildscripts/configure-android-defaults.gradle"))

android {

    defaultConfig {
        val localProperties = gradleLocalProperties(rootDir)
        buildConfigField("String", "LAST_FM_KEY", localProperties.getProperty("last_fm_key"))
        buildConfigField("String", "LAST_FM_SECRET", localProperties.getProperty("last_fm_secret"))
    }

}

dependencies {
    lintChecks(project(":lint"))

    implementation(project(":core"))
    implementation(project(":domain"))

    implementation(project(":libraries:image-loader"))
    implementation(project(":libraries:media"))

    implementation(project(":navigation"))
    implementation(project(":features:presentation-base"))

    implementation(project(":shared-android"))
    implementation(project(":shared"))

    implementation(libs.kotlin)
    implementation(libs.Coroutines.core)

    implementation(libs.dagger.core)
    kapt(libs.dagger.kapt)
    implementation(libs.dagger.hilt)
    kapt(libs.dagger.hiltKapt)

    implementation(libs.X.core)
    implementation(libs.X.appcompat)
    implementation(libs.X.fragments)
    implementation(libs.X.recyclerView)
    implementation(libs.X.constraintLayout)
    implementation(libs.X.preference)
    implementation(libs.X.material)

    implementation(libs.UX.dialogs)

    implementation(libs.Utils.colorDesaturation)
    implementation(libs.Utils.scrollHelper)
    implementation(libs.Utils.lastFmBinding) // TODO remove this

    implementation(libs.Debug.timber)

    testImplementation(libs.Test.junit)
    testImplementation(libs.Test.mockito)
    testImplementation(libs.Test.mockitoKotlin)
    testImplementation(libs.Test.android)
    testImplementation(libs.Test.robolectric)
    testImplementation(libs.Coroutines.test)
}
