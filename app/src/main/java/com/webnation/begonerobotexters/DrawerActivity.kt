package com.webnation.begonerobotexters.utils

import android.os.Bundle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity

import android.view.MenuItem
import com.webnation.begonerobotexters.R


class DrawerActivity : AppCompatActivity() {

    lateinit var draw_layout: DrawerLayout
    lateinit var mDrawerToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()

        draw_layout = findViewById<DrawerLayout>(R.id.drawer_layout)
        mDrawerToggle = ActionBarDrawerToggle(this, draw_layout, 0, 0)
        draw_layout.addDrawerListener(mDrawerToggle)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayShowHomeEnabled(true)
            actionBar.setDisplayShowTitleEnabled(true)
            actionBar.setDisplayUseLogoEnabled(false)
            actionBar.setHomeButtonEnabled(true)
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        mDrawerToggle.syncState()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item)
    }
}