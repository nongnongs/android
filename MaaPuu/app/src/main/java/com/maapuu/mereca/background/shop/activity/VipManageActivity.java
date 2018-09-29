package com.maapuu.mereca.background.shop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.luck.picture.lib.decoration.RecycleViewDivider;
import com.maapuu.mereca.R;
import com.maapuu.mereca.background.shop.bean.MemberBean;
import com.maapuu.mereca.background.shop.bean.MemberManageBean;
import com.maapuu.mereca.background.shop.dialog.ChooseShopFilter;
import com.maapuu.mereca.base.BaseActivity;
import com.maapuu.mereca.bean.ShopBean;
import com.maapuu.mereca.constant.UrlUtils;
import com.maapuu.mereca.recycleviewutil.BaseRecyclerAdapter;
import com.maapuu.mereca.recycleviewutil.BaseRecyclerHolder;
import com.maapuu.mereca.recycleviewutil.FullyLinearLayoutManager;
import com.maapuu.mereca.util.FastJsonTools;
import com.maapuu.mereca.util.HttpModeBase;
import com.maapuu.mereca.util.LoginUtil;
import com.maapuu.mereca.util.StringUtils;
import com.maapuu.mereca.util.ToastUtil;
import com.maapuu.mereca.util.UIUtils;
import com.maapuu.mereca.view.CircleImgView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 会员管理
 * Created by Jia on 2018/3/10.
 */

public class VipManageActivity extends BaseActivity{
    @BindView(R.id.txt_left)
    TextView txtLeft;
    @BindView(R.id.txt_title)
    TextView txtTitle;
    @BindView(R.id.ll_has)
    LinearLayout llHas;

    @BindView(R.id.ao_choose_shop_lt)
    LinearLayout chooseShopLt;
    @BindView(R.id.choose_shop_tv)
    TextView shopTv;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private FullyLinearLayoutManager mLayoutManager;
    private BaseRecyclerAdapter<MemberBean> adapter;
    private int page = 1;
    String shop_id = "";
    boolean isShowProgress = true;

    List<ShopBean> shopList;

    @Override
    public void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.shop_activity_vip_manage);
    }

    @Override
    public void initView() {
        shop_id = getIntent().getStringExtra("shop_id");
        txtLeft.setTypeface(StringUtils.getFont(mContext));
        txtTitle.setText("会员管理");

        refreshLayout.setEnableRefresh(true);//是否启用下拉刷新功能
        refreshLayout.setEnableLoadMore(true);//是否启用上拉加载功能
        refreshLayout.setEnableAutoLoadMore(false);//是否启用自动加载功能
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                page = 1;
                getMemberList();
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(final RefreshLayout refreshLayout) {
                page++;
                getMemberList();
            }
        });
        mLayoutManager = new FullyLinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        //分割线
        recyclerView.addItemDecoration(new RecycleViewDivider(mContext, LinearLayoutManager.HORIZONTAL,
                UIUtils.dip2px(mContext,10),
                getResources().getColor(R.color.background)));
        adapter = new BaseRecyclerAdapter<MemberBean>(mContext, R.layout.shop_item_vip_manage) {
            @Override
            public void convert(BaseRecyclerHolder holder, MemberBean bean, int position, boolean isScrolling) {
                CircleImgView icon = holder.getView(R.id.vm_icon);
                UIUtils.loadIcon(mContext,bean.getAvatar(),icon);
                holder.setText(R.id.vm_name,bean.getNick_name());
                int sex = bean.getSex();//性别：1男；2女；0未知
                ImageView sexIv = holder.getView(R.id.vm_sex_iv);
                if(sex == 1){
                    sexIv.setVisibility(View.VISIBLE);
                    sexIv.setImageResource(R.mipmap.nanbiao);
                } else if(sex == 2){
                    sexIv.setVisibility(View.VISIBLE);
                    sexIv.setImageResource(R.mipmap.nvbiaoji);
                } else {
                    sexIv.setVisibility(View.GONE);
                }
                holder.setText(R.id.vm_age,bean.getAge());
                holder.setText(R.id.vm_phone,bean.getPhone());
                holder.setText(R.id.vm_card_name,bean.getCard_name());
                holder.setText(R.id.vm_member_end,"有效期："+bean.getMember_end().substring(0,10));
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position) {
                MemberBean bean = adapter.getList().get(position);
                it = new Intent(mContext,MemberConsumeActivity.class);
                it.putExtra("member_id",bean.getMember_id());
                startActivity(it);
            }
        });
    }

    @Override
    public void initData() {
        getMemberList();
    }

    private void setUI(MemberManageBean bean) {
        if(bean.getShop_list() != null && bean.getShop_list().size()>0) shopList = bean.getShop_list();
        if(page == 1 && (bean.getMember_info() == null || bean.getMember_info().size() ==0)){
            llHas.setVisibility(View.VISIBLE);
        }else {
            llHas.setVisibility(View.GONE);
        }
        List<MemberBean> memberList = bean.getMember_info();
        if(page == 1) adapter.clear();
        adapter.addList(memberList);
        if(page > 1){
            if((memberList ==null || memberList.size()==0)){
                page--;
                ToastUtil.show(mContext,"暂无更多数据");
            }
        }
    }

    private void getMemberList() {
        mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_1,
                UrlUtils.s_member_list_get(LoginUtil.getInfo("token"), LoginUtil.getInfo("uid"),
                        shop_id,
                        page),isShowProgress);
    }

    @Override
    @OnClick({R.id.txt_left,R.id.ao_choose_shop_lt})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_left:
                finish();
                break;

            case R.id.ao_choose_shop_lt:
                //选择商铺
                if(shopList != null && shopList.size()>0){
                    ChooseShopFilter chooseShopFilter = new ChooseShopFilter(mContext, shopList, new ChooseShopFilter.ChooseCall() {
                        @Override
                        public void onChoose(ShopBean item) {
                            shopTv.setText(item.getShop_name());
                            shop_id = item.getShop_id();
                            page = 1;
                            getMemberList();
                        }
                    });
                    chooseShopFilter.createPopup();
                    chooseShopFilter.showAsDropDown(chooseShopLt);
                } else {
                    ToastUtil.show(mContext,"没有可供选择的商铺");
                }

                break;
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case HttpModeBase.HTTP_REQUESTCODE_1:
                try {
                    isShowProgress = false;
                    refreshLayout.finishRefresh();
                    refreshLayout.finishLoadMore();
                    JSONObject object = new JSONObject((String) msg.obj);
                    if (object.optInt("status") == 1) {
                        if (!StringUtils.isEmpty(object.optString("result"))) {
                            MemberManageBean bean = FastJsonTools.getPerson(object.optString("result"), MemberManageBean.class);
                            if(bean != null){
                                setUI(bean);
                            }
                        }
                    } else {
                        ToastUtil.show(mContext, object.optString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case HttpModeBase.HTTP_ERROR:
                refreshLayout.finishRefresh();
                refreshLayout.finishLoadMore();
                String result_e = (String) msg.obj;
                ToastUtil.show(mContext, result_e);
                break;
        }
        return false;
    }

}
