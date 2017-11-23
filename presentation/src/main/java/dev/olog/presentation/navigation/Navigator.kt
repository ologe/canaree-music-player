package dev.olog.presentation.navigation

interface Navigator {

    fun toMainActivity()

    fun toDetailActivity(mediaId: String)

}