package com.maapuu.mereca.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.maapuu.mereca.R;
import com.maapuu.mereca.base.AppConfig;
import com.maapuu.mereca.base.BaseActivity;
import com.maapuu.mereca.bean.CardBean;
import com.maapuu.mereca.bean.ShopBean;
import com.maapuu.mereca.constant.UrlUtils;
import com.maapuu.mereca.recycleviewutil.BaseRecyclerAdapter;
import com.maapuu.mereca.recycleviewutil.BaseRecyclerHolder;
import com.maapuu.mereca.recycleviewutil.FullyLinearLayoutManager;
import com.maapuu.mereca.recycleviewutil.RecyclerViewDivider;
import com.maapuu.mereca.util.FastJsonTools;
import com.maapuu.mereca.util.HttpModeBase;
import com.maapuu.mereca.util.LoginUtil;
import com.maapuu.mereca.util.StringUtils;
import com.maapuu.mereca.util.ToastUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by dell on 2018/2/24.
 */

public class MembershipCenterActivity extends BaseActivity {
    @BindView(R.id.txt_left)
    TextView txtLeft;
    @BindView(R.id.txt_title)
    TextView txtTitle;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.txt_year_card)
    TextView txtYearCard;
    @BindView(R.id.txt_project_card)
    TextView txtProjectCard;
    @BindView(R.id.txt_charge_card)
    TextView txtChargeCard;
    @BindView(R.id.iv_shop)
    SimpleDraweeView ivShop;
    @BindView(R.id.txt_shop_name)
    TextView txtShopName;
    @BindView(R.id.ll_has)
    LinearLayout llHas;

    private TextView[] tvs;
    private LinearLayoutManager mLayoutManager;
    private List<CardBean> list;
    private BaseRecyclerAdapter<CardBean> adapter;
    private int page = 1;
    private String shop_id = "0";
    private String shop_name ;
    private String shop_logo ;
    private int card_type = 1;//卡类型：1会籍；2项目卡；3充值卡
    public static MembershipCenterActivity activity;

    @Override
    public void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_membership_center);
        activity = this;
    }

    @Override
    public void initView() {
        txtLeft.setTypeface(StringUtils.getFont(mContext));
        txtTitle.setText("会员中心");
        tvs = new TextView[]{txtYearCard,txtProjectCard,txtChargeCard};
        setHead(0);

        list = new ArrayList<>();
        mLayoutManager=new FullyLinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mLayoutManager);
        //这句就是添加我们自定义的分隔线
        recyclerView.addItemDecoration(new RecyclerViewDivider(mContext, LinearLayoutManager.VERTICAL,20
                ,getResources().getColor(R.color.background)));
        refreshLayout.setEnableRefresh(true);//是否启用下拉刷新功能
        refreshLayout.setEnableLoadMore(true);//是否启用上拉加载功能
        refreshLayout.setEnableAutoLoadMore(false);//是否启用自动加载功能
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                page = 1;
                initData(page);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(final RefreshLayout refreshLayout) {
                initData(page);
            }
        });
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

    @Override
    public void initData() {
        initData(page);
    }

    private void initData(int pageNum) {
        mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_1, UrlUtils.new_card_list_get(LoginUtil.getInfo("token"),LoginUtil.getInfo("uid"),
                pageNum,shop_id,card_type),true);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case HttpModeBase.HTTP_REQUESTCODE_1:
                refreshLayout.finishRefresh();
                refreshLayout.finishLoadMore();
                try {
                    JSONObject object = new JSONObject((String) msg.obj);
                    if (object.optInt("status") == 1) {
                        JSONObject resultObj = object.optJSONObject("result");
                        if(resultObj.has("shop_data") && !StringUtils.isEmpty(resultObj.optString("shop_data"))){
                            JSONObject dataObj = resultObj.optJSONObject("shop_data");
                            shop_id = dataObj.optString("shop_id");
                            shop_name = dataObj.optString("shop_name");
                            shop_logo = dataObj.optString("shop_logo");
                            ivShop.setImageURI(Uri.parse(dataObj.optString("shop_logo")));
                            txtShopName.setText(dataObj.optString("shop_name"));
                        }
                        if(resultObj.has("card_list") && !StringUtils.isEmpty(resultObj.optString("card_list")) && resultObj.optJSONArray("card_list").length() > 0){
                            llHas.setVisibility(View.GONE);
                            List<CardBean> lsJson = FastJsonTools.getPersons(resultObj.optString("card_list"),CardBean.class);
                            setAdapter(lsJson);
                            page ++ ;
                        }else {
                            List<CardBean> lsJson = new ArrayList<>();
                            setAdapter(lsJson);
                            if(page != 1){
                                llHas.setVisibility(View.GONE);
                                ToastUtil.show(mContext,"暂无更多数据");
                            }else {
                                llHas.setVisibility(View.VISIBLE);
                            }
                        }
                    } else {
                        ToastUtil.show(mContext,object.optString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case HttpModeBase.HTTP_ERROR:
                refreshLayout.finishRefresh();
                refreshLayout.finishLoadMore();
                String result_e = (String) msg.obj;
                ToastUtil.show(mContext,result_e);
                break;
        }
        return false;
    }

    private void setAdapter(List<CardBean> lsJson) {
        if(page == 1)
            list.clear();
        list.addAll(lsJson);
        if(adapter == null){
            adapter = new BaseRecyclerAdapter<CardBean>(mContext,list,R.layout.layout_membership_card_item) {
                @Override
                public void convert(BaseRecyclerHolder holder, CardBean bean, int position, boolean isScrolling) {
                    switch (bean.getCard_type()){
                        case 1:
                            holder.setImageResource(R.id.iv_card_type,R.mipmap.nianka);
                            break;
                        case 2:
                            holder.setImageResource(R.id.iv_card_type,R.mipmap.xiangmuka);
                            break;
                        case 3:
                            holder.setImageResource(R.id.iv_card_type,R.mipmap.chongzhika);
                            break;
                    }
                    holder.setText(R.id.txt_card_name,bean.getCard_name());
                    holder.setText(R.id.txt_card_desc,bean.getCard_desc());
                    holder.setText(R.id.txt_card_cost,bean.getRecharge_amount());
                }
            };
            recyclerView.setAdapter(adapter);
        }else {
            if(page > 1){
                adapter.notifyItemRangeInserted(list.size()-lsJson.size(),lsJson.size());
                recyclerView.smoothScrollToPosition(list.size()-lsJson.size()-1);
            }else {
                adapter.notifyDataSetChanged();
            }
        }
        adapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position) {
                it = new Intent(mContext,MembershipDetailActivity.class);
                it.putExtra("shop_id",shop_id);
                it.putExtra("card_name",list.get(position).getCard_name());
                it.putExtra("shop_name",shop_name);
                it.putExtra("shop_logo",shop_logo);
                it.putExtra("card_id",list.get(position).getCard_id());
                startActivity(it);
            }
        });
    }

    @Override
    @OnClick({R.id.txt_left,R.id.txt_change_shop,R.id.txt_year_card,R.id.txt_project_card,R.id.txt_charge_card})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.txt_left:
                finish();
                break;
            case R.id.txt_change_shop:
                startActivityForResult(new Intent(mContext,BanKaChooseShopActivity.class), AppConfig.ACTIVITY_REQUESTCODE);
                break;
            case R.id.txt_year_card:
                setHead(0);
                card_type = 1;
                page = 1;
                initData(page);
                break;
            case R.id.txt_project_card:
                setHead(1);
                card_type = 2;
                page = 1;
                initData(page);
                break;
            case R.id.txt_charge_card:
                setHead(2);
                card_type = 3;
                page = 1;
                initData(page);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case AppConfig.ACTIVITY_RESULTCODE:
                ShopBean shop = (ShopBean) data.getSerializableExtra("shop");
                shop_id = shop.getShop_id();
                shop_name = shop.getShop_name();
                shop_logo = shop.getShop_logo();
                ivShop.setImageURI(Uri.parse(shop.getShop_logo()));
                txtShopName.setText(shop.getShop_name());
                page = 1;
                initData(page);
                break;
        }
    }
}
