
package dev.olog.data.api.lastfm.album;

import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;

public class AlbumSearch {

    @Expose
    @Nullable
    public Results results;

    public static class Results {

        @Expose
        @Nullable
        public Albummatches albummatches;
    }


}
