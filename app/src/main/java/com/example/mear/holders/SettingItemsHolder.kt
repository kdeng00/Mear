package com.example.mear.holders

import android.support.v7.widget.RecyclerView
import android.view.View

import com.example.mear.models.SettingItems

class SettingItemsHolder(view: View): RecyclerView.ViewHolder(view){

    private var view: View = view

    fun bindSettingItem(settingItem: SettingItems, clickable: (SettingItems) -> Unit) {
        try {
            // TODO: Implement the binding of setting items

        }
        catch (ex: Exception) {
            val exMsg = ex.message
        }
    }
}