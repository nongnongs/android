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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.mobileim.YWAPI;
import com.maapuu.mereca.R;
import com.maapuu.mereca.adapter.help.BaseAdapterHelper;
import com.maapuu.mereca.adapter.help.QuickAdapter;
import com.maapuu.mereca.base.AppConfig;
import com.maapuu.mereca.base.BaseActivity;
import com.maapuu.mereca.bean.ShareBean;
import com.maapuu.mereca.constant.UrlUtils;
import com.maapuu.mereca.fragment.goodschild.GoodsCommentFragment;
import com.maapuu.mereca.fragment.goodschild.GoodsContentDetailFragment;
import com.maapuu.mereca.fragment.goodschild.GoodsDetailFragment;
import com.maapuu.mereca.util.FastJsonTools;
import com.maapuu.mereca.util.HttpModeBase;
import com.maapuu.mereca.util.LoginUtil;
import com.maapuu.mereca.util.ShareUtil;
import com.maapuu.mereca.util.StringUtils;
import com.maapuu.mereca.util.ToastUtil;
import com.maapuu.mereca.view.MyListView;
import com.othershe.nicedialog.BaseNiceDialog;
import com.othershe.nicedialog.NiceDialog;
import com.othershe.nicedialog.ViewConvertListener;
import com.othershe.nicedialog.ViewHolder;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by dell on 2018/3/5.
 */

public class GoodsDetailActivity extends BaseActivity {
    @BindView(R.id.txt_left)
    TextView txtLeft;
    @BindView(R.id.tabs)
    TabLayout tabs;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.txt_cart_num)
    TextView txtCartNum;
    @BindView(R.id.txt_shoucang)
    TextView txtShoucang;
    @BindView(R.id.viewpager)
    ViewPager viewpager;
    @BindView(R.id.rl_cart)
    RelativeLayout rlCart;
    @BindView(R.id.txt_add_cart)
    TextView txtAddCart;

    private MinePagerAdapter adapter;

    private int type; //1商品  2项目
    private String item_id;
    private String result;
    private boolean isCollect;
    private String item_shop_id;
    private String shop_id;
    private String shop_service;
    private ShareBean shareBean;

    private int position = -1;
    private int pos = -1;
    private int num = 0;
    private boolean isAdd = false;

    public static GoodsDetailActivity activity;

    @Override
    public void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_goods_detail);
        activity = this;
    }

    @Override
    public void initView() {
        type = getIntent().getIntExtra("type",1);
        item_id = getIntent().getStringExtra("item_id");
        shop_id = getIntent().getStringExtra("shop_id");
        position = getIntent().getIntExtra("position",-1);
        pos = getIntent().getIntExtra("pos",-1);
        txtLeft.setTypeface(StringUtils.getFont(mContext));
        if(type == 2){
            rlCart.setVisibility(View.VISIBLE);
            txtAddCart.setVisibility(View.VISIBLE);
        }else {
            rlCart.setVisibility(View.VISIBLE);
            txtAddCart.setVisibility(View.VISIBLE);
        }
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                if(position == 0){
                    GoodsDetailFragment fragment = (GoodsDetailFragment) adapter.getItem(0);
                    fragment.mHandler.sendEmptyMessage(300);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }

    public int getType(){
        return type;
    }

    @Override
    public void initData() {
        if(type == 1){
            mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_1, UrlUtils.commodity_info_get(LoginUtil.getInfo("token"),LoginUtil.getInfo("uid"),
                    shop_id,item_id),true);
        }else {
            mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_1, UrlUtils.project_info_get(LoginUtil.getInfo("token"),LoginUtil.getInfo("uid"),
                    shop_id,item_id),true);
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case HttpModeBase.HTTP_REQUESTCODE_1:
                try {
                    JSONObject object = new JSONObject((String) msg.obj);
                    if (object.optInt("status") == 1) {
                        result = object.optString("result");
                        JSONObject resultObj = object.optJSONObject("result");
                        if(resultObj.has("share_data") && !StringUtils.isEmpty(resultObj.optString("share_data"))){
                            shareBean = FastJsonTools.getPerson(resultObj.optString("share_data"),ShareBean.class);
                        }
                        shop_service = resultObj.optString("shop_service");
                        item_shop_id = resultObj.optString("item_shop_id");
                        if(LoginUtil.getLoginState() && resultObj.optInt("cart_num")>0){
                            txtCartNum.setVisibility(View.VISIBLE);
                            txtCartNum.setText(resultObj.optString("cart_num"));
                        }else {
                            txtCartNum.setVisibility(View.GONE);
                        }
                        if(LoginUtil.getLoginState() && resultObj.optString("is_collect").equals("1")){
                            txtShoucang.setSelected(true);
                            txtShoucang.setText("已收藏");
                            isCollect = true;
                        }else {
                            txtShoucang.setSelected(false);
                            txtShoucang.setText("收藏");
                            isCollect = false;
                        }
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
            case HttpModeBase.HTTP_REQUESTCODE_2:
                try {
                    JSONObject object = new JSONObject((String) msg.obj);
                    if (object.optInt("status") == 1) {
                        if(isCollect){
                            ToastUtil.show(mContext,"取消收藏成功");
                            txtShoucang.setSelected(false);
                            txtShoucang.setText("收藏");
                            isCollect = false;
                        }else {
                            ToastUtil.show(mContext,"收藏成功");
                            txtShoucang.setSelected(true);
                            txtShoucang.setText("已收藏");
                            isCollect = true;
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
                        ToastUtil.show(mContext,"添加购物车成功");
                        isAdd = true;
                        num ++ ;
                        txtCartNum.setVisibility(View.VISIBLE);
                        txtCartNum.setText(object.optJSONObject("result").optString("cart_num"));
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
            case 101:
                viewpager.setCurrentItem(2);
                GoodsCommentFragment fragment = (GoodsCommentFragment) adapter.getItem(2);
                fragment.refresh(msg.arg1);
                break;
            case 120:
                viewpager.setCurrentItem(0);//滑到头部
                break;
            case 130:
                viewpager.setCurrentItem(1);//滑到底部
                break;
            case 999:
                initData();
                break;
        }
        return false;
    }

    @Override
    @OnClick({R.id.txt_left,R.id.rl_cart,R.id.iv_share,R.id.txt_shoucang,R.id.txt_add_cart,R.id.txt_kefu,R.id.txt_buy})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.txt_left:
                if(isAdd && position != -1 && pos != -1){
                    it = new Intent();
                    it.putExtra("position",position);
                    it.putExtra("pos",pos);
                    it.putExtra("num",num);
                    setResult(AppConfig.ACTIVITY_RESULTCODE_4,it);
                }
                finish();
                break;
            case R.id.rl_cart:
                if(LoginUtil.getLoginState()){
                    it = new Intent(mContext,ShoppingCartActivity.class);
                    startActivityForResult(it, AppConfig.ACTIVITY_REQUESTCODE);
                }else {
                    startActivity(new Intent(mContext,LoginActivity.class));
                }
                break;
            case R.id.txt_shoucang:
                if(LoginUtil.getLoginState()){
                    mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_2,UrlUtils.item_collect_set(LoginUtil.getInfo("token"),LoginUtil.getInfo("uid"),
                            shop_id,item_id,isCollect?"0":"1"),true);
                }else {
                    startActivity(new Intent(mContext,LoginActivity.class));
                }
                break;
            case R.id.iv_share:
                if(shareBean != null){
                    share();
                }
                break;
            case R.id.txt_add_cart:
                if(LoginUtil.getLoginState()){
                    mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_3,UrlUtils.cart_add_set(LoginUtil.getInfo("token"),LoginUtil.getInfo("uid"),item_shop_id),true);
                }else {
                    startActivity(new Intent(mContext,LoginActivity.class));
                }
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
                    if(type == 1){
                        it = new Intent(mContext,ConfirmOrderActivity.class);
                        it.putExtra("item_shop_data","[{\"item_shop_id\":"+item_shop_id+",\"num\":\"1\"}]");
                        it.putExtra("is_cart",0);
                        startActivityForResult(it,AppConfig.ACTIVITY_REQUESTCODE);
                    }else {
                        it = new Intent(mContext,ConfirmProjectOrderActivity.class);
                        it.putExtra("item_shop_id",item_shop_id);
                        startActivityForResult(it,AppConfig.ACTIVITY_REQUESTCODE);
                    }
                }else {
                    startActivity(new Intent(mContext,LoginActivity.class));
                }
                break;
        }
    }

    private void share() {
        NiceDialog.init().setLayoutId(R.layout.pop_share)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    public void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        holder.setOnClickListener(R.id.txt_pyq, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new ShareUtil(GoodsDetailActivity.this, SHARE_MEDIA.WEIXIN_CIRCLE,shareBean);
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.txt_wx, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new ShareUtil(GoodsDetailActivity.this, SHARE_MEDIA.WEIXIN,shareBean);
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.txt_qq, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new ShareUtil(GoodsDetailActivity.this, SHARE_MEDIA.QQ,shareBean);
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.txt_wb, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new ShareUtil(GoodsDetailActivity.this, SHARE_MEDIA.SINA,shareBean);
                                dialog.dismiss();
                            }
                        });
                    }
                })
                .setOutCancel(true).setShowBottom(true)
                .show(getSupportFragmentManager());
    }

    private void add() {
        final List<String> list = new ArrayList<>();
        list.add("");list.add("");list.add("");list.add("");list.add("");list.add("");
        NiceDialog.init().setLayoutId(R.layout.pop_add_cart)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    public void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        TextView txtCommit = holder.getView(R.id.txt_commit);
                        MyListView listView = holder.getView(R.id.list_view);
                        listView.setAdapter(new QuickAdapter<String>(mContext,R.layout.layout_add_cart_item,list) {
                            @Override
                            protected void convert(BaseAdapterHelper helper, String item) {

                            }
                        });
                        txtCommit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                    }
                })
                .setOutCancel(true).setShowBottom(true).setHeight(420)
                .show(getSupportFragmentManager());
    }

    /**
     * ViewPager的PagerAdapter
     */
    public class MinePagerAdapter extends FragmentPagerAdapter {
        Fragment[] fragments = new Fragment[]{new GoodsDetailFragment(), new GoodsContentDetailFragment(), new GoodsCommentFragment()};
        String[] titles = new String[]{type == 1?"商品":"项目", "详情", "评价"};

        public MinePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = fragments[position];
            Bundle bundle=new Bundle();
            bundle.putString("item_id",item_id);
            bundle.putString("shop_id",shop_id);
            bundle.putString("result",result);
//            switch (position){
//                case 0:
//                    fragment = new GoodsDetailFragment();
//                    break;
//                case 1:
//                    fragment = new GoodsContentDetailFragment();
//                    break;
//                case 2:
//                    fragment = new GoodsCommentFragment();
//                    break;
//            }
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

    @Override
    public void onBackPressed() {
        if(isAdd && position != -1 && pos != -1){
            it = new Intent();
            it.putExtra("position",position);
            it.putExtra("pos",pos);
            it.putExtra("num",num);
            setResult(AppConfig.ACTIVITY_RESULTCODE_4,it);
        }
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activity = null;
    }
}
