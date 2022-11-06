package com.codezilla.guessthecolour

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ResultAdapter(context: Context, list:ArrayList<Result>): RecyclerView.Adapter<ResultAdapter.ResultViewHolder>() {
    public var contxt: Context? = null
    private var arr: ArrayList<Result>? = null
    init {
        arr=list
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        var view: View = LayoutInflater.from(parent.getContext()).inflate(R.layout.resultcard,parent,false)
        Log.d("tag","oncrate")

        return ResultViewHolder(view)
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        Log.d("tag","onbind")

        var res:Result =  arr?.get(position)!!
        var str1:String = res.CorrectAns
        var str2:String = res.YourAns
        holder.txtView1.setText("Correct Answer : ${str1}")
        holder.txtView2.setText("Your Answer : ${str2}")
    }

    override fun getItemCount(): Int {
        return arr!!.size
    }

    class ResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txtView1: TextView
        var txtView2 : TextView
        init {
            Log.d("tag","holder")

            txtView1= itemView.findViewById<TextView>(R.id.txtcard1) as TextView
            txtView2=itemView.findViewById<TextView>(R.id.txtcard2) as TextView
        }
    }

}