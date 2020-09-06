plugins {
    id(buildPlugins.androidLibrary)
    id(buildPlugins.kotlinAndroid)
    id(buildPlugins.kotlinKapt)
    id(buildPlugins.hilt)
}

android {
    applyDefaults()

}

dependencies {
    lintChecks(project(":lint"))

    implementation(project(":domain"))
    implementation(project(":shared"))

    implementation(libs.kotlin)
    implementation(libs.Coroutines.core)

    implementation(libs.dagger.core)
    kapt(libs.dagger.kapt)
    implementation(libs.dagger.hilt)
    kapt(libs.dagger.hiltKapt)

    implementation(libs.Network.retrofit)
    implementation(libs.Network.retrofitGson)
    implementation(libs.Network.okHttp)
    implementation(libs.Network.okHttpInterceptor)

    implementation(libs.Debug.timber)
    debugImplementation(libs.Debug.chucker)
    releaseImplementation(libs.Debug.chuckerNoOp)

    testImplementation(libs.Test.junit)
    testImplementation(libs.Test.mockito)
    testImplementation(libs.Test.mockitoKotlin)
    testImplementation(libs.Test.android)
    testImplementation(libs.Test.robolectric)
    testImplementation(libs.Coroutines.test)
}
