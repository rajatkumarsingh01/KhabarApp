package com.example.khabarapp.service
import com.example.khabarapp.Utils.Companion.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {

    companion object{
        private val retrofit by lazy {
            //lazy means to intialize it once
            //to log response of retrofit

            val loggingInterceptor=HttpLoggingInterceptor()
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

            val client=OkHttpClient.Builder().addInterceptor(loggingInterceptor).build()

            Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

        }

        val api by lazy {
            retrofit.create(ApiService::class.java)
        }
    }
}