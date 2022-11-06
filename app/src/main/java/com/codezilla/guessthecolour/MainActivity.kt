package com.codezilla.guessthecolour

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.green
import androidx.lifecycle.Observer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Collections.shuffle


class MainActivity : AppCompatActivity() {
    var colorlist:ArrayList<Colour> = ArrayList<Colour>()
    var resultlist:ArrayList<Result> = ArrayList<Result>()
    lateinit var colourDatabase:ColourDatabase
    lateinit var resultDatabase: ResultDatabase
    val MAX_COLOURS:Int=12 //Or WE CAN SAY LEVELS
    var colourPointer:Int=0

    //CURRENT COLOUR ATTRIBUTES
    lateinit var colourName:String
    var colourNameLength:Int=0

    //DECLARING VIEW VARIABLES-------------------------
     var txtViewList:ArrayList<TextView> =ArrayList<TextView>()
     lateinit var imageMain:ImageView
     lateinit var linearView1:LinearLayout
     lateinit var linearView2:LinearLayout
     lateinit var sheetLinearView:LinearLayout
     var relativelyt:RelativeLayout?=null
    //-------------------------------------------------

    //VIEW VARIABLES AS PER USER INPUT-----------------
    var inputList:ArrayList<View> =ArrayList<View>()
    var INPUT_COUNT:Int=0
    lateinit var ANSWER:String
    //-------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val actionBar:ActionBar?
        actionBar = supportActionBar
        val colorDrawable=ColorDrawable(Color.parseColor("#148485"))
        actionBar!!.setBackgroundDrawable(colorDrawable)
        initiateViews()

        //GETTING COLOUR DATABASE INSTANCE
        colourDatabase = ColourDatabase.getDatabase(this)

        //SHARED PREFERENCE TO ADD THE COLOR DATA ONLY ONCE
        val sharedPref = getSharedPreferences("ADDER", MODE_PRIVATE)
        val flag:Boolean=sharedPref.getBoolean("ADD_AGAIN",true)
        if(flag)
        {val loadColours:LoadColours= LoadColours(colourDatabase)
         loadColours.loadDatabase()
         val editor=sharedPref.edit()
         editor.putBoolean("ADD_AGAIN",false)
         editor.apply()}
        //GETTING RESULT DATABASE INSTANCE
        resultDatabase = ResultDatabase.getDatabase(this)

