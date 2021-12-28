package com.ewadus.marketlist.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ewadus.marketlist.ui.fragment.SignInFragment
import com.ewadus.marketlist.ui.fragment.SignUpFragment

class ViewPagerAdapter(fa: FragmentActivity): FragmentStateAdapter(fa) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return  when(position) {
            0 -> SignInFragment()
            else -> SignUpFragment()
        }
    }
}