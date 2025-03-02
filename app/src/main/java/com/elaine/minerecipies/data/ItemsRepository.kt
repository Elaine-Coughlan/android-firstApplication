package com.elaine.minerecipies.data

import android.content.Context
import com.elaine.minerecipies.util.loadItems

class ItemsRepository(private val context: Context) {

    fun getItems() : List<Items>{
        return loadItems(context)
    }
}