import com.android.build.gradle.BaseExtension
import org.gradle.api.JavaVersion

fun BaseExtension.applyDefaults() {
    compileSdkVersion(AndroidSdk.compile)

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

//    testOptions {
//        unitTests.isIncludeAndroidResources = true
//        unitTests.isReturnDefaultValues = true
//    }
}

fun BaseExtension.enableJava8() {
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}