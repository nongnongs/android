package com.maapuu.mereca.fragment.homechild;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.maapuu.mereca.R;
import com.maapuu.mereca.activity.GoodsDetailActivity;
import com.maapuu.mereca.activity.TeamDetailActivity;
import com.maapuu.mereca.activity.WebViewActivity;
import com.maapuu.mereca.base.BaseFragment;
import com.maapuu.mereca.bean.BannerBean;
import com.maapuu.mereca.bean.HairStylistBean;
import com.maapuu.mereca.bean.HotItemBean;
import com.maapuu.mereca.bean.PromotionBean;
import com.maapuu.mereca.recycleviewutil.BaseRecyclerAdapter;
import com.maapuu.mereca.recycleviewutil.BaseRecyclerHolder;
import com.maapuu.mereca.recycleviewutil.FullyLinearLayoutManager;
import com.maapuu.mereca.recycleviewutil.RecyclerViewDivider;
import com.maapuu.mereca.util.DisplayUtil;
import com.maapuu.mereca.util.FastJsonTools;
import com.maapuu.mereca.view.BannerHolderView;
import com.maapuu.mereca.view.LocalImageHolderView;
import com.maapuu.mereca.view.TimerTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by dell on 2018/2/22.
 * 主页——首页
 */

public class HomeIndexFragment extends BaseFragment {
    @BindView(R.id.banner_advs)
    ConvenientBanner bannerAdvs;
    @BindView(R.id.banner)
    ConvenientBanner banner;
    @BindView(R.id.ll_hairstylist)
    LinearLayout llHairstylist;
    @BindView(R.id.ll_qg)
    LinearLayout llQg;
    @BindView(R.id.recycler_view_h)
    RecyclerView recyclerViewH;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private List<BannerBean> advsList;
    private List<HairStylistBean> hairList;
    private List<PromotionBean> hList;//横向的
    private List<HotItemBean> list;
    private BaseRecyclerAdapter<PromotionBean> hAdapter;//横向的
    private BaseRecyclerAdapter<HotItemBean> adapter;
    private String index_info;
    private String shop_id;

    @Override
    protected int setContentViewById() {
        Bundle bundle=getArguments();
        //判断需写
        if(bundle!=null) {
            index_info = bundle.getString("index_info");
            shop_id = bundle.getString("shop_id");
        }
        return R.layout.fragment_home_index;
    }

    public void refresh(String index_info){
        this.index_info = index_info;
        advsList.clear();hairList.clear();hList.clear();list.clear();
        initData();
    }

    @Override
    protected void initView(View v) {
        LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) bannerAdvs.getLayoutParams();
        ll.height=((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DisplayUtil.getWidthDP(mContext)*2/5, mContext.getResources().getDisplayMetrics()));
        bannerAdvs.setLayoutParams(ll);
        recyclerViewH.setLayoutManager(new FullyLinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL,false));
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new FullyLinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false));
        recyclerView.addItemDecoration(new RecyclerViewDivider(mContext, LinearLayoutManager.VERTICAL,20
                ,getResources().getColor(R.color.background)));
        advsList = new ArrayList<>();hairList = new ArrayList<>();hList = new ArrayList<>();list = new ArrayList<>();

    }

    @Override
    protected void initData() {
        try {
            JSONObject infoObject = new JSONObject(index_info);
            if(infoObject.has("adv_data") && infoObject.optJSONArray("adv_data").length() > 0){
                bannerAdvs.setVisibility(View.VISIBLE);
                advsList = FastJsonTools.getPersons(infoObject.optString("adv_data"),BannerBean.class);
            }else {
                bannerAdvs.setVisibility(View.GONE);
            }
            if(infoObject.has("staff_data") && infoObject.optJSONArray("staff_data").length() > 0){
                llHairstylist.setVisibility(View.VISIBLE);
                hairList = FastJsonTools.getPersons(infoObject.optString("staff_data"),HairStylistBean.class);
            }else {
                llHairstylist.setVisibility(View.GONE);
            }
            if(infoObject.has("promotion_data") && infoObject.optJSONArray("promotion_data").length() > 0){
                llQg.setVisibility(View.VISIBLE);
                hList = FastJsonTools.getPersons(infoObject.optString("promotion_data"),PromotionBean.class);
            }else {
                llQg.setVisibility(View.GONE);
            }
            if(infoObject.has("hot_item_data") && infoObject.optJSONArray("hot_item_data").length() > 0){
                list = FastJsonTools.getPersons(infoObject.optString("hot_item_data"),HotItemBean.class);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        bannerAdvs.setPages(new CBViewHolderCreator() {
            @Override
            public Object createHolder() {
                return new LocalImageHolderView(mContext,advsList);
            }
        }, advsList)
                .setPageIndicator(new int[]{R.mipmap.dot1, R.mipmap.dot})
                .startTurning(5000)
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL)
                .setManualPageable(true);
        bannerAdvs.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                String shop_item_ids = advsList.get(position).getAdv_value();
                switch (advsList.get(position).getAdv_type()){
                    case 1://1发型师
                        it = new Intent(mContext, TeamDetailActivity.class);
                        it.putExtra("staff_id",shop_item_ids);
                        startActivity(it);
                        break;
                    case 2://2项目
                        if(!TextUtils.isEmpty(shop_item_ids) && shop_item_ids.contains(",")){
                            it = new Intent(mContext,GoodsDetailActivity.class);
                            it.putExtra("type",2);
                            it.putExtra("shop_id",shop_item_ids.split(",")[0]);
                            it.putExtra("item_id",shop_item_ids.split(",")[1]);
                            startActivity(it);
                        }

                        break;
                    case 3://3商品
                        if(!TextUtils.isEmpty(shop_item_ids) && shop_item_ids.contains(",")){
                            it = new Intent(mContext,GoodsDetailActivity.class);
                            it.putExtra("type",1);
                            it.putExtra("shop_id",shop_item_ids.split(",")[0]);
                            it.putExtra("item_id",shop_item_ids.split(",")[1]);
                            startActivity(it);
                        }

                        break;
                    case 4://4链接
                        it = new Intent(mContext, WebViewActivity.class);
                        it.putExtra("title","网页");
                        it.putExtra("isHtml",true);
                        it.putExtra("content",shop_item_ids);
                        startActivity(it);
                        break;
                }
            }
        });
        banner.setPages(new CBViewHolderCreator() {
            @Override
            public Object createHolder() {
                return new BannerHolderView(mContext,hairList);
            }
        }, hairList)
                .setPageIndicator(new int[]{R.mipmap.dot1, R.mipmap.dot})
                .startTurning(3000)
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL)
                .setManualPageable(true);
        banner.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                it = new Intent(mContext, TeamDetailActivity.class);
                it.putExtra("staff_id",hairList.get(position).getStaff_id());
                startActivity(it);
            }
        });
        hAdapter = new BaseRecyclerAdapter<PromotionBean>(mContext,hList,R.layout.layout_home_index_qg_item) {
            @Override
            public void convert(BaseRecyclerHolder holder, PromotionBean item, int position, boolean isScrolling) {
                LinearLayout ll =holder.getView(R.id.ll);
                if(position == 0){
                    ll.setPadding(DisplayUtil.dip2px(mContext,12),DisplayUtil.dip2px(mContext,10),DisplayUtil.dip2px(mContext,15),DisplayUtil.dip2px(mContext,10));
                }else if(position == hList.size()-1){
                    ll.setPadding(0,DisplayUtil.dip2px(mContext,10),DisplayUtil.dip2px(mContext,12),DisplayUtil.dip2px(mContext,10));
                }else {
                    ll.setPadding(0,DisplayUtil.dip2px(mContext,10),DisplayUtil.dip2px(mContext,15),DisplayUtil.dip2px(mContext,10));
                }
                LinearLayout.LayoutParams lp =(LinearLayout.LayoutParams) holder.getParams(R.id.ll_1);
                lp.width=((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (DisplayUtil.getWidthDP(mContext)-39)/2, mContext.getResources().getDisplayMetrics()));
                holder.setParams(R.id.ll_1,lp);
                holder.setParams(R.id.rl_1,lp);
                LinearLayout.LayoutParams lp1 =(LinearLayout.LayoutParams) holder.getParams(R.id.image);
                lp1.width = ((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (DisplayUtil.getWidthDP(mContext)-39)/2, mContext.getResources().getDisplayMetrics()));
                lp1.height = lp1.width;
                holder.setParams(R.id.image,lp1);

                holder.setSimpViewImageUri(R.id.image, Uri.parse(item.getItem_img()));
                holder.setText(R.id.txt_qg_name,item.getItem_name());
                holder.setText(R.id.txt_qg_old_price,"¥"+item.getPrice());
                holder.setTextFlags(R.id.txt_qg_old_price, Paint.STRIKE_THRU_TEXT_FLAG);
                holder.setText(R.id.txt_qg_price,"¥"+item.getPromotion_price());
                holder.setText(R.id.txt_qg_num,"已抢"+item.getSale_num());
                TimerTextView txtQgCountdown = holder.getView(R.id.txt_qg_countdown);
                //获得时间差
                long diff = Long.parseLong(item.getDiff_seconds())*1000;
                if(diff > 0){
                    txtQgCountdown.setTimes(diff);//设置时间
                    if(!txtQgCountdown.isRun()){txtQgCountdown.start();}//开始倒计时
                }
                txtQgCountdown.setPosition(position);
                txtQgCountdown.setCall(new TimerTextView.CallBack() {
                    @Override
                    public void onCall() {

                    }

                    @Override
                    public void onCall(int position) {
                        hList.remove(position);
                        hAdapter.notifyDataSetChanged();
                        if(hList.size() == 0){
                            llQg.setVisibility(View.GONE);
                        }
                    }
                });
            }
        };
        recyclerViewH.setAdapter(hAdapter);
        hAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position) {
                it = new Intent(mContext,GoodsDetailActivity.class);
                it.putExtra("type",hList.get(position).getCatalog_type() == 1 ? 2 : 1);
                it.putExtra("shop_id",shop_id);
                it.putExtra("item_id",hList.get(position).getItem_id());
                startActivity(it);
            }
        });
        adapter = new BaseRecyclerAdapter<HotItemBean>(mContext,list,R.layout.layout_home_index_project_item) {
            @Override
            public void convert(BaseRecyclerHolder holder, HotItemBean item, int position, boolean isScrolling) {
                LinearLayout.LayoutParams lp =(LinearLayout.LayoutParams) holder.getParams(R.id.image);
                lp.height=((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DisplayUtil.getWidthDP(mContext)*2/5, mContext.getResources().getDisplayMetrics()));
                holder.setParams(R.id.image,lp);
                holder.setSimpViewImageUri(R.id.image, Uri.parse(item.getHot_item_img()));
                holder.setText(R.id.txt_hot_item_name,item.getItem_name());
                holder.setText(R.id.txt_hot_item_price,"¥"+item.getPrice());
                holder.setText(R.id.txt_hot_item_sale_num,"已售"+item.getSale_num());
                holder.setText(R.id.txt_hot_item_market_price,"门市价 ¥"+item.getMarket_price());
                holder.setTextFlags(R.id.txt_hot_item_market_price,Paint.STRIKE_THRU_TEXT_FLAG);
                if(item.getPrice().equals(item.getMarket_price()) || Double.parseDouble(item.getPrice()) == Double.parseDouble(item.getMarket_price())){
                    holder.setVisible(R.id.txt_hot_item_market_price,false);
                }else {
                    holder.setVisible(R.id.txt_hot_item_market_price,true);
                }
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position) {
                it = new Intent(mContext,GoodsDetailActivity.class);
                it.putExtra("type",2);
                it.putExtra("shop_id",shop_id);
                it.putExtra("item_id",list.get(position).getItem_id());
                startActivity(it);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        banner.startTurning(3000);
        bannerAdvs.startTurning(5000);
    }

    @Override
    public void onPause() {
        super.onPause();
        banner.stopTurning();
        bannerAdvs.stopTurning();
    }

    @Override
    public void onClick(View view) {

    }
}
