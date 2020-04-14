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

    api(project(":features:feature-presentation-base"))
    api(project(":features:feature-app-shortcuts"))
    api(project(":features:feature-library"))
    api(project(":features:feature-search"))
    api(project(":features:feature-detail"))
    api(project(":features:feature-player"))
    api(project(":features:feature-player-mini"))
    api(project(":features:feature-queue"))
    api(project(":features:feature-settings"))
    api(project(":features:feature-about"))
    api(project(":features:feature-onboarding"))
    api(project(":features:feature-equalizer"))
    api(project(":features:feature-edit"))

    api(project(":features:feature-service-music"))
    api(project(":features:feature-service-floating"))
    api(project(":presentation"))
}