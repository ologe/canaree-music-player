package dev.olog.msc

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.project

internal class LibrariesContainer(
    project: Project
) {

    private val catalog = project.extensions.getByType<VersionCatalogsExtension>().named("libs")
    private val scope = DependencyHandlerScope.of(project.dependencies)

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

    fun debugImplementation(
        dependency: String,
        configurationAction: Action<Dependency>? = null,
    ) {
        addDependencyTo(
            configuration = "debugImplementation",
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

    fun androidTestImplementation(
        dependency: String,
        configurationAction: Action<Dependency>? = null,
    ) {
        addDependencyTo(
            configuration = "androidTestImplementation",
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

    fun kaptAndroidTest(
        dependency: String,
        configurationAction: Action<Dependency>? = null,
    ) {
        addDependencyTo(
            configuration = "kaptAndroidTest",
            dependencyNotation = catalog.findLibrary(dependency).get(),
            configurationAction = configurationAction,
        )
    }

    fun add(
        configurationName: String,
        dependency: String,
        configurationAction: Action<Dependency>? = null,
    ) {
        addDependencyTo(
            configuration = configurationName,
            dependencyNotation = catalog.findLibrary(dependency).get(),
            configurationAction = configurationAction,
        )
    }

    @Suppress("UNCHECKED_CAST")
    private fun addDependencyTo(
        configuration: String,
        dependencyNotation: Provider<MinimalExternalModuleDependency>,
        configurationAction: Action<Dependency>?
    ) {
        if (configurationAction == null) {
            scope.add(configuration, dependencyNotation)
            return
        }
        val dependency = scope.create(dependencyNotation.get())
        configurationAction.execute(dependency)
        scope.add(configuration, dependency)
    }

}

internal fun Project.libraries(block: LibrariesContainer.() -> Unit) {
    block(LibrariesContainer(this))
}