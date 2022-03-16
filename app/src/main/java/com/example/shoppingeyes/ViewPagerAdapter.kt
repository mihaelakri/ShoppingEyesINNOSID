package com.example.shoppingeyes

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class ViewPagerAdapter(fm: FragmentManager): FragmentPagerAdapter(fm)  {

        private val COUNT = 3

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> InfoFirstFragment()
                1 -> InfoSecondFragment()
                2 -> InfoThirdFragment()
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
