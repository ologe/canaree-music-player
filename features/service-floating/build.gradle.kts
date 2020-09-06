plugins {
    id(buildPlugins.androidLibrary)
    id(buildPlugins.kotlinAndroid)
    id(buildPlugins.kotlinKapt)
    id(buildPlugins.kotlinAndroidExtensions)
    id(buildPlugins.hilt)
}

android {
    applyDefaults()
}

dependencies {
    lintChecks(project(":lint"))

    implementation(project(":core"))
    implementation(project(":features:presentation-base"))
    implementation(project(":navigation"))

    implementation(project(":domain"))
    implementation(project(":libraries:image-loader"))
    implementation(project(":shared-android"))
    implementation(project(":shared"))
    implementation(project(":prefs-keys"))
    implementation(project(":libraries:media"))
    implementation(project(":libraries:offline-lyrics"))

    implementation(libs.kotlin)
    implementation(libs.Coroutines.core)

    implementation(libs.dagger.core)
    kapt(libs.dagger.kapt)
    implementation(libs.dagger.hilt)
    kapt(libs.dagger.hiltKapt)

    implementation(libs.X.core)
    implementation(libs.X.media)
    implementation(libs.X.constraintLayout)
    implementation(libs.X.appcompat)
    implementation(libs.X.material)
    implementation(libs.X.recyclerView)
    implementation(libs.X.Lifecycle.java8)
    implementation(libs.X.Lifecycle.service)
    implementation(libs.X.Lifecycle.runtime)

    implementation(libs.UX.blurKit)
    implementation(libs.UX.glide)

    implementation(libs.Debug.timber)

    testImplementation(libs.Test.junit)
    testImplementation(libs.Test.mockito)
    testImplementation(libs.Test.mockitoKotlin)
    testImplementation(libs.Test.android)
    testImplementation(libs.Test.robolectric)
    testImplementation(libs.Coroutines.test)
}
