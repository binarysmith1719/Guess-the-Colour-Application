package com.codezilla.guessthecolour

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "colors")
class Colour (
    @PrimaryKey(autoGenerate = true)
    val id : Long,
    val name:String,
    val hexCode:String
)