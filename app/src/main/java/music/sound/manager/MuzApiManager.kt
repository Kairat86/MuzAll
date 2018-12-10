package music.sound.manager

import music.sound.BuildConfig
import music.sound.model.MuzResponse
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class MuzApiManager @Inject constructor() : ApiManager {

    companion object {
        private val TAG = MuzApiManager::class.java.simpleName
        private const val SERVER =
            "http://api.soundcloud.com/"
        private const val PATH = "tracks/?limit=25&client_id=${BuildConfig.SOUND_CLOUD_ID}"
    }

    private var apiService: APIService

    init {
        apiService = Retrofit.Builder()
            .client(OkHttpClient.Builder().readTimeout(14, TimeUnit.SECONDS).build())
            .baseUrl(SERVER)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(APIService::class.java)
    }

    override fun search(q: String, offset: Int, callback: Callback<MuzResponse>) =
        apiService.search(q, offset).enqueue(callback)

    override fun getPopular(offset: Int, callback: Callback<MuzResponse>) =
        apiService.getPopular(offset).enqueue(callback)

    private interface APIService {

        @GET(PATH)
        fun search(@Query("search") q: String, @Query("offset") offset: Int): Call<MuzResponse>

        @GET("$PATH&order=popularity_month")
        fun getPopular(@Query("offset") offset: Int): Call<MuzResponse>
    }
}