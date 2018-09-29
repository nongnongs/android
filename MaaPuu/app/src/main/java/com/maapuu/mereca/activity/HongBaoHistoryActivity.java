package com.maapuu.mereca.activity;

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

public class HongBaoHistoryActivity extends BaseActivity {
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
    @BindView(R.id.txt_history)
    TextView txtHistory;
    @BindView(R.id.txt_label)
    TextView txtLabel;
    @BindView(R.id.txt_no_use)
    TextView txtNoUse;

    private LinearLayoutManager mLayoutManager;
    private List<RedBean> list;
    private BaseRecyclerAdapter<RedBean> adapter;
    private int page = 1;

    @Override
    public void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_hongbao);
    }

    @Override
    public void initView() {
        txtLeft.setTypeface(StringUtils.getFont(mContext));
        txtTitle.setText("历史红包");
        txtLabel.setText("没有历史红包");
        txtHistory.setVisibility(View.GONE);
        txtNoUse.setVisibility(View.GONE);
        list = new ArrayList<>();
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
                3,1,"","0",pageNum),true);
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
                    if("2".equals(bean.getStatus())){
                        if(bean.getRed_type().equals("1")){
                            holder.setBackgroundRes(R.id.ll_item,R.mipmap.xm_ysy);
                        }else {
                            holder.setBackgroundRes(R.id.ll_item,R.mipmap.sp_ysy);
                        }
                    }else {
                        if(bean.getRed_type().equals("1")){
                            holder.setBackgroundRes(R.id.ll_item,R.mipmap.xm_ls);
                        }else {
                            holder.setBackgroundRes(R.id.ll_item,R.mipmap.sp_ls);
                        }
                    }
                    holder.setText(R.id.txt_name,"      "+bean.getShop_name());
                    holder.setText(R.id.txt_time,bean.getValid_date());
                    holder.setText(R.id.txt_amount,bean.getRed_amount());
                    holder.setText(R.id.txt_full_amount,"满"+bean.getFullcut_amount()+"可用");
                    holder.setTextColor(R.id.txt_name,getResources().getColor(R.color.text_99));
                    holder.setTextColor(R.id.txt_time,getResources().getColor(R.color.text_99));
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
    }

    @Override
    @OnClick({R.id.txt_left})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.txt_left:
                finish();
                break;
        }
    }

}
