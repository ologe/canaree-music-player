plugins {
    id(buildPlugins.androidLibrary)
    id(buildPlugins.kotlinAndroid)
    id(buildPlugins.kotlinKapt)
    id(buildPlugins.hilt)
}

android {

    applyDefaults()

    defaultConfig {
        javaCompileOptions {
            annotationProcessorOptions {
//                arguments = mapOf( TODO restore
//                    "room.schemaLocation" to "$projectDir/schemas",
//                    "room.incremental" to "true",
//                    "room.expandProjection" to "true"
//                )
            }
        }

        configField("LAST_FM_KEY" to localProperties.lastFmKey)
        configField("LAST_FM_SECRET" to localProperties.lastFmSecret)

    }

    sourceSets {
        getByName("test").assets.srcDir("$projectDir/schemas")
        getByName("androidTest").assets.srcDir("$projectDir/schemas")
    }

}

dependencies {
    lintChecks(project(":lint"))

    implementation(project(":domain"))
    implementation(project(":shared"))
    implementation(project(":shared-android"))
    implementation(project(":prefs-keys"))
    implementation(project(":libraries:network"))
    implementation(project(":data-spotify"))

    implementation(libs.kotlin)
    implementation(libs.Coroutines.core)

    implementation(libs.dagger.core)
    kapt(libs.dagger.kapt)
    implementation(libs.dagger.hilt)
    kapt(libs.dagger.hiltKapt)

    implementation(libs.X.core)
    implementation(libs.X.preference)

    implementation(libs.X.Room.core)
    implementation(libs.X.Room.coroutines)
    kapt(libs.X.Room.kapt)

    implementation(libs.Network.retrofit)
    implementation(libs.Network.gson)

    implementation(libs.Utils.sqlContentResolver)
    implementation(libs.Utils.fuzzy)

    implementation(libs.Debug.timber)

    testImplementation(libs.Test.junit)
    testImplementation(libs.Test.mockito)
    testImplementation(libs.Test.mockitoKotlin)
    testImplementation(libs.Test.android)
    testImplementation(libs.Test.robolectric)
    testImplementation(libs.Coroutines.test)

}
