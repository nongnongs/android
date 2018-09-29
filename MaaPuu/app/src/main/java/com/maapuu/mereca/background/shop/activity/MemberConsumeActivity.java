package com.maapuu.mereca.background.shop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.maapuu.mereca.R;
import com.maapuu.mereca.background.shop.bean.ConsumeBean;
import com.maapuu.mereca.background.shop.bean.MemberBean;
import com.maapuu.mereca.background.shop.bean.MemberDetailBean;
import com.maapuu.mereca.base.AppConfig;
import com.maapuu.mereca.base.BaseActivity;
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
import butterknife.OnClick;

/**
 * 消费记录
 * Created by dell on 2018/3/22.
 */

public class MemberConsumeActivity extends BaseActivity {
    @BindView(R.id.txt_left)
    TextView txtLeft;
    @BindView(R.id.txt_title)
    TextView txtTitle;

    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    @BindView(R.id.txt_name)
    TextView nameTv;
    @BindView(R.id.em_sex_iv)
    ImageView sexIv;
    @BindView(R.id.ac_age_tv)
    TextView ageTv;
    @BindView(R.id.ac_card_name_tv)
    TextView cardNameTv;
    @BindView(R.id.ac_phone_tv)
    TextView phoneTv;
    @BindView(R.id.txt_remain_label)
    TextView txtRemainLabel;
    @BindView(R.id.txt_remain_amount)
    TextView txtRemainAmount;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private BaseRecyclerAdapter adapter;
    private FullyLinearLayoutManager layoutManager;

    String member_id = "";
    int page = 1;
    boolean isShowProgress = true;

    @Override
    public void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.shop_activity_member_consume);
    }

    @Override
    public void initView() {
        member_id = getIntent().getStringExtra("member_id");
        txtLeft.setTypeface(StringUtils.getFont(mContext));
        txtTitle.setText("消费记录");

        refreshLayout.setEnableRefresh(true);//是否启用下拉刷新功能
        refreshLayout.setEnableLoadMore(true);//是否启用上拉加载功能
        refreshLayout.setEnableAutoLoadMore(false);//是否启用自动加载功能
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                page = 1;
                getMemberDetail();
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(final RefreshLayout refreshLayout) {
                page++;
                getMemberDetail();
            }
        });
        layoutManager = new FullyLinearLayoutManager(mContext, LinearLayoutManager.VERTICAL,false);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new BaseRecyclerAdapter<ConsumeBean>(mContext,R.layout.shop_item_member_consume) {
            @Override
            public void convert(BaseRecyclerHolder holder, ConsumeBean bean, int position, boolean isScrolling) {
                holder.setText(R.id.mc_item_name,bean.getItem_name());
                holder.setText(R.id.mc_pay_amount,"¥"+bean.getPay_amount());
                holder.setText(R.id.mc_shop_name, bean.getShop_name());
                holder.setText(R.id.mc_create_time_text, bean.getCreate_time_text());
            }
        };
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void initData() {
        getMemberDetail();
    }

    private void getMemberDetail() {
        mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_1,
                UrlUtils.s_member_detail_get(LoginUtil.getInfo("token"), LoginUtil.getInfo("uid"),
                        member_id,
                        page),isShowProgress);
    }

    private void setUI(MemberDetailBean bean) {
        MemberBean member = bean.getMember_info();
        if(member != null){

            nameTv.setText(member.getNick_name());
            int sex = member.getSex();//性别：1男；2女；0未知
            if(sex == 1){
                sexIv.setVisibility(View.VISIBLE);
                sexIv.setImageResource(R.mipmap.nanbiao);
            } else if(sex == 2){
                sexIv.setVisibility(View.VISIBLE);
                sexIv.setImageResource(R.mipmap.nvbiaoji);
            } else {
                sexIv.setVisibility(View.GONE);
            }
            ageTv.setText(member.getAge());
            phoneTv.setText(member.getPhone());
            cardNameTv.setText(member.getCard_name());
            if("3".equals(member.getCard_type())){
                txtRemainAmount.setVisibility(View.VISIBLE);
                txtRemainLabel.setVisibility(View.VISIBLE);
                txtRemainAmount.setText("¥"+member.getRemain_amount());
            }else {
                txtRemainAmount.setVisibility(View.GONE);
                txtRemainLabel.setVisibility(View.GONE);
            }
        }

        List<ConsumeBean> consumeList = bean.getOrder_list();
        if(page == 1) adapter.clear();
        adapter.addList(consumeList);
        if(page > 1){
            if((consumeList ==null || consumeList.size()==0)){
                page--;
                ToastUtil.show(mContext,"暂无更多数据");
            } else {
                recyclerView.smoothScrollToPosition(adapter.getList().size()-consumeList.size()-1);
            }
        }
    }


    @Override
    @OnClick({R.id.txt_left,R.id.ac_card_name_tv})
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.txt_left:
                finish();
                break;

            case R.id.ac_card_name_tv:
                it = new Intent(mContext,ChooseVipCardActivity.class);
                it.putExtra("member_id",member_id);
                startActivityForResult(it, AppConfig.ACTIVITY_REQUESTCODE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && data != null){
            switch (requestCode){
                case AppConfig.ACTIVITY_REQUESTCODE:
                    member_id = data.getStringExtra("member_id");
                    page = 1;
                    getMemberDetail();

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
                            MemberDetailBean bean = FastJsonTools.getPerson(object.optString("result"), MemberDetailBean.class);
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
