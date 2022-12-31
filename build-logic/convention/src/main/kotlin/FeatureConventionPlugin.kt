import org.gradle.api.Plugin
import org.gradle.api.Project

class FeatureConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply("dev.olog.msc.library")
            apply("kotlin-android-extensions")
            apply("kotlin-kapt")
            apply("dev.olog.msc.hilt")
        }
    }
}