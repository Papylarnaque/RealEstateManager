package com.openclassrooms.realestatemanager

import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication
import androidx.preference.PreferenceManager
import com.openclassrooms.realestatemanager.viewmodel.ListDetailViewModel

class REMApplication : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        ListDetailViewModel(this).filterEstates(null)
        setupDarkModePreference()
    }

    private fun setupDarkModePreference() {
        val defaultValue = resources.getString(R.string.system_default_value)
        val disabledValue = resources.getString(R.string.disabled_value)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        val darkModeSetting =
            sharedPreferences.getString(resources.getString(R.string.dark_mode_key), defaultValue)

        if (!darkModeSetting.equals(defaultValue)) {
            if (darkModeSetting.equals(disabledValue)) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }
    }
}