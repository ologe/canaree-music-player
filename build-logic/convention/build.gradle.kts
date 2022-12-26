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
        register("HiltConventionPlugin") {
            id = "dev.olog.msc.hilt"
            implementationClass = "HiltConventionPlugin"
        }

    }
}