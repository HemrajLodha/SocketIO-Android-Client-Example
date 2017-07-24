package com.hems.socketio.client.utils;

import android.content.Context;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by planet on 5/2/2017.
 */

public class ShortcutUtils {
    private static final int MAX_SHORTCUT_LENGTH = 5;
    private Context context;
    private static ShortcutUtils shortcutUtils;

    private ShortcutUtils(Context context) {
        this.context = context;
    }

    public static ShortcutUtils getInstance(Context context) {
        if (shortcutUtils == null) {
            shortcutUtils = new ShortcutUtils(context);
        }
        return shortcutUtils;
    }

    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    public void createShortcuts() {
        ShortcutManager soManager = context.getSystemService(ShortcutManager.class);
        int i = 0;
        List<ShortcutInfo> shortcutInfos = new ArrayList<>();
        ShortcutInfo shortcutInfo = new ShortcutInfo.Builder(context, "shortcut" + i)
                //.setIntent()
                .setShortLabel("title")
                .setLongLabel("long title")
                //.setDisabledMessage("Login to open this")
                //.setIcon(Icon.createWithResource(context, item.getIcon()))
                .build();
        shortcutInfos.add(shortcutInfo);
        soManager.removeAllDynamicShortcuts();
        soManager.setDynamicShortcuts(shortcutInfos);
        enablePinnedShortcuts(shortcutInfos);
    }

    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    public void removeShortcuts() {
        ShortcutManager soManager = context.getSystemService(ShortcutManager.class);
        soManager.removeAllDynamicShortcuts();
    }

    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    public void enablePinnedShortcuts(boolean enable) {
        ShortcutManager soManager = context.getSystemService(ShortcutManager.class);
        List<String> ids = new ArrayList<>();
        for (ShortcutInfo shortcutInfo : soManager.getPinnedShortcuts()) {
            ids.add(shortcutInfo.getId());
        }
        if (enable) {
            soManager.enableShortcuts(ids);
        } else {
            soManager.disableShortcuts(ids);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    public void enablePinnedShortcuts(List<ShortcutInfo> shortcutInfos) {
        ShortcutManager soManager = context.getSystemService(ShortcutManager.class);
        List<ShortcutInfo> pinnedShortcuts = soManager.getPinnedShortcuts();
        if (pinnedShortcuts.size() == 0) {
            return;
        }

        for (ShortcutInfo item : pinnedShortcuts) {
            for (ShortcutInfo shortcutInfo : shortcutInfos) {
                if (shortcutInfo.getId().equalsIgnoreCase(item.getId())) {
                    soManager.enableShortcuts(Collections.singletonList(item.getId()));
                    soManager.updateShortcuts(Collections.singletonList(shortcutInfo));
                    break;
                }
            }
        }

    }

}
