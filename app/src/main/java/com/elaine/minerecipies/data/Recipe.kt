package com.elaine.minerecipies.data

data class Recipe(val item: String, val quantity: Int,
                  val recipe: List<Any?>, val shapeless: Boolean)
