package com.tetherkick;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.widget.Toast;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;

/**
 * 本体。現在のテザリング状態を判定し、
 * ON なら「補助OFF」、OFF なら「補助ON」を開いて即終了する。
 * ルーチン側は「補助ON/OFF が開いたとき」だけを条件にすればよく、
 * テザリング状態を条件にしないので ON→OFF の往復が起きない。
 */
public class ToggleActivity extends Activity {

    private static final String PKG_ON = "com.tetherkick.on";
    private static final String PKG_OFF = "com.tetherkick.off";
    private static final String HOLD = "com.tetherkick.HoldActivity";

    // WifiManager の AP 状態定数（非公開 API の値）
    private static final int AP_DISABLING = 10;
    private static final int AP_DISABLED = 11;
    private static final int AP_ENABLING = 12;
    private static final int AP_ENABLED = 13;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);

        Boolean on = isHotspotOn();
        if (on == null) {
            Toast.makeText(this, R.string.err_detect, Toast.LENGTH_SHORT).show();
            finishQuietly();
            return;
        }

        Intent i = new Intent(Intent.ACTION_MAIN)
                .setComponent(new ComponentName(on ? PKG_OFF : PKG_ON, HOLD))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        try {
            startActivity(i);
        } catch (Exception e) {
            Toast.makeText(this, R.string.err_helper, Toast.LENGTH_LONG).show();
        }
        finishQuietly();
    }

    private void finishQuietly() {
        finish();
        overridePendingTransition(0, 0);
    }

    /** true=ON（起動中含む） / false=OFF（停止中含む） / null=判定不能 */
    private Boolean isHotspotOn() {
        // 1) WifiManager#getWifiApState（非公開 API。使えれば最も正確）
        try {
            WifiManager wm = (WifiManager) getApplicationContext()
                    .getSystemService(Context.WIFI_SERVICE);
            int state = (Integer) WifiManager.class.getMethod("getWifiApState").invoke(wm);
            if (state == AP_ENABLED || state == AP_ENABLING) return true;
            if (state == AP_DISABLED || state == AP_DISABLING) return false;
        } catch (Throwable ignored) {
            // 非公開 API がブロックされている場合は 2) へ
        }

        // 2) ホットスポット用ネットワークインターフェース（Galaxy は swlan0）に
        //    IPv4 アドレスが付いているかで判定
        try {
            for (NetworkInterface ni : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                String name = ni.getName();
                if (!(name.startsWith("swlan") || name.startsWith("ap")
                        || name.equals("softap0"))) continue;
                if (!ni.isUp()) continue;
                for (InetAddress a : Collections.list(ni.getInetAddresses())) {
                    if (a instanceof Inet4Address) return true;
                }
            }
            return false;
        } catch (Throwable e) {
            return null;
        }
    }
}
