package dev.olog.msc

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

internal fun Project.configureKotlinAndroid(
    commonExtension: CommonExtension<*, *, *, *, *, *>
) = with(commonExtension) {

    compileSdk = config.compileSdk

    defaultConfig {
        minSdk = config.minSdk
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
    }

    compileOptions {
        sourceCompatibility = config.java
        targetCompatibility = config.java
        isCoreLibraryDesugaringEnabled = true
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
    }

    kotlin {
        compilerOptions {
            freeCompilerArgs.add("-opt-in=kotlinx.coroutines.FlowPreview")
            freeCompilerArgs.add("-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi")
            jvmTarget.set(JvmTarget.fromTarget(config.java.toString()))

            // on kotlin 2.0 fails on
//            ERROR_CALL 'Unresolved reference: <Strange deserialized enum value: androidx/lifecycle/Lifecycle$Event.ON_DESTROY>#' type=androidx.lifecycle.Lifecycle$Event
            languageVersion.set(KotlinVersion.KOTLIN_1_9)
            apiVersion.set(KotlinVersion.KOTLIN_1_9)
        }
    }

    val catalog = versionCatalog
    dependencies {
        add("implementation", catalog.findLibrary("kotlin").get())
        add("implementation", catalog.findLibrary("coroutines").get())
        add("implementation", catalog.findLibrary("coroutines-android").get())
        add("testImplementation", catalog.findLibrary("coroutines-test").get())
        add("testImplementation", catalog.findLibrary("junit").get())
        add("testImplementation", catalog.findLibrary("mockito").get())
        add("testImplementation", catalog.findLibrary("mockito-kotlin").get())
        add("coreLibraryDesugaring", catalog.findLibrary("desugaring").get())
    }

}