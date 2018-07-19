package com.lu.tool.widget.banner;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.lu.tool.widget.banner.indicator.DotIndicator;
import com.lu.tool.widget.banner.indicator.IIndicator;

import java.lang.reflect.Field;
import java.util.List;

import lu.basetool.R;


/**
 * author: luqihua
 * date:2018/7/13
 * description:
 **/
public class Banner extends FrameLayout implements ViewPager.OnPageChangeListener,
        IBannerClickListener {
    private Context mContext;
    //轮播的viewpager
    private ViewPager mBannerPager;
    //指标相关view
    private IIndicator mIndicator;
    private FrameLayout.LayoutParams mIndicatorLayoutParams;
    //轮播控制参数
    private int mInterval = Constants.DEFAULT_BANNER_INTERVAL;
    private int mScrollTime = Constants.DEFAULT_BANNER_SCROLL_TIME;
    private int mIndicatorMargin = Constants.DEFAULT_INDICATOR_MARGIN;
    private ViewPager.PageTransformer mTransformer;

    private List<?> mData;
    private BannerAdapter mBannerAdapter;
    private IBannerCreator mBannerCreator;
    private IBannerClickListener mBannerClickListener;
    //定时任务
    private Runnable mTask = new Runnable() {
        @Override
        public void run() {
            mBannerPager.setCurrentItem(mBannerPager.getCurrentItem() + 1);
            postDelayed(this, mInterval);
        }
    };


    public Banner(@NonNull Context context) {
        this(context, null);
    }

    public Banner(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        LayoutInflater.from(context).inflate(R.layout.wiget_banner, this);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.Banner);
        if (ta != null) {
            try {
                final String pageCreatorClass = ta.getString(R.styleable.Banner_banner_creator);
                Class<IBannerCreator> c = (Class<IBannerCreator>) Class.forName(pageCreatorClass.trim());
                mBannerCreator = c.newInstance();
            } catch (Exception e) {
                throw new BannerException("you need to set a pageCreator by use app:page_creator=\"your class path\"");
            }
            ta.recycle();
        }
        initView();
        initPager();
        initIndicator();
    }

    private void initView() {
        mBannerPager = findViewById(R.id.vp_banner_pager);
        mBannerPager.addOnPageChangeListener(this);
    }

    private void initPager() {
        if (mTransformer != null) {
            mBannerPager.setPageTransformer(true, mTransformer);
        }
        try {
            Field field = mBannerPager.getClass().getDeclaredField("mScroller");
            field.setAccessible(true);
            BannerScroller bannerScroller = new BannerScroller(mContext);
            bannerScroller.setDuration(mScrollTime);
            field.set(mBannerPager, bannerScroller);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void initIndicator() {
        mIndicator = new DotIndicator(mContext);
        mIndicatorLayoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mIndicatorLayoutParams.gravity = Gravity.CENTER | Gravity.BOTTOM;
        mIndicatorLayoutParams.setMargins(mIndicatorMargin, mIndicatorMargin, mIndicatorMargin, mIndicatorMargin);
        mIndicator.setLayoutParams(mIndicatorLayoutParams);
        addView((View) mIndicator);
    }

    //重写这个方法是为了解决手指滑动viewpager和定时任务控制viewpager切换冲突问题
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                start();
                break;
            default:
                stop();
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public void onPageScrolled(int position, float offset, int offsetPixels) {
        mIndicator.onPageScrolled(position, offset, offsetPixels);
    }

    @Override
    public void onPageSelected(int position) {
        //viewpager切换完成更新指示器
        mIndicator.onPageSelected(position % mData.size());
    }

    @Override
    public void onPageScrollStateChanged(int position) {
        mIndicator.onPageScrollStateChanged(position);
    }


    /*===========================对外开放方法============================*/

    /**
     * 开启轮播
     */
    public void start() {
        if (mBannerAdapter == null || mBannerAdapter.getCount() == 0) return;
        stop();
        postDelayed(mTask, mInterval);
    }

    /**
     * 停止轮播
     */
    public void stop() {
        removeCallbacks(mTask);
    }

    /**
     * 设置控制指示器
     */
    public void setIndicator(IIndicator indicator) {
        if (this.mIndicator != null) {
            removeView((View) mIndicator);
            mIndicator = null;
        }
        this.mIndicator = indicator;
        this.mIndicator.setLayoutParams(mIndicatorLayoutParams);
        addView((View) this.mIndicator);
    }

    /**
     * 设置控制轮播的参数
     *
     * @param config
     */
    public void setBannerConfig(BannerConfig config) {
        this.mScrollTime = config.getScrollTime();
        //正确的间隔时间应该是要加上轮播切换的时间的，这样才会精确
        this.mInterval = config.getInterval() + mScrollTime;
        this.mTransformer = config.getBannerTransformer();
        this.mIndicatorLayoutParams.gravity = config.getIndicatorGravity() | Gravity.BOTTOM;
        initPager();
    }

    /**
     * 设置数据源
     *
     * @param data
     */
    public void setData(List<?> data) {
        stop();
        mBannerPager.removeAllViews();
        this.mData = data;
        mBannerAdapter = new BannerAdapter(mContext, mBannerCreator, data, this);
        mBannerPager.setAdapter(mBannerAdapter);
        mBannerPager.setCurrentItem(mData.size() * BannerAdapter.CYCLE / 2);
        this.mIndicator.initIndicator(mData.size(), 0);
        start();
    }

    public void setBannerClickListener(IBannerClickListener listener) {
        this.mBannerClickListener = listener;
    }

    @Override
    public void onClick(Object o) {
        if (mBannerClickListener != null) {
            mBannerClickListener.onClick(o);
        }
    }
}
