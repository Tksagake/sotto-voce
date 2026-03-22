package com.vivace.sottovoce.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.vivace.sottovoce.R

class SearchFragment : Fragment(R.layout.fragment_search) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recycler = view.findViewById<RecyclerView>(R.id.rv_search_grid)

        val genres = listOf("Podcasts", "Made For You", "New Releases", "Hip-Hop", "Afrobeats", "Classical", "Pop", "Workout")
        recycler.adapter = GenreAdapter(genres)
    }

    inner class GenreAdapter(private val items: List<String>) : RecyclerView.Adapter<GenreAdapter.ViewHolder>() {
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val title: TextView = view.findViewById(R.id.genre_title)
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_search_genre, parent, false)
            return ViewHolder(view)
        }
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.title.text = items[position]
        }
        override fun getItemCount() = items.size
    }
}