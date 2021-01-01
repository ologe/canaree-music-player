package dev.olog.domain

interface IEncrypter {
    fun encrypt(string: String): String
    fun decrypt(string: String): String
}