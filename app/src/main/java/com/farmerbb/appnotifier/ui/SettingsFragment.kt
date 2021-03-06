/* Copyright 2020 Braden Farmer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.farmerbb.appnotifier.ui

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.core.net.toUri
import androidx.preference.CheckBoxPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.farmerbb.appnotifier.BuildConfig
import com.farmerbb.appnotifier.R
import com.farmerbb.appnotifier.initAppNotifierService
import com.farmerbb.appnotifier.isPlayStoreInstalled
import java.util.*

class SettingsFragment: PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.prefs)
        setListeners("notify_installs", "notify_updates", "notify_play_store", "notify_other_sources")

        findPreference<Preference>("about_content")?.apply {
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("America/Denver")).apply {
                timeInMillis = BuildConfig.TIMESTAMP
            }

            summary = getString(R.string.about_content, calendar.get(Calendar.YEAR))

            setOnPreferenceClickListener {
                if(!requireContext().isPlayStoreInstalled())
                    return@setOnPreferenceClickListener true

                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = "market://details?id=${BuildConfig.APPLICATION_ID}".toUri()
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }

                startActivity(intent)
                true
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setDivider(ColorDrawable(Color.TRANSPARENT))
        setDividerHeight(0)
    }

    private fun setListeners(vararg keys: String) {
        for(key in keys) {
            findPreference<CheckBoxPreference>(key)?.setOnPreferenceChangeListener { _, _ ->
                Handler().post {
                    requireContext().initAppNotifierService()
                }

                true
            }
        }
    }
}
