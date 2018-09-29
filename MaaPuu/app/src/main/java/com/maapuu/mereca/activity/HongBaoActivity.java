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

import com.maapuu.mereca.R;
import com.maapuu.mereca.base.AppConfig;
import com.maapuu.mereca.base.BaseActivity;
import com.maapuu.mereca.bean.RedBean;
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

public class HongBaoActivity extends BaseActivity {
    @BindView(R.id.txt_left)
    TextView txtLeft;
    @BindView(R.id.txt_title)
    TextView txtTitle;
    @BindView(R.id.ll_has)
    LinearLayout llHas;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.txt_no_use)
    TextView txtNoUse;

    private LinearLayoutManager mLayoutManager;
    private List<RedBean> list;
    private BaseRecyclerAdapter<RedBean> adapter;
    private int page = 1;
    private int red_type = 3;
    private String item_shop_ids ="";//下单时的参数：店铺项目id，多个半角逗号分隔(1,2,3)，非下单传空字符串
    private String order_amount ="0";//下单时的参数：订单金额，非下单传0

    private boolean isOrder = false;

    @Override
    public void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_hongbao);
    }

    @Override
    public void initView() {
        txtLeft.setTypeface(StringUtils.getFont(mContext));
        txtTitle.setText("红包");
        list = new ArrayList<>();
        isOrder = getIntent().getBooleanExtra("isOrder",false);
        red_type = getIntent().getIntExtra("red_type",3);
        if(red_type != 3){
            item_shop_ids = getIntent().getStringExtra("item_shop_ids");
            order_amount = getIntent().getStringExtra("order_amount");
        }
        if(isOrder){
            txtNoUse.setVisibility(View.VISIBLE);
        }else {
            txtNoUse.setVisibility(View.GONE);
        }
        mLayoutManager=new FullyLinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
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

    @Override
    public void initData() {
        initData(page);
    }

    private void initData(int pageNum) {
        mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_1, UrlUtils.red_list_get(LoginUtil.getInfo("token"),LoginUtil.getInfo("uid"),
                red_type,0,item_shop_ids,order_amount,pageNum),true);
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
                        if(!StringUtils.isEmpty(object.optString("result")) && object.optJSONArray("result").length() > 0){
                            llHas.setVisibility(View.GONE);
                            refreshLayout.setVisibility(View.VISIBLE);
                            List<RedBean> lsJson = FastJsonTools.getPersons(object.optString("result"),RedBean.class);
                            setAdapter(lsJson);
                            page ++ ;
                        }else {
                            List<RedBean> lsJson = new ArrayList<>();
                            setAdapter(lsJson);
                            if(page != 1){
                                ToastUtil.show(mContext,"暂无更多数据");
                            }else {
                                llHas.setVisibility(View.VISIBLE);
                                refreshLayout.setVisibility(View.GONE);
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

    private void setAdapter(List<RedBean> lsJson) {
        if(page == 1)
            list.clear();
        list.addAll(lsJson);
        if(adapter == null){
            adapter = new BaseRecyclerAdapter<RedBean>(mContext,list,R.layout.layout_hongbao_item) {
                @Override
                public void convert(BaseRecyclerHolder holder, RedBean bean, int position, boolean isScrolling) {
                    if(position == list.size() - 1){
                        holder.setMargins(R.id.ll_item,mContext,12,10,12,10);
                    }else {
                        holder.setMargins(R.id.ll_item,mContext,12,10,12,0);
                    }
                    if(bean.getRed_type().equals("1")){
                        holder.setBackgroundRes(R.id.ll_item,R.mipmap.xm);
                    }else {
                        holder.setBackgroundRes(R.id.ll_item,R.mipmap.sp);
                    }
                    holder.setText(R.id.txt_name,"      "+bean.getShop_name());
                    holder.setText(R.id.txt_time,bean.getValid_date());
                    holder.setText(R.id.txt_amount,bean.getRed_amount());
                    holder.setText(R.id.txt_full_amount,"满"+bean.getFullcut_amount()+"可用");
                    holder.setSimpViewImageUri(R.id.image, Uri.parse(AppConfig.getImagePath(bean.getShop_logo())));
                }
            };
            recyclerView.setAdapter(adapter);
        }else {
//            if(page > 1){
//                adapter.notifyItemRangeInserted(list.size()-lsJson.size(),lsJson.size());
//                recyclerView.smoothScrollToPosition(list.size()-lsJson.size()-1);
//            }else {
//            }
            adapter.notifyDataSetChanged();
        }
        adapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position) {
                if(isOrder){
                    it = new Intent();
                    it.putExtra("red",list.get(position));
                    setResult(AppConfig.ACTIVITY_RESULTCODE,it);
                    finish();
                }
            }
        });
    }

    @Override
    @OnClick({R.id.txt_left,R.id.txt_history,R.id.txt_no_use})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.txt_left:
                finish();
                break;
            case R.id.txt_no_use:
                setResult(AppConfig.ACTIVITY_RESULTCODE_4);
                finish();
                break;
            case R.id.txt_history:
                startActivity(new Intent(mContext,HongBaoHistoryActivity.class));
                break;
        }
    }

}
