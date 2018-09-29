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
import com.maapuu.mereca.background.shop.bean.GoodsRefundBean;
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

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 商品订单退款列表
 * Created by Jia on 2018/4/25.
 */

public class GoodsOrderRefundListActivity extends BaseActivity {
    @BindView(R.id.txt_left)
    TextView txtLeft;
    @BindView(R.id.txt_title)
    TextView txtTitle;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private FullyLinearLayoutManager mLayoutManager;
    private BaseRecyclerAdapter adapter;
    String oid = "";

    @Override
    public void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.shop_activity_goods_order_refund_list);
    }

    @Override
    public void initView() {
        oid = getIntent().getStringExtra("oid");
        txtLeft.setTypeface(StringUtils.getFont(mContext));
        txtTitle.setText("商品订单退款列表");

        mLayoutManager = new FullyLinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        //分割线
        recyclerView.addItemDecoration(new RecycleViewDivider(mContext, LinearLayoutManager.HORIZONTAL,
                UIUtils.dip2px(mContext,10),
                getResources().getColor(R.color.background)));

        adapter = new BaseRecyclerAdapter<GoodsRefundBean>(mContext, R.layout.shop_item_goods_order_refund) {
            @Override
            public void convert(BaseRecyclerHolder holder, final GoodsRefundBean bean, final int position, boolean isScrolling) {
                holder.setText(R.id.nick_name,bean.getNick_name());
                holder.setText(R.id.phone,bean.getPhone());
                holder.setText(R.id.txt_goods_name,bean.getItem_name());
                holder.setText(R.id.refund_type,bean.getRefund_type());
                holder.setText(R.id.refund_amount,"¥"+bean.getRefund_amount());
                holder.setText(R.id.order_no,bean.getOrder_no());
                holder.setText(R.id.apply_time,bean.getApply_time());

                TextView refundTv = holder.getView(R.id.refund_status);
                if(bean.getRefund_status() == 1){
                    refundTv.setText("退款中");
                } else if(bean.getRefund_status() == 2){
                    refundTv.setText("退款成功");
                } else if(bean.getRefund_status() == 3){
                    refundTv.setText("用户取消退款");
                } else if(bean.getRefund_status() == 4){
                    refundTv.setText("商家拒绝");
                }
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position) {
                //退款详情
                GoodsRefundBean bean = (GoodsRefundBean) adapter.getList().get(position);
                it = new Intent(mContext,OrderRefundActivity.class);
                it.putExtra("refund_id",bean.getRefund_id());
                it.putExtra("refund_status",bean.getRefund_status());
                startActivity(it);
            }
        });
    }

    @Override
    public void initData() {
        getRefundList();
    }

    private void setUI(List<GoodsRefundBean> list) {
        adapter.clear();
        adapter.addList(list);
    }

    private void getRefundList() {
        mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_1,
                UrlUtils.s_commodity_refund_list_get(LoginUtil.getInfo("token"), LoginUtil.getInfo("uid"),
                        oid),true);
    }

    @Override
    @OnClick({R.id.txt_left})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_left:
                finish();
                break;
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case HttpModeBase.HTTP_REQUESTCODE_1:
                try {
                    JSONObject object = new JSONObject((String) msg.obj);
                    if (object.optInt("status") == 1) {
                        if (!StringUtils.isEmpty(object.optString("result"))) {
                            List<GoodsRefundBean> list = FastJsonTools.getPersons(object.optString("result"), GoodsRefundBean.class);
                            setUI(list);
                        }
                    } else {
                        ToastUtil.show(mContext, object.optString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case HttpModeBase.HTTP_ERROR:
                String result_e = (String) msg.obj;
                ToastUtil.show(mContext, result_e);
                break;
        }
        return false;
    }

}
