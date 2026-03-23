package com.vivace.sottovoce.models

data class Track(
    val title: String,
    val artist: String,
    val browseId: String = "",   // The secret YouTube ID
    val coverUrl: String = ""    // The Thumbnail URL
)