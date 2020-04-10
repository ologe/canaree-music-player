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
    implementation("com.android.tools.build:gradle:3.6.2")
    implementation("org.jetbrains.kotlin:kotlin-compiler-embeddable:1.3.71")
    implementation("net.java.dev.jna:jna:4.2.2")
}

gradlePlugin {

    plugins {
        create("dynamic-flavor") {
            id = "dynamic-flavor"
            implementationClass = "DynamicFlavorPlugin"
        }
    }

}