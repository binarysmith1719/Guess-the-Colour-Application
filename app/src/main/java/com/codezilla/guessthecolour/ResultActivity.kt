package com.codezilla.guessthecolour

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ResultActivity : AppCompatActivity() {
    var resultlist:ArrayList<Result> = ArrayList<Result>()
    var list:ArrayList<String> = ArrayList<String>()

    lateinit var resultDatabase: ResultDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resut)
        val actionBar: ActionBar?
        actionBar = supportActionBar
        val colorDrawable= ColorDrawable(Color.parseColor("#148485"))
        actionBar!!.setBackgroundDrawable(colorDrawable)

        list = (intent.getSerializableExtra("mylist") as ArrayList<String>?)!!

        var i=1;
        var CrtAns:String=""
        var UrAns:String=""
        list.forEach {
            if(i%2==1  )
            {CrtAns=it}
            else {
                UrAns=it
                if(!UrAns.equals("")) {
                    var result = Result(0, UrAns, CrtAns)
                    resultlist.add(result)
                }
            }
            i++ }

        var recView=findViewById(R.id.rcycView) as RecyclerView
        recView!!.setHasFixedSize(true)
        recView!!.layoutManager= LinearLayoutManager(this)

        var resultAdapter= ResultAdapter(this,resultlist!!)
        recView!!.adapter=resultAdapter

        resultAdapter.notifyDataSetChanged()
    }
}