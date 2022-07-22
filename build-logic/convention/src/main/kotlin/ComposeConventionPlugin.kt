@file:Suppress("UnstableApiUsage")

import dev.olog.msc.configureLibrary
import dev.olog.msc.kotlinOptions
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

            kotlinOptions {
                freeCompilerArgs = freeCompilerArgs + listOf(
                    "-opt-in=com.google.accompanist.pager.ExperimentalPagerApi",
                    "-opt-in=androidx.compose.animation.ExperimentalAnimationApi",
                    "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi",
                    "-opt-in=androidx.compose.material.ExperimentalMaterialApi",
                )
            }

            libraries {
                implementation("compose-ui")
                debugImplementation("compose-tooling")
                implementation("compose-tooling-preview")
                implementation("compose-material")
                implementation("compose-activity")
                implementation("compose-icons")
                implementation("compose-constraintLayout")
                implementation("compose-pager")
                implementation("compose-pager-indicator")
            }
        }
    }
}