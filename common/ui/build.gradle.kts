plugins {
    id("dev.msc.library")
}

dependencies {
    implementation(projects.common.shared)
    implementation(projects.common.platform)

    implementation(libs.androidx.palette)
    implementation(libs.androidx.preference)
}