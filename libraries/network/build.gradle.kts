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

    implementation(Libraries.kotlin)
    implementation(Libraries.Coroutines.core)

    implementation(Libraries.Dagger.core)
    kapt(Libraries.Dagger.kapt)
    implementation(Libraries.Dagger.hilt)
    kapt(Libraries.Dagger.hiltKapt)

    implementation(Libraries.Network.retrofit)
    implementation(Libraries.Network.retrofitGson)
    implementation(Libraries.Network.okHttp)
    implementation(Libraries.Network.okHttpInterceptor)

    implementation(Libraries.Debug.timber)
    debugImplementation(Libraries.Debug.chucker)
    releaseImplementation(Libraries.Debug.chuckerNoOp)

    testImplementation(Libraries.Test.junit)
    testImplementation(Libraries.Test.mockito)
    testImplementation(Libraries.Test.mockitoKotlin)
    testImplementation(Libraries.Test.android)
    testImplementation(Libraries.Test.robolectric)
    testImplementation(Libraries.Coroutines.test)
}
