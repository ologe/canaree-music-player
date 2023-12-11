import dev.olog.msc.configureLibrary
import dev.olog.msc.libraries
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

class ComposeConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        configureLibrary {
            buildFeatures {
                compose = true
            }

            val catalog = extensions.getByType<VersionCatalogsExtension>().named("libs")
            composeOptions {
                kotlinCompilerExtensionVersion = catalog.findVersion("compose-compiler").get().toString()
            }

            libraries {
                add("implementation", platform(findLibrary("compose-bom")))
                implementation("compose-ui")
                implementation("compose-foundation")
                implementation("compose-viewbinding")
                debugImplementation("compose-tooling")
                implementation("compose-tooling-preview")
                implementation("compose-accompanist-drawablepainter")
            }
        }
    }
}