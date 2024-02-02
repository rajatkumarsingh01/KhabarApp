package com.example.khabarapp.mvvm

import androidx.lifecycle.LiveData
import com.example.khabarapp.db.SavedArticle
import com.example.khabarapp.service.RetrofitInstance

class NewsRepo(val newsDao: NewsDao) {

    fun getAllSavedNews():LiveData<List<SavedArticle>>{
        return newsDao.getAllNews()
    }

    fun getNewsById():LiveData<SavedArticle>{
        return newsDao.getNewsById()
    }


    //getting breaking News

    suspend fun getBreakingNews(code:String,pageNumber:Int)=RetrofitInstance.api.getBreakingNews(code,pageNumber)


    //getting category news

    suspend fun getCategoryNews(code: String)=RetrofitInstance.api.getByCategory(code)

    //to delete all News
    fun deleteAll(){
        newsDao.deleteAll()
    }

    suspend fun insertNews(savedArticle: SavedArticle){
        newsDao.insertNews(savedArticle)
    }

}