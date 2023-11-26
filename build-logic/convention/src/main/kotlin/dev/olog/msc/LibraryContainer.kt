package dev.olog.msc

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.getByType

internal class LibraryContainer(
    project: Project
) : DependencyHandler by project.dependencies {

    private val catalog = project.extensions.getByType<VersionCatalogsExtension>().named("libs")

    fun implementation(
        dependency: String,
        configurationAction: Action<Dependency>? = null,
    ) {
        addDependencyTo(
            configuration = "implementation",
            dependencyNotation = catalog.findLibrary(dependency).get(),
            configurationAction = configurationAction,
        )
    }

    fun kapt(
        dependency: String,
        configurationAction: Action<Dependency>? = null,
    ) {
        addDependencyTo(
            configuration = "kapt",
            dependencyNotation = catalog.findLibrary(dependency).get(),
            configurationAction = configurationAction,
        )
    }

    fun testImplementation(
        dependency: String,
        configurationAction: Action<Dependency>? = null,
    ) {
        addDependencyTo(
            configuration = "testImplementation",
            dependencyNotation = catalog.findLibrary(dependency).get(),
            configurationAction = configurationAction,
        )
    }

    private fun addDependencyTo(
        configuration: String,
        dependencyNotation: Provider<MinimalExternalModuleDependency>,
        configurationAction: Action<Dependency>?
    ) {
        if (configurationAction == null) {
            add(configuration, dependencyNotation)
            return
        }
        val dependency = create(dependencyNotation.get())
        configurationAction.execute(dependency)
        add(configuration, dependency)
    }

}

internal fun Project.libraries(block: LibraryContainer.() -> Unit) {
    block(LibraryContainer(this))
}