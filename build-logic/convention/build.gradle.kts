plugins {
    `kotlin-dsl`
}

group = "dev.olog.canaree.buildlogic"

dependencies {
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.hilt.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "canaree.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "canaree.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("jvmLibrary") {
            id = "canaree.jvm.library"
            implementationClass = "JvmLibraryConventionPlugin"
        }
        register("hilt") {
            id = "canaree.hilt"
            implementationClass = "HiltConventionPlugin"
        }
    }
}
