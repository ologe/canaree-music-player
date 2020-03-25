import com.android.build.gradle.internal.dsl.DefaultConfig

fun DefaultConfig.applyDefaults() {
    minSdkVersion(AndroidSdk.min)
    targetSdkVersion(AndroidSdk.target)

    versionCode = AndroidSdk.versionCode
    versionName = AndroidSdk.versionName

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    vectorDrawables {
        useSupportLibrary = true
    }
}