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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.luck.picture.lib.decoration.RecycleViewDivider;
import com.maapuu.mereca.R;
import com.maapuu.mereca.background.shop.bean.StaffBean;
import com.maapuu.mereca.background.shop.dialog.ChooseShopFilter;
import com.maapuu.mereca.base.AppConfig;
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
 * 员工管理
 * Created by Jia on 2018/3/8.
 */

public class EmployeeManageActivity extends BaseActivity {
    @BindView(R.id.txt_left)
    TextView txtLeft;
    @BindView(R.id.txt_title)
    TextView txtTitle;
    @BindView(R.id.txt_right)
    TextView txtRight;
    @BindView(R.id.txt_shop_name)
    TextView txtShopName;
    @BindView(R.id.ll_has)
    LinearLayout llHas;

    @BindView(R.id.ao_choose_shop_lt)
    LinearLayout chooseShopLt;

    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private FullyLinearLayoutManager mLayoutManager;
    private BaseRecyclerAdapter<StaffBean> adapter;
    private List<StaffBean> list;
    private List<ShopBean> shopList;
    private int page = 1;

    private String shop_id = "0";

    @Override
    public void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.shop_activity_employee_manage);
    }

    @Override
    public void initView() {
        txtLeft.setTypeface(StringUtils.getFont(mContext));
        txtTitle.setText("员工管理");
        txtRight.setVisibility(View.VISIBLE);
        txtRight.setTypeface(StringUtils.getFont(mContext));
        txtRight.setText("\ue6c3");//&#xe6c3;
        txtRight.setTextSize(17);

        mLayoutManager = new FullyLinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        //分割线
        recyclerView.addItemDecoration(new RecycleViewDivider(mContext, LinearLayoutManager.HORIZONTAL,
                UIUtils.dip2px(mContext,10),
                getResources().getColor(R.color.background)));
        list = new ArrayList<>();
        shopList = new ArrayList<>();

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

    @Override
    public void initData() {
        initData(page);
    }

    private void initData(int pageNum) {
        mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_1, UrlUtils.s_staff_list_get(LoginUtil.getInfo("token"),LoginUtil.getInfo("uid"),shop_id,pageNum),true);
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
                        if(resultObj.has("shop_list") && !StringUtils.isEmpty(resultObj.optString("shop_list"))){
                            shopList = FastJsonTools.getPersons(resultObj.optString("shop_list"),ShopBean.class);
                        }
                        if(!StringUtils.isEmpty(resultObj.optString("staff_list")) && resultObj.optJSONArray("staff_list").length() > 0){
                            llHas.setVisibility(View.GONE);
                            List<StaffBean> lsJson = FastJsonTools.getPersons(resultObj.optString("staff_list"),StaffBean.class);
                            setAdapter(lsJson);
                            page ++ ;
                        }else {
                            List<StaffBean> lsJson = new ArrayList<>();
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

    private void setAdapter(List<StaffBean> lsJson) {
        if(page == 1)
            list.clear();
        list.addAll(lsJson);
        if (adapter == null) {
            adapter = new BaseRecyclerAdapter<StaffBean>(mContext, list, R.layout.shop_item_employee_manage) {
                @Override
                public void convert(BaseRecyclerHolder holder, StaffBean bean, int position, boolean isScrolling) {
                    ImageView image = holder.getView(R.id.image);
                    ImageView sex = holder.getView(R.id.iv_sex);
                    RequestOptions options = new RequestOptions().error(R.mipmap.nopic);
                    options.circleCrop();
//                    Glide.with(mContext).load(R.id.iv_sex).apply(options).into(sex);
                    UIUtils.loadImg(mContext,bean.getStaff_avatar(),image);
                    holder.setText(R.id.txt_name,bean.getStaff_name());
                    if(bean.getSex().equals("0")){
                        holder.setVisible(R.id.ll_age,false);
                    }else {
                        holder.setVisible(R.id.ll_age,true);
                        if(bean.getSex().equals("1")){
                            Glide.with(mContext).load(R.mipmap.nanbiao).apply(options).into(sex);
//                            holder.setBackgroundColor(R.id.iv_sex,R.mipmap.nanbiao);
                        }else {
                            Glide.with(mContext).load(R.mipmap.nvbiaoji).apply(options).into(sex);
//                            holder.setBackgroundColor(R.id.iv_sex,R.mipmap.nvbiaoji);
                        }
                        holder.setText(R.id.txt_age,bean.getAge());
                    }
                    holder.setText(R.id.txt_phone,bean.getPhone());
                    if(StringUtils.isEmpty(bean.getPost_name())){
                        holder.setVisible(R.id.iv_post,false);
                        holder.setVisible(R.id.txt_post_name,false);
                    }else {
                        holder.setVisible(R.id.iv_post,true);
                        holder.setVisible(R.id.txt_post_name,true);
                        holder.setText(R.id.txt_post_name,bean.getPost_name());
                    }
                    if(StringUtils.isEmpty(bean.getPost_name())){
                        holder.setText(R.id.txt_no,"编号：暂无");
                    }else {
                        holder.setText(R.id.txt_no,"编号："+bean.getStaff_no());
                    }
                }
            };
            recyclerView.setAdapter(adapter);
        } else {
            if(page > 1){
                adapter.notifyItemRangeInserted(list.size()-lsJson.size(),lsJson.size());
            }else {
                adapter.notifyDataSetChanged();
            }
        }
        adapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position) {
                //员工信息
                it = new Intent(mContext,EmployeeInfoActivity.class);
                it.putExtra("staff_id",list.get(position).getStaff_id());
                startActivityForResult(it, AppConfig.ACTIVITY_REQUESTCODE);
            }
        });
    }

    @Override
    @OnClick({R.id.txt_left,R.id.txt_right,R.id.ao_choose_shop_lt})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_left:
                finish();
                break;
            case R.id.txt_right://员工审核
                it = new Intent(mContext,EmployeeCheckActivity.class);
                it.putExtra("shop_id",shop_id);
                startActivity(it);
                break;
            case R.id.ao_choose_shop_lt:
                ChooseShopFilter chooseShopFilter = new ChooseShopFilter(mContext, shopList, new ChooseShopFilter.ChooseCall() {
                    @Override
                    public void onChoose(ShopBean item) {
                        txtShopName.setText(item.getShop_name());
                        shop_id = item.getShop_id();
                        page = 1;
                        initData(page);
                    }
                });
                chooseShopFilter.createPopup();
                chooseShopFilter.showAsDropDown(chooseShopLt);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case AppConfig.ACTIVITY_RESULTCODE:
                page = 1;
                initData(page);
                break;
        }
    }
}
