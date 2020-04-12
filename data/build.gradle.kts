plugins {
    id(BuildPlugins.androidLibrary)
    id(BuildPlugins.kotlinAndroid)
    id(BuildPlugins.kotlinKapt)
}

android {

    applyDefaults()

    defaultConfig {
        javaCompileOptions {
            annotationProcessorOptions {
                arguments = mapOf(
                    "room.schemaLocation" to "$projectDir/schemas",
                    "room.incremental" to "true",
                    "room.expandProjection" to "true"
                )
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
    implementation(project(":libraries:lib.network"))
    implementation(project(":data-spotify"))

    implementation(Libraries.kotlin)
    implementation(Libraries.Coroutines.core)

    implementation(Libraries.Dagger.core)
    kapt(Libraries.Dagger.kapt)

    implementation(Libraries.X.core)
    implementation(Libraries.X.preference)

    implementation(Libraries.X.Room.core)
    implementation(Libraries.X.Room.coroutines)
    kapt(Libraries.X.Room.kapt)

    implementation(Libraries.Network.retrofit)
    implementation(Libraries.Network.gson)

    implementation(Libraries.Utils.sqlContentResolver)
    implementation(Libraries.Utils.fuzzy)

    implementation(Libraries.Debug.timber)

    testImplementation(Libraries.Test.junit)
    testImplementation(Libraries.Test.mockito)
    testImplementation(Libraries.Test.mockitoKotlin)
    testImplementation(Libraries.Test.android)
    testImplementation(Libraries.Test.robolectric)
    testImplementation(Libraries.Coroutines.test)

}
