package dev.olog.injection

import com.tozny.crypto.android.AesCbcWithIntegrity
import javax.inject.Inject
import javax.inject.Singleton

private const val PASSWORD = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgjEHhgXIywB+vz6d7ljbp2bi3Fx9jQpG/Yg0q2BePJOYYd7pWA97fY1jyrt5l/+k//FeFJoovwunQeoVJmzefKBFSgnn/+JWz5diW0uFPm9l8GgU70lMboqQ9nVPz7t0gYa9p8JimRFd1rpCaHCs6LOcQ9Odg5YIjCJBudlFCH6e0TCFpdw3HuzUR+4jjOCB3lS3R4e8K4hXJqg4BbCM+gN9F0IbxnFep8/TSZFseSfMf3ZUp7PTP64N4wnlNuQ7MBkOBIrcl2hbPuYb5/QmnSicgVqBrISB5qX9AmHjc6eaSUjl153rg4m5ulW9L/NaYefwiWMBIQPzym6Y6g7x+QIDAQAB"
private val SALT = byteArrayOf(
        -46, 65, 30, -128, -103, -57, 74, -64, 51, 88, -95,
        -45, 77, -117, -36, -113, -11, 32, -64, 89
)

@Singleton
class LastFmEncrypter @Inject constructor() {

    private val key by lazy { AesCbcWithIntegrity.generateKeyFromPassword(
        PASSWORD,
        SALT
    ) }

    fun encrypt(string: String): String {
        if (string.isNotBlank()){
            val cipher = AesCbcWithIntegrity.encrypt(string, key)
            return cipher.toString()
        } else {
            return string
        }

    }

    fun decrypt(string: String): String{
        if (string.isNotBlank()){
            val cipher = AesCbcWithIntegrity.CipherTextIvMac(string)
            return AesCbcWithIntegrity.decryptString(cipher, key)
        } else {
            return string
        }

    }

}