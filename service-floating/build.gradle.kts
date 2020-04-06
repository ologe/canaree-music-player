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

    implementation(project(":domain"))
    implementation(project(":injection"))
    implementation(project(":image-provider"))
    implementation(project(":shared-android"))
    implementation(project(":shared"))
    implementation(project(":shared-widgets"))
    implementation(project(":prefs-keys"))
    implementation(project(":media"))
    implementation(project(":offline-lyrics"))

    implementation(Libraries.kotlin)
    implementation(Libraries.Coroutines.core)

    implementation(Libraries.Dagger.core)
    kapt(Libraries.Dagger.kapt)

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
