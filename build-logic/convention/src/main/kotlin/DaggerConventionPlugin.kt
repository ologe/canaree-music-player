import dev.olog.msc.libraries
import org.gradle.api.Plugin
import org.gradle.api.Project

class DaggerConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply("kotlin-kapt")
        }

        libraries {
            implementation("dagger")
            kapt("dagger-processor")
            implementation("dagger-android")
            implementation("dagger-android-support")
            kapt("dagger-android-processor")
        }
    }
}