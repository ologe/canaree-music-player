package dev.olog.canaree

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

internal fun Project.configureKotlinAndroid(
    commonExtension: CommonExtension,
) {
    commonExtension.apply {
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

        lint {
            checkReleaseBuilds = false
            disable.add("MissingTranslation")
        }

        buildFeatures {
            viewBinding = true
        }
    }

    val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

    dependencies {
        add("implementation", libs.findLibrary("kotlin-stdlib").get())
        add("implementation", libs.findLibrary("kotlinx-coroutines-core").get())
        add("implementation", libs.findLibrary("kotlinx-coroutines-android").get())
        
        add("testImplementation", libs.findLibrary("kotlinx-coroutines-test").get())
        
        add("testImplementation", libs.findLibrary("junit").get())
        add("testImplementation", libs.findLibrary("mockito-android").get())
        add("testImplementation", libs.findLibrary("mockito-kotlin").get())
        
        add("androidTestImplementation", libs.findLibrary("mockito-android").get())
        add("androidTestImplementation", libs.findLibrary("mockito-kotlin").get())
        add("androidTestImplementation", libs.findLibrary("androidx-test-core").get())
        add("androidTestImplementation", libs.findLibrary("androidx-test-runner").get())
        add("androidTestImplementation", libs.findLibrary("androidx-test-junit").get())
    }
}
