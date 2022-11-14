package com.example.aop_part3_chapter05.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.aop_part3_chapter05.R
import com.example.aop_part3_chapter05.model.CardItem

class CardItemAdapter(
    private var cardItems: List<CardItem> = mutableListOf(
        CardItem("id1","that"),
        CardItem("id2","that2")
    )
) : RecyclerView.Adapter<CardItemAdapter.CardViewHolder>() {
    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val nameTextView: TextView = itemView.findViewById(R.id.name_textview)

        fun bindViews(cardItem: CardItem) {
            nameTextView.text = cardItem.userName
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