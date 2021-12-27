package com.kosyakoff.foodapp.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class RecipePagerAdapter(
    private val resultBundle: Bundle,
    private val fragments: List<Fragment>,
    private val titles: List<String>,
    fragmentManager: FragmentManager
) : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getCount(): Int = fragments.size

    override fun getItem(position: Int): Fragment =
        fragments[position].apply { arguments = resultBundle }

    override fun getPageTitle(position: Int): CharSequence? {
        return titles[position]
    }

}