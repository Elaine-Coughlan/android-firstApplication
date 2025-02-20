package com.elaine.minerecipies.data.Blocks

data class Blocks( val name: String, val namespaceId: String, val description: String,
    val image: String, val item: String, val tool: String, val flammable: Boolean, val transparent: Boolean, val luminance: Int,
    val blastResistance: Float, val colors: List<BlockColour>)
