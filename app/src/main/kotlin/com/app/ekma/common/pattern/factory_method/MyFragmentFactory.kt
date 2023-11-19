package com.app.ekma.common.pattern.factory_method

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.app.ekma.activities.main.ItemViewPagerBaseFragment
import com.app.ekma.common.FRAGMENT_TYPE

class MyFragmentFactory() : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment =
        when (loadFragmentClass(classLoader, className)) {
            else -> super.instantiate(classLoader, className)
        }
}

class BaseFragmentFactory {

    companion object {
        fun create(position: Int): Fragment {
            val bundle = bundleOf(FRAGMENT_TYPE to position)
            val fragment = ItemViewPagerBaseFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}