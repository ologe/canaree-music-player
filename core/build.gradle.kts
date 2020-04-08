plugins {
    id(BuildPlugins.androidLibrary)
    id(BuildPlugins.kotlinAndroid)
}

android {
    applyDefaults()
}

dependencies {
    lintChecks(project(":lint"))

    implementation(Libraries.kotlin)
    implementation(Libraries.Coroutines.core)

    implementation(Libraries.Dagger.core)

    implementation(Libraries.X.core)
    implementation(Libraries.X.material)
    implementation(Libraries.X.preference)
    implementation(Libraries.X.fragments)
    implementation(Libraries.X.Lifecycle.java8)

    testImplementation(Libraries.Test.junit)
    testImplementation(Libraries.Test.mockito)
    testImplementation(Libraries.Test.mockitoKotlin)
    testImplementation(Libraries.Coroutines.test)
}
