package com.maapuu.mereca.background.shop.fragment;

import android.content.Intent;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.luck.picture.lib.decoration.RecycleViewDivider;
import com.maapuu.mereca.R;
import com.maapuu.mereca.background.shop.activity.OrderComplaintActivity;
import com.maapuu.mereca.background.shop.activity.OrderManageActivity;
import com.maapuu.mereca.background.shop.activity.OrderRefundActivity;
import com.maapuu.mereca.background.shop.activity.ProjectOrderCommentListActivity;
import com.maapuu.mereca.background.shop.activity.ShopOrderProjectDetailActivity;
import com.maapuu.mereca.background.shop.bean.OrderBean;
import com.maapuu.mereca.background.shop.bean.OrderManageBean;
import com.maapuu.mereca.base.AppConfig;
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
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;

/**
 * 项目订单
 * Created by Jia on 2018/4/20.
 */

public class OrderProjectFragment extends BaseFragment implements OrderManageActivity.OnSelectListener {

    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.ll_has)
    LinearLayout llHas;

    private FullyLinearLayoutManager mLayoutManager;
    private BaseRecyclerAdapter<OrderBean> adapter;
    private int page = 1;
    boolean isShowProgress = true;

    String shop_id = "0"; //店铺id，全部店铺传0
    String date = ""; //日期过滤，如：2018-04-18
    String status = ""; //订单状态：待使用,待评价,已完成，状态传中文
    String complaint = ""; //投诉：1投诉建议,2已处理
    String refund = ""; //退款：1退款申请,2退款成功,4商家拒绝

    @Override
    protected int setContentViewById() {
        return R.layout.shop_fragment_order_project;
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

        adapter = new BaseRecyclerAdapter<OrderBean>(mContext, R.layout.shop_item_order_manage) {
            @Override
            public void convert(BaseRecyclerHolder holder, final OrderBean bean, final int position, boolean isScrolling) {
                ImageView img = holder.getView(R.id.item_img);
                UIUtils.loadImg(mContext,bean.getItem_img(),img);
                holder.setText(R.id.item_name,bean.getItem_name());
                holder.setText(R.id.pay_amount,"¥"+bean.getPay_amount());
                holder.setText(R.id.shop_name,bean.getShop_name());
                holder.setText(R.id.status,bean.getStatus());
                holder.setText(R.id.create_time_text,bean.getCreate_time_text());
                holder.setText(R.id.order_no,bean.getOrder_no());
                TextView complaintTv = holder.getView(R.id.complaint_status);
                TextView refundTv = holder.getView(R.id.refund_status);
                TextView commentTv = holder.getView(R.id.om_comment);

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

//                        it = new Intent(mContext,ShopProjectOrderDetailActivity.class);
//                        it.putExtra("type",position%3);
//                        startActivity(it);
                    }
                });

                if("0".equals(bean.getRefund_status())){
                    refundTv.setVisibility(View.GONE);
                } else {
                    refundTv.setVisibility(View.VISIBLE);
                    //退款状态
                    if("1".equals(bean.getRefund_status())){
                        refundTv.setText("退款中");
                    } else if("2".equals(bean.getRefund_status())){
                        refundTv.setText("退款成功");
                    } else if("3".equals(bean.getRefund_status())){
                        refundTv.setText("用户取消退款");
                    } else if("4".equals(bean.getRefund_status())){
                        refundTv.setText("商家拒绝");
                    } else {
                        refundTv.setVisibility(View.GONE);
                    }
                }
                refundTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //退款
                        it = new Intent(mContext,OrderRefundActivity.class);
                        it.putExtra("refund_id",bean.getRefund_id());
                        it.putExtra("refund_status",bean.getRefund_status());
                        startActivityForResult(it, AppConfig.ACTIVITY_REQUESTCODE);
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
                        it = new Intent(mContext,ProjectOrderCommentListActivity.class);
                        it.putExtra("oid",bean.getOid());
                        startActivityForResult(it, AppConfig.ACTIVITY_REQUESTCODE);
                    }
                });

                holder.setOnClickListener(R.id.om_order_ll, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        it = new Intent(mContext,ShopOrderProjectDetailActivity.class);
                        it.putExtra("oid",bean.getOid());
                        startActivity(it);
                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void initData() {
        getOrderList();
    }

    private void setUI(OrderManageBean bean) {
        if(page == 1 && (bean.getOrder_list() == null || bean.getOrder_list().size() ==0)){
            llHas.setVisibility(View.VISIBLE);
        }else {
            llHas.setVisibility(View.GONE);
        }
        List<OrderBean> list = bean.getOrder_list();
        if(page == 1) adapter.clear();
        adapter.addList(list);
        if(page > 1){
            if((list ==null || list.size()==0)){
                page--;
                ToastUtil.show(mContext,"暂无更多数据");
            }
        }
    }

    private void getOrderList() {
        mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_1,
                UrlUtils.s_project_list_get(LoginUtil.getInfo("token"), LoginUtil.getInfo("uid"),
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
