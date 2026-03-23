package com.vivace.sottovoce.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vivace.sottovoce.R
import com.vivace.sottovoce.YtmClient
import com.vivace.sottovoce.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import coil.load
import coil.transform.RoundedCornersTransformation
import com.google.android.material.imageview.ShapeableImageView
import com.vivace.sottovoce.MainActivity

class PlaylistDetailFragment : Fragment(R.layout.fragment_playlist_detail) {

    private lateinit var adapter: DetailAdapter
    private var spinner: ProgressBar? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val browseId = arguments?.getString("browseId") ?: ""
        val title = arguments?.getString("title") ?: "Playlist"

        spinner = view.findViewById(R.id.loading_spinner)
        val titleView = view.findViewById<TextView>(R.id.playlist_title_header)
        val coverView = view.findViewById<ImageView>(R.id.playlist_header_image)
        val recycler = view.findViewById<RecyclerView>(R.id.rv_playlist_songs)

        titleView?.text = title
        recycler?.layoutManager = LinearLayoutManager(requireContext())
        adapter = DetailAdapter(mutableListOf())
        recycler?.adapter = adapter

        view.findViewById<androidx.appcompat.widget.Toolbar>(R.id.playlist_toolbar)?.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        fetchSongs(browseId, coverView)
    }

    private fun fetchSongs(id: String, coverView: ImageView?) {
        spinner?.visibility = View.VISIBLE
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val client = YtmClient(requireContext())
            val rawJson = client.fetchPlaylistDetails(id)
            val songList = mutableListOf<Track>()

            try {
                val headerThumbRegex = "\"thumbnails\"\\s*:\\s*\\[\\s*\\{\\s*\"url\"\\s*:\\s*\"([^\"]+)\"".toRegex()
                val headerThumb = headerThumbRegex.find(rawJson)?.groupValues?.get(1)?.replace("&amp;", "&")

                val blocks = rawJson.split("musicResponsiveListItemRenderer")
                for (i in 1 until blocks.size) {
                    val block = blocks[i]
                    val videoId = "\"videoId\"\\s*:\\s*\"([^\"]+)\"".toRegex().find(block)?.groupValues?.get(1) ?: ""
                    val title = "\"text\"\\s*:\\s*\"([^\"]+)\"".toRegex().find(block)?.groupValues?.get(1) ?: continue
                    val artist = "\"text\"\\s*:\\s*\"([^\"]+)\"".toRegex().findAll(block).toList().getOrNull(1)?.groupValues?.get(1) ?: "Unknown"
                    val thumb = "\"url\"\\s*:\\s*\"([^\"]+)\"".toRegex().find(block)?.groupValues?.get(1)?.replace("&amp;", "&") ?: ""

                    if (videoId.isNotEmpty() && !title.lowercase().contains("play all")) {
                        songList.add(Track(title, artist, videoId, thumb))
                    }
                }

                withContext(Dispatchers.Main) {
                    spinner?.visibility = View.GONE
                    adapter.updateData(songList)
                    headerThumb?.let { coverView?.load(it) { crossfade(true) } }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { spinner?.visibility = View.GONE }
            }
        }
    }

    inner class DetailAdapter(private var items: List<Track>) : RecyclerView.Adapter<DetailAdapter.ViewHolder>() {
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val title: TextView = view.findViewById(R.id.track_title)
            val subtitle: TextView = view.findViewById(R.id.track_artist)
            val cover: ShapeableImageView = view.findViewById(R.id.track_cover)
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_track, parent, false)
            return ViewHolder(view)
        }
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            holder.title.text = item.title
            holder.subtitle.text = item.artist
            holder.cover.load(item.coverUrl) { transformations(RoundedCornersTransformation(8f)) }
            holder.itemView.setOnClickListener { (requireActivity() as? MainActivity)?.playTrack(item) }
        }
        override fun getItemCount() = items.size
        @SuppressLint("NotifyDataSetChanged")
        fun updateData(newItems: List<Track>) {
            items = newItems
            notifyDataSetChanged()
        }
    }
}