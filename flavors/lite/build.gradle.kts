plugins {
    id(buildPlugins.androidLibrary)
    id(buildPlugins.kotlinAndroid)
    id(buildPlugins.kotlinKapt)
}

android {
    applyDefaults()
}

dependencies {
    lintChecks(project(":lint"))

    implementation(Libraries.kotlin)
    implementation(Libraries.Dagger.core)

    api(project(":features:presentation-base"))
    api(project(":features:app-shortcuts"))
    api(project(":features:library"))
    api(project(":features:search"))
    api(project(":features:player"))
    api(project(":features:player-mini"))
    api(project(":features:queue"))
    api(project(":features:onboarding"))

    api(project(":features:service-music"))
    api(project(":presentation"))
}