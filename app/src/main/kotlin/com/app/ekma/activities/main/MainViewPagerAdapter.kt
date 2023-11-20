package com.app.ekma.activities.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.app.ekma.common.pattern.factory_method.BaseFragmentFactory

class MainViewPagerAdapter(
    fragmentActivity: FragmentActivity
) : FragmentStateAdapter(fragmentActivity) {

    private val tabs = 4

    override fun getItemCount() = tabs

    override fun createFragment(position: Int): Fragment {
        return BaseFragmentFactory.create(position)
    }
}