import dev.olog.msc.libraries
import org.gradle.api.Plugin
import org.gradle.api.Project

class HiltConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply("kotlin-kapt")
            apply("dagger.hilt.android.plugin")
        }

        libraries {
            implementation("hilt-core")
            kapt("hilt-compiler")
        }
    }
}