package com.samego.alic.monitor.wechat.wechatrecord.utils;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

/**
 * 状态栏工具包
 * Created by alic on 16-4-9.
 */
public class StatusBarCompat {

    public static final String TAG = StatusBarCompat.class.getName();

    //定义状态栏的背景颜色常量
    public static final int COLOR_ToolBar_WHITE = Color.parseColor("#FFFFFFFF");
    public static final int COLOR_ToolBar_HIGHTBLUE = Color.parseColor("#ff5ABEDC");
    public static final int COLOR_ToolBar_BLUE = Color.parseColor("#3F51B5");
    public static final int COLOR_ToolBar_DEFAULT_PINK = Color.parseColor("#FFEF4968");

    /**
     * 设置状态栏的颜色  默认颜色
     *
     * @param activity 界面
     */
    public static void setStatusBarColor(Activity activity) {
        setStatusBarColor(activity, COLOR_ToolBar_DEFAULT_PINK);
    }

    /**
     * 设置状态栏的颜色以及透明度
     *
     * @param statusColor 工具栏的颜色
     * @param alpha       0 - 255
     */
    public static void setStatusBarColor(Activity activity, int statusColor, int alpha) {
        setStatusBarColor(activity, calculateStatusBarColor(statusColor, alpha));
    }

    /**
     * 设置状态栏状态颜色 自定义颜色
     *
     * @param activity    界面
     * @param statusColor 工具栏的颜色
     */
    public static void setStatusBarColor(Activity activity, int statusColor) {
        Window window = activity.getWindow();
        ViewGroup mContentView = (ViewGroup) activity.findViewById(Window.ID_ANDROID_CONTENT);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            //First translucent status bar.
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                //After LOLLIPOP not translucent status bar
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                //Then call setStatusBarColor.
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(statusColor);
                //set child View not fill the system window
                View mChildView = mContentView.getChildAt(0);
                if (mChildView != null) {
                    ViewCompat.setFitsSystemWindows(mChildView, true);
                }
            } else {
                int statusBarHeight = getStatusBarHeight(activity);

                View mChildView = mContentView.getChildAt(0);
                if (mChildView != null) {
                    FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mChildView.getLayoutParams();
                    //if margin top has already set, just skip.
                    if (lp != null && lp.topMargin < statusBarHeight && lp.height != statusBarHeight) {
                        //do not use fitsSystemWindows
                        ViewCompat.setFitsSystemWindows(mChildView, false);
                        //add margin to content
                        lp.topMargin += statusBarHeight;
                        mChildView.setLayoutParams(lp);
                    }
                }

                //Before LOLLIPOP create a fake status bar View.
                View statusBarView = mContentView.getChildAt(0);
                if (statusBarView != null && statusBarView.getLayoutParams() != null && statusBarView.getLayoutParams().height == statusBarHeight) {
                    //if fake status bar view exist, we can setBackgroundColor and return.
                    statusBarView.setBackgroundColor(statusColor);
                    return;
                }
                statusBarView = new View(activity);
                ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, statusBarHeight);
                statusBarView.setBackgroundColor(statusColor);
                mContentView.addView(statusBarView, 0, lp);
            }
        }
    }

    /**
     * 透明状态栏
     * 如果使用了全屏 Activity ,记得调用StatusBarCompat.translucentStatusBar(activity);
     *
     * @param activity 界面
     */
    public static void translucentStatusBar(Activity activity) {
        translucentStatusBar(activity, false);
    }

    /**
     * 透明状态栏
     *
     * @param hideStatusBarBackground 当SDK > 21时, 是否需要隐藏状态栏的背景色(默认的黑色背景)
     */
    public static void translucentStatusBar(Activity activity, boolean hideStatusBarBackground) {
        Window window = activity.getWindow();
        ViewGroup mContentView = (ViewGroup) activity.findViewById(Window.ID_ANDROID_CONTENT);

        //set child View not fill the system window
        View mChildView = mContentView.getChildAt(0);
        if (mChildView != null) {
            ViewCompat.setFitsSystemWindows(mChildView, false);
        }

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            int statusBarHeight = getStatusBarHeight(activity);

            //透明状态栏
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                //After LOLLIPOP just set LayoutParams.
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                if (hideStatusBarBackground) {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    window.setStatusBarColor(Color.parseColor("#00000000"));
                } else {
                    window.setStatusBarColor(Color.parseColor("#55000000"));
                }
                //must call requestLayout, otherwise it will have space in screen bottom
                if (mChildView != null) {
                    ViewCompat.requestApplyInsets(mChildView);
                }
            } else {
                if (mChildView != null && mChildView.getLayoutParams() != null && mChildView.getLayoutParams().height == statusBarHeight) {
                    //Before LOLLIPOP need remove fake status bar view.
                    mContentView.removeView(mChildView);
                    mChildView = mContentView.getChildAt(0);
                }
                if (mChildView != null) {
                    FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mChildView.getLayoutParams();
                    //cancel the margin top
                    if (lp != null && lp.topMargin > statusBarHeight) {
                        lp.topMargin -= statusBarHeight;
                        mChildView.setLayoutParams(lp);
                    }
                }
            }
        }
    }

    /**
     * 获取状态栏的高度
     *
     * @param context 上下文
     * @return int
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resId > 0) {
            result = context.getResources().getDimensionPixelOffset(resId);
        }
        return result;
    }

    //Get alpha color
    private static int calculateStatusBarColor(int color, int alpha) {
        float a = 1 - alpha / 255f;
        int red = color >> 16 & 0xff;
        int green = color >> 8 & 0xff;
        int blue = color & 0xff;
        red = (int) (red * a + 0.5);
        green = (int) (green * a + 0.5);
        blue = (int) (blue * a + 0.5);
        return 0xff << 24 | red << 16 | green << 8 | blue;
    }

    /**
     * 隐藏状态栏
     * 一定要在setContentView前
     * @param activity 界面
     * @param hideTitle 是否隐藏标题栏
     */
    public static void hideStatusBar(Activity activity, boolean hideTitle) {
        if (hideTitle)
            //隐藏标题栏
            activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //隐藏状态栏
        //定义全屏参数
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //获得当前窗体对象
        Window window = activity.getWindow();
        //设置当前窗体为全屏显示
        window.setFlags(flag, flag);
    }

    /**
     * 非全屏处理
     * @param activity 界面
     */
    public static void displayStatusBar(Activity activity){
        //获得当前窗体对象
        Window window = activity.getWindow();
        //设置当前窗体为非全屏显示
        window.setFlags(0, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}