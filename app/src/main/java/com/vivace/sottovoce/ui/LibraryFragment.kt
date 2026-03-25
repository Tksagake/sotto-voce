package com.vivace.sottovoce.ui

import android.os.Bundle
import android.view.View
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

    private var adapter = LibraryAdapter(mutableListOf())

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recycler = view.findViewById<RecyclerView>(R.id.rv_library_list)
        recycler?.layoutManager = LinearLayoutManager(requireContext())
        recycler?.adapter = adapter
        refresh()
    }

    fun refresh() {
        val spinner = view?.findViewById<ProgressBar>(R.id.loading_spinner)
        spinner?.visibility = View.VISIBLE

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val rawJson = YtmClient(requireContext()).fetchLibrary()
            val list = mutableListOf<Track>()

            // Greedier Regex: Find ID first, then Title nearby
            val regex = "\"browseId\"\\s*:\\s*\"([^\"]+)\".*?\"text\"\\s*:\\s*\"([^\"]+)\"".toRegex(RegexOption.DOT_MATCHES_ALL)
            regex.findAll(rawJson).forEach { m ->
                val id = m.groupValues[1]
                if (id.startsWith("PL") || id.startsWith("VL") || id.startsWith("MPRE")) {
                    list.add(Track(m.groupValues[2], "Collection", id, ""))
                }
            }

            withContext(Dispatchers.Main) {
                if (!isAdded) return@withContext
                spinner?.visibility = View.GONE
                adapter.items = list.distinctBy { it.browseId }.toMutableList()
                adapter.notifyDataSetChanged()
            }
        }
    }

    inner class LibraryAdapter(var items: MutableList<Track>) : RecyclerView.Adapter<LibraryAdapter.VH>() {
        inner class VH(v: View) : RecyclerView.ViewHolder(v)
        override fun onCreateViewHolder(p: android.view.ViewGroup, t: Int) = VH(android.view.LayoutInflater.from(p.context).inflate(R.layout.item_track, p, false))
        override fun onBindViewHolder(h: VH, pos: Int) {
            val item = items[pos]
            h.itemView.findViewById<TextView>(R.id.track_title).text = item.title
            h.itemView.setOnClickListener {
                val b = Bundle().apply { putString("browseId", item.browseId); putString("title", item.title) }
                findNavController().navigate(R.id.playlistDetailFragment, b)
            }
        }
        override fun getItemCount() = items.size
    }
}