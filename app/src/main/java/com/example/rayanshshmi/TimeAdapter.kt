package com.example.rayanshshmi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TimeAdapter(
    private val list: MutableList<TimeData>,
    private val section: Int,
    private val onDelete: (Int) -> Unit
) : RecyclerView.Adapter<TimeAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTime: TextView = view.findViewById(R.id.tvTime)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDeleteTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_time, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.tvTime.text = when (section) {
            1 -> "${item.millisecond} ms"
            else -> "${item.second} sec ${item.millisecond} ms"
        }
        holder.btnDelete.setOnClickListener { onDelete(position) }
    }
}