package com.openclassrooms.realestatemanager

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment

class MainActivity : AppCompatActivity() {

    private lateinit var navHostFragment: NavHostFragment

    private var smallestWidth: Float = 0F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setDeviceOrientations()

        setContentView(R.layout.activity_main)
        navHostFragment = supportFragmentManager
                .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
    }

    private fun setDeviceOrientations() {
        val displayMetrics: DisplayMetrics = resources.displayMetrics
        val dpHeight = displayMetrics.heightPixels / displayMetrics.density
        val dpWidth = displayMetrics.widthPixels / displayMetrics.density
        smallestWidth = dpWidth.coerceAtMost(dpHeight)

        Log.i("MainActivity",
                "SMALLER_WIDTH = $smallestWidth " +
                        "/ width = $dpWidth " +
                        "/ height = $dpHeight ")

        val swPORTRAIT = resources.getInteger(R.integer.sw_portrait_only)
        val swLANDSCAPE = resources.getInteger(R.integer.sw_landscape_only)

        when {
            // PORTRAIT if < smartphone size
            smallestWidth < swPORTRAIT
            -> requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT


            smallestWidth < swLANDSCAPE
            -> requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE

            // else refer to user orientation
            smallestWidth >= swLANDSCAPE
            -> requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER
        }

        Log.i("MainActivity", "requestedOrientation = $requestedOrientation")
    }
}
