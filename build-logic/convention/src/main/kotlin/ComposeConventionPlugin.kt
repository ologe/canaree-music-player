import dev.olog.msc.configureAndroid
import dev.olog.msc.versionCatalog
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class ComposeConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply("org.jetbrains.kotlin.plugin.compose")
        }

        configureAndroid {
            buildFeatures.compose = true
        }

        val catalog = versionCatalog
        dependencies {
            add("implementation", platform(catalog.findLibrary("compose-bom").get()))
            add("implementation", catalog.findLibrary("compose-foundation").get())
            add("implementation", catalog.findLibrary("compose-material-icons").get())
            add("debugImplementation", catalog.findLibrary("compose-tooling").get())
            add("implementation", catalog.findLibrary("compose-tooling-preview").get())
            add("debugImplementation", catalog.findLibrary("compose-pooling-container").get())
            add("implementation", catalog.findLibrary("compose-drawablepainter").get())
            add("implementation", catalog.findLibrary("compose-shapes").get())
        }
    }
}