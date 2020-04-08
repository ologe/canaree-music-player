plugins {
    id(BuildPlugins.androidLibrary)
    id(BuildPlugins.kotlinAndroid)
    id(BuildPlugins.kotlinAndroidExtensions)
}

android {
    applyDefaults()
}

androidExtensions {
    isExperimental = true
}

dependencies {
    lintChecks(project(":lint"))

    implementation(project(":core"))
    implementation(project(":domain"))
    implementation(project(":shared"))
    implementation(project(":shared-android"))
    implementation(project(":shared-widgets"))

    implementation(Libraries.kotlin)
    implementation(Libraries.Coroutines.core)

    implementation(Libraries.Dagger.androidSupport)

    implementation(Libraries.X.material)
    implementation(Libraries.X.core)
    implementation(Libraries.X.fragments)
    implementation(Libraries.X.palette)
    implementation(Libraries.X.preference)
    implementation(Libraries.X.constraintLayout)
    implementation(Libraries.X.Lifecycle.runtime)

    implementation(Libraries.Utils.colorDesaturation)

    implementation(Libraries.Debug.timber)

    testImplementation(Libraries.Test.junit)
    testImplementation(Libraries.Test.mockito)
    testImplementation(Libraries.Test.mockitoKotlin)
    testImplementation(Libraries.Test.android)
    testImplementation(Libraries.Test.robolectric)
    testImplementation(Libraries.Coroutines.test)
}
