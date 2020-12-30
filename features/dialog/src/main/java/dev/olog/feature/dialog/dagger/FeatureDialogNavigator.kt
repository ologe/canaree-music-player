package dev.olog.feature.dialog.dagger

import androidx.fragment.app.Fragment
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.multibindings.IntoMap
import dev.olog.feature.dialog.delete.DeleteDialog
import dev.olog.feature.dialog.favorite.AddFavoriteDialog
import dev.olog.feature.dialog.play.later.PlayLaterDialog
import dev.olog.feature.dialog.play.next.PlayNextDialog
import dev.olog.feature.dialog.playlist.clear.ClearPlaylistDialog
import dev.olog.feature.dialog.playlist.create.CreatePlaylistDialog
import dev.olog.feature.dialog.playlist.duplicates.RemovePlaylistDuplicatesDialog
import dev.olog.feature.dialog.playlist.rename.RenamePlaylistDialog
import dev.olog.feature.dialog.popup.PopupMenuFactoryImpl
import dev.olog.feature.dialog.ringtone.SetRingtoneDialog
import dev.olog.feature.dialog.sleep.timer.SleepTimerPickerDialog
import dev.olog.navigation.PopupMenuFactory
import dev.olog.navigation.dagger.FragmentScreenKey
import dev.olog.navigation.destination.FragmentScreen
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object FeatureDialogNavigator {

    @Binds
    @Singleton
    internal fun providePopupFactory(impl: PopupMenuFactoryImpl): PopupMenuFactory = impl

    @Provides
    @IntoMap
    @FragmentScreenKey(FragmentScreen.DIALOG_DELETE)
    fun provideDelete(): Fragment = DeleteDialog()

    @Provides
    @IntoMap
    @FragmentScreenKey(FragmentScreen.DIALOG_ADD_FAVORITE)
    fun provideAddFavorites(): Fragment = AddFavoriteDialog()

    @Provides
    @IntoMap
    @FragmentScreenKey(FragmentScreen.DIALOG_PLAY_LATER)
    fun providePlayLater(): Fragment = PlayLaterDialog()

    @Provides
    @IntoMap
    @FragmentScreenKey(FragmentScreen.DIALOG_PLAY_NEXT)
    fun providePlayNext(): Fragment = PlayNextDialog()

    @Provides
    @IntoMap
    @FragmentScreenKey(FragmentScreen.DIALOG_PLAYLIST_CLEAR)
    fun providePlaylistClear(): Fragment = ClearPlaylistDialog()

    @Provides
    @IntoMap
    @FragmentScreenKey(FragmentScreen.DIALOG_PLAYLIST_CREATE)
    fun providePlaylistCreate(): Fragment = CreatePlaylistDialog()

    @Provides
    @IntoMap
    @FragmentScreenKey(FragmentScreen.DIALOG_PLAYLIST_REMOVE_DUPLICATES)
    fun providePlaylistDuplicates(): Fragment = RemovePlaylistDuplicatesDialog()

    @Provides
    @IntoMap
    @FragmentScreenKey(FragmentScreen.DIALOG_PLAYLIST_RENAME)
    fun providePlaylistRename(): Fragment = RenamePlaylistDialog()

    @Provides
    @IntoMap
    @FragmentScreenKey(FragmentScreen.DIALOG_RINGTONE)
    fun provideRingtone(): Fragment = SetRingtoneDialog()

    @Provides
    @IntoMap
    @FragmentScreenKey(FragmentScreen.SLEEP_TIMER)
    fun provideSleepTimer(): Fragment = SleepTimerPickerDialog()

}