package com.maapuu.mereca.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.alibaba.mobileim.YWAPI;
import com.maapuu.mereca.R;
import com.maapuu.mereca.base.AppConfig;
import com.maapuu.mereca.base.BaseActivity;
import com.maapuu.mereca.constant.UrlUtils;
import com.maapuu.mereca.fragment.goodschild.CampaignContentDetailFragment;
import com.maapuu.mereca.fragment.goodschild.CampaignDetailFragment;
import com.maapuu.mereca.util.HttpModeBase;
import com.maapuu.mereca.util.LoginUtil;
import com.maapuu.mereca.util.StringUtils;
import com.maapuu.mereca.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by dell on 2018/3/5.
 */

public class CampaignDetailActivity extends BaseActivity {
    @BindView(R.id.txt_left)
    TextView txtLeft;
    @BindView(R.id.tabs)
    TabLayout tabs;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.viewpager)
    ViewPager viewpager;

    private MinePagerAdapter adapter;
    private String pack_id;
    private String shop_id;
    private String result;
    private String shop_service;//客服id

    @Override
    public void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_campaign_detail);
    }

    @Override
    public void initView() {
        txtLeft.setTypeface(StringUtils.getFont(mContext));
        pack_id = getIntent().getStringExtra("pack_id");
        shop_id = getIntent().getStringExtra("shop_id");
    }

    @Override
    public void initData() {
        mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_1, UrlUtils.action_info_get(LoginUtil.getInfo("token"),LoginUtil.getInfo("uid"),
                pack_id,shop_id),true);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case HttpModeBase.HTTP_REQUESTCODE_1:
                try {
                    JSONObject object = new JSONObject((String) msg.obj);
                    if (object.optInt("status") == 1) {
                        JSONObject resultObj = object.optJSONObject("result");
                        result = object.optString("result");
                        shop_service = resultObj.optJSONObject("action_data").optString("shop_service");
                        adapter = new MinePagerAdapter(getSupportFragmentManager());
                        viewpager.setOffscreenPageLimit(2);
                        viewpager.setAdapter(adapter);
                        tabs.setupWithViewPager(viewpager);
                    } else {
                        ToastUtil.show(mContext,object.optString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case HttpModeBase.HTTP_ERROR:
                String result_e = (String) msg.obj;
                ToastUtil.show(mContext,result_e);
                break;
        }
        return false;
    }

    @Override
    @OnClick({R.id.txt_left,R.id.txt_kefu,R.id.txt_buy})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.txt_left:
                finish();
                break;
            case R.id.txt_kefu:
                if(LoginUtil.getLoginState()){
                    if(AppConfig.mIMKit == null){
                        AppConfig.mIMKit = YWAPI.getIMKitInstance(LoginUtil.getInfo("uid"), AppConfig.APP_KEY);
                    }
                    Intent chat = AppConfig.mIMKit.getChattingActivityIntent(shop_service);
                    chat.putExtra("uid",shop_service);
                    startActivity(chat);
                }else {
                    it = new Intent(mContext, LoginActivity.class);
                    startActivity(it);
                }
                break;
            case R.id.txt_buy:
                if(LoginUtil.getLoginState()){
                    it = new Intent(mContext,ConfirmCampaignOrderActivity.class);
                    it.putExtra("pack_id",pack_id);
                    it.putExtra("shop_id",shop_id);
                    startActivityForResult(it,AppConfig.ACTIVITY_REQUESTCODE);
                }else {
                    startActivity(new Intent(mContext,LoginActivity.class));
                }
                break;
        }
    }

    /**
     * ViewPager的PagerAdapter
     */
    public class MinePagerAdapter extends FragmentPagerAdapter {
        Fragment[] fragments = new Fragment[]{CampaignDetailFragment.newInstance(), CampaignContentDetailFragment.newInstance()};
        String[] titles = new String[]{"项目", "详情"};

        public MinePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position){
                case 0:
                    fragment = new CampaignDetailFragment();
                    break;
                case 1:
                    fragment = new CampaignContentDetailFragment();
                    break;
            }
            Bundle bundle=new Bundle();
            bundle.putString("pack_id",pack_id);
            bundle.putString("shop_id",shop_id);
            bundle.putString("result",result);
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public int getCount() {
            return titles.length;
        }
    }
}
