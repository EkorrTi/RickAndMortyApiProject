package com.example.rickandmortyapiproject.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rickandmortyapiproject.R
import com.example.rickandmortyapiproject.models.Location

class LocationListAdapter: RecyclerView.Adapter<LocationListAdapter.LocationListViewHolder>() {
    var data: List<Location> = emptyList()

    override fun onBindViewHolder(holder: LocationListViewHolder, position: Int) {
        val l = data[position]

        holder.apply {
            name.text = l.name
            type.text = l.type
            dimension.text = l.dimension
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationListViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.location_list_item, parent, false)
        return LocationListViewHolder(adapterLayout)
    }

    override fun getItemCount(): Int = data.size

    class LocationListViewHolder(view: View): RecyclerView.ViewHolder(view){
        val name: TextView = view.findViewById(R.id.location_name)
        val type: TextView = view.findViewById(R.id.location_type)
        val dimension: TextView = view.findViewById(R.id.location_dimension)
    }
}