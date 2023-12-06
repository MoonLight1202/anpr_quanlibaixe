package com.example.mongodbrealmcourse.view.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class HomePagerAdapter(fa: FragmentActivity) :
    FragmentStateAdapter(fa) {
    private var fragmentList: MutableList<Fragment> = ArrayList()

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }

    fun addPager(fragment: Fragment) {
        fragmentList.add(fragment)
    }
}