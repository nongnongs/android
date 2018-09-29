package com.maapuu.mereca.background.shop.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.luck.picture.lib.decoration.RecycleViewDivider;
import com.maapuu.mereca.R;
import com.maapuu.mereca.background.shop.activity.GoodsOrderRefundListActivity;
import com.maapuu.mereca.background.shop.activity.GoodsOrderRemindShipActivity;
import com.maapuu.mereca.background.shop.activity.GoodsOrderShipActivity;
import com.maapuu.mereca.background.shop.activity.OrderCommentDetailActivity;
import com.maapuu.mereca.background.shop.activity.OrderComplaintActivity;
import com.maapuu.mereca.background.shop.activity.OrderManageActivity;
import com.maapuu.mereca.background.shop.activity.ShopOrderGoodsDetailActivity;
import com.maapuu.mereca.background.shop.bean.OrderBean;
import com.maapuu.mereca.background.shop.bean.OrderManageBean;
import com.maapuu.mereca.base.AppConfig;
import com.maapuu.mereca.base.BaseFragment;
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
import com.maapuu.mereca.util.UIUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 商品订单
 * Created by Jia on 2018/4/23.
 */

public class OrderCommodityFragment extends BaseFragment implements OrderManageActivity.OnSelectListener  {

    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.ll_has)
    LinearLayout llHas;

    private FullyLinearLayoutManager mLayoutManager;
    private BaseRecyclerAdapter<OrderBean> adapter;
    private List<OrderBean> list;
    private int page = 1;
    boolean isShowProgress = true;

    String shop_id = "0"; //店铺id，全部店铺传0
    String date = ""; //日期过滤，如：2018-04-18
    String status = ""; //订单状态：待使用,待评价,已完成，状态传中文
    String complaint = ""; //投诉：1投诉建议,2已处理
    String refund = ""; //退款：1退款申请,2退款成功,4商家拒绝

    @Override
    protected int setContentViewById() {
        return R.layout.shop_fragment_order_commodity;
    }

    @Override
    protected void initView(View v) {
        refreshLayout.setEnableRefresh(true);//是否启用下拉刷新功能
        refreshLayout.setEnableLoadMore(true);//是否启用上拉加载功能
        refreshLayout.setEnableAutoLoadMore(false);//是否启用自动加载功能
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                page = 1;
                getOrderList();
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(final RefreshLayout refreshLayout) {
                page++;
                getOrderList();
            }
        });
        mLayoutManager = new FullyLinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        //分割线
        recyclerView.addItemDecoration(new RecycleViewDivider(mContext, LinearLayoutManager.HORIZONTAL,
                UIUtils.dip2px(mContext,10),
                getResources().getColor(R.color.background)));
        list = new ArrayList<>();
    }

    @Override
    protected void initData() {
        getOrderList();
    }

    private void setUI(OrderManageBean bean) {
        if(page == 1 && (bean.getCommodity() == null || bean.getCommodity().size() ==0)){
            llHas.setVisibility(View.VISIBLE);
        }else {
            llHas.setVisibility(View.GONE);
        }
        if(page == 1){
            list.clear();
        }
        list.addAll(bean.getCommodity());
        if(adapter == null){
            adapter = new BaseRecyclerAdapter<OrderBean>(mContext,list, R.layout.shop_item_order_commodity) {
                @Override
                public void convert(BaseRecyclerHolder holder, final OrderBean bean, final int position, boolean isScrolling) {

                    holder.setText(R.id.txt_shop_name,bean.getShop_name());
                    holder.setText(R.id.txt_status,bean.getStatus());
                    holder.setText(R.id.txt_goods_num,"共"+bean.getItem_num()+"件商品");
                    holder.setText(R.id.txt_pay_amount,"¥"+bean.getPay_amount());
                    holder.setText(R.id.order_no,bean.getOrder_no());

                    TextView complaintTv = holder.getView(R.id.complaint_status);
                    TextView refundTv = holder.getView(R.id.refund_status);
                    TextView commentTv = holder.getView(R.id.om_comment);
                    TextView remindTv = holder.getView(R.id.remind_status);
                    TextView shipTv = holder.getView(R.id.ship);
                    refundTv.setText(bean.getRefund_status());

                    if(1 == list.get(position).getIs_refund() && "退款成功".equals(bean.getRefund_status())){
//                        holder.setTextColor(R.id.refund_status,getResources().getColor(R.color.text_d9));
//                        holder.setBackgroundRes(R.id.refund_status,R.drawable.bg_solid_stroke_d9_radius20);
                        holder.setTextColor(R.id.remind_status,getResources().getColor(R.color.text_d9));
                        holder.setBackgroundRes(R.id.remind_status,R.drawable.bg_solid_stroke_d9_radius20);
                        holder.setTextColor(R.id.ship,getResources().getColor(R.color.text_d9));
                        holder.setBackgroundRes(R.id.ship,R.drawable.bg_solid_stroke_d9_radius20);
                    }else {
//                        holder.setTextColor(R.id.refund_status,getResources().getColor(R.color.text_33));
//                        holder.setBackgroundRes(R.id.refund_status,R.drawable.bg_solid_stroke_99_radius20);
                        holder.setTextColor(R.id.remind_status,getResources().getColor(R.color.text_33));
                        holder.setBackgroundRes(R.id.remind_status,R.drawable.bg_solid_stroke_99_radius20);
                        holder.setTextColor(R.id.ship,getResources().getColor(R.color.text_33));
                        holder.setBackgroundRes(R.id.ship,R.drawable.bg_solid_stroke_99_radius20);
                    }

                    if(bean.getComplaint_status() == 0){
                        complaintTv.setVisibility(View.GONE);
                    } else {
                        complaintTv.setVisibility(View.VISIBLE);
                        //投诉状态
                        if(bean.getComplaint_status() == 1){
                            complaintTv.setText("待处理");
                        } else if(bean.getComplaint_status() == 2){
                            complaintTv.setText("处理完成");
                        } else {
                            complaintTv.setVisibility(View.GONE);
                        }
                    }
                    complaintTv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //投诉
                            it = new Intent(mContext,OrderComplaintActivity.class);
                            it.putExtra("complaint_id",bean.getComplaint_id());
                            it.putExtra("complaint_status",bean.getComplaint_status());
                            startActivityForResult(it, AppConfig.ACTIVITY_REQUESTCODE);
                        }
                    });


                    if(bean.getIs_refund() == 0){
                        refundTv.setVisibility(View.GONE);
                    } else {
                        refundTv.setVisibility(View.VISIBLE);
                        if(1 == list.get(position).getIs_refund() && "退款成功".equals(bean.getRefund_status())){
                            refundTv.setText("退款成功");
                        }else {
                            refundTv.setText("退款");
                        }
                    }
                    refundTv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //退款   退款列表
                            it = new Intent(mContext,GoodsOrderRefundListActivity.class);
                            it.putExtra("oid",bean.getOid());
                            startActivityForResult(it, AppConfig.ACTIVITY_REQUESTCODE);
                        }
                    });

                    if(bean.getRemind_status() == 0){
                        remindTv.setVisibility(View.GONE);
                    } else {
                        remindTv.setVisibility(View.VISIBLE);
                        //提醒状态  ，1 未阅读(提醒发货)；2 已阅读(已处理提醒)；
                        if(bean.getRemind_status() == 1){
                            remindTv.setText("提醒发货");
                        } else if(bean.getRemind_status() == 2){
                            remindTv.setText("已阅读");
                        } else {
                            remindTv.setVisibility(View.GONE);
                        }
                    }
                    remindTv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //提醒
                            if(!(1 == list.get(position).getIs_refund() && "退款成功".equals(bean.getRefund_status()))){
                                it = new Intent(mContext,GoodsOrderRemindShipActivity.class);
                                it.putExtra("remind_id",bean.getRemind_id());
                                startActivityForResult(it, AppConfig.ACTIVITY_REQUESTCODE_1);
                                list.get(position).setRemind_status(2);
                                adapter.notifyItemChanged(position);
                            }
                        }
                    });

                    // 待发货状态时显示发货
                    if("待发货".equals(bean.getStatus())){
                        shipTv.setVisibility(View.VISIBLE);
                    } else {
                        shipTv.setVisibility(View.GONE);
                    }
                    shipTv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //发货
                            if(!(1 == list.get(position).getIs_refund() && "退款成功".equals(bean.getRefund_status()))){
                                it = new Intent(mContext,GoodsOrderShipActivity.class);
                                it.putExtra("oid",bean.getOid());
                                startActivityForResult(it, AppConfig.ACTIVITY_REQUESTCODE);
                            }
                        }
                    });

                    //是否评价
                    if(bean.getIs_evl()!=0){//不为0，说明有评价
                        commentTv.setVisibility(View.VISIBLE);
                    } else {
                        commentTv.setVisibility(View.GONE);
                    }
                    commentTv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //评价
                            it = new Intent(mContext,OrderCommentDetailActivity.class);
                            it.putExtra("evl_id",bean.getEvl_id());
                            startActivityForResult(it, AppConfig.ACTIVITY_REQUESTCODE);
                        }
                    });

                    //多个商品
                    RecyclerView rvGoods = holder.getView(R.id.recycler_view_goods);
                    rvGoods.setHasFixedSize(true);
                    rvGoods.setLayoutManager(new FullyLinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false));
                    rvGoods.addItemDecoration(new RecyclerViewDivider(mContext, LinearLayoutManager.VERTICAL,10
                            ,getResources().getColor(R.color.white)));
                    BaseRecyclerAdapter<OrderChildBean> childAdapter = new BaseRecyclerAdapter<OrderChildBean>(mContext,bean.getItems(),R.layout.layout_goods_order_good_item) {
                        @Override
                        public void convert(BaseRecyclerHolder cHolder, OrderChildBean child, final int pos, boolean isScrolling) {
                            cHolder.setSimpViewImageUri(R.id.iv_goods, Uri.parse(child.getItem_img()));
                            cHolder.setText(R.id.txt_goods_name,child.getItem_name());
                            cHolder.setText(R.id.txt_goods_spec,"规格："+child.getItem_specification());
                            cHolder.setText(R.id.txt_goods_price,"¥"+child.getPrice());
                            cHolder.setText(R.id.txt_goods_num,"×"+child.getNum());
//                            if(child.getIs_refund().equals("1")){
//                                cHolder.setVisible(R.id.txt_refund_status,false);
//                            }else {
//                            }
                            cHolder.setVisible(R.id.txt_refund_status,true);
                            cHolder.setText(R.id.txt_refund_status,child.getRefund_text());
                            cHolder.setOnClickListener(R.id.goods_order_lt, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //商品订单详情
                                    it = new Intent(mContext,ShopOrderGoodsDetailActivity.class);
                                    it.putExtra("oid",bean.getOid());
                                    startActivity(it);
                                }
                            });
                        }
                    };
                    rvGoods.setAdapter(childAdapter);
                }
            };
            recyclerView.setAdapter(adapter);
        }else {
            if(page > 1){
                adapter.notifyItemRangeInserted(list.size()-bean.getCommodity().size(),bean.getCommodity().size());
            }else {
                adapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * @return	true
     */
    private boolean isAbleClick(int pos){
        boolean flag = false;
        for(int i=0 ; i < list.get(pos).getItems().size() ; i++){
            if ("1".equals(list.get(pos).getItems().get(i).getIs_refund())) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    private void getOrderList() {
        mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_1,
                UrlUtils.s_commodity_order_list_get(LoginUtil.getInfo("token"), LoginUtil.getInfo("uid"),
                        shop_id,
                        date,
                        status,
                        complaint,
                        refund,
                        page),isShowProgress);
    }


    @Override
    public void onClick(View v) {
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case HttpModeBase.HTTP_REQUESTCODE_1:
                try {
                    isShowProgress = true;
                    refreshLayout.finishRefresh();
                    refreshLayout.finishLoadMore();
                    JSONObject object = new JSONObject((String) msg.obj);
                    if (object.optInt("status") == 1) {
                        if (!StringUtils.isEmpty(object.optString("result"))) {
                            OrderManageBean bean = FastJsonTools.getPerson(object.optString("result"), OrderManageBean.class);
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

    @Override
    public void onSelect(String shop_id, String date, String status, String complaint, String refund) {
        page = 1;
        this.shop_id = shop_id;
        this.date = date;
        this.status = status;
        this.complaint = complaint;
        this.refund = refund;

        /*  shop_id;//店铺id，全部店铺传0
            date;//日期过滤，如：2018-04-18
            status;//订单状态：待使用,待评价,已完成，状态传中文
            complaint;//投诉：1投诉建议,2已处理
            refund;//退款：1退款申请,2退款成功,4商家拒绝*/
        getOrderList();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case AppConfig.ACTIVITY_RESULTCODE:
                page = 1;
                getOrderList();
                break;
        }
    }
}
