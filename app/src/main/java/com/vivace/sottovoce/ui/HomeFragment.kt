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

class HomeFragment : Fragment(R.layout.fragment_home) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Find our two new RecyclerViews
        val gridRecycler = view.findViewById<RecyclerView>(R.id.rv_recent_grid)
        val horizontalRecycler = view.findViewById<RecyclerView>(R.id.rv_jump_back_in)
        view.findViewById<View>(R.id.home_profile_btn).setOnClickListener {
            androidx.navigation.Navigation.findNavController(view).navigate(R.id.nav_profile)
        }

        // 2. Mock Data for the 6-item Grid (Matching your screenshot)
        val gridData = listOf(
            "Liked Songs", "This Is Sauti Sol",
            "KeiMFables", "Assinata Radio",
            "Your Episodes", "Mic Cheque Podcast"
        )

        // 3. Mock Data for the "Jump Back In" cards
        val cardData = listOf(
            Track("Ngogoyo Ya Nyimbo Cia Tene", "Various Artists"),
            Track("YEBO (feat Vestine)", "BLEY-250"),
            Track("Bach: Partita No. 6", "Johann Sebastian Bach"),
            Track("Blue Moon", "Manchester City Anthem")
        )

        // 4. Attach the adapters
        gridRecycler.adapter = GridAdapter(gridData)
        horizontalRecycler.adapter = CardAdapter(cardData)
    }


    // --- QUICK ADAPTER FOR THE TOP GRID ---
    inner class GridAdapter(private val items: List<String>) : RecyclerView.Adapter<GridAdapter.GridViewHolder>() {
        inner class GridViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val titleText: TextView = view.findViewById(R.id.grid_title)
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recent_grid, parent, false)
            return GridViewHolder(view)
        }
        override fun onBindViewHolder(holder: GridViewHolder, position: Int) {
            holder.titleText.text = items[position]
        }
        override fun getItemCount() = items.size
    }

    // --- QUICK ADAPTER FOR THE HORIZONTAL CARDS ---
    inner class CardAdapter(private val items: List<Track>) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {
        inner class CardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val titleText: TextView = view.findViewById(R.id.card_title)
            val subtitleText: TextView = view.findViewById(R.id.card_subtitle)
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_horizontal_card, parent, false)
            return CardViewHolder(view)
        }
        override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
            holder.titleText.text = items[position].title
            holder.subtitleText.text = items[position].artist
        }
        override fun getItemCount() = items.size
    }
}