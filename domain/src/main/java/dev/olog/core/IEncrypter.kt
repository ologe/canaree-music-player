package dev.olog.core

interface IEncrypter {
    fun encrypt(string: String): String
    fun decrypt(string: String): String
}