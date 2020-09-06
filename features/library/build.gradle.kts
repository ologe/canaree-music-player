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
    applyDefaults(compose = true)
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

    dagger()
    coroutines()
    compose()

    implementation(libs.X.core)
    implementation(libs.X.appcompat)
    implementation(libs.X.fragments)
    implementation(libs.X.recyclerView)
    implementation(libs.X.constraintLayout)
    implementation(libs.X.preference)
    implementation(libs.X.material)

    implementation(libs.Utils.scrollHelper)

    implementation(libs.Debug.timber)

    testImplementation(libs.Test.junit)
    testImplementation(libs.Test.mockito)
    testImplementation(libs.Test.mockitoKotlin)
    testImplementation(libs.Test.android)
    testImplementation(libs.Test.robolectric)
}
