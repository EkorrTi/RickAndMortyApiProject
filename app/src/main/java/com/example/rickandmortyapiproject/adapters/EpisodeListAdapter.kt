package com.example.rickandmortyapiproject.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rickandmortyapiproject.R
import com.example.rickandmortyapiproject.models.Episode

class EpisodeListAdapter: RecyclerView.Adapter<EpisodeListAdapter.EpisodeListViewHolder>() {
    var data: List<Episode> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var onClick: ((ep: Episode) -> Unit) = {}

    override fun onBindViewHolder(holder: EpisodeListViewHolder, position: Int) {
        val ep = data[position]

        holder.apply {
            name.text = ep.name
            number.text = ep.episode
            date.text = ep.airDate
            itemView.setOnClickListener { onClick(ep) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodeListViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.episode_list_item, parent, false)
        return EpisodeListViewHolder(adapterLayout)
    }

    override fun getItemCount(): Int = data.size

    class EpisodeListViewHolder(view: View): RecyclerView.ViewHolder(view){
        val name: TextView = view.findViewById(R.id.episode_name)
        val number: TextView = view.findViewById(R.id.episode_number)
        val date: TextView = view.findViewById(R.id.episode_date)
    }
}