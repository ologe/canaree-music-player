import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {

    repositories {
        google()
        jcenter()
    }

    dependencies {
        classpath(buildPlugins.classpath.android)
        classpath(buildPlugins.classpath.kotlin)
        classpath(buildPlugins.classpath.gms)
        classpath(buildPlugins.classpath.crashlytics)
        classpath(buildPlugins.classpath.hilt)
    }

}

allprojects {

    repositories {
        google()
        jcenter()
        maven { setUrl("https://jitpack.io") }
        maven { setUrl("https://dl.bintray.com/ijabz/maven") } // jaudiotagger
    }

    gradle.projectsEvaluated { // TODO remove after coroutines became stable
        tasks.withType(KotlinCompile::class).all {
            kotlinOptions.freeCompilerArgs += listOf(
                "-Xuse-experimental=kotlinx.coroutines.FlowPreview",
                "-Xuse-experimental=kotlinx.coroutines.ExperimentalCoroutinesApi"
            )
            kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
        }
    }

}

tasks.register("clean", Delete::class) {
    delete(buildDir)
}