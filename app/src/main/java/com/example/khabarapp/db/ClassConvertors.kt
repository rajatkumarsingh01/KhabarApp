package com.example.khabarapp.db

import androidx.room.TypeConverter

class ClassCovertors {

    @TypeConverter
    fun fromSource(source: Source):String{
        return source.name!!

    }
    @TypeConverter
    fun toSource(name:String):Source{
        return Source(name,name)
    }
}