plugins {
    id(buildPlugins.androidLibrary)
    id(buildPlugins.kotlinAndroid)
    id(buildPlugins.kotlinKapt)
}

android {
    applyDefaults()
}

dependencies {
    implementation(project(":domain"))

    implementation(Libraries.kotlin)
    implementation(Libraries.Coroutines.core)

    implementation(Libraries.Test.junit)
    implementation(Libraries.Coroutines.test)

}
