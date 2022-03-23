package com.example.shoppingeyes

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class AboutPagerAdapter(fm: FragmentManager): FragmentPagerAdapter(fm)  {

    private val COUNT = 5

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> Hrvoje()
            1 -> Iva()
            2 -> Zsolt()
            3 -> Mihaela()
            4 -> Joao()
            else -> InfoFirstFragment()
        }
    }

    override fun getCount(): Int {
        return COUNT
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return "Tab " + (position + 1)
    }
}