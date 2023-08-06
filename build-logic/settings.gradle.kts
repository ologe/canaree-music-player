enableFeaturePreview("VERSION_CATALOGS")

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