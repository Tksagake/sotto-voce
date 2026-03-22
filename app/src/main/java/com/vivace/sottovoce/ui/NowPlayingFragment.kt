package com.vivace.sottovoce.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.vivace.sottovoce.R

class NowPlayingFragment : Fragment(R.layout.fragment_now_playing) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Wire the Close button to slide back down
        view.findViewById<View>(R.id.np_close_btn).setOnClickListener {
            findNavController().navigateUp()
        }
    }
}