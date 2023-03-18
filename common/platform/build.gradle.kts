plugins {
    id("dev.msc.library")
    id("dev.msc.hilt")
}

dependencies {
    implementation(projects.common.localization)
    implementation(projects.common.shared)

    implementation(libs.androidx.fragments)
    implementation(libs.androidx.material)

    implementation(libs.androidx.lifecycle.process)
}