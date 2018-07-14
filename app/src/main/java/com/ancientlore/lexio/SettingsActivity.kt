package com.ancientlore.lexio

import android.content.Context
import android.os.Bundle
import android.preference.PreferenceFragment
import android.preference.SwitchPreference
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import kotlinx.android.synthetic.main.toolbar.*

class SettingsActivity: AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_settings)
		setSupportActionBar(toolbar)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		fragmentManager.beginTransaction()
				.replace(R.id.content_container, SettingsFragment())
				.commitAllowingStateLoss()
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		return when (item.itemId) {
			android.R.id.home -> {
				finish()
				true
			}
			else -> super.onOptionsItemSelected(item)
		}
	}

	class SettingsFragment : PreferenceFragment() {

		override fun onCreate(savedInstanceState: Bundle?) {
			super.onCreate(savedInstanceState)
			addPreferencesFromResource(R.xml.prefs)

			val prefs = activity.getSharedPreferences(Consts.PREFS_NAME, Context.MODE_PRIVATE)

			val autoTranslationSwitch = preferenceManager.findPreference("autotranslation") as SwitchPreference
			autoTranslationSwitch.isChecked = prefs.getBoolean(Consts.PREF_AUTO_TRANSLATE, true)
			autoTranslationSwitch.setOnPreferenceClickListener {
				prefs.edit().putBoolean(Consts.PREF_AUTO_TRANSLATE, (it as SwitchPreference).isChecked).apply()
				true
			}
		}
	}
}