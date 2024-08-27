plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.plugin.agp)
    implementation(libs.plugin.kotlin)
}

gradlePlugin {
    plugins {
        register("AppConventionPlugin") {
            id = "dev.msc.app"
            implementationClass = "AppConventionPlugin"
        }
        register("LibraryConventionPlugin") {
            id = "dev.msc.library"
            implementationClass = "LibraryConventionPlugin"
        }
        register("HiltConventionPlugin") {
            id = "dev.msc.hilt"
            implementationClass = "HiltConventionPlugin"
        }
        register("ComposeConventionPlugin") {
            id = "dev.msc.compose"
            implementationClass = "ComposeConventionPlugin"
        }
    }
}