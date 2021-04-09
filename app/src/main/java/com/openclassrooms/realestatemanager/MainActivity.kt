package com.openclassrooms.realestatemanager

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment

class MainActivity : AppCompatActivity() {

    private lateinit var navHostFragment: NavHostFragment
    var twoPane: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        isTwoPane()
    }


    private fun isTwoPane() {
        twoPane = findViewById<View>(R.id.detail_fragment_container) != null
    }

    override fun onResume() {
        isTwoPane()
        super.onResume()
    }

}
