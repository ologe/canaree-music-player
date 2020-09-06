repositories {
    jcenter()
    mavenCentral()
    google()
}

plugins {
    `kotlin-dsl`
}

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}

dependencies {
    implementation("com.android.tools.build:gradle:4.2.0-alpha09")
    implementation("org.jetbrains.kotlin:kotlin-compiler-embeddable:1.3.71")
}