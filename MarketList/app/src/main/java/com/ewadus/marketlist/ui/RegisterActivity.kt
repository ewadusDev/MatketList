package com.ewadus.marketlist.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import androidx.viewpager.widget.ViewPager
import com.ewadus.marketlist.adapter.ViewPagerAdapter
import com.ewadus.marketlist.databinding.ActivityRegisterBinding
import com.ewadus.marketlist.ui.fragment.SignInFragment
import com.ewadus.marketlist.ui.fragment.SignUpFragment
import com.google.android.material.tabs.TabLayout

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewPager: ViewPager
    private lateinit var tab: TabLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewPager = binding.viewpager
        tab = binding.tabLayout


        val adapter = ViewPagerAdapter(supportFragmentManager)


        adapter.addFragment(SignInFragment(),"SIGN IN")
        adapter.addFragment(SignUpFragment(),"SIGN UP")

        viewPager.adapter = adapter
        tab.setupWithViewPager(viewPager)




    }


}