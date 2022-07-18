package dev.olog.msc

import com.android.build.api.dsl.CommonExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions

fun Project.configureLibrary(block: LibraryExtension.() -> Unit) {
    extensions.configure(LibraryExtension::class.java, block)
}

fun Project.configureApp(block: BaseAppModuleExtension.() -> Unit) {
    extensions.configure(BaseAppModuleExtension::class.java, block)
}

fun CommonExtension<*, *, *, *>.kotlinOptions(block: KotlinJvmOptions.() -> Unit) {
    (this as ExtensionAware).extensions.configure("kotlinOptions", block)
}