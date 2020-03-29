import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import org.gradle.api.Project
import java.util.*

val Project.localProperties: Properties
    get() = gradleLocalProperties(rootDir)

val Properties.lastFmKey: String
    get() = getProperty("last_fm_key")

val Properties.lastFmSecret: String
    get() = getProperty("last_fm_secret")

val Properties.aesPassword: String
    get() = getProperty("aes_password")

val Properties.spotifyEncodedClient: String
    get() = getProperty("spotify_encoded_client")