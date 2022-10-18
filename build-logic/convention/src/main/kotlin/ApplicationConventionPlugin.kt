@file:Suppress("UnstableApiUsage")

import dev.olog.msc.LocalProperties
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
        }

        val localProperties = LocalProperties()

        configureApp {
            configureKotlinAndroid(this)

            defaultConfig {
                versionCode = config.versionCode
                versionName = config.versionName

                buildConfigField("String", "LAST_FM_KEY", localProperties["last_fm_key"] as String)
                buildConfigField("String", "LAST_FM_SECRET", localProperties["last_fm_secret"] as String)
                buildConfigField("String", "AES_PASSWORD", localProperties["aes_password"] as String)
            }

            bundle {
                language.enableSplit = true
                density.enableSplit = true
                abi.enableSplit = true
            }

            lint {
                checkDependencies = true
                checkReleaseBuilds = false
                disable.add("MissingTranslation")
            }

            buildFeatures {
                buildConfig = true
            }

        }
    }
}