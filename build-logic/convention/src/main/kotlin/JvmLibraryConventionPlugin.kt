import dev.olog.msc.config
import dev.olog.msc.libraries
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile

class JvmLibraryConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply("kotlin")
        }

        tasks.withType(JavaCompile::class.java) {
            sourceCompatibility = config.javaVersion.toString()
            targetCompatibility = config.javaVersion.toString()
        }

        libraries {
            implementation("kotlin")
            implementation("coroutines")
        }
    }
}