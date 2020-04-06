plugins {
    id(BuildPlugins.androidLibrary)
    id(BuildPlugins.kotlinAndroid)
    id(BuildPlugins.kotlinKapt)
}

android {
    applyDefaults()

    buildTypes {
        val properties = localProperties
        debug {
            configField("AES_PASSWORD" to properties.aesPassword)
        }
        release {
            configField("AES_PASSWORD" to properties.aesPassword)
        }
    }

}

dependencies {
    lintChecks(project(":lint"))

    implementation(project(":domain"))
    implementation(project(":analytics"))
    implementation(project(":data"))
    implementation(project(":data-spotify"))
    implementation(project(":data-shared"))
    implementation(project(":shared"))
    implementation(project(":equalizer"))

    implementation(Libraries.kotlin)
    implementation(Libraries.Coroutines.core)

    implementation(Libraries.Dagger.core)
    kapt(Libraries.Dagger.kapt)

    implementation(Libraries.X.appcompat)
    implementation(Libraries.X.Room.core)

    implementation(Libraries.Network.okHttp)
    implementation(Libraries.Network.retrofit)

    implementation(Libraries.Firebase.analytics)

    implementation(Libraries.Utils.aesCrypto)
    implementation(Libraries.Debug.timber)

    testImplementation(Libraries.Test.junit)
    testImplementation(Libraries.Test.mockito)
    testImplementation(Libraries.Test.mockitoKotlin)
    testImplementation(Libraries.Test.android)
    testImplementation(Libraries.Test.robolectric)
    testImplementation(Libraries.Coroutines.test)
}
