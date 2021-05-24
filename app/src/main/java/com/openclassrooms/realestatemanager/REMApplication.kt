package com.openclassrooms.realestatemanager

import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication
import androidx.preference.PreferenceManager
import com.openclassrooms.realestatemanager.database.EstateDatabase
import com.openclassrooms.realestatemanager.repository.EstateRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class REMApplication : MultiDexApplication() {

    private val applicationScope = CoroutineScope(SupervisorJob())
    private val db by lazy { EstateDatabase.getDatabase(this, applicationScope) }
    val estateRepository by lazy {
        EstateRepository(
            db.estateDao()
        )
    }

    override fun onCreate() {
        super.onCreate()
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