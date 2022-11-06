package com.codezilla.guessthecolour

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "results")
class Result (
    @PrimaryKey(autoGenerate = true)
    val id : Long,
    val YourAns:String,
    val CorrectAns:String
)