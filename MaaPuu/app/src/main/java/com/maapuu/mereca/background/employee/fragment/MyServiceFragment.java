package com.maapuu.mereca.background.employee.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.luck.picture.lib.decoration.RecycleViewDivider;
import com.maapuu.mereca.R;
import com.maapuu.mereca.background.employee.activity.ServiceDetailActivity;
import com.maapuu.mereca.background.employee.bean.MySrvBean;
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
import com.maapuu.mereca.util.UIUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 我的服务
 * Created by Jia on 2018/2/28.
 */

public class MyServiceFragment extends BaseFragment {

    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.ll_has)
    LinearLayout llHas;
    @BindView(R.id.txt_empty_label)
    TextView txtEmptyLabel;

    private FullyLinearLayoutManager mLayoutManager;
    private BaseRecyclerAdapter<MySrvBean> adapter;
    private List<MySrvBean> list;
    private int selectedIndex = 0;
    private boolean isShowProgress = true;

    public static MyServiceFragment newInstance(int selectedIndex){
        MyServiceFragment fragment = new MyServiceFragment();
        Bundle args = new Bundle();
        args.putInt("selectedIndex",selectedIndex);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initBundle(Bundle bundle) {
        if(bundle != null){
            selectedIndex = bundle.getInt("selectedIndex",0);
        }
    }

    @Override
    protected int setContentViewById() {
        return R.layout.em_fragment_my_service;
    }

    @Override
    protected void initView(View v) {
        mLayoutManager=new FullyLinearLayoutManager(mContext, LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        //分割线
        recyclerView.addItemDecoration(new RecycleViewDivider(mContext,LinearLayoutManager.HORIZONTAL,2,
                getResources().getColor(R.color.background)));
        list = new ArrayList<>();
        refreshLayout.setEnableRefresh(true);//是否启用下拉刷新功能
        refreshLayout.setEnableLoadMore(false);//是否启用上拉加载功能
        refreshLayout.setEnableAutoLoadMore(false);//是否启用自动加载功能
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getServiceDetail();
            }
        });
    }

    @Override
    protected void initData() {
        //getServiceDetail();
    }

    @Override
    public void onResume() {
        super.onResume();
        getServiceDetail();
    }

    @Override
    public void loadData() {
        super.loadData();
        getServiceDetail();
    }

    private void getServiceDetail(){
        if(selectedIndex == 0){
            mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_1, UrlUtils.s_being_srv_get(LoginUtil.getInfo("token"),LoginUtil.getInfo("uid")),isShowProgress);
        }else {
            mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_1, UrlUtils.s_completed_srv_get(LoginUtil.getInfo("token"),LoginUtil.getInfo("uid")),isShowProgress);
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case HttpModeBase.HTTP_REQUESTCODE_1:
                refreshLayout.finishRefresh();
                refreshLayout.finishLoadMore();
                isShowProgress = false;
                try {
                    JSONObject object = new JSONObject((String) msg.obj);
                    if (object.optInt("status") == 1) {
                        if(!StringUtils.isEmpty(object.optString("result")) && object.optJSONArray("result").length() > 0){
                            llHas.setVisibility(View.GONE);
                            list = FastJsonTools.getPersons(object.optString("result"),MySrvBean.class);
                        }else {
                            llHas.setVisibility(View.VISIBLE);
                            if(selectedIndex == 0){
                                txtEmptyLabel.setText("目前还没有需要服务的项目");
                            }else {
                                txtEmptyLabel.setText("目前还没有已完成的服务");
                            }
                        }
                        setAdapter();
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

    private void setAdapter() {
        if(adapter == null){
            adapter = new BaseRecyclerAdapter<MySrvBean>(mContext,list,R.layout.em_item_my_service) {
                @Override
                public void convert(BaseRecyclerHolder holder, MySrvBean bean, int position, boolean isScrolling) {
                    holder.setSimpViewImageUri(R.id.iv_icon, Uri.parse(bean.getAvatar()));
                    holder.setTypeface(StringUtils.getFont(mContext),R.id.arrow_right);
                    holder.setText(R.id.txt_name,bean.getNick_name());
                    holder.setText(R.id.txt_goods_name,bean.getItem_name());
                    holder.setText(R.id.s_state_tv,bean.getSrv_name());

                    TextView timeTv = holder.getView(R.id.s_time_tv);
                    if(selectedIndex == 1){
                        holder.setVisible(R.id.arrow_right,false);
                        holder.setTextColor(R.id.s_state_tv,UIUtils.getColor(R.color.bg_red));
                        holder.setTextColor(R.id.s_time_tv,UIUtils.getColor(R.color.text_99));
                        timeTv.setText(bean.getSrv_end_time_text());
                    } else {
                        holder.setVisible(R.id.arrow_right,false);
                        holder.setTextColor(R.id.s_state_tv,UIUtils.getColor(R.color.bg_red));
                        holder.setTextColor(R.id.s_time_tv,UIUtils.getColor(R.color.text_99));

                        String time = bean.getUsed_time();
                        String timeTxt = "已经进行"+time+"分钟";
                        SpannableString ss = new SpannableString(timeTxt);
                        // 设置字体大小
                        ss.setSpan(new AbsoluteSizeSpan(UIUtils.sp2px(mContext, 18)), timeTxt.indexOf(time), 4+time.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        // 设置字体颜色
                        ss.setSpan(new ForegroundColorSpan(UIUtils.getColor(R.color.bg_red)), timeTxt.indexOf(time), 4+time.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        timeTv.setText(ss);
                    }
                }
            };
            recyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
        adapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position) {
                MySrvBean bean = list.get(position);
                Intent intent = new Intent(mContext,ServiceDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("selectedIndex",selectedIndex); //工作中0 已完成1
                bundle.putString("appoint_srv_id",bean.getAppoint_srv_id());
                intent.putExtras(bundle);
                UIUtils.startActivity(mContext,intent);
            }
        });
    }

    @Override
    public void onClick(View v) {

    }
}
