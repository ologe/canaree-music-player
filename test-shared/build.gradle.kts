plugins {
    id(BuildPlugins.androidLibrary)
    id(BuildPlugins.kotlinAndroid)
    id(BuildPlugins.kotlinKapt)
}

android {
    applyDefaults()
}

dependencies {
    implementation(project(":core"))

    implementation(Libraries.kotlin)
    implementation(Libraries.Coroutines.core)

    implementation(Libraries.Test.junit)
    implementation(Libraries.Coroutines.test)

}
