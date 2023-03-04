@file:Suppress("UnstableApiUsage")

import dev.olog.msc.config
import dev.olog.msc.configureApp
import dev.olog.msc.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project

class ApplicationConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply("com.android.application")
            apply("kotlin-android")
            apply("kotlin-android-extensions")
        }

        configureApp {
            configureKotlinAndroid(this)

            defaultConfig {
                targetSdk = config.targetSdkVersion
                versionCode = config.versionCode
                versionName = config.versionName
            }

            bundle {
                language.enableSplit = true
                density.enableSplit = true
                abi.enableSplit = true
            }

            lint {
                isCheckReleaseBuilds = false
                disable.add("MissingTranslation")
            }
        }
    }
}