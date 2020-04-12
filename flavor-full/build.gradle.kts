plugins {
    id(BuildPlugins.androidApplication)
    id(BuildPlugins.kotlinAndroid)
    id(BuildPlugins.kotlinKapt)
}

android {
    applyDefaults()
}

dependencies {
    lintChecks(project(":lint"))

    implementation(Libraries.kotlin)

    api(project(":feature-app-shortcuts"))
    api(project(":feature-library"))
    api(project(":feature-search"))
    api(project(":feature-detail"))
    api(project(":feature-player"))
    api(project(":feature-player-mini"))
    api(project(":feature-queue"))
    api(project(":feature-settings"))
    api(project(":feature-about"))
    api(project(":feature-onboarding"))
    api(project(":feature-equalizer"))
    api(project(":feature-edit"))

    api(project(":feature-service-music"))
    api(project(":feature-service-floating"))
    api(project(":presentation"))
}