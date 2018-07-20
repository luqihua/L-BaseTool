package com.lu.tool.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import com.lu.tool.util.ActivityStackUtil;

/**
 * 基础activity
 */
public class CloseableActivity extends AppCompatActivity {

    protected CloseableActivity mCurrentActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentActivity = this;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN
                && keyCode == KeyEvent.KEYCODE_BACK) {
            onKeyBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //关闭整个app
    public static void closeApp() {
        ActivityStackUtil.getInstance().closeApp();
    }

    protected void onKeyBack() {
        finish();
    }
}
