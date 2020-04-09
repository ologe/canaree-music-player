plugins {
    id(BuildPlugins.javaLibrary)
    id(BuildPlugins.kotlin)
}

dependencies {
    compileOnly(Libraries.kotlin)

    compileOnly(Libraries.Lint.core)
    compileOnly(Libraries.Lint.checks)

    testImplementation(Libraries.Test.junit)
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