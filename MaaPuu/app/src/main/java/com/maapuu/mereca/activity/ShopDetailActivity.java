package com.maapuu.mereca.activity;

import android.content.Intent;
import android.graphics.Outline;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.gyf.barlibrary.ImmersionBar;
import com.maapuu.mereca.R;
import com.maapuu.mereca.base.AppManager;
import com.maapuu.mereca.base.BaseActivity;
import com.maapuu.mereca.constant.UrlUtils;
import com.maapuu.mereca.fragment.HomeFragment;
import com.maapuu.mereca.fragment.homechild.HomeCampaignsFragment;
import com.maapuu.mereca.fragment.homechild.HomeGoodsFragment_v2;
import com.maapuu.mereca.fragment.homechild.HomeIndexFragment;
import com.maapuu.mereca.fragment.homechild.HomeProjectFragment_v2;
import com.maapuu.mereca.fragment.homechild.HomeTeamFragment;
import com.maapuu.mereca.util.HttpModeBase;
import com.maapuu.mereca.util.LoginUtil;
import com.maapuu.mereca.util.StringUtils;
import com.maapuu.mereca.util.ToastUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;
import com.scwang.smartrefresh.layout.util.DensityUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by dell on 2018/3/5.
 */

public class ShopDetailActivity extends BaseActivity {
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.classics_header)
    ClassicsHeader classicsHeader;
    @BindView(R.id.txt_left_1)
    TextView txtLeft1;
    @BindView(R.id.txt_title_1)
    TextView txtTitle1;
    @BindView(R.id.iv_bg)
    ImageView ivBg;
    @BindView(R.id.app_bar)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.txt_index)
    TextView txtIndex;
    @BindView(R.id.txt_project)
    TextView txtProject;
    @BindView(R.id.txt_team)
    TextView txtTeam;
    @BindView(R.id.txt_campaigns)
    TextView txtCampaigns;
    @BindView(R.id.txt_goods)
    TextView txtGoods;
    @BindView(R.id.iv_shop)
    SimpleDraweeView ivShop;
    @BindView(R.id.txt_shop_name)
    TextView txtShopName;
    @BindView(R.id.txt_shop_locate)
    TextView txtShopLocate;
    @BindView(R.id.txt_fans)
    TextView txtFans;
    @BindView(R.id.txt_attection)
    TextView txtAttection;
    @BindView(R.id.iv_shop_phone)
    ImageView ivShopPhone;

    private TextView[] tvs;

    private FragmentManager fragmentManager;
    private HomeIndexFragment indexFragment;
    private HomeProjectFragment_v2 projectFragment;
    private HomeTeamFragment teamFragment;
    private HomeCampaignsFragment campaignsFragment;
    private HomeGoodsFragment_v2 goodsFragment;

    private AlertView alertView;

    private String shop_id;
    private String shop_phone;
    private String index_info;
    private String is_attention;
    private String attention_num;

    private int mTab = 0;
    private int mOffset = 0;

    public static ShopDetailActivity activity;

    @Override
    public void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_shop_detail);
        activity = this;
    }

    @Override
    public void initView() {
        ImmersionBar.with(this).fitsSystemWindows(false).transparentStatusBar().titleBar(toolbar).init();
        txtLeft1.setTypeface(StringUtils.getFont(mContext));
        shop_id = getIntent().getStringExtra("shop_id");
        txtTitle1.setVisibility(View.GONE);

        classicsHeader.getLastUpdateText().setTextColor(getResources().getColor(R.color.white));
        classicsHeader.getTitleText().setTextColor(getResources().getColor(R.color.white));
        classicsHeader.setArrowResource(R.mipmap.refresh_arrow);//设置箭头资源

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ivShopPhone.setOutlineProvider(new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0,0,view.getWidth(),view.getHeight());
                }
            });
        }

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                switch (mTab){
                    case 0:
                        initData();
                        break;
                    case 1:
                        projectFragment.refresh(shop_id);
                        break;
                    case 2:
                        teamFragment.refresh(shop_id);
                        break;
                    case 3:
                        campaignsFragment.refresh(shop_id);
                        break;
                    case 4:
                        goodsFragment.refresh(shop_id);
                        break;
                }
            }
        });
        refreshLayout.setOnMultiPurposeListener(new SimpleMultiPurposeListener() {
            @Override
            public void onHeaderPulling(RefreshHeader header, float percent, int offset, int bottomHeight, int extendHeight) {
                mOffset = offset;
                ivBg.setTranslationY(mOffset);
            }
            @Override
            public void onHeaderReleasing(RefreshHeader header, float percent, int offset, int bottomHeight, int extendHeight) {
                mOffset = offset;
                ivBg.setTranslationY(mOffset);
            }
        });

        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
//                if(Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()){
//                }
                if(verticalOffset != 0 ){
                    ivBg.setTranslationY(verticalOffset);
                }
                //200是appbar的高度
                if (Math.abs(verticalOffset) == DensityUtil.dp2px(180) - toolbar.getHeight()) {//关闭
                    toolbar.setBackgroundColor(getResources().getColor(R.color.white));
                    collapsingToolbarLayout.setContentScrimResource(R.color.white);
                    txtLeft1.setTextColor(getResources().getColor(R.color.text_33));
                    txtTitle1.setTextColor(getResources().getColor(R.color.text_33));
                    txtTitle1.setVisibility(View.VISIBLE);
                } else {  //展开
                    toolbar.setBackgroundColor(getResources().getColor(R.color.transparent));
                    collapsingToolbarLayout.setContentScrimResource(R.color.transparent);
                    txtLeft1.setTextColor(getResources().getColor(R.color.white));
                    txtTitle1.setTextColor(getResources().getColor(R.color.white));
                    txtTitle1.setVisibility(View.GONE);
                }
            }
        });
        tvs = new TextView[]{txtIndex,txtProject,txtTeam,txtCampaigns,txtGoods};
        setHead(0);
        fragmentManager = getSupportFragmentManager();
    }

    private void setHead(int postion) {
        for (int i = 0; i < tvs.length; i++){
            if(i == postion){
                tvs[postion].setSelected(true);
            }else {
                tvs[i].setSelected(false);
            }
        }
    }

    // 隐藏所有的Fragment,避免fragment混乱
    private void hideFragments(FragmentTransaction transaction) {
        if (indexFragment != null) {
            transaction.hide(indexFragment);
        }
        if (projectFragment != null) {
            transaction.hide(projectFragment);
        }
        if (teamFragment != null) {
            transaction.hide(teamFragment);
        }
        if (campaignsFragment != null) {
            transaction.hide(campaignsFragment);
        }
        if (goodsFragment != null) {
            transaction.hide(goodsFragment);
        }
    }

    private void addFragment(int i) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        hideFragments(fragmentTransaction);
        switch (i) {
            case 0:
                if (indexFragment == null) {
                    indexFragment = new HomeIndexFragment();
                    Bundle bundle=new Bundle();
                    bundle.putString("index_info",index_info);
                    bundle.putString("shop_id",shop_id);
                    indexFragment.setArguments(bundle);
                    fragmentTransaction.add(R.id.fl_container, indexFragment);
                } else {
                    fragmentTransaction.show(indexFragment);
                }
                break;
            case 1:
                if (projectFragment == null) {
                    projectFragment = new HomeProjectFragment_v2();
                    Bundle bundle=new Bundle();
                    bundle.putString("shop_id",shop_id);
                    projectFragment.setArguments(bundle);
                    fragmentTransaction.add(R.id.fl_container, projectFragment);
                } else {
                    fragmentTransaction.show(projectFragment);
                }
                break;
            case 2:
                if (teamFragment == null) {
                    teamFragment = new HomeTeamFragment();
                    Bundle bundle=new Bundle();
                    bundle.putString("shop_id",shop_id);
                    teamFragment.setArguments(bundle);
                    fragmentTransaction.add(R.id.fl_container, teamFragment);
                } else {
                    fragmentTransaction.show(teamFragment);
                }
                break;
            case 3:
                if (campaignsFragment == null) {
                    campaignsFragment = new HomeCampaignsFragment();
                    Bundle bundle=new Bundle();
                    bundle.putString("shop_id",shop_id);
                    campaignsFragment.setArguments(bundle);
                    fragmentTransaction.add(R.id.fl_container, campaignsFragment);
                } else {
                    fragmentTransaction.show(campaignsFragment);
                }
                break;
            case 4:
                if (goodsFragment == null) {
                    goodsFragment = new HomeGoodsFragment_v2();
                    Bundle bundle=new Bundle();
                    bundle.putString("shop_id",shop_id);
                    goodsFragment.setArguments(bundle);
                    fragmentTransaction.add(R.id.fl_container, goodsFragment);
                } else {
                    fragmentTransaction.show(goodsFragment);
                }
                break;
        }
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public void initData() {
        mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_1, UrlUtils.shop_detail_get(LoginUtil.getInfo("token"),LoginUtil.getInfo("uid"),shop_id),true);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case HttpModeBase.HTTP_REQUESTCODE_1:
                refreshLayout.finishRefresh();
                try {
                    JSONObject object = new JSONObject((String) msg.obj);
                    if (object.optInt("status") == 1) {
                        JSONObject resultObj = object.optJSONObject("result");
                        JSONObject shopObject = resultObj.optJSONObject("shop_data");
                        index_info = resultObj.optString("shop_data");
                        if(shopObject.has("info") && !StringUtils.isEmpty(shopObject.optString("info"))){
                            JSONObject infoObject = shopObject.optJSONObject("info");
                            txtShopName.setText(infoObject.optString("shop_name"));
                            txtTitle1.setText(infoObject.optString("shop_name"));
                            txtShopLocate.setText(infoObject.optString("city_name"));
                            attention_num = StringUtils.isEmpty(infoObject.optString("attention_num"))?"0":infoObject.optString("attention_num");
                            txtFans.setText("粉丝"+infoObject.optString("attention_num"));
                            shop_phone = infoObject.optString("shop_tel");
                            is_attention = infoObject.optString("is_attention");
                            txtAttection.setText(infoObject.optString("is_attention").equals("1")?"已关注":"关注");
                            if(!StringUtils.isEmpty(infoObject.optString("shop_cover"))){
                                new DownloadImageTask().execute(infoObject.optString("shop_cover")) ;
                            }
                            if(!StringUtils.isEmpty(infoObject.optString("shop_logo"))){
                                ivShop.setImageURI(Uri.parse(infoObject.optString("shop_logo")));
                            }
                        }
                        addFragment(mTab);
                    } else {
                        ToastUtil.show(mContext,object.optString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case HttpModeBase.HTTP_REQUESTCODE_2:
                try {
                    JSONObject object = new JSONObject((String) msg.obj);
                    if (object.optInt("status") == 1) {
                        if(is_attention.equals("1")){
                            is_attention = "0";
//                            ToastUtil.show(mContext,"取消关注成功");
                            txtAttection.setText("关注");
                            attention_num = String.valueOf(Integer.parseInt(attention_num)-1);
                            txtFans.setText("粉丝"+attention_num);
                        }else {
                            is_attention = "1";
//                            ToastUtil.show(mContext,"关注成功");
                            txtAttection.setText("已关注");
                            attention_num = String.valueOf(Integer.parseInt(attention_num)+1);
                            txtFans.setText("粉丝"+attention_num);
                        }
                    } else {
                        ToastUtil.show(mContext,object.optString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case HttpModeBase.HTTP_REQUESTCODE_3:
                try {
                    JSONObject object = new JSONObject((String) msg.obj);
                    if (object.optInt("status") == 1) {
                        ToastUtil.show(mContext,"设置成功");
                        AppManager.getAppManager().finishOtherActivity();
                        HomeFragment.homeFragment.mHandler.sendEmptyMessage(999);
                    } else {
                        ToastUtil.show(mContext,object.optString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case HttpModeBase.HTTP_ERROR:
                refreshLayout.finishRefresh();
                String result_e = (String) msg.obj;
                ToastUtil.show(mContext,result_e);
                break;
            case 888:
                refreshLayout.finishRefresh();
                break;
        }
        return false;
    }

    //将网络图片转为Drawable
    private class DownloadImageTask extends AsyncTask<String, Void, Drawable> {
        protected Drawable doInBackground(String... urls) {
            return loadImageFromNetwork(urls[0]);
        }
        protected void onPostExecute(Drawable result) {
            if(result == null){
                ivBg.setBackgroundColor(getResources().getColor(R.color.text_dd));
            }else {
                ivBg.setBackground(result);
            }
        }
    }

    private Drawable loadImageFromNetwork(String imageUrl) {
        Drawable drawable = null;
        try {// 可以在这里通过文件名来判断，是否本地有此图片
            drawable = Drawable.createFromStream(new URL(imageUrl).openStream(), "image.jpg");
        } catch (IOException e) {
            Log.d("test", e.getMessage());
        }
        return drawable ;
    }

    @Override
    @OnClick({R.id.txt_left_1,R.id.txt_index,R.id.txt_project,R.id.txt_attection,
            R.id.txt_team, R.id.txt_campaigns,R.id.txt_goods,R.id.iv_shop_phone,R.id.txt_make_index})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.txt_left_1:
                finish();
                break;
            case R.id.txt_index:
                setHead(0);mTab = 0;addFragment(mTab);
                break;
            case R.id.txt_project:
                setHead(1);mTab = 1;addFragment(mTab);
                break;
            case R.id.txt_team:
                setHead(2);mTab = 2;addFragment(mTab);
                break;
            case R.id.txt_campaigns:
                setHead(3);mTab = 3;addFragment(mTab);
                break;
            case R.id.txt_goods:
                setHead(4);mTab = 4;addFragment(mTab);
                break;
            case R.id.txt_attection:
                if(LoginUtil.getLoginState()){
                    if(StringUtils.isEmpty(is_attention)){
                        return;
                    }
                    mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_2,UrlUtils.attention_set(LoginUtil.getInfo("token"),LoginUtil.getInfo("uid"),"1",
                            shop_id,is_attention.equals("1")?"0":"1"),true);
                }else {
                    it = new Intent(mContext, LoginActivity.class);
                    startActivity(it);
                }
                break;
            case R.id.iv_shop_phone:
                if(LoginUtil.getLoginState()){
                    if(!StringUtils.isEmpty(shop_phone)){
                        alertView = new AlertView(null, "TEL:"+shop_phone, "取消", null, new String[]{"拨打"}, mContext,
                                AlertView.Style.Alert, new OnItemClickListener() {
                            @Override
                            public void onItemClick(Object o, int position) {
                                if (position == 0) {
                                    Intent intent = new Intent(Intent.ACTION_DIAL);
                                    intent.setData(Uri.parse("tel:" + shop_phone));
                                    startActivity(intent);
                                }
                            }
                        });
                        alertView.show();
                    }
                }else {
                    ToastUtil.show(mContext,"请先登录");
                }
                break;
            case R.id.txt_make_index:
                alertView = new AlertView(null, "是否设为首页门店？", "取消", null, new String[]{"确定"}, mContext,
                        AlertView.Style.Alert, new OnItemClickListener() {
                    @Override
                    public void onItemClick(Object o, int position) {
                        if (position == 0) {
                            if(!LoginUtil.getLoginState()){
                                LoginUtil.setInfo("unlogin_shop_id",shop_id);
                                ToastUtil.show(mContext,"设置成功");
                                AppManager.getAppManager().finishOtherActivity();
                                HomeFragment.homeFragment.mHandler.sendEmptyMessage(999);
                            }else {
                                mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_3,UrlUtils.home_shop_set(LoginUtil.getInfo("token"),LoginUtil.getInfo("uid"),shop_id),true);
                            }
                        }
                    }
                });
                alertView.show();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if(alertView != null && alertView.isShowing()){
            alertView.dismiss();
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activity = null;
    }
}
