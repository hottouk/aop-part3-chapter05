package com.example.aop_part3_chapter05.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.aop_part3_chapter05.R
import com.example.aop_part3_chapter05.model.CardItem

class CardItemAdapter(
    private val cardItems : List<CardItem>
) : RecyclerView.Adapter<CardItemAdapter.CardViewHolder>(){
    inner class CardViewHolder(itemVIew:View): RecyclerView.ViewHolder(itemVIew) {
        private val nameTextView : TextView

        init {
            nameTextView = itemVIew.findViewById<TextView>(R.id.name_textview)
        }
        fun bindViews(cardItem : CardItem){
            nameTextView.text = cardItem.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val cardView = inflater.inflate(R.layout.card_item, parent, false)
        return CardViewHolder(cardView)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bindViews(cardItems[position])
    }

    override fun getItemCount(): Int {
        return cardItems.size
    }
}