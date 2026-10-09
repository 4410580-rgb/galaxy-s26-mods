package com.tetherkick;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

/**
 * 何もしない起動専用アプリ。
 * 「モードとルーチン」の「アプリを開いたとき」条件で検知させるため、
 * 透明な画面を一瞬だけ前面に出してから閉じる。
 */
public class KickActivity extends Activity {

    // ルーチンに検知されるよう前面に留まる時間（ミリ秒）。反応しない場合は増やす
    private static final long HOLD_MS = 800;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.makeText(this, getString(R.string.toast), Toast.LENGTH_SHORT).show();
        new Handler(Looper.getMainLooper()).postDelayed(this::finish, HOLD_MS);
    }
}
