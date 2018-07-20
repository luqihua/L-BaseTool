package com.lu.tool.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import com.lu.tool.util.ActivityStackUtil;
import com.lu.tool.widget.SlideBackLayout;

/**
 * 基础activity
 */
public class LBaseActivity extends RxLifeActivity {

    protected LBaseActivity mCurrentActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (isSupportSlideBack()) {
            new SlideBackLayout(this).attach2Activity(this, null);
        }
        super.onCreate(savedInstanceState);
        mCurrentActivity = this;
    }

    /**
     * 是否支持滑动返回
     *
     * @return
     */
    protected boolean isSupportSlideBack() {
        return false;
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


    /**
     * 返回键
     */
    protected void onKeyBack() {
        finish();
    }
}
