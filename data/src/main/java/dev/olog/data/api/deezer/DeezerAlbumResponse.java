package dev.olog.data.api.deezer;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class DeezerAlbumResponse {

    @Expose
    public List<Data> data = new ArrayList<>();

    public static class Data {

        @Expose
        public String cover;

        @SerializedName("cover_big")
        @Expose
        public String coverBig;

        @SerializedName("cover_medium")
        @Expose
        public String coverMedium;

        @SerializedName("cover_small")
        @Expose
        public String coverSmall;

        @SerializedName("cover_xl")
        @Expose
        public String coverXl;

    }

}
