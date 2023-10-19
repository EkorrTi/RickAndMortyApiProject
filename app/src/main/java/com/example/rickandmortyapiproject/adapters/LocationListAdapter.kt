package com.example.rickandmortyapiproject.adapters

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rickandmortyapiproject.R
import com.example.rickandmortyapiproject.models.Location

class LocationListAdapter: RecyclerView.Adapter<LocationListAdapter.LocationListViewHolder>() {
    var data: MutableList<Location> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var onClick: ((loc: Location) -> Unit) = {}

    override fun onBindViewHolder(holder: LocationListViewHolder, position: Int) {
        val loc = data[position]

        holder.apply {
            name.text = loc.name
            type.text = Html.fromHtml(
                this.itemView.context.getString(R.string.character_type, loc.type),
                Html.FROM_HTML_MODE_LEGACY
            )
            dimension.text = Html.fromHtml(
                this.itemView.context.getString(R.string.location_dimension, loc.dimension),
                Html.FROM_HTML_MODE_LEGACY
            )
            itemView.setOnClickListener { onClick(loc) }
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

    fun addData(list: List<Location>) {
        val size = data.size
        data.addAll(list)
        val newSize = data.size
        notifyItemRangeChanged(size, newSize)
    }
}