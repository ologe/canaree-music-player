plugins {
    id(BuildPlugins.androidLibrary)
    id(BuildPlugins.kotlinAndroid)
    id(BuildPlugins.kotlinKapt)
    id(BuildPlugins.kotlinAndroidExtensions)
}

android {
    applyDefaults()
}

dependencies {
    lintChecks(project(":lint"))

    implementation(project(":core"))
    implementation(project(":feature-presentation-base"))
    implementation(project(":navigation"))

    implementation(project(":domain"))
    implementation(project(":lib.image-loader"))
    implementation(project(":shared-android"))
    implementation(project(":shared"))
    implementation(project(":prefs-keys"))
    implementation(project(":lib.media"))
    implementation(project(":lib.offline-lyrics"))

    implementation(Libraries.kotlin)
    implementation(Libraries.Coroutines.core)

    implementation(Libraries.Dagger.core)
    kapt(Libraries.Dagger.kapt)
    implementation(Libraries.Dagger.android)
    implementation(Libraries.Dagger.androidSupport)
    kapt(Libraries.Dagger.androidKapt)

    implementation(Libraries.X.core)
    implementation(Libraries.X.media)
    implementation(Libraries.X.constraintLayout)
    implementation(Libraries.X.appcompat)
    implementation(Libraries.X.material)
    implementation(Libraries.X.recyclerView)
    implementation(Libraries.X.Lifecycle.java8)
    implementation(Libraries.X.Lifecycle.service)
    implementation(Libraries.X.Lifecycle.runtime)

    implementation(Libraries.UX.blurKit)
    implementation(Libraries.UX.glide)

    implementation(Libraries.Debug.timber)

    testImplementation(Libraries.Test.junit)
    testImplementation(Libraries.Test.mockito)
    testImplementation(Libraries.Test.mockitoKotlin)
    testImplementation(Libraries.Test.android)
    testImplementation(Libraries.Test.robolectric)
    testImplementation(Libraries.Coroutines.test)
}
