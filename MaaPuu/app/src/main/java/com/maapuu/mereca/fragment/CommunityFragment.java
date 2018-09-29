package com.maapuu.mereca.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.gyf.barlibrary.ImmersionBar;
import com.maapuu.mereca.R;
import com.maapuu.mereca.activity.ArticleDetailActivity;
import com.maapuu.mereca.activity.LoginActivity;
import com.maapuu.mereca.activity.PublicTopicActivity;
import com.maapuu.mereca.adapter.CommunityRecyclerAdapter;
import com.maapuu.mereca.base.AppConfig;
import com.maapuu.mereca.base.BaseFragment;
import com.maapuu.mereca.bean.CircleBean;
import com.maapuu.mereca.constant.UrlUtils;
import com.maapuu.mereca.recycleviewutil.FullyLinearLayoutManager;
import com.maapuu.mereca.recycleviewutil.RecyclerViewDivider;
import com.maapuu.mereca.util.EmojiUtil;
import com.maapuu.mereca.util.FastJsonTools;
import com.maapuu.mereca.util.HttpModeBase;
import com.maapuu.mereca.util.LoginUtil;
import com.maapuu.mereca.util.StringUtils;
import com.maapuu.mereca.util.ToastUtil;
import com.maapuu.mereca.view.SearchEditText;
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
 * Created by dell on 2018/1/11.
 */

public class CommunityFragment extends BaseFragment {
    @BindView(R.id.txt_left)
    TextView txtLeft;
    @BindView(R.id.txt_right)
    TextView txtRight;
    @BindView(R.id.ll_title_search)
    LinearLayout llTitleSearch;
    @BindView(R.id.et_search)
    SearchEditText etSearch;
    @BindView(R.id.txt_back)
    TextView txtBack;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private LinearLayoutManager mLayoutManager;
    private List<CircleBean> list;
    private CommunityRecyclerAdapter adapter;
    private int page = 1;
    private String keywords = "";

