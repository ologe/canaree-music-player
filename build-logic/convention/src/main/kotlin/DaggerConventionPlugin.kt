import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class DaggerConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply("kotlin-kapt")
        }

        dependencies {
            val libs = project.extensions.getByType<VersionCatalogsExtension>().named("libs")
            add("implementation", libs.findDependency("dagger-core").get())
            add("kapt", libs.findDependency("dagger-processor").get())
            add("implementation", libs.findDependency("dagger-android-core").get())
            add("implementation", libs.findDependency("dagger-android-support").get())
            add("kapt", libs.findDependency("dagger-android-processor").get())
        }

    }
}