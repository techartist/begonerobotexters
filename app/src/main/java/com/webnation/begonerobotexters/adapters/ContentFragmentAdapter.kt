package com.webnation.begonerobotexters.adapters

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentManager
import com.webnation.begonerobotexters.R
import com.webnation.begonerobotexters.fragments.FragmentBlocked
import com.webnation.begonerobotexters.fragments.FragmentHome

class ContentFragmentAdapter(fragmentManager: FragmentManager, private val c: Context, item_count: Int) : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    init {
        NUM_ITEMS = item_count
    }

    // Returns total number of pages
    override fun getCount(): Int {
        return NUM_ITEMS
    }

    // Returns the fragment to display for that page
    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> return FragmentHome.getInstance()

            1 -> return FragmentBlocked.getInstance()

            else -> return FragmentHome.getInstance()
        }
    }

    // Returns the page title for the top indicator
    override fun getPageTitle(position: Int): CharSequence? {
        when (position) {
            0 -> return c.resources.getString(R.string.tab1)
            1 -> return c.resources.getString(R.string.tab2)
        }
        return ""
    }

    companion object {
        private var NUM_ITEMS = 1
    }

}
