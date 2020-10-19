package z.music.manager

import android.util.Log
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.http.GET
import retrofit2.http.Query
import z.music.model.CollectionHolder
import z.music.model.Token
import z.music.model.Track
import z.music.model.TrackList
import javax.inject.Inject


class MuzApiManager @Inject constructor(private val apiService: APIService) : ApiManager {

    companion object {
        private val TAG = MuzApiManager::class.java.simpleName
        private const val SEARCH = "search?limit=25&client_id="
        private const val TOP = "top"
        private const val TRACKS = "tracks?client_id="
        private const val HELLO = "hello"
        private const val AUTH = "auth"
    }

    override fun search(q: String, offset: Int, callback: Callback<CollectionHolder<Track>>) =
        apiService.search(q, offset).enqueue(callback)

    override fun getTop(token: String, page: Int): Single<TrackList> {
        Log.i(TAG, "page=>$page")
      return  apiService.getTop(token, page).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun tracksBy(ids: String?, callback: Callback<List<Track>>) =
        apiService.tracksBy(ids).enqueue(callback)

    override fun getToken() =
        apiService.getToken().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

    override fun getAccessToken(t: String, hash: String) =
        apiService.getAccessToken(t, hash).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())


    interface APIService {

        @GET(SEARCH)
        fun search(
            @Query("q") q: String,
            @Query("offset") offset: Int
        ): Call<CollectionHolder<Track>>

        @GET(TOP)
        fun getTop(
            @Query("access_token") token: String,
            @Query("page") page: Int
        ): Single<TrackList>

        @GET(TRACKS)
        fun tracksBy(@Query("ids") ids: String?): Call<List<Track>>

        @GET(HELLO)
        fun getToken(): Single<Token>

        @GET(AUTH)
        fun getAccessToken(@Query("code") code: String, @Query("hash") hash: String): Single<Token>
    }
}