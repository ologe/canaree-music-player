package dev.olog.msc.api.last.fm

import io.reactivex.Single
import retrofit2.http.GET

const val API_KEY = "56553f687cba2aa671c99caff536def1"

interface LastFmRest {

    @GET("?method=track.getInfo&api_key=$API_KEY&artist=cher&track=believe&format=json")
    fun getTrackInfo() : Single<TrackInfo>

}