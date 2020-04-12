plugins {
    id(BuildPlugins.androidLibrary)
    id(BuildPlugins.kotlinAndroid)
    id(BuildPlugins.kotlinKapt)
}

android {
    applyDefaults()
}

dependencies {
    lintChecks(project(":lint"))

    implementation(Libraries.kotlin)
    implementation(Libraries.Dagger.core)

    api(project(":feature-app-shortcuts"))
    api(project(":feature-library"))
    api(project(":feature-search"))
    api(project(":feature-player"))
    api(project(":feature-player-mini"))
    api(project(":feature-queue"))
    api(project(":feature-onboarding"))

    api(project(":feature-service-music"))
    api(project(":feature-service-floating"))
    api(project(":presentation"))
}