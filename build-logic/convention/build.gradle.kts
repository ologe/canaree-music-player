plugins {
    `kotlin-dsl`
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    implementation(libs.plugin.gradle)
    implementation(libs.plugin.kotlin)
}

gradlePlugin {
    plugins {
        register("ApplicationConventionPlugin") {
            id = "dev.msc.application"
            implementationClass = "ApplicationConventionPlugin"
        }
        register("LibraryConventionPlugin") {
            id = "dev.msc.library"
            implementationClass = "LibraryConventionPlugin"
        }
        register("JvmLibraryConventionPlugin") {
            id = "dev.msc.library.jvm"
            implementationClass = "JvmLibraryConventionPlugin"
        }
        register("HiltConventionPlugin") {
            id = "dev.msc.hilt"
            implementationClass = "HiltConventionPlugin"
        }
    }
}