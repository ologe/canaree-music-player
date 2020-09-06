plugins {
    id(buildPlugins.androidLibrary)
    id(buildPlugins.kotlinAndroid)
    id(buildPlugins.kotlinAndroidExtensions)
    id(buildPlugins.kotlinKapt)
    id(buildPlugins.hilt)
}

apply(from = rootProject.file("buildscripts/configure-android-defaults.gradle"))

androidExtensions {
    isExperimental = true
}

dependencies {
    lintChecks(project(":lint"))

    implementation(project(":libraries:image-loader"))

    implementation(project(":core"))
    implementation(project(":domain"))
    implementation(project(":libraries:media")) // TODO not too sure about that
    implementation(project(":shared"))
    implementation(project(":shared-android"))

    implementation(project(":navigation"))

    implementation(libs.kotlin)
    implementation(libs.Coroutines.core)

    implementation(libs.dagger.core)
    kapt(libs.dagger.kapt)
    implementation(libs.dagger.hilt)
    kapt(libs.dagger.hiltKapt)
    implementation(libs.dagger.hiltX)
    kapt(libs.dagger.hiltXKapt)

    implementation(libs.X.material)
    implementation(libs.X.core)
    implementation(libs.X.fragments)
    implementation(libs.X.palette)
    implementation(libs.X.preference)
    implementation(libs.X.constraintLayout)
    implementation(libs.X.Lifecycle.runtime)

    implementation(libs.UX.glide)

    implementation(libs.Utils.colorDesaturation)

    implementation(libs.Debug.timber)

    testImplementation(libs.Test.junit)
    testImplementation(libs.Test.mockito)
    testImplementation(libs.Test.mockitoKotlin)
    testImplementation(libs.Test.android)
    testImplementation(libs.Test.robolectric)
    testImplementation(libs.Coroutines.test)
}
