package com.tetherkick;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

/**
 * 何もしない起動専用アプリ。
 * 「モードとルーチン」の「アプリ起動時」条件で検知させるため、
 * 透明な画面を一瞬だけ前面に出してから閉じる。
 * ON/OFF の判定はルーチン側（現在のテザリング状態を条件にする）で行う。
 */
public class KickActivity extends Activity {

    // 前面に留まる時間（ミリ秒）。
    // 短すぎるとルーチンが検知しない／長すぎると ON→OFF の往復が起きうる
    private static final long HOLD_MS = 800;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Handler(Looper.getMainLooper()).postDelayed(this::finish, HOLD_MS);
    }
}