    private int delPos = -1;
    private int zanPos = -1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImmersionBar.with(getActivity()).fitsSystemWindows(true).statusBarColor(R.color.white)
                .statusBarDarkFont(true,0f).init();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
            ImmersionBar.with(getActivity()).fitsSystemWindows(true).statusBarDarkFont(true,0f).init();
        }
    }

    @Override
    protected int setContentViewById() {
        return R.layout.fragment_community;
    }

    @Override
    protected void initView(View v) {
        txtLeft.setTypeface(StringUtils.getFont(mContext));
        txtRight.setTypeface(StringUtils.getFont(mContext));
        list = new ArrayList<>();
        mLayoutManager=new FullyLinearLayoutManager(mContext, LinearLayoutManager.VERTICAL,false);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new RecyclerViewDivider(mContext, LinearLayoutManager.VERTICAL,2
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

        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH){
                    if(StringUtils.isEmpty(etSearch.getText().toString())){
                        keywords = "";
                    }else {
                        keywords = EmojiUtil.stringToUtf8(etSearch.getText().toString().trim()).replace("+","%20");
                    }
                    page = 1;
                    initData(page);
                }
                return false;
            }
        });

    }

    @Override
    protected void initData() {
        initData(page);
    }

    private void initData(int pageNum) {
        mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_1,
                UrlUtils.circle_list_get(LoginUtil.getInfo("token"),LoginUtil.getInfo("uid"),keywords,pageNum),true);
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
                            List<CircleBean> lsJson = FastJsonTools.getPersons(object.optString("result"),CircleBean.class);
                            setAdapter(lsJson);
                            page ++ ;
                        }else {
                            List<CircleBean> lsJson = new ArrayList<>();
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
            case HttpModeBase.HTTP_REQUESTCODE_2:
                try {
                    JSONObject object = new JSONObject((String) msg.obj);
                    if (object.optInt("status") == 1) {
                        ToastUtil.show(mContext,"删除成功");
                        if(delPos != -1){
                            list.remove(delPos);
                            adapter.notifyDataSetChanged();
                            delPos = -1;
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
                        if(zanPos != -1){
                            if(list.get(zanPos).getIs_praise() == 1){
//                                ToastUtil.show(mContext,"取消成功");
                                list.get(zanPos).setIs_praise(0);
                                if(!StringUtils.isEmpty(list.get(zanPos).getPraise_num()) && !list.get(zanPos).getPraise_num().equals("0")){
                                    if(Integer.parseInt(list.get(zanPos).getPraise_num())-1 == 0){
                                        list.get(zanPos).setPraise_num("0");
                                    }else {
                                        list.get(zanPos).setPraise_num(String.valueOf(Integer.parseInt(list.get(zanPos).getPraise_num())-1));
                                    }
                                }
                            }else {
//                                ToastUtil.show(mContext,"点赞成功");
                                list.get(zanPos).setIs_praise(1);
                                if(!StringUtils.isEmpty(list.get(zanPos).getPraise_num()) && !list.get(zanPos).getPraise_num().equals("0")){
                                    list.get(zanPos).setPraise_num(String.valueOf(Integer.parseInt(list.get(zanPos).getPraise_num())+1));
                                }else {
                                    list.get(zanPos).setPraise_num("1");
                                }
                            }
                            adapter.notifyDataSetChanged();
                            zanPos = -1;
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

    private void setAdapter(List<CircleBean> lsJson) {
        if(page == 1)
            list.clear();
        list.addAll(lsJson);
        if(adapter == null){
            adapter = new CommunityRecyclerAdapter(mContext,list);
            recyclerView.setAdapter(adapter);
        }else {
            if(page > 1){
                adapter.notifyItemRangeInserted(list.size()-lsJson.size(),lsJson.size());
//                recyclerView.smoothScrollToPosition(list.size()-lsJson.size()-1);
            }else {
                adapter.notifyDataSetChanged();
            }
        }
        adapter.setOnItemClickListener(new CommunityRecyclerAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view) {
                int position=recyclerView.getChildAdapterPosition(view);
                it = new Intent(mContext, ArticleDetailActivity.class);
                it.putExtra("circle_id",list.get(position).getCircle_id());
                startActivity(it);
            }

            @Override
            public void onDelClick(final int position) {
                if(LoginUtil.getLoginState()){
                    new AlertView(null, "确定删除吗？", "取消", null, new String[]{"确定"}, mContext,
                            AlertView.Style.Alert, new OnItemClickListener() {
                        @Override
                        public void onItemClick(Object o, int pos) {
                            if (pos == 0) {
                                delPos = position;
                                mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_2,UrlUtils.circle_delete_set(LoginUtil.getInfo("token"),LoginUtil.getInfo("uid"),
                                        list.get(position).getCircle_id()),true);
                            }
                        }
                    }).show();
                }else {
                    startActivity(new Intent(mContext, LoginActivity.class));
                }
            }

            @Override
            public void onZanClick(int position) {
                if(LoginUtil.getLoginState()){
                    zanPos = position;
                    mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_3,UrlUtils.circle_praise_set(LoginUtil.getInfo("token"),LoginUtil.getInfo("uid"),
                            list.get(position).getCircle_id(),"1".equals(list.get(position).getIs_praise())?"0":"1"),true);
                }else {
                    startActivity(new Intent(mContext, LoginActivity.class));
                }
            }
        });
    }

    @Override
    @OnClick({R.id.txt_left,R.id.txt_right,R.id.txt_back})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.txt_left:
                llTitleSearch.setVisibility(View.VISIBLE);
                break;
            case R.id.txt_right:
                if(LoginUtil.getLoginState()){
                    startActivityForResult(new Intent(mContext, PublicTopicActivity.class), AppConfig.ACTIVITY_REQUESTCODE);
                }else {
                    startActivity(new Intent(mContext, LoginActivity.class));
                }
                break;

            case R.id.txt_back:
                llTitleSearch.setVisibility(View.GONE);
                if(!StringUtils.isEmpty(keywords)){
                    etSearch.getText().clear();
                    keywords = "";
                    page = 1;
                    initData(1);
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case AppConfig.ACTIVITY_RESULTCODE:
                page = 1;
                initData(page);
                break;
        }
    }
}