        var clr:Colour?=null
        var lock:Int=0
        //OBSERVING THE COLOUR DATABASE
        colourDatabase.colourDao().getColours().observe(this, Observer{
                 colorlist.removeAll(colorlist)
                 colorlist.addAll(it)
                 clr=colorlist.get(colourPointer) //COLOUR POINTER IS INITIALLY ZERO {0}
                if(lock==0)
                {   setColourAttributes(clr!!)
                    setColourAndKeywords(clr!!)
                    setAnswerSheet(clr!!)
                    ANSWER=""
                    lock=1 }
        })
        var nextLock=0
        //OBSERVING THE RESULT DATABASE
        resultDatabase.resultDao().getResults().observe(this, Observer{
            resultlist.removeAll(resultlist)
            resultlist.addAll(it)
            if(nextLock==1)
            {updateColour()}
            nextLock=1
        })
    }

    private fun setColourAttributes(clr: Colour) {
        colourName=clr.name
        colourNameLength=clr.name.length
    }

    //SETTING UP THE MAIN COLOUR AND KEYWORDS TO BE PRESSED
    private fun setColourAndKeywords(clr:Colour) {
        //SETTING UP THE COLOUR IN THE VIEW
        imageMain.setColorFilter(Color.parseColor("#${clr.hexCode}"), PorterDuff.Mode.SRC_IN)
        var name: String = colourName
        var len = colourNameLength //length of the colour name
        var remLen = 12-len // length(12-len) of remaining keywords for the keyboard
        var last = len-1
        var rest: String = ""
        for (it in 'A'..'Z') {
            var flag: Int = 0
            for (x in 0..last) {
                if (name[x].equals(it))
                    flag = 1
            }
            if (flag == 0) {
                rest += it
            }
        }
        var insert: String = ""
        for (i in 1..remLen) {
                insert += rest.random()
        }
        //GIVING RANDOM INDEX TO THE COLOUR NAME ALPHABETS
        val s: MutableSet<Int> = mutableSetOf()
        while (s.size < len) { s.add((0..11).random()) }
        val myRandomValues = s.toList()
        var count = 0
        var array= arrayOf("0","0","0","0","0","0","0","0","0","0","0","0")
        myRandomValues.forEach {
                    if(it < 12)
                    {array[it] = name.get(count).toString()}
                    count++
                }
        count = 0
        var i = 0
        for (c in array) {
            if (c.equals("0")) {
                array[i] = insert.get(count).toString()
                count++
            }
            i+=1
        }

        try {
            count = 0
            for (it in array) {
                var tv: TextView = txtViewList.get(count) as TextView
                tv.setText(it)
                count++
            }
        } catch (e:IndexOutOfBoundsException) {}
    }

    //LOADING THE ANSWER SCREEN
    private fun setAnswerSheet(clr:Colour)
    {
        for(i in 1..colourNameLength) {
            val inflater= getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var relativeInputView:View=inflater.inflate(R.layout.relative_input,null)
            inputList.add(relativeInputView)
            sheetLinearView.addView(relativeInputView, sheetLinearView.childCount)
        }
    }

    //CALLED ON CLICKING THE KEYS
    fun clicked(view: View)
    {
        var parentView:View=view.parent as View
        var mainView:View=parentView.parent as View
        var Id:Int=mainView.id

        if(INPUT_COUNT<colourNameLength) {

            //GETTING THE KEY OUT OF VIEW
            var txtInput:TextView=view as TextView
            val inputText:String= txtInput.text.toString()

            //REMOVING THE KEY
            if(Id==R.id.llview1)
            {linearView1.removeView(parentView)}
            else{linearView2.removeView(parentView)}

            //UPDATING THE ANSWER TEXT
            var view: View = inputList.get(INPUT_COUNT) as View
            var textInput: TextView = view.findViewById<TextView>(R.id.txt_input)
            textInput.setText(inputText)
            var textHyphen: TextView = view.findViewById<TextView>(R.id.txt_hyphen)
            textHyphen.setText("_")

            //UPADATING INPUT COUNT AND ANSWER
            ANSWER+=inputText
            INPUT_COUNT++

            if(INPUT_COUNT==colourNameLength)
            { insertResut()}
        }
    }

    //INSERTING RESULT
    private fun insertResut() {
      val result=Result(0,ANSWER,colourName)
        GlobalScope.launch{
                resultDatabase.resultDao().insertResult(result)
        }
    }

    //UPDATE COLOUR
    fun updateColour()
    {
        if(ANSWER.equals(colourName))
        { Toast.makeText(this@MainActivity,"CORRECT",Toast.LENGTH_SHORT).show() }
        else
        { Toast.makeText(this@MainActivity,"WRONG",Toast.LENGTH_SHORT).show()
            colourPointer-- }

        if(colourPointer<MAX_COLOURS-1) {
            inputList.removeAll(inputList)
            txtViewList.removeAll(txtViewList)
            sheetLinearView.removeAllViews()
            linearView1.removeAllViews()
            linearView2.removeAllViews()
            ResetKeywordView()

            colourPointer++
            var clr:Colour=colorlist.get(colourPointer)
            setColourAttributes(clr)
            setColourAndKeywords(clr)
            setAnswerSheet(clr)

            INPUT_COUNT=0
            ANSWER = ""
        }
        else{  Toast.makeText(this@MainActivity,"GAME OVER",Toast.LENGTH_SHORT).show() }
    }

    private fun initiateViews() {
        linearView1=findViewById(R.id.llview1)
        linearView2=findViewById(R.id.llview2)
        sheetLinearView=findViewById(R.id.llInput)
        relativelyt=findViewById(R.id.rlclr)
        imageMain=findViewById(R.id.img_clr)
        ResetKeywordView()
    }

    fun ResetKeywordView() //RESETING THE DELETED KEYWORDS
    {

        for(i in 1..6) {

            val inflater= getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var keyRlView:View=inflater.inflate(R.layout.keyword,null) as RelativeLayout
            txtViewList.add(keyRlView.findViewById(R.id.txt_key))
            linearView1.addView(keyRlView, linearView1.childCount)
        }
        for(i in 1..6) {
            val inflater= getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var keyRlView:View=inflater.inflate(R.layout.keyword,null) as RelativeLayout
            txtViewList.add(keyRlView.findViewById(R.id.txt_key))
            linearView2.addView(keyRlView, linearView2.childCount)
        }
    }

    //MENU
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        var menuInflater=getMenuInflater().inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val intent = Intent(this@MainActivity,ResultActivity::class.java)
        var list:ArrayList<String> = ArrayList<String>()
        resultlist.forEach {
            list.add(it.CorrectAns)
            list.add(it.YourAns)
        }
        intent.putExtra("mylist",list)
        startActivity(intent)
        return true
    }

    //ON HINT BUTTON CLICK
    fun hint(view:View)
    {
     var mutableList=mutableListOf<String>()
        var firstEle= colourName.get(0)

        var strRest=""
        for(s in 1..(colourNameLength-1))
        { strRest+=colourName.get(s).toString() }

        for(s in 0..(colourNameLength-2))
        { mutableList.add(strRest.get(s).toString()) }
        shuffle(mutableList)

        strRest=""
        mutableList.forEach { strRest+=it }
        strRest+=firstEle
        Toast.makeText(this,"${strRest}",Toast.LENGTH_SHORT).show()
        for(it in 0..(colourNameLength-1)) {
            var view: View = inputList.get(it) as View
            var textInput: TextView = view.findViewById<TextView>(R.id.txt_input)
            textInput.setHint(strRest.get(it).toString())

        }
    }

}
