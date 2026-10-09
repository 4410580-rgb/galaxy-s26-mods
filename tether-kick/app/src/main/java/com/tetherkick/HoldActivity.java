package com.tetherkick;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

/**
 * 補助アプリ（ON用 / OFF用）。
 * 「モードとルーチン」の「アプリ起動時」に検知させるため、一定時間だけ前面に留まる。
 * 窓は 1px・透明・タッチ素通しなので、その間も下のアプリを普通に操作できる。
 */
public class HoldActivity extends Activity {

    // ルーチンの検知遅れを吸収する時間。状態条件を使わないので長めでも往復しない
    private static final long HOLD_MS = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);

        Window w = getWindow();
        w.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.width = 1;
        lp.height = 1;
        lp.gravity = Gravity.TOP | Gravity.START;
        w.setAttributes(lp);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            finish();
            overridePendingTransition(0, 0);
        }, HOLD_MS);
    }
}
