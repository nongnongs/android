package com.maapuu.mereca.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.maapuu.mereca.R;
import com.maapuu.mereca.base.AppConfig;
import com.maapuu.mereca.base.BaseActivity;
import com.maapuu.mereca.bean.MyCardBean;
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
 * Created by dell on 2018/3/5.
 */

public class MyMembershipCardActivity extends BaseActivity {
    @BindView(R.id.txt_left)
    TextView txtLeft;
    @BindView(R.id.txt_title)
    TextView txtTitle;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private LinearLayoutManager mLayoutManager;
    private List<MyCardBean> list;
    private BaseRecyclerAdapter<MyCardBean> adapter;
    private String item_shop_ids;
    private String member_id;
    private int page = 1;

    @Override
    public void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_my_membership_card);
    }

    @Override
    public void initView() {
        txtLeft.setTypeface(StringUtils.getFont(mContext));
        txtTitle.setText("我的会员卡");
        list = new ArrayList<>();
        item_shop_ids = getIntent().getStringExtra("item_shop_ids");
        member_id = getIntent().getStringExtra("member_id");

        mLayoutManager=new FullyLinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false);
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

    @Override
    public void initData() {
        initData(page);
    }

    private void initData(int pageNum){
        mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_1, UrlUtils.order_member_list_get(LoginUtil.getInfo("token"),
                LoginUtil.getInfo("uid"),item_shop_ids,pageNum),true);
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
                            List<MyCardBean> lsJson = FastJsonTools.getPersons(object.optString("result"),MyCardBean.class);
                            setAdapter(lsJson);
                            page ++ ;
                        } else {
                            List<MyCardBean> lsJson = new ArrayList<>();
                            setAdapter(lsJson);
                            if(page != 1){
                                ToastUtil.show(mContext,"暂无更多数据");
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

    private void setAdapter(List<MyCardBean> lsJson) {
        if(page == 1)
            list.clear();
        list.addAll(lsJson);
        if(!StringUtils.isEmpty(member_id)){
            for (int i = 0; i < list.size(); i++){
                if(list.get(i).getMember_id().equals(member_id)){
                    list.get(i).setBool(true);
                    break;
                }
            }
        }
        if(adapter == null){
            adapter = new BaseRecyclerAdapter<MyCardBean>(mContext,list,R.layout.layout_my_membership_card_item) {
                @Override
                public void convert(BaseRecyclerHolder holder, MyCardBean bean, int position, boolean isScrolling) {
                    holder.setSelected(R.id.iv_choose,bean.isBool());
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
        adapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position) {
                it = new Intent();
                it.putExtra("membercard",list.get(position));
                setResult(AppConfig.ACTIVITY_RESULTCODE_1,it);
                finish();
            }
        });
    }

    @Override
    @OnClick({R.id.txt_left,R.id.txt_no_use})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.txt_left:
                finish();
                break;
            case R.id.txt_no_use:
                setResult(AppConfig.ACTIVITY_RESULTCODE_3);
                finish();
                break;
        }
    }
}
