package com.example.rayanshshmi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class WordAdapter(
    private val list: MutableList<String>
) : RecyclerView.Adapter<WordAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvWord: TextView = view.findViewById(R.id.tvWord)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDeleteWord)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_word, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvWord.text = list[position]
        holder.btnDelete.setOnClickListener {
            removeItem(holder.adapterPosition)
        }
    }

    private fun removeItem(position: Int) {
        if (position >= 0 && position < list.size) {
            list.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, list.size - position)
        }
    }

    fun getList(): List<String> = list
}