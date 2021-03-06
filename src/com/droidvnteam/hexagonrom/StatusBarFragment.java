package com.droidvnteam.hexagonrom;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;
import net.margaritov.preference.colorpicker.ColorPickerPreference;
import android.support.v4.app.Fragment;

import com.droidvnteam.R;
import com.droidvnteam.hexagonrom.utils.Helpers;

public class StatusBarFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivity().getFragmentManager().beginTransaction()
                .replace(R.id.content_main, new SettingsPreferenceFragment())
                .commit();
    }

    public static class SettingsPreferenceFragment extends PreferenceFragment
            implements OnPreferenceChangeListener {

        private static final String PREF_CATEGORY_INDICATORS = "pref_category_indicators";
        private static final String PREF_TRAFFIC = "traffic";
        private static final String KEY_SHOW_FOURG = "show_fourg";
        private static final String PREF_BATTERY_BAR = "batterybar";
        private static final String KEY_HEXAGON_LOGO_COLOR = "status_bar_hex_logo_color";
        private static final String KEY_HEXAGON_LOGO_STYLE = "status_bar_hex_logo_style";
        private static final String PREF_CARRIE_LABEL = "carrierlabel";
        private static final String PREF_TICKER = "ticker";
        private static final String PREF_STATUS_BAR_WEATHER = "status_bar_weather";
        private static final String WEATHER_SERVICE_PACKAGE = "org.omnirom.omnijaws";

        private Preference mTraffic;
        private SwitchPreference mShowFourG;
        private Preference mBatteryBar;
        private ColorPickerPreference mHexagonLogoColor;
        private ListPreference mHexagonLogoStyle;
        private Preference mCarrierLabel;
        private Preference mTicker;
        private ListPreference mStatusBarWeather;

        public SettingsPreferenceFragment() {
        }


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.statusbar_layout);

            PreferenceScreen prefSet = getPreferenceScreen();
            PreferenceCategory categoryIndicators =
                    (PreferenceCategory) prefSet.findPreference(PREF_CATEGORY_INDICATORS);
            final ContentResolver resolver = getActivity().getContentResolver();
            Context context = getActivity();
            ConnectivityManager cm = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);

            mTraffic = prefSet.findPreference(PREF_TRAFFIC);
            mBatteryBar = prefSet.findPreference(PREF_BATTERY_BAR);
            mCarrierLabel = prefSet.findPreference(PREF_CARRIE_LABEL);
            mTicker = prefSet.findPreference(PREF_TICKER);

            // Show 4G
            mShowFourG = (SwitchPreference) prefSet.findPreference(KEY_SHOW_FOURG);
            PackageManager pm = getActivity().getPackageManager();
            if (!pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {
                categoryIndicators.removePreference(mShowFourG);
            }

            mHexagonLogoStyle = (ListPreference) findPreference(KEY_HEXAGON_LOGO_STYLE);
            int hexLogoStyle = Settings.System.getIntForUser(resolver,
                    Settings.System.STATUS_BAR_HEXAGON_LOGO_STYLE, 0,
                    UserHandle.USER_CURRENT);
            mHexagonLogoStyle.setValue(String.valueOf(hexLogoStyle));
            mHexagonLogoStyle.setSummary(mHexagonLogoStyle.getEntry());
            mHexagonLogoStyle.setOnPreferenceChangeListener(this);

            // Hexagon logo color
            mHexagonLogoColor =
                (ColorPickerPreference) prefSet.findPreference(KEY_HEXAGON_LOGO_COLOR);
            mHexagonLogoColor.setOnPreferenceChangeListener(this);
            int intColor = Settings.System.getInt(resolver,
                    Settings.System.STATUS_BAR_HEXAGON_LOGO_COLOR, 0xffffffff);
            String hexColor = String.format("#%08x", (0xffffffff & intColor));
            mHexagonLogoColor.setSummary(hexColor);
            mHexagonLogoColor.setNewPreviewColor(intColor);

            if (!cm.isNetworkSupported(ConnectivityManager.TYPE_MOBILE)) {
                categoryIndicators.removePreference(mCarrierLabel);
            }

            // Status bar weather
            mStatusBarWeather = (ListPreference) prefSet.findPreference(PREF_STATUS_BAR_WEATHER);
            if (mStatusBarWeather != null && (!Helpers.isPackageInstalled(WEATHER_SERVICE_PACKAGE, pm))) {
                categoryIndicators.removePreference(mStatusBarWeather);
            } else {
                int temperatureShow = Settings.System.getIntForUser(resolver,
                    Settings.System.STATUS_BAR_SHOW_WEATHER_TEMP, 0,
                    UserHandle.USER_CURRENT);
                mStatusBarWeather.setValue(String.valueOf(temperatureShow));
                if (temperatureShow == 0) {
                    mStatusBarWeather.setSummary(R.string.statusbar_weather_summary);
                } else {
                    mStatusBarWeather.setSummary(mStatusBarWeather.getEntry());
                }
                mStatusBarWeather.setOnPreferenceChangeListener(this);
            }
        }

        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
            if (preference == mTraffic) {
                Intent intent = new Intent(getActivity(), Traffic.class);
                getActivity().startActivity(intent);
                return true;
            } else if (preference == mBatteryBar) {
                Intent intent = new Intent(getActivity(), BatteryBar.class);
                getActivity().startActivity(intent);
                return true;
            } else if (preference == mCarrierLabel) {
                Intent intent = new Intent(getActivity(), CarrierLabel.class);
                getActivity().startActivity(intent);
            } else if (preference == mTicker) {
                Intent intent = new Intent(getActivity(), Ticker.class);
                getActivity().startActivity(intent);
            }
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            ContentResolver resolver = getActivity().getContentResolver();
            if (preference == mHexagonLogoColor) {
                String hex = ColorPickerPreference.convertToARGB(
                        Integer.parseInt(String.valueOf(newValue)));
                preference.setSummary(hex);
                int intHex = ColorPickerPreference.convertToColorInt(hex);
                Settings.System.putInt(resolver,
                        Settings.System.STATUS_BAR_HEXAGON_LOGO_COLOR, intHex);
                return true;
            } else if (preference == mHexagonLogoStyle) {
                int hexLogoStyle = Integer.valueOf((String) newValue);
                int index = mHexagonLogoStyle.findIndexOfValue((String) newValue);
                Settings.System.putIntForUser(
                        resolver, Settings.System.STATUS_BAR_HEXAGON_LOGO_STYLE, hexLogoStyle,
                        UserHandle.USER_CURRENT);
                mHexagonLogoStyle.setSummary(
                        mHexagonLogoStyle.getEntries()[index]);
                return true;
            } else if (preference == mStatusBarWeather) {
                int temperatureShow = Integer.valueOf((String) newValue);
                int index = mStatusBarWeather.findIndexOfValue((String) newValue);
                Settings.System.putIntForUser(resolver,
                        Settings.System.STATUS_BAR_SHOW_WEATHER_TEMP,
                        temperatureShow, UserHandle.USER_CURRENT);
                if (temperatureShow == 0) {
                    mStatusBarWeather.setSummary(R.string.statusbar_weather_summary);
                } else {
                    mStatusBarWeather.setSummary(
                            mStatusBarWeather.getEntries()[index]);
                }
                return true;
            }
            return false;
        }
    }
}
