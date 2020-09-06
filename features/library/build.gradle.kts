plugins {
    id(buildPlugins.androidLibrary)
    id(buildPlugins.kotlinAndroid)
    id(buildPlugins.kotlinKapt)
    id(buildPlugins.kotlinAndroidExtensions)
    id(buildPlugins.hilt)
}

androidExtensions {
    isExperimental = true
}

android {
    applyDefaults()
}

dependencies {
    lintChecks(project(":lint"))

    implementation(project(":core"))
    implementation(project(":domain"))

    implementation(project(":libraries:image-loader"))
    implementation(project(":libraries:media"))
    implementation(project(":libraries:analytics")) // TODO check if is needed

    implementation(project(":navigation"))
    implementation(project(":features:presentation-base"))

    implementation(project(":shared-android"))
    implementation(project(":shared"))

    implementation(Libraries.kotlin)
    implementation(Libraries.Coroutines.core)

    implementation(Libraries.Dagger.core)
    kapt(Libraries.Dagger.kapt)
    implementation(Libraries.Dagger.hilt)
    kapt(Libraries.Dagger.hiltKapt)
    implementation(Libraries.Dagger.hiltX)
    kapt(Libraries.Dagger.hiltXKapt)

    implementation(Libraries.X.core)
    implementation(Libraries.X.appcompat)
    implementation(Libraries.X.fragments)
    implementation(Libraries.X.recyclerView)
    implementation(Libraries.X.constraintLayout)
    implementation(Libraries.X.preference)
    implementation(Libraries.X.material)

    implementation(Libraries.Utils.scrollHelper)

    implementation(Libraries.Debug.timber)

    testImplementation(Libraries.Test.junit)
    testImplementation(Libraries.Test.mockito)
    testImplementation(Libraries.Test.mockitoKotlin)
    testImplementation(Libraries.Test.android)
    testImplementation(Libraries.Test.robolectric)
    testImplementation(Libraries.Coroutines.test)
}
