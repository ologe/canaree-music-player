import dev.olog.msc.configureKotlinAndroid
import dev.olog.msc.configureLibrary
import org.gradle.api.Plugin
import org.gradle.api.Project

internal class LibraryConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply("com.android.library")
            apply("kotlin-android")
        }

        configureLibrary {
            configureKotlinAndroid(this)
        }
    }
}