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
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
        )
    }

    libraries {
        implementation("kotlin")
        implementation("coroutines")
        add("coreLibraryDesugaring", "desugar")

        add("testImplementation", project(":test-shared"))
        testImplementation("coroutines-test")
        testImplementation("test-junit")
        testImplementation("test-mockito")
        testImplementation("test-mockito-inline")
        testImplementation("test-mockito-kotlin")
        testImplementation("test-livedata")
        testImplementation("test-robolectric")

        androidTestImplementation("testui-runner")
        androidTestImplementation("testui-rules")
    }

}