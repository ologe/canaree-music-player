package dev.olog.data.api.deezer.artist;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class DeezerArtistResponse {

    @Expose
    public List<Album> data = new ArrayList<>();

    public static class Album {

        @Expose
        public String picture;

        @SerializedName("picture_big")
        @Expose
        public String pictureBig;

        @SerializedName("picture_medium")
        @Expose
        public String pictureMedium;

        @SerializedName("picture_small")
        @Expose
        public String pictureSmall;

        @SerializedName("picture_xl")
        @Expose
        public String pictureXl;

    }

}
