package com.codezilla.guessthecolour

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ColourDao {
    @Insert
    suspend fun insertColour(colour:Colour)

    @Update
    suspend fun updateColour(colour:Colour)

    @Delete
    suspend fun deleteColour(colour:Colour)

    @Query("SELECT * FROM colors")
    fun getColours():LiveData<List<Colour>>
}