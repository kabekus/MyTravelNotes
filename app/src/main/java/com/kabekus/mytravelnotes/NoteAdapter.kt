package com.kabekus.mytravelnotes

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kabekus.mytravelnotes.databinding.RecyclerRowBinding

class NoteAdapter(val noteList : ArrayList<Note>):RecyclerView.Adapter<NoteAdapter.NoteHolder>() {
    class NoteHolder (val binding: RecyclerRowBinding):RecyclerView.ViewHolder(binding.root){

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteHolder {
       val binding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return  NoteHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteHolder, position: Int) {
        holder.binding.rowTxt.text = noteList.get(position).noteTitle

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context,DetailActivity::class.java)
            intent.putExtra("info","oldNote")
            intent.putExtra("id",noteList.get(position).id)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return noteList.size
    }
}