package com.example.aop_part3_chapter05.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.aop_part3_chapter05.R
import com.example.aop_part3_chapter05.model.CardItem

class MatchedUserAdpater(
    val matchedUsers: MutableList<CardItem> = mutableListOf()
) : RecyclerView.Adapter<MatchedUserAdpater.matchedUserViewHolder>() {
    inner class matchedUserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.user_name_textview)

        fun bindViews(cardItem: CardItem) {
            nameTextView.text = cardItem.userName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): matchedUserViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val cardView = inflater.inflate(R.layout.matched_user_item, parent, false)
        return matchedUserViewHolder(cardView)
    }

    override fun onBindViewHolder(holder: matchedUserViewHolder, position: Int) {
        holder.bindViews(matchedUsers[position])
    }

    override fun getItemCount(): Int {
        return  matchedUsers.size ?: 0
    }
}