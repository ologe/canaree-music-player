
package dev.olog.msc.api.last.fm.album.info;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AlbumInfo {

    @SerializedName("album")
    @Expose
    public Album album;

    /**
     * No args constructor for use in serialization
     * 
     */
    public AlbumInfo() {
    }

    /**
     * 
     * @param album
     */
    public AlbumInfo(Album album) {
        super();
        this.album = album;
    }

    @Override
    public String toString() {
        return "AlbumInfo{" +
                "album=" + album +
                '}';
    }
}
