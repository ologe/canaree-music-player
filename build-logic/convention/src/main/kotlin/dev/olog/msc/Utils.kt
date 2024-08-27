package dev.olog.msc

import com.android.build.api.dsl.LibraryExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

internal fun Project.configureAndroid(block: BaseExtension.() -> Unit) {
    extensions.configure(BaseExtension::class.java, block)
}

internal fun Project.configureApp(block: BaseAppModuleExtension.() -> Unit) {
    extensions.configure(BaseAppModuleExtension::class.java, block)
}

internal fun Project.configureLibrary(block: LibraryExtension.() -> Unit) {
    extensions.configure(LibraryExtension::class.java, block)
}

internal fun Project.kotlin(block: KotlinAndroidProjectExtension.() -> Unit) {
    (this as ExtensionAware).extensions.configure("kotlin", block)
}

internal val Project.versionCatalog: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")