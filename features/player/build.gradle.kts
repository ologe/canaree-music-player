plugins {
    id(buildPlugins.androidLibrary)
    id(buildPlugins.kotlinAndroid)
    id(buildPlugins.kotlinKapt)
    id(buildPlugins.kotlinAndroidExtensions)
    id(buildPlugins.hilt)
}

apply(from = rootProject.file("buildscripts/configure-android-defaults.gradle"))

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
    implementation(libs.dagger.hiltX)
    kapt(libs.dagger.hiltXKapt)

    implementation(libs.X.core)
    implementation(libs.X.appcompat)
    implementation(libs.X.fragments)
    implementation(libs.X.recyclerView)
    implementation(libs.X.constraintLayout)
    implementation(libs.X.preference)
    implementation(libs.X.material)

    implementation(libs.UX.glide)
    implementation(libs.UX.blurKit)
    implementation(libs.UX.lottie)

    implementation(libs.Debug.timber)

    testImplementation(libs.Test.junit)
    testImplementation(libs.Test.mockito)
    testImplementation(libs.Test.mockitoKotlin)
    testImplementation(libs.Test.android)
    testImplementation(libs.Test.robolectric)
    testImplementation(libs.Coroutines.test)
}
