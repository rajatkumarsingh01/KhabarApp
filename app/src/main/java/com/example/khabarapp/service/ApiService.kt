package com.example.khabarapp.service

import com.example.khabarapp.Utils.Companion.API_KEY
import com.example.khabarapp.db.News
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiService {
    @GET("v2/top-headlines")
    suspend fun getBreakingNews(
        @Query("country")
        countryCode: String="us",

        @Query("page")
        pageNumber: Int=1,

        @Query("apikey")
        apikey:String=API_KEY

    ):Response<News>


    @GET("v2/everything")
    suspend fun getByCategory(
        @Query("q")
        category: String="",

        @Query("apikey")
        apikey:String=API_KEY

    ):Response<News>
}