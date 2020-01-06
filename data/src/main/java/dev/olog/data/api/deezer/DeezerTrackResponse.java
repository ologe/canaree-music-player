package dev.olog.data.api.deezer;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class DeezerTrackResponse {

    public List<Data> data = new ArrayList<>();

    public static class Data {

        public Album album;

        public static class Album {

            public String cover;

            @SerializedName("cover_big")
            public String coverBig;

            @SerializedName("cover_medium")
            public String coverMedium;

            @SerializedName("cover_small")
            public String coverSmall;

            @SerializedName("cover_xl")
            public String coverXl;

        }

    }

}
