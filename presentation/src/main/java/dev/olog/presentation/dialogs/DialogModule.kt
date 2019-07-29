package dev.olog.presentation.dialogs

import dagger.Module
import dagger.android.ContributesAndroidInjector
import dev.olog.presentation.dialogs.delete.DeleteDialog
import dev.olog.presentation.dialogs.favorite.AddFavoriteDialog
import dev.olog.presentation.dialogs.play.later.PlayLaterDialog
import dev.olog.presentation.dialogs.play.next.PlayNextDialog
import dev.olog.presentation.dialogs.playlist.clear.ClearPlaylistDialog
import dev.olog.presentation.dialogs.playlist.create.NewPlaylistDialog
import dev.olog.presentation.dialogs.playlist.duplicates.RemoveDuplicatesDialog
import dev.olog.presentation.dialogs.playlist.rename.RenameDialog
import dev.olog.presentation.dialogs.ringtone.SetRingtoneDialog

@Module
abstract class DialogModule {

    @ContributesAndroidInjector
    abstract fun provideDeleteDialog(): DeleteDialog

    @ContributesAndroidInjector
    abstract fun provideAddFavoriteDialog(): AddFavoriteDialog

    @ContributesAndroidInjector
    abstract fun providePlayNextDialog(): PlayNextDialog

    @ContributesAndroidInjector
    abstract fun providePlayLaterDialog(): PlayLaterDialog

    @ContributesAndroidInjector
    abstract fun provideClearPlaylistDialog(): ClearPlaylistDialog

    @ContributesAndroidInjector
    abstract fun provideCreatePlaylistDialog(): NewPlaylistDialog

    @ContributesAndroidInjector
    abstract fun provideRemoveDuplicatesDialog(): RemoveDuplicatesDialog

    @ContributesAndroidInjector
    abstract fun provideRenametDialog(): RenameDialog

    @ContributesAndroidInjector
    abstract fun provideRenamePlaylistDialog(): RenameDialog

    @ContributesAndroidInjector
    abstract fun provideSetRingtoneDialog(): SetRingtoneDialog

}