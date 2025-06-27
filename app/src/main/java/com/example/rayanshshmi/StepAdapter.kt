package com.example.rayanshshmi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class StepAdapter(
    private val list: MutableList<StepData>,
    private val onDelete: (Int) -> Unit
) : RecyclerView.Adapter<StepAdapter.StepViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_step, parent, false)
        return StepViewHolder(view)
    }

    override fun onBindViewHolder(holder: StepViewHolder, position: Int) {
        val data = list[position]
        holder.tvStep.text = "Count: ${data.count}, Pause: ${data.pauseSec} sec"
        holder.btnDelete.setOnClickListener { onDelete(position) }
    }

    override fun getItemCount(): Int = list.size

    class StepViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvStep: TextView = view.findViewById(R.id.tvStep)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDeleteStep)
    }
}