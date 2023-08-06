package dev.olog.msc

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

internal fun Project.configureKotlinAndroid(
    commonExtensions: CommonExtension<*, *, *, *>
) = with(commonExtensions) {
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
            // TODO remove after coroutines became stable
            "-Xuse-experimental=kotlinx.coroutines.FlowPreview",
            "-Xuse-experimental=kotlinx.coroutines.ExperimentalCoroutinesApi"
        )
    }

    dependencies {
        val libs = project.extensions.getByType<VersionCatalogsExtension>().named("libs")
        add("implementation", libs.findDependency("kotlin-core").get())
        add("implementation", libs.findDependency("coroutines-core").get())
        add("implementation", libs.findDependency("coroutines-android").get())
        add("testImplementation", libs.findDependency("coroutines-test").get())
    }

}