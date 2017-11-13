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


package com.droidvnteam.hexagonrom;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Bundle;

import java.util.Arrays;

import com.droidvnteam.hexagonrom.utils.Util;

public class LauncherActivity extends SettingsActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initShortcutManager();
    }

    private void initShortcutManager() {
        final ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);

        if (Util.isPackageEnabled(Constants.HEXAGON_OTA_PACKAGE, this.getPackageManager())) {
            // Intent for launching HEX OTA
            final Intent INTENT_OTA = new Intent().setComponent(new ComponentName(
                    Constants.HEXAGON_OTA_PACKAGE, Constants.HEXAGON_OTA_ACTIVITY));
            INTENT_OTA.setAction(Intent.ACTION_VIEW);

            ShortcutInfo hexagonotaShortcut = new ShortcutInfo.Builder(this, "hexagonota")
                    .setShortLabel(getString(R.string.hexagon_ota_title))
                    .setLongLabel(getString(R.string.hexagon_ota_title))
                    .setIcon(Icon.createWithResource(this,
                            R.drawable.ic_launcher_shortcut_ota))
                    .setIntent(INTENT_OTA)
                    .setRank(0)
                    .build();
            shortcutManager.setDynamicShortcuts(Arrays.asList(hexagonotaShortcut));
        } else {
            ShortcutInfo downloadsShortcut = new ShortcutInfo.Builder(this, "downloads")
                    .setShortLabel(getString(R.string.hexagon_downloads_title))
                    .setLongLabel(getString(R.string.hexagon_downloads_title))
                    .setIcon(Icon.createWithResource(this,
                            R.drawable.ic_launcher_shortcut_downloads))
                    .setIntent(new Intent(Intent.ACTION_VIEW, Uri.parse(
                            Util.getDownloadLinkForDevice(this))))
                    .setRank(0)
                    .build();
            shortcutManager.setDynamicShortcuts(Arrays.asList(downloadsShortcut));
        }
    }
}
