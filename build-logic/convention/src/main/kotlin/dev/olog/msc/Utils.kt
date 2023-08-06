package dev.olog.msc

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions

internal fun Project.configureApp(action: Action<BaseAppModuleExtension>) {
    extensions.configure(BaseAppModuleExtension::class.java, action)
}

internal fun Project.configureLibrary(action: Action<LibraryExtension>) {
    extensions.configure(LibraryExtension::class.java, action)
}

internal fun CommonExtension<*, *, *, *>.kotlinOptions(action: Action<KotlinJvmOptions>) {
    (this as ExtensionAware).extensions.configure("kotlinOptions", action)
}