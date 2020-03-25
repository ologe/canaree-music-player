import com.android.build.gradle.BaseExtension
import org.gradle.api.JavaVersion

const val FLAVOR_DIMENSION = "version"

enum class Flavors(val value: String) {
    MOCK("mock"),
    FULL("full")
}

fun BaseExtension.applyDefaults() {
    compileSdkVersion(AndroidSdk.compile)

    defaultConfig {
        applyDefaults()
    }

    setupFlavors()
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

fun BaseExtension.setupFlavors() {
    flavorDimensions(FLAVOR_DIMENSION)
    productFlavors {
        register(Flavors.MOCK.value) {
            dimension = FLAVOR_DIMENSION
        }
        register(Flavors.FULL.value) {
            dimension = FLAVOR_DIMENSION
        }
    }
}

fun BaseExtension.enableJava8() {
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}