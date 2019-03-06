package com.example.mear.repositories

import android.content.Context

import org.jetbrains.anko.db.*

import com.example.mear.database
import com.example.mear.models.Settings

class SettingRepository(val context: Context) {
    fun getSettings(id: Int): Settings = context.database.use {
        select("SettingsActivity").where("Id = $id")
            .parseSingle(object: MapRowParser<Settings>{
                override fun parseRow(columns: Map<String, Any?>): Settings {
                    val id = columns.getValue("Id").toString().toInt()
                    val darkTheme = columns.getValue("DarkTheme").toString().toBoolean()

                    val settings = Settings (id, darkTheme)

                    return settings
                }
            })
    }
    fun insertSettings(settings: Settings) = context.database.use {
        insert("SettingsActivity",
            "Id" to settings.id,
            "DarkTheme" to false)
    }
    fun updateSettings(settings: Settings) = context.database.use {
        update("SettingsActivity",
            "DarkTheme" to settings.darkTheme)
            .where("Id = {settingId}", "settingId" to settings.id).exec()
    }

    fun delete(table: Settings?) = context.database.use {
        delete("SettingsActivity", whereClause = "id = {$table.id}")
    }
    fun delete() = context.database.use {
        delete("SettingsActivity")
    }
}