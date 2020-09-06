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

}

tasks.register("clean", Delete::class) {
    delete(buildDir)
}