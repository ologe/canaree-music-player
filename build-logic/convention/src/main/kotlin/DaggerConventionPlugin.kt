import dev.olog.msc.libraries
import org.gradle.api.Plugin
import org.gradle.api.Project

class DaggerConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply("dagger.hilt.android.plugin")
            apply("kotlin-kapt")
        }

        libraries {
            implementation("hilt")
            kapt("hilt-processor")
        }
    }
}