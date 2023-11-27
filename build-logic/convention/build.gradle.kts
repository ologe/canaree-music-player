plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.plugin.gradle)
    implementation(libs.plugin.kotlin)
}

gradlePlugin {
    plugins {
        register("ApplicationConventionPlugin") {
            id = "dev.olog.msc.app"
            implementationClass = "ApplicationConventionPlugin"
        }
        register("LibraryConventionPlugin") {
            id = "dev.olog.msc.library"
            implementationClass = "LibraryConventionPlugin"
        }
        register("JvmLibraryConventionPlugin") {
            id = "dev.olog.msc.library.jvm"
            implementationClass = "JvmLibraryConventionPlugin"
        }
        register("DaggerConventionPlugin") {
            id = "dev.olog.msc.dagger"
            implementationClass = "DaggerConventionPlugin"
        }
        register("ComposeConventionPlugin") {
            id = "dev.olog.msc.compose"
            implementationClass = "ComposeConventionPlugin"
        }
    }
}