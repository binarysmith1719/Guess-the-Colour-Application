package com.codezilla.guessthecolour

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Colour::class], version = 1)
abstract class ColourDatabase : RoomDatabase(){
   abstract fun colourDao():ColourDao

   companion object
   {
       @Volatile
       private var INSTANCE: ColourDatabase? = null

       fun getDatabase(context:Context):ColourDatabase{
           if(INSTANCE==null)
           {
               synchronized(this)
               {
                   INSTANCE=Room.databaseBuilder(context.applicationContext,ColourDatabase::class.java,"DBofColors").build()
               }
           }
           return INSTANCE!!
       }

   }
}