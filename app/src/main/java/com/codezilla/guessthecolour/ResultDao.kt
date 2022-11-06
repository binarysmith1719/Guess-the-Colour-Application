package com.codezilla.guessthecolour

import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface ResultDao {
    @Insert
    suspend fun insertResult(result:Result)

    @Update
    suspend fun updateResult(result:Result)

    @Delete
    suspend fun deleteResult(result:Result)

    @Query("SELECT * FROM results")
    fun getResults(): LiveData<List<Result>>
}