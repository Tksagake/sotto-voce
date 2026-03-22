package com.vivace.sottovoce.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.vivace.sottovoce.R
import com.vivace.sottovoce.models.Track

class LibraryFragment : Fragment(R.layout.fragment_library) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recycler = view.findViewById<RecyclerView>(R.id.rv_library_list)

        // Recycling the Track model to mock Playlists!
        val playlists = listOf(
            Track("Liked Songs", "Playlist • 1,204 songs"),
            Track("Discover Weekly", "Playlist • Spotify"),
            Track("Classical Focus", "Playlist • Siko_leer"),
            Track("Coding Vibes 2026", "Playlist • Siko_leer"),
            Track("Your Top Songs 2025", "Playlist • Spotify")
        )

        recycler.adapter = LibraryAdapter(playlists)
    }

    inner class LibraryAdapter(private val items: List<Track>) : RecyclerView.Adapter<LibraryAdapter.ViewHolder>() {
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val title: TextView = view.findViewById(R.id.track_title)
            val subtitle: TextView = view.findViewById(R.id.track_artist)
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_track, parent, false)
            return ViewHolder(view)
        }
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.title.text = items[position].title
            holder.subtitle.text = items[position].artist
        }
        override fun getItemCount() = items.size
    }
}