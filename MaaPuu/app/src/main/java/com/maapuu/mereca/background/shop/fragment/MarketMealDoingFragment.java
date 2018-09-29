package com.maapuu.mereca.background.shop.fragment;

import android.content.Intent;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.luck.picture.lib.decoration.RecycleViewDivider;
import com.maapuu.mereca.R;
import com.maapuu.mereca.background.shop.activity.MarketingDoingActivity;
import com.maapuu.mereca.background.shop.activity.TaoCanDetailActivity;
import com.maapuu.mereca.background.shop.bean.ActBean;
import com.maapuu.mereca.background.shop.bean.PackActBean;
import com.maapuu.mereca.base.AppConfig;
import com.maapuu.mereca.base.BaseFragment;
import com.maapuu.mereca.constant.UrlUtils;
import com.maapuu.mereca.recycleviewutil.BaseRecyclerAdapter;
import com.maapuu.mereca.recycleviewutil.BaseRecyclerHolder;
import com.maapuu.mereca.recycleviewutil.FullyLinearLayoutManager;
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

import java.util.List;

import butterknife.BindView;

import static android.app.Activity.RESULT_OK;

/**
 * 营销活动 套餐活动
 * Created by Jia on 2018/3/10.
 */

public class MarketMealDoingFragment extends BaseFragment implements MarketingDoingActivity.OnShopSelectListener{

    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.ll_has)
    LinearLayout llHas;

    private FullyLinearLayoutManager mLayoutManager;
    private BaseRecyclerAdapter<PackActBean> adapter;

    boolean isShowProgress = true;
    int act_type = 2; //活动类型：1会员卡；2套餐活动；3红包
    String shop_id = "0";//店铺id，0表示全部店铺
    int page = 1;//第几页


    @Override
    protected int setContentViewById() {
        //布局有共用
        return R.layout.shop_fragment_rv_one;
    }

    @Override
    protected void initView(View v) {
        refreshLayout.setEnableRefresh(true);//是否启用下拉刷新功能
        refreshLayout.setEnableLoadMore(true);//是否启用上拉加载功能
        refreshLayout.setEnableAutoLoadMore(false);//是否启用自动加载功能
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                page = 1;
                getActList();
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(final RefreshLayout refreshLayout) {
                page++;
                getActList();
            }
        });
        mLayoutManager=new FullyLinearLayoutManager(mContext, LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        //分割线
        recyclerView.addItemDecoration(new RecycleViewDivider(mContext,LinearLayoutManager.HORIZONTAL,2,
                getResources().getColor(R.color.background)));
        adapter = new BaseRecyclerAdapter<PackActBean>(mContext,R.layout.shop_item_market_meal_doing) {
            @Override
            public void convert(BaseRecyclerHolder holder, PackActBean bean, int position, boolean isScrolling) {
                holder.setText(R.id.mmd_title_tv,bean.getPack_name());
                holder.setTextColor(R.id.mmd_title_tv,"1".equals(bean.getIs_valid())?getResources().getColor(R.color.text_33):getResources().getColor(R.color.text_99));
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position) {
                PackActBean bean = adapter.getList().get(position);
                it = new Intent(mContext, TaoCanDetailActivity.class);
                //it.putExtra("act_type",act_type);//活动类型：1会员卡；2套餐活动；3红包
                it.putExtra("business_id",bean.getPack_id());
                startActivityForResult(it, AppConfig.ACTIVITY_REQUESTCODE);
            }
        });
    }

    @Override
    protected void initData() {
        getActList();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && data != null){
            switch (requestCode){
                case AppConfig.ACTIVITY_REQUESTCODE:
                    //刷新
                    page = 1;
                    getActList();

                    break;
            }
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
                            ActBean bean = FastJsonTools.getPerson(object.optString("result"), ActBean.class);
                            if(bean != null){
                                if(page == 1 && (bean.getPack_act_list() == null || bean.getPack_act_list().size() ==0)){
                                    llHas.setVisibility(View.VISIBLE);
                                }else {
                                    llHas.setVisibility(View.GONE);
                                }
                                List<PackActBean> list = bean.getPack_act_list();
                                if(page == 1) adapter.clear();
                                adapter.addList(list);
                                if(page > 1){
                                    if((list ==null || list.size()==0)){
                                        page--;
                                        ToastUtil.show(mContext,"暂无更多数据");
                                    }
                                }
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


    private void getActList() {
        mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_1,
                UrlUtils.s_act_list_get(LoginUtil.getInfo("token"), LoginUtil.getInfo("uid"),
                        act_type,
                        shop_id,
                        page),isShowProgress);
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void loadData() {//切换fragment时调用
        if(mHttpModeBase != null){
            page = 1;
            isShowProgress = true;
            getActList();
        }
    }

    @Override
    public void onShopSelect(String shop_id) {//切换店铺时调用
        this.shop_id = shop_id;
        loadData();
    }

}
