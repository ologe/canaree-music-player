@file:Suppress("UnstableApiUsage")

package dev.olog.msc

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project

internal fun Project.configureKotlinAndroid(
    commonExtension: CommonExtension<*, *, *, *>
) = with(commonExtension) {
    compileSdk = config.compileSdkVersion

    defaultConfig {
        minSdk = config.minSdkVersion

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
    }

    compileOptions {
        sourceCompatibility = config.javaVersion
        targetCompatibility = config.javaVersion
    }

    kotlinOptions {
        jvmTarget = config.javaVersion.toString()
        freeCompilerArgs = freeCompilerArgs + listOf(
            "-opt-in=kotlinx.coroutines.FlowPreview",
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
        )
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
    }

    libraries {
        implementation("kotlin-core")
        implementation("coroutines-core")
        implementation("coroutines-android")
        testImplementation("coroutines-test")

        testImplementation("test-junit")
        testImplementation("test-mockito-core")
        testImplementation("test-mockito-kotlin")
        testImplementation("test-robolectric")
    }

}