import com.android.build.gradle.internal.dsl.DefaultConfig

fun DefaultConfig.applyDefaults() {
    minSdkVersion(sdk.min)
    targetSdkVersion(sdk.target)

    versionCode = sdk.versionCode
    versionName = sdk.versionName

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    vectorDrawables {
        useSupportLibrary = true
    }
}