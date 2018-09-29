package com.maapuu.mereca.util;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TabLayout;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.maapuu.mereca.R;
import com.maapuu.mereca.base.BaseApplication;

import java.lang.reflect.Field;

/**
 *
 * 界面相关的工具类
 *
 * Created by Jia on 2017/5/15.
 */

public class UIUtils {

    /** 获取上下文 */
    public static Context getContext() {
        return BaseApplication.getInstance();
    }

    public static Resources getResources() {
        return getContext().getResources();
    }

    /** 获取字符数组 */
    public static String[] getStringArray(int id) {
        return getResources().getStringArray(id);
    }

    /** 获取颜色id */
    public static int getColor(int colorId) {
        return getResources().getColor(colorId);
    }

    /** 根据id获取尺寸 */
    public static int getDimens(int id) {
        return getResources().getDimensionPixelSize(id);
    }

    public static View inflate(int id) {
        return View.inflate(getContext(), id, null);
    }

    public static String getString(int id) {
        return getResources().getString(id);
    }

    public static Drawable getDrawable(int id) {
        return getResources().getDrawable(id);
    }

    public static void startActivity(Context activity, Class activityClass){
        Intent intent = new Intent(activity,activityClass);
        activity.startActivity(intent);
    }

    public static void startActivity(Context activity, Intent intent){
        activity.startActivity(intent);
    }

    /**
     * 将TabLayout全屏宽等分
     * @param tabLayout 控件对象
     * @param columns 列数
     */
    public static void fitTab(final TabLayout tabLayout, final int columns) {
        fitTab(tabLayout,columns,0);
    }

    /**
     * 将TabLayout全屏宽等分
     * @param tabLayout 控件对象
     * @param columns 列数
     * @param margin 左右间距(单位dp)
     */
    public static void fitTab(final TabLayout tabLayout, final int columns, final int margin) {
        //了解源码得知 线的宽度是根据 tabView的宽度来设置的
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                try {
                    //拿到tabLayout的mTabStrip属性
                    LinearLayout mTabStrip = (LinearLayout) tabLayout.getChildAt(0);

                    int dpMargin = dip2px(tabLayout.getContext(), margin);

                    for (int i = 0; i < mTabStrip.getChildCount(); i++) {
                        View tabView = mTabStrip.getChildAt(i);

                        //拿到tabView的mTextView属性  tab的字数不固定一定用反射取mTextView
                        Field mTextViewField = tabView.getClass().getDeclaredField("mTextView");
                        mTextViewField.setAccessible(true);

                        TextView mTextView = (TextView) mTextViewField.get(tabView);

                        tabView.setPadding(dpMargin, 0, dpMargin, 0);

                        //因为我想要的效果是   字多宽线就多宽，所以测量mTextView的宽度
                        int width = 0;
                        //width = mTextView.getWidth();
                        width = getWidthPX()/columns;
                        if (width == 0) {
                            mTextView.measure(0, 0);
                            width = mTextView.getMeasuredWidth();
                        }

                        //设置tab左右间距为10dp  注意这里不能使用Padding 因为源码中线的宽度是根据 tabView的宽度来设置的
                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tabView.getLayoutParams();
                        params.width = width;
                        params.leftMargin = dpMargin;
                        params.rightMargin = dpMargin;
                        tabView.setLayoutParams(params);

                        tabView.invalidate();
                    }

                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 屏幕宽高 dp
     * @param context
     * @return
     */
    public static int getWidthDP(Context context){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (wm.getDefaultDisplay().getWidth() / scale + 0.5f);
    }
    public static int getHeightDP(Context context){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (wm.getDefaultDisplay().getHeight() / scale + 0.5f);
    }
    /**
     * 屏幕宽高 px
     * @param context
     * @return
     */
    public static int getWidthPX(Context context){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getWidth();
    }

    public static int getWidthPX(){
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getWidth();
    }

    public static int getHeightPX(Context context){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        final float scale = context.getResources().getDisplayMetrics().density;
        return wm.getDefaultDisplay().getHeight();
    }
    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     *
     * @param pxValue
     *            （DisplayMetrics类中属性density）
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param dipValue
     *            （DisplayMetrics类中属性density）
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
     *            （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     *            （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 加载图片
     * @param context
     * @param photoUrl
     * @param iv
     * @param resId 默认图片
     */
    public static void loadImg(Context context,String photoUrl,ImageView iv,int resId) {
        RequestOptions options = new RequestOptions()
                .placeholder(resId).error(resId);
        Glide.with(context).load(photoUrl).apply(options).into(iv);
    }

    /**
     * 加载图片
     * @param context
     * @param photoUrl
     * @param iv
     */
    public static void loadImg(Context context,String photoUrl,ImageView iv) {
//        RequestOptions options = new RequestOptions()
//                .placeholder(R.mipmap.nopic).error(R.mipmap.nopic);
        RequestOptions options = new RequestOptions().error(R.mipmap.nopic);
        Glide.with(context).load(photoUrl).apply(options).into(iv);
    }

    public static void loadIcon(Context context,String photoUrl,ImageView iv) {
        RequestOptions options = new RequestOptions().error(R.mipmap.morentouxiang);
        Glide.with(context).load(photoUrl).apply(options).into(iv);
    }

    public static void loadImg(Context context,String photoUrl,ImageView iv,boolean isCircle) {
        RequestOptions options = new RequestOptions().error(R.mipmap.nopic);
        if(isCircle){
            options.circleCrop();
        }
        Glide.with(context).load(photoUrl).apply(options).into(iv);
    }
    public static void loadImg(Context context,String photoUrl,ImageView iv,boolean isCircle,int resource) {
        RequestOptions options = new RequestOptions().error(resource);
        if(isCircle){
            options.circleCrop();
        }
        Glide.with(context).load(photoUrl).apply(options).into(iv);
    }
}
