package com.elaine.minerecipies.data

import android.content.Context
import com.elaine.minerecipies.data.api.loadItems

class ItemsRepository(private val context: Context) {

    fun getItems() : List<Items>{
        return loadItems(context)
    }
}