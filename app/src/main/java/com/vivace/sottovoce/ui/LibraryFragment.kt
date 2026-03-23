package com.vivace.sottovoce.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
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

class LibraryFragment : Fragment(R.layout.fragment_library) {

    // Initializing with empty list immediately prevents "lateinit" crashes
    private var adapter = LibraryAdapter(mutableListOf())

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recycler = view.findViewById<RecyclerView>(R.id.rv_library_list)
        val spinner = view.findViewById<ProgressBar>(R.id.loading_spinner)

        recycler?.layoutManager = LinearLayoutManager(requireContext())
        recycler?.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val ytmClient = YtmClient(requireContext())
            val rawJson = ytmClient.fetchLibrary()
            val list = mutableListOf<Track>()

            try {
                // UNIVERSAL HEIST REGEX: Grabs Title, BrowseId, and Thumbnail
                val pattern = "\"title\"\\s*:\\s*\\{.*?\"text\"\\s*:\\s*\"([^\"]+)\".*?\"browseId\"\\s*:\\s*\"([^\"]+)\".*?\"url\"\\s*:\\s*\"([^\"]+)\"".toRegex(RegexOption.DOT_MATCHES_ALL)
                pattern.findAll(rawJson).forEach { m ->
                    val id = m.groupValues[2]
                    // Catch Playlists (PL), Albums (MPRE), and Your Likes (VLLM)
                    if (id.startsWith("PL") || id.startsWith("VL") || id.startsWith("MPRE") || id == "VLLM") {
                        list.add(Track(m.groupValues[1], "Collection", id, m.groupValues[3].replace("&amp;", "&")))
                    }
                }

                withContext(Dispatchers.Main) {
                    // Safety check: Don't touch UI if the user navigated away
                    if (!isAdded) return@withContext

                    spinner?.visibility = View.GONE

                    if (list.isNotEmpty()) {
                        adapter.updateData(list.distinctBy { it.browseId })
                    } else {
                        Log.e("SOTTO_HEIST", "Zero items found. Body length: ${rawJson.length}")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { spinner?.visibility = View.GONE }
                Log.e("SOTTO_HEIST", "Parser failure", e)
            }
        }
    }

    inner class LibraryAdapter(private var items: MutableList<Track>) : RecyclerView.Adapter<LibraryAdapter.VH>() {
        inner class VH(v: View) : RecyclerView.ViewHolder(v)

        override fun onCreateViewHolder(p: ViewGroup, t: Int): VH {
            val v = LayoutInflater.from(p.context).inflate(R.layout.item_track, p, false)
            return VH(v)
        }

        override fun onBindViewHolder(h: VH, pos: Int) {
            val item = items[pos]
            h.itemView.findViewById<TextView>(R.id.track_title)?.text = item.title
            h.itemView.findViewById<TextView>(R.id.track_artist)?.text = item.artist
            val cover = h.itemView.findViewById<com.google.android.material.imageview.ShapeableImageView>(R.id.track_cover)

            cover?.load(item.coverUrl) {
                crossfade(true)
                transformations(RoundedCornersTransformation(8f))
            }

            h.itemView.setOnClickListener {
                val b = Bundle().apply {
                    putString("browseId", item.browseId)
                    putString("title", item.title)
                }
                findNavController().navigate(R.id.playlistDetailFragment, b)
            }
        }

        override fun getItemCount() = items.size

        fun updateData(new: List<Track>) {
            this.items = new.toMutableList()
            notifyDataSetChanged()
        }
    }
}