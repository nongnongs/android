package com.maapuu.mereca.background.shop.activity;

import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.maapuu.mereca.R;
import com.maapuu.mereca.background.shop.bean.ProjectOrderDetailBean;
import com.maapuu.mereca.background.shop.bean.ShopDataBean;
import com.maapuu.mereca.base.BaseActivity;
import com.maapuu.mereca.bean.OrderChildBean;
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
import com.maapuu.mereca.view.NestedRecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 后台 商品订单详情
 * Created by Jia on 2018/4/24.
 */

public class ShopOrderGoodsDetailActivity extends BaseActivity {
    @BindView(R.id.txt_left)
    TextView txtLeft;
    @BindView(R.id.txt_title)
    TextView txtTitle;

    @BindView(R.id.iv_status)
    ImageView ivStatus;
    @BindView(R.id.txt_status)
    TextView txtStatus;
    @BindView(R.id.txt_name)
    TextView txtName;
    @BindView(R.id.txt_phone)
    TextView txtPhone;
    @BindView(R.id.txt_address)
    TextView txtAddress;
    @BindView(R.id.txt_shop_name)
    TextView txtShopName;

    @BindView(R.id.recycler_view)
    NestedRecyclerView recyclerView;

    @BindView(R.id.txt_srv_price)
    TextView txtSrvPrice;//暂未使用
    @BindView(R.id.txt_youhui_price)
    TextView txtYouhuiPrice;
    @BindView(R.id.txt_order_price)
    TextView txtOrderPrice;

    @BindView(R.id.txt_pay_amount)
    TextView txtPayAmount;

    @BindView(R.id.logistics_ll)
    LinearLayout logisticsLl;//物流信息模块
    @BindView(R.id.logistics_no)
    TextView logisticsNoTv;
    @BindView(R.id.company_name)
    TextView companyNameTv;


    @BindView(R.id.txt_order_no)
    TextView txtOrderNo;
    @BindView(R.id.txt_pay_type)
    TextView txtPayType;
    @BindView(R.id.txt_order_time)
    TextView txtOrderTime;

    private BaseRecyclerAdapter<OrderChildBean> adapter;
    private String oid;

    @Override
    public void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.shop_activity_order_detail_goods);
    }

    @Override
    public void initView() {
        txtLeft.setTypeface(StringUtils.getFont(mContext));
        txtTitle.setText("订单详情");
        oid = getIntent().getStringExtra("oid");
    }

    @Override
    public void initData() {
        getOrderDetail();
    }

    private void getOrderDetail() {
        mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_1,
                UrlUtils.s_commodity_order_detail_get(LoginUtil.getInfo("token"), LoginUtil.getInfo("uid"),
                        oid),true);
    }

    private void setUI(ProjectOrderDetailBean bean) {
        final List<OrderChildBean> items = bean.getItems();
        if(items != null && items.size()>0){
            //多个商品
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new FullyLinearLayoutManager(mContext, LinearLayoutManager.VERTICAL,false));
            recyclerView.addItemDecoration(new RecyclerViewDivider(mContext, LinearLayoutManager.VERTICAL,10
                    ,getResources().getColor(R.color.white)));
            adapter = new BaseRecyclerAdapter<OrderChildBean>(mContext,items,R.layout.layout_goods_order_good_item) {
                @Override
                public void convert(BaseRecyclerHolder cHolder, OrderChildBean child, final int pos, boolean isScrolling) {
                    cHolder.setSimpViewImageUri(R.id.iv_goods, Uri.parse(child.getItem_img()));
                    cHolder.setText(R.id.txt_goods_name,child.getItem_name());
                    cHolder.setText(R.id.txt_goods_spec,"规格："+child.getItem_specification());
                    cHolder.setText(R.id.txt_goods_price,"¥"+child.getPrice());
                    cHolder.setText(R.id.txt_goods_num,"×"+child.getNum());
                    if(items.get(pos).getIs_refund().equals("1")){
                        cHolder.setVisible(R.id.txt_apply_refund,true);
                        cHolder.setVisible(R.id.txt_refund_status,false);
                    }else {
                        cHolder.setVisible(R.id.txt_apply_refund,false);
                        cHolder.setVisible(R.id.txt_refund_status,true);
                        cHolder.setText(R.id.txt_refund_status,child.getRefund_text());
                    }
                }
            };
            recyclerView.setAdapter(adapter);
        }

        //地址信息
        ProjectOrderDetailBean.AddressInfo address = bean.getAddress_info();
        if(address != null){
            txtName.setText(address.getReceiver());
            txtPhone.setText(address.getReceiver_phone());
            txtAddress.setText(address.getAddress_detail());

            if(!TextUtils.isEmpty(address.getLogistics_no())){
                logisticsLl.setVisibility(View.VISIBLE);
                logisticsNoTv.setText("物流单号："+address.getLogistics_no());
                companyNameTv.setText("物流公司："+address.getCompany_name());
            }
        }

        ShopDataBean shop = bean.getShop_data();
        if(shop != null){
            txtShopName.setText(shop.getShop_name());
        }

        ProjectOrderDetailBean.OrderDetailBean order = bean.getOrder_detail();
        if(order != null){
            txtStatus.setText(order.getStatus());//状态
            txtYouhuiPrice.setText("¥"+order.getDis_amount());
            txtOrderPrice.setText("¥"+order.getOrder_amount());
            txtPayAmount.setText("¥"+order.getPay_amount());

            txtOrderNo.setText("订单编号："+order.getOrder_no());
            switch (order.getPay_type()){
                case 1:
                    txtPayType.setText("支付方式："+"支付宝支付");
                    break;
                case 2:
                    txtPayType.setText("支付方式："+"微信支付");
                    break;
                case 3:
                    txtPayType.setText("支付方式："+"余额支付");
                    break;
            }
            txtOrderTime.setText("下单时间："+order.getCreate_time_text());
        }

    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case HttpModeBase.HTTP_REQUESTCODE_1:
                try {
                    JSONObject object = new JSONObject((String) msg.obj);
                    if (object.optInt("status") == 1) {
                        ProjectOrderDetailBean bean = FastJsonTools.getPerson(object.optString("result"), ProjectOrderDetailBean.class);
                        if(bean != null){
                            setUI(bean);
                        }
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
    @OnClick({R.id.txt_left})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.txt_left:
                finish();
                break;
        }
    }

}
