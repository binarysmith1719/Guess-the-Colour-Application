package com.codezilla.guessthecolour

import android.content.Context
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Room
import androidx.room.RoomDatabase
import com.codezilla.guessthecolour.Result

@Database(entities = [Result::class],version=1)
abstract class ResultDatabase:RoomDatabase(){
    abstract fun resultDao():ResultDao

    companion object
    {
        @Volatile
        private var INSTANCE:ResultDatabase?=null
        fun getDatabase(context: Context):ResultDatabase {
            if(INSTANCE==null)
            {
                synchronized(this){
                    INSTANCE=Room.databaseBuilder(context.applicationContext,ResultDatabase::class.java,"resultsDB").build()
                }
            }
            return INSTANCE!!
        }
    }

}