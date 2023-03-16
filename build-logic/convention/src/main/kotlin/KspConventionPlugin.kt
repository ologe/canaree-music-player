import com.android.build.gradle.api.BaseVariant
import dev.olog.msc.configureApp
import dev.olog.msc.configureLibrary
import org.gradle.api.DomainObjectSet
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension

class KspConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        pluginManager.apply("com.google.devtools.ksp")

        pluginManager.withPlugin("dev.msc.app") {
            configureApp {
                configureSourceSets(applicationVariants)
            }
        }

        pluginManager.withPlugin("dev.msc.library") {
            configureLibrary {
                configureSourceSets(libraryVariants)
            }
        }

    }

    private fun Project.configureSourceSets(variants: DomainObjectSet<out BaseVariant>) {
        variants.configureEach {
            kotlinExtension.sourceSets.configureEach {
                kotlin.srcDir("$buildDir/generated/ksp/$name/kotlin/")
            }
        }
    }

}