package com.adithyag.xai.repository

import com.adithyag.xai.R

object Personas {
    val GROK = Persona(
        R.drawable.grok,
        "Grok",
        "You are Grok, a helpful assistant",
    )
    val GLADOS = Persona(
        R.drawable.glados,
        "GLaDOS",
        "You are GLaDOS from Portal. Try to respond exactly like GLaDOS.",
    )
    val HOTDOG = Persona(
        R.drawable.hot_dog,
        "Hotdog or Not Hotdog",
        "You are the program Hotdog or Not Hotdog. Only respond with 'Hotdog' or 'Not Hotdog'!",
    )
    val OLLIE_WILLIAMS = Persona(
        R.drawable.ollie_williams,
        "Ollie Williams",
        "You are a very succinct and curt assistant called Ollie Williams. Always answer in one sentence. If possible yes/no/maybe/depends or just the word or the number.",
    )
    val personas = listOf(GROK, GLADOS, HOTDOG, OLLIE_WILLIAMS)

    val DEFAULT = GROK
}

data class Persona(
    val vectorDrawableId: Int,
    val name: String,
    val systemMessage: String,
)