package com.webnation.begonerobotexters.adapters

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentManager
import com.webnation.begonerobotexters.R
import com.webnation.begonerobotexters.fragments.FragmentBlocked
import com.webnation.begonerobotexters.fragments.FragmentHome


/**
 * Copyright (C) 2015 Mustafa Ozcan
 * Created on 06 May 2015 (www.mustafaozcan.net)
 * *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * *
 * http://www.apache.org/licenses/LICENSE-2.0
 * *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * From https://github.com/mustafaozcan/MaterialNavigation
 */
class ContentFragmentAdapter(fragmentManager: FragmentManager, private val c: Context, item_count: Int) : FragmentPagerAdapter(fragmentManager) {

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
