import dev.olog.msc.LocalProperties
import dev.olog.msc.configureApp
import org.gradle.api.Plugin
import org.gradle.api.Project

class SecretsConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        val properties = LocalProperties()

        configureApp {
            defaultConfig {
                buildConfigField("String", "LAST_FM_KEY", properties["last_fm_key"].toString())
                buildConfigField("String", "LAST_FM_SECRET", properties["last_fm_secret"].toString())
                // todo remove
                buildConfigField("String", "AES_PASSWORD", properties["aes_password"].toString())
            }
        }
    }
}