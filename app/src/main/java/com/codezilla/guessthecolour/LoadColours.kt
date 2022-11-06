package com.codezilla.guessthecolour

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LoadColours(val DBreference:ColourDatabase) {
    val nameList= arrayListOf("BLUE","PINK","ORANGE","PURPLE","BACK","GREEN","RED","YELLOW","INDIGO","WHITE","BROWN","GRAY")
    val hexCodeList= arrayListOf("4169E1","FF1493","FFA500","8A2BE2","000000","00FF7F","FF0000","FFFF00","4B0082","FFFAFA","A0522D","778899")

    fun loadDatabase() {
        GlobalScope.launch{
            for(i in 0..11) {
                DBreference.colourDao().insertColour(Colour(0,nameList.get(i), hexCodeList.get(i)))
            }
        }
    }
}