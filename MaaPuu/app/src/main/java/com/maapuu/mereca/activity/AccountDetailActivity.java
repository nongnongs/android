package com.maapuu.mereca.activity;

import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.maapuu.mereca.R;
import com.maapuu.mereca.base.BaseActivity;
import com.maapuu.mereca.bean.BalanceLogBean;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by dell on 2018/2/24.
 */

public class AccountDetailActivity extends BaseActivity {
    @BindView(R.id.txt_left)
    TextView txtLeft;
    @BindView(R.id.txt_title)
    TextView txtTitle;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.ll_has)
    LinearLayout llHas;
    @BindView(R.id.txt_all)
    TextView txtAll;
    @BindView(R.id.txt_income)
    TextView txtIncome;
    @BindView(R.id.txt_outcome)
    TextView txtOutcome;

    private TextView[] tvs;
    private LinearLayoutManager mLayoutManager;
    private List<BalanceLogBean> list;
    private BaseRecyclerAdapter<BalanceLogBean> adapter;
    private int page = 1;
    private String balance_id;
    private String data_type = "1";

    @Override
    public void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_account_detail);
    }

    @Override
    public void initView() {
        txtLeft.setTypeface(StringUtils.getFont(mContext));
        txtTitle.setText("明细");
        tvs = new TextView[]{txtAll,txtIncome,txtOutcome};
        setHead(0);
        list = new ArrayList<>();
        balance_id = getIntent().getStringExtra("balance_id");

        mLayoutManager=new FullyLinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(mLayoutManager);

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
        mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_1, UrlUtils.account_detail_get(LoginUtil.getInfo("token"),LoginUtil.getInfo("uid"),balance_id,
                data_type,pageNum),true);
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
                        if(!StringUtils.isEmpty(resultObj.optString("balance_log")) && resultObj.optJSONArray("balance_log").length() > 0){
                            llHas.setVisibility(View.GONE);
                            List<BalanceLogBean> lsJson = FastJsonTools.getPersons(resultObj.optString("balance_log"),BalanceLogBean.class);
                            setAdapter(lsJson);
                            page ++ ;
                        }else {
                            List<BalanceLogBean> lsJson = new ArrayList<>();
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

    private void setAdapter(List<BalanceLogBean> lsJson) {
        if(page == 1)
            list.clear();
        list.addAll(lsJson);
        if(adapter == null){
            adapter = new BaseRecyclerAdapter<BalanceLogBean>(mContext,list,R.layout.layout_my_account_detail) {
                @Override
                public void convert(BaseRecyclerHolder holder, BalanceLogBean bean, int position, boolean isScrolling) {
                    holder.setText(R.id.txt_title,bean.getBusiness_text());
                    holder.setText(R.id.txt_time,bean.getCreate_time());
                    holder.setText(R.id.txt_amount,bean.getAmount()+"元");
                    if(position == list.size() - 1){
                        holder.setVisible(R.id.line,false);
                    }else {
                        holder.setVisible(R.id.line,true);
                    }
                }
            };
            recyclerView.setAdapter(adapter);
        }else {
            if(page > 1){
                adapter.notifyItemRangeInserted(list.size()-lsJson.size(),lsJson.size());
            }else {
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    @OnClick({R.id.txt_left,R.id.txt_all,R.id.txt_income,R.id.txt_outcome})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.txt_left:
                finish();
                break;
            case R.id.txt_all:
                setHead(0);data_type = "1";page = 1;initData(page);
                break;
            case R.id.txt_income:
                setHead(1);data_type = "2";page = 1;initData(page);
                break;
            case R.id.txt_outcome:
                setHead(2);data_type = "3";page = 1;initData(page);
                break;
        }
    }

}
