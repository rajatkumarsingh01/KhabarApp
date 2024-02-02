package com.example.khabarapp.mvvm

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.khabarapp.db.ClassCovertors
import com.example.khabarapp.db.SavedArticle

@Database(entities = [SavedArticle::class], version = 1)
@TypeConverters(ClassCovertors::class)
abstract class NewsDatabase : RoomDatabase() {

    // declaring abstract reference for the interface

    abstract  val newsDao: NewsDao

    // singleton instances

    companion object{
        // THIS MAKES THE FIELD IMMEDIATELY VISIBILE TO OTHER THREAD
        @Volatile
        private var INSTANCE : NewsDatabase? = null
        fun getInstance (context: Context) : NewsDatabase{
            synchronized(this){
                var instance  = INSTANCE

                if (instance == null){

                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        NewsDatabase::class.java,
                        "news_database"


                    ).build()

                    INSTANCE = instance
                }

                return instance
            }
        }
    }


}