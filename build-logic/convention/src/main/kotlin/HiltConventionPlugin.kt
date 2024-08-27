import dev.olog.msc.versionCatalog
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

internal class HiltConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply("kotlin-kapt") // TODO migrate to ksp
            apply("dagger.hilt.android.plugin")
        }


        val catalog = versionCatalog
        dependencies {
            add("implementation", catalog.findLibrary("hilt").get())
            add("kapt", catalog.findLibrary("hilt-compiler").get())
        }
    }
}