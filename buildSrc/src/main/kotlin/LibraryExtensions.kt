import com.android.build.gradle.BaseExtension
import org.gradle.api.JavaVersion

fun BaseExtension.applyDefaults(
    compose: Boolean = false
) {
    compileSdkVersion(sdk.compile)

    defaultConfig {
        applyDefaults()
    }

    enableJava8()

    lintOptions {
        isAbortOnError = false
    }

    buildTypes {
        release {
            isDebuggable = false
        }
        debug {
            isDebuggable = true
        }
    }

    if (compose) {
        buildFeatures.compose = true
        composeOptions.kotlinCompilerExtensionVersion = versions.compose
        composeOptions.kotlinCompilerVersion = versions.kotlin
    }

//    testOptions {
//        unitTests.isIncludeAndroidResources = true
//        unitTests.isReturnDefaultValues = true
//    }
}

fun BaseExtension.enableJava8() {
    compileOptions {
        sourceCompatibility = versions.java
        targetCompatibility = versions.java
    }
}