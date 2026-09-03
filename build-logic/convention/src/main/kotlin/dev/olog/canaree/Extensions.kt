package dev.olog.canaree

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.BuildFeatures
import com.android.build.api.dsl.BuildType
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.CompileOptions
import com.android.build.api.dsl.DefaultConfig
import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.dsl.Lint
import com.android.build.api.dsl.TestOptions
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

internal fun Project.kotlin(block: Action<KotlinAndroidProjectExtension>) {
    (this as ExtensionAware).extensions.configure("kotlin", block)
}

internal fun CommonExtension.defaultConfig(action: DefaultConfig.() -> Unit) {
    when (this) {
        is LibraryExtension -> defaultConfig(action)
        is ApplicationExtension -> defaultConfig(action)
        else -> error("unknown extension ${this::class.java.name}")
    }
}

internal fun CommonExtension.compileOptions(action: CompileOptions.() -> Unit) {
    when (this) {
        is LibraryExtension -> compileOptions(action)
        is ApplicationExtension -> compileOptions(action)
        else -> error("unknown extension ${this::class.java.name}")
    }
}

internal fun CommonExtension.testOptions(action: TestOptions.() -> Unit) {
    when (this) {
        is LibraryExtension -> testOptions(action)
        is ApplicationExtension -> testOptions(action)
        else -> error("unknown extension ${this::class.java.name}")
    }
}

internal fun CommonExtension.lint(action: Lint.() -> Unit) {
    when (this) {
        is LibraryExtension -> lint(action)
        is ApplicationExtension -> lint(action)
        else -> error("unknown extension ${this::class.java.name}")
    }
}

internal fun CommonExtension.buildTypes(action: NamedDomainObjectContainer<out BuildType>.() -> Unit) {
    when (this) {
        is LibraryExtension -> buildTypes(action)
        is ApplicationExtension -> buildTypes(action)
        else -> error("unknown extension ${this::class.java.name}")
    }
}

internal fun CommonExtension.buildFeatures(action: BuildFeatures.() -> Unit) {
    when (this) {
        is LibraryExtension -> buildFeatures(action)
        is ApplicationExtension -> buildFeatures(action)
        else -> error("unknown extension ${this::class.java.name}")
    }
}