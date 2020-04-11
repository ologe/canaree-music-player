package dev.olog.lib.audio.tagger

import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.audio.exceptions.CannotReadException
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException
import org.jaudiotagger.tag.*
import timber.log.Timber
import java.io.File
import java.io.IOException
import javax.inject.Inject

internal class AudioTaggerImpl @Inject constructor(

) : AudioTagger {

    private val yerRegex = "\\d{4}".toRegex()


    init {
        TagOptionSingleton.getInstance().isAndroid = true
    }

    override fun canBeHandled(file: File): Boolean {
        try {
            AudioFileIO.read(file)
            return true
        } catch (cannotRead: CannotReadException) {
            Timber.d(cannotRead)
            return false
        } catch (io: IOException) {
            Timber.d(io)
            return false
        } catch (readOnly: ReadOnlyFileException) {
            Timber.d(readOnly)
            return false
        } catch (invalid: InvalidAudioFrameException) {
            Timber.d(invalid)
            return false
        }
    }

    override fun read(file: File): Tags {
        val audioFile = AudioFileIO.read(file)
        val tag = audioFile.tagOrCreateAndSetDefault
        val audioHeader = audioFile.audioHeader

        return Tags(
            title = tag.getMetadata(FieldKey.TITLE),
            artist = tag.getMetadata(FieldKey.ARTIST),
            album = tag.getMetadata(FieldKey.ALBUM),
            albumArtist = tag.getMetadata(FieldKey.ALBUM_ARTIST),
            genre = tag.getMetadata(FieldKey.GENRE),
            discNo = tag.getMetadata(FieldKey.DISC_NO),
            trackNo = tag.getMetadata(FieldKey.TRACK),
            year = tag.getYear(),
            bpm = tag.getMetadata(FieldKey.BPM),
            format = audioHeader.format,
            sampling = "${audioHeader.sampleRate} Hz",
            bitrate = "${audioHeader.bitRate} kb/s"
        )
    }

    override fun save(file: File, tags: Tags) {
        val audioFile = AudioFileIO.read(file)
        val tag = audioFile.tag

        tags.title?.let { tag.setMetadata(file, FieldKey.TITLE, it) }
        tags.artist?.let { tag.setMetadata(file, FieldKey.ARTIST, it) }
        tags.album?.let { tag.setMetadata(file, FieldKey.ALBUM, it) }
        tags.albumArtist?.let { tag.setMetadata(file, FieldKey.ALBUM_ARTIST, it) }
        tags.genre?.let { tag.setMetadata(file, FieldKey.GENRE, it) }
        tags.year?.let { tag.setMetadata(file, FieldKey.YEAR, it) }
        tags.discNo?.let { tag.setMetadata(file, FieldKey.DISC_NO, it) }
        tags.trackNo?.let { tag.setMetadata(file, FieldKey.TRACK, it) }

        audioFile.commit()
    }

    private fun Tag.getMetadata(key: FieldKey): String? {
        try {
            return getFirst(key)
        } catch (ex: KeyNotFoundException) {
            Timber.d(ex)
            return null
        }
    }

    private fun Tag.setMetadata(file: File, key: FieldKey, value: String) {
        try {
            setField(key, value)
        } catch (notKey: KeyNotFoundException) {
            // cannot do anything here
        } catch (invalidData: FieldDataInvalidException) {
            Timber.e(invalidData, "file extension ${file.extension}")
        }
    }

    // sometimes `FieldKey.YEAR` contains the date, not the year
    private fun Tag.getYear(): String? {
        val year = getMetadata(FieldKey.YEAR) ?: return null
        return yerRegex.find(year)?.value
    }

}