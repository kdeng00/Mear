package com.example.mear.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.LinearLayout
import android.widget.ListView

import kotlinx.android.synthetic.main.activity_song_view.*
import kotlinx.android.synthetic.main.content_song_view.*

import com.example.mear.R
import com.example.mear.repositories.TrackRepository

class SongViewActivity : AppCompatActivity() {

    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_song_view)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initializeAdapter()
    }



    private fun initializeAdapter() {
        listView = findViewById<ListView>(R.id.recipe_list_view)
// 1
        val recipeList = Recipe.getRecipesFromFile("recipes.json", this)
// 2
        val listItems = arrayOfNulls<String>(recipeList.size)
// 3
        for (i in 0 until recipeList.size) {
            val recipe = recipeList[i]
            listItems[i] = recipe.title
        }
// 4
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listItems)
        listView.adapter = adapter
    }
}
