plugins {
    id(BuildPlugins.androidLibrary)
    id(BuildPlugins.kotlinAndroid)
    id(BuildPlugins.kotlinAndroidExtensions)
    id(BuildPlugins.kotlinKapt)
    id(BuildPlugins.hilt)
}

android {
    applyDefaults()
}

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

    implementation(Libraries.kotlin)
    implementation(Libraries.Coroutines.core)

    implementation(Libraries.Dagger.core)
    kapt(Libraries.Dagger.kapt)
    implementation(Libraries.Dagger.hilt)
    kapt(Libraries.Dagger.hiltKapt)
    implementation(Libraries.Dagger.hiltX)
    kapt(Libraries.Dagger.hiltXKapt)

    implementation(Libraries.X.material)
    implementation(Libraries.X.core)
    implementation(Libraries.X.fragments)
    implementation(Libraries.X.palette)
    implementation(Libraries.X.preference)
    implementation(Libraries.X.constraintLayout)
    implementation(Libraries.X.Lifecycle.runtime)

    implementation(Libraries.UX.glide)

    implementation(Libraries.Utils.colorDesaturation)

    implementation(Libraries.Debug.timber)

    testImplementation(Libraries.Test.junit)
    testImplementation(Libraries.Test.mockito)
    testImplementation(Libraries.Test.mockitoKotlin)
    testImplementation(Libraries.Test.android)
    testImplementation(Libraries.Test.robolectric)
    testImplementation(Libraries.Coroutines.test)
}
