package com.example.magicphoto

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewPager = findViewById<ViewPager2>(R.id.view_pager)

        val viewPagerAdapter = ViewPagerAdapter(this)
        viewPagerAdapter.addFragment(SelectImage())

        viewPager.apply {
            adapter = viewPagerAdapter
        }

    }
}