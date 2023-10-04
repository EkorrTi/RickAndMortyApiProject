package com.example.rickandmortyapiproject.adapters

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.rickandmortyapiproject.R
import com.example.rickandmortyapiproject.models.Character

class CharactersListAdapter :
    RecyclerView.Adapter<CharactersListAdapter.CharacterListViewHolder>() {
    var data: List<Character> = emptyList()
    var onClick: ((ch: Character) -> Unit) = {}

    override fun onBindViewHolder(holder: CharacterListViewHolder, position: Int) {
        val ch = data[position]

        holder.apply {
            image.load(ch.image)
            name.text = ch.name
            species.text = Html.fromHtml(
                itemView.resources.getString(R.string.character_species, ch.species),
                Html.FROM_HTML_MODE_LEGACY
            )
            status.text = Html.fromHtml(
                itemView.resources.getString(R.string.character_status, ch.status),
                Html.FROM_HTML_MODE_LEGACY
            )
            gender.text = Html.fromHtml(
                itemView.resources.getString(R.string.character_gender, ch.gender),
                Html.FROM_HTML_MODE_LEGACY
            )
            itemView.setOnClickListener { onClick(ch) }
        }
    }

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterListViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.character_list_item, parent, false)
        return CharacterListViewHolder(adapterLayout)
    }

    class CharacterListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.image_view)
        val name: TextView = view.findViewById(R.id.name_text_view)
        val species: TextView = view.findViewById(R.id.species_text_view)
        val status: TextView = view.findViewById(R.id.status_text_view)
        val gender: TextView = view.findViewById(R.id.gender_text_view)
    }
}