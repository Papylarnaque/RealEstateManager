package com.openclassrooms.realestatemanager.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.openclassrooms.realestatemanager.R

class DayNightFragment : PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val darkModePref =
            preferenceManager.findPreference<Preference>(resources.getString(R.string.dark_mode_key))
        darkModePref?.onPreferenceChangeListener = this

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.fragment_daynight, rootKey)
    }

    private fun refreshDarkModePreference(newValue: String) {
        val enabled = resources.getString(R.string.enabled_value)
        val disabled = resources.getString(R.string.disabled_value)
        val default = resources.getString(R.string.system_default_value)

        when (newValue) {
            enabled -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            disabled -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            default -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        refreshDarkModePreference(newValue as String)
        return true
    }
}