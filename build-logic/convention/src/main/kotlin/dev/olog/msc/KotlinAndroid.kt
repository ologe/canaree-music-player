package dev.olog.msc

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project

internal fun Project.configureKotlinAndroid(
    commonExtension: CommonExtension<*, *, *, *, *>
) = with(commonExtension) {
    compileSdk = config.compileSdk

    defaultConfig {
        minSdk = config.minSdk
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
            "-Xuse-experimental=kotlinx.coroutines.FlowPreview",
            "-Xuse-experimental=kotlinx.coroutines.ExperimentalCoroutinesApi"
        )
    }

    libraries {
        implementation("kotlin")
        implementation("coroutines")
        implementation("coroutines-android")

        testImplementation("coroutines-test")
        testImplementation("test-junit")
        testImplementation("test-mockito")
        testImplementation("test-mockito-kotlin")
    }


}