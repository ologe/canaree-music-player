package dev.olog.presentation.fragment_edit_info

interface EditInfoFragmentView {

    fun toggleLoading(show: Boolean)
    fun showToast(message: String)

    fun setTitle(title: String)
    fun setArtist(artist: String)
    fun setAlbum(album: String)

}