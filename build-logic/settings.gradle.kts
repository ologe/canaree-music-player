enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    versionCatalogs {
        create("libs") {
            from(files("${rootDir.parent}/gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "build-logic"

include(":convention")