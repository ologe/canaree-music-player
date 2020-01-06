package dev.olog.data.api.deezer;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class DeezerArtistResponse {

    public List<Album> data = new ArrayList<>();

    public static class Album {

        public String picture;

        @SerializedName("picture_big")
        public String pictureBig;

        @SerializedName("picture_medium")
        public String pictureMedium;

        @SerializedName("picture_small")
        public String pictureSmall;

        @SerializedName("picture_xl")
        public String pictureXl;

    }

}
