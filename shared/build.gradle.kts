plugins {
    id(buildPlugins.javaLibrary)
    id(buildPlugins.kotlin)
}

dependencies {
    implementation(libs.kotlin)
    implementation(libs.Coroutines.core)

    implementation(libs.dagger.core)

    testImplementation(libs.Test.junit)
    testImplementation(libs.Test.mockito)
    testImplementation(libs.Test.mockitoKotlin)
    testImplementation(libs.Coroutines.test)
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
