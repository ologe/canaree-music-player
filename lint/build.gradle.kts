plugins {
    id(buildPlugins.javaLibrary)
    id(buildPlugins.kotlin)
}

dependencies {
    compileOnly(libs.kotlin)

    compileOnly(libs.Lint.core)
    compileOnly(libs.Lint.checks)

    testImplementation(libs.Test.junit)
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.getting(Jar::class) {
    manifest {
        attributes["Lint-Registry"] = "dev.olog.lint.CustomLintRegistry"
    }
}