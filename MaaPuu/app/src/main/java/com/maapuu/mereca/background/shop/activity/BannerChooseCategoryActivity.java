package com.maapuu.mereca.background.shop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.luck.picture.lib.decoration.RecycleViewDivider;
import com.maapuu.mereca.R;
import com.maapuu.mereca.background.shop.bean.ItemBean;
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
import com.maapuu.mereca.util.UIUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 添加banner 选择 项目 发型师 商品
 * Created by Jia on 2018/3/14.
 */

public class BannerChooseCategoryActivity extends BaseActivity {

    @BindView(R.id.txt_left)
    TextView txtLeft;
    @BindView(R.id.txt_title)
    TextView txtTitle;
    @BindView(R.id.txt_right)
    TextView txtRight;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private FullyLinearLayoutManager mLayoutManager;
    private BaseRecyclerAdapter<ItemBean> adapter;
    private List<ItemBean> list;

    private String shop_id;
    private int adv_type;
    private String adv_value;
    private String adv_name;

    @Override
    public void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.shop_activity_banner_choose_category);
    }

    @Override
    public void initView() {
        txtLeft.setTypeface(StringUtils.getFont(mContext));
        txtTitle.setText(getIntent().getStringExtra("title"));//选择发型师 选择项目 选择商品
        txtRight.setVisibility(View.VISIBLE);
        txtRight.setText("保存");
        shop_id = getIntent().getStringExtra("shop_id");
        adv_value = getIntent().getStringExtra("adv_value");
        adv_type = getIntent().getIntExtra("adv_type",0);

        mLayoutManager = new FullyLinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        //分割线
        recyclerView.addItemDecoration(new RecycleViewDivider(mContext, LinearLayoutManager.HORIZONTAL,
                UIUtils.dip2px(mContext,1),
                getResources().getColor(R.color.background)));
        list = new ArrayList<>();
    }

    @Override
    public void initData() {
        if(adv_type == 2){
            mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_1, UrlUtils.s_shop_project_sel_get(LoginUtil.getInfo("token"),LoginUtil.getInfo("uid"),shop_id),true);
        }else if(adv_type == 3){
            mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_1, UrlUtils.s_shop_commodity_sel_get(LoginUtil.getInfo("token"),LoginUtil.getInfo("uid"),shop_id),true);
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case HttpModeBase.HTTP_REQUESTCODE_1:
                try {
                    JSONObject object = new JSONObject((String) msg.obj);
                    if (object.optInt("status") == 1) {
                        if(!StringUtils.isEmpty(object.optString("result")) && object.optJSONArray("result").length() > 0){
                            list = FastJsonTools.getPersons(object.optString("result"),ItemBean.class);
                        }
                        if(!StringUtils.isEmpty(adv_value)){
                            for (int i = 0; i < list.size(); i++){
                                if(list.get(i).getItem_shop_id().equals(adv_value)){
                                    list.get(i).setBool(true);
                                    break;
                                }
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
                String result_e = (String) msg.obj;
                ToastUtil.show(mContext,result_e);
                break;
        }
        return false;
    }

    @Override
    @OnClick({R.id.txt_left,R.id.txt_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_left:
                finish();
                break;
            case R.id.txt_right:
                if(StringUtils.isEmpty(adv_value)){
                    ToastUtil.show(mContext,"请"+txtTitle.getText().toString());
                    return;
                }
                it = new Intent();
                it.putExtra("adv_value",adv_value);
                it.putExtra("adv_name",adv_name);
                setResult(AppConfig.ACTIVITY_RESULTCODE,it);
                finish();
                break;
        }
    }

    private void setAdapter() {
        if (adapter == null) {
            adapter = new BaseRecyclerAdapter<ItemBean>(mContext, list, R.layout.shop_item_add_project) {
                @Override
                public void convert(BaseRecyclerHolder holder, ItemBean bean, int position, boolean isScrolling) {
                    holder.setTypeface(StringUtils.getFont(mContext),R.id.ap_check_status_ic);
                    holder.setText(R.id.pmc_project_name_tv,bean.getItem_name());
                    holder.setVisible(R.id.ap_check_status_ic,bean.isBool());
                }
            };
            recyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
        adapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position) {
                for (int i = 0; i < list.size(); i++){
                    if(position == i){
                        adv_value = list.get(position).getItem_shop_id();
                        adv_name = list.get(position).getItem_name();
                        list.get(position).setBool(true);
                    }else {
                        list.get(i).setBool(false);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

}
