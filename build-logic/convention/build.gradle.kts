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
    implementation(libs.plugin.hilt)
}

gradlePlugin {
    plugins {
        register("LibraryConventionPlugin") {
            id = "dev.olog.msc.library"
            implementationClass = "LibraryConventionPlugin"
        }
        register("ApplicationConventionPlugin") {
            id = "dev.olog.msc.application"
            implementationClass = "ApplicationConventionPlugin"
        }
        register("FeatureConventionPlugin") {
            id = "dev.olog.msc.feature"
            implementationClass = "FeatureConventionPlugin"
        }
        register("HiltConventionPlugin") {
            id = "dev.olog.msc.hilt"
            implementationClass = "HiltConventionPlugin"
        }

    }
}