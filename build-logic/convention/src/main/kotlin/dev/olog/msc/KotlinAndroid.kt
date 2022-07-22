@file:Suppress("UnstableApiUsage")

package dev.olog.msc

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project

fun Project.configureKotlinAndroid(
    commonExtension: CommonExtension<*, *, *, *>,
) = with(commonExtension) {

    compileSdk = config.compileSdk

    defaultConfig {
        minSdk = config.minSdk
        targetSdk(config.targetSdk)

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables.useSupportLibrary = true
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = config.javaVersion
        targetCompatibility = config.javaVersion
    }

    kotlinOptions {
        jvmTarget = config.javaVersion.toString()
        freeCompilerArgs = freeCompilerArgs + listOf(
            "-opt-in=kotlinx.coroutines.FlowPreview",
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-opt-in=androidx.compose.ui.ExperimentalComposeUiApi",
            "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi",
            "-opt-in=androidx.compose.material.ExperimentalMaterialApi",
            "-opt-in=kotlinx.coroutines.flow.FlowPreview",
        )
    }

    buildFeatures {
        viewBinding = true
    }

    libraries {
        add("coreLibraryDesugaring", "desugaring")
        implementation("kotlin")

        implementation("coroutines")
        implementation("coroutines-android")
        testImplementation("coroutines-test")
        testImplementation("test-flowAssertions")

        testImplementation("test-junit")
        testImplementation("test-mockito")
        testImplementation("test-mockito-kotlin")
        testImplementation("test-robolectric")
    }

}