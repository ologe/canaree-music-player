package dev.olog.msc

import dev.olog.media.MediaExposer
import dev.olog.msc.music.service.MusicService
import org.junit.Assert
import org.junit.Test

class ClassTest {

    @Test
    fun checkMusicServiceExistence(){
        Assert.assertEquals(MediaExposer.MUSIC_SERVICE, MusicService::class.java.name)
    }

}