package com.example.rickandmortyapiproject.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.rickandmortyapiproject.R
import com.example.rickandmortyapiproject.models.Character

class CharactersListAdapter : RecyclerView.Adapter<CharactersListAdapter.CharacterListViewHolder>() {
    var data: List<Character> = emptyList()

    override fun onBindViewHolder(holder: CharacterListViewHolder, position: Int) {
        val ch = data[position]

        holder.image.load(ch.image)
        holder.name.text = ch.name
        holder.species.text = ch.species
        holder.status.text = ch.status
        holder.gender.text = ch.gender
    }

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterListViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.character_list_item, parent, false)
        return CharacterListViewHolder(adapterLayout)
    }

    class CharacterListViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val image: ImageView = view.findViewById(R.id.image_view)
        val name: TextView = view.findViewById(R.id.name_text_view)
        val species: TextView = view.findViewById(R.id.species_text_view)
        val status: TextView = view.findViewById(R.id.status_text_view)
        val gender: TextView = view.findViewById(R.id.gender_text_view)
    }
}