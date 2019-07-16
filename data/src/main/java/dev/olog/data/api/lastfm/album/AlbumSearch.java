
package dev.olog.data.api.lastfm.album;

import com.google.gson.annotations.Expose;

public class AlbumSearch {

    @Expose
    public Results results;

    public static class Results {

        @Expose
        public Albummatches albummatches;
    }


}
