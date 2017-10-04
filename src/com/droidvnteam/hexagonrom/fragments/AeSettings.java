/*
 * Copyright (C) 2017 HexagonRom
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.droidvnteam.hexagonrom.fragments;

import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v14.preference.SwitchPreference;
import android.widget.Toast;

import com.droidvnteam.hexagonrom.BaseSettingsFragment;
import com.droidvnteam.hexagonrom.Constants;
import com.droidvnteam.hexagonrom.R;
import com.droidvnteam.hexagonrom.utils.Util;

public class AeSettings extends BaseSettingsFragment
        implements Preference.OnPreferenceChangeListener {

    private ListPreference mTheme;

    @Override
    protected int getPreferenceResource() {
        return R.xml.ae_settings;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTheme = (ListPreference) findPreference(Constants.PREF_THEME);
        mTheme.setOnPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Util.setSummaryToValue(mTheme);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mTheme) {
            Util.setSummaryToValue(mTheme, newValue);
            if (!mTheme.getValue().equals(newValue)) {
                getActivity().recreate();
            }
            return true;
        } else {
            return false;
        }
    }
}
