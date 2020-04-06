plugins {
    id(BuildPlugins.androidLibrary)
    id(BuildPlugins.kotlinAndroid)
}

android {
    applyDefaults()
}

dependencies {
    implementation(Libraries.kotlin)
    implementation(Libraries.Coroutines.core)

//    implementation(Libraries.Dagger.core)

    testImplementation(Libraries.Test.junit)
    testImplementation(Libraries.Test.mockito)
    testImplementation(Libraries.Test.mockitoKotlin)
}
