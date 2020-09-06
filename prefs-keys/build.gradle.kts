plugins {
    id(buildPlugins.androidLibrary)
    id(buildPlugins.kotlinAndroid)
}

android {
    applyDefaults()
}

dependencies {
    lintChecks(project(":lint"))

    implementation(Libraries.kotlin)
}