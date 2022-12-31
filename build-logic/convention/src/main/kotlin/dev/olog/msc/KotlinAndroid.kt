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
        sourceCompatibility = config.javaVersion
        targetCompatibility = config.javaVersion
    }

    kotlinOptions {
        jvmTarget = config.javaVersion.toString()
        freeCompilerArgs = freeCompilerArgs + listOf(

        )
    }

}