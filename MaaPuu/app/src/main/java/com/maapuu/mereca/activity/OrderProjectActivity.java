package com.maapuu.mereca.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.bigkoo.alertview.AlertView;
import com.maapuu.mereca.R;
import com.maapuu.mereca.base.AppConfig;
import com.maapuu.mereca.base.BaseActivity;
import com.maapuu.mereca.bean.OrderChildBean;
import com.maapuu.mereca.bean.ProjectOrderBean;
import com.maapuu.mereca.constant.UrlUtils;
import com.maapuu.mereca.recycleviewutil.BaseRecyclerAdapter;
import com.maapuu.mereca.recycleviewutil.BaseRecyclerHolder;
import com.maapuu.mereca.recycleviewutil.FullyLinearLayoutManager;
import com.maapuu.mereca.recycleviewutil.RecyclerViewDivider;
import com.maapuu.mereca.util.FastJsonTools;
import com.maapuu.mereca.util.HttpModeBase;
import com.maapuu.mereca.util.LoginUtil;
import com.maapuu.mereca.util.PayUitl;
import com.maapuu.mereca.util.StringUtils;
import com.maapuu.mereca.util.ToastUtil;
import com.maapuu.mereca.util.alipay.PayResult;
import com.maapuu.mereca.view.CustomerKeyboard;
import com.maapuu.mereca.view.PayPsdInputView;
import com.othershe.nicedialog.BaseNiceDialog;
import com.othershe.nicedialog.NiceDialog;
import com.othershe.nicedialog.ViewConvertListener;
import com.othershe.nicedialog.ViewHolder;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import info.wangchen.simplehud.SimpleHUD;

/**
 * Created by dell on 2018/2/24.
 */

public class OrderProjectActivity extends BaseActivity {
    @BindView(R.id.txt_left)
    TextView txtLeft;
    @BindView(R.id.txt_title)
    TextView txtTitle;
    @BindView(R.id.txt_right)
    TextView txtRight;
    @BindView(R.id.ll_has)
    LinearLayout llHas;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.txt_all)
    TextView txtAll;
    @BindView(R.id.txt_daifk)
    TextView txtDaifk;
    @BindView(R.id.txt_daisy)
    TextView txtDaisy;
    @BindView(R.id.txt_daipj)
    TextView txtDaipj;

    private AlertView alertView;
    private TextView[] tvs;
    private LinearLayoutManager mLayoutManager;
    private List<ProjectOrderBean> list;
    private BaseRecyclerAdapter<ProjectOrderBean> adapter;
    private BaseRecyclerAdapter<OrderChildBean> childAdapter;
    private String status = "全部";//订单状态：全部、待付款、待使用、待评价
    private int delPos = -1; //删除下标
    private int page = 1;
    private String balance;

    private int pay_type = 1;
    private IWXAPI api;//微信支付参数
    public static OrderProjectActivity activity;
    private int tabPosition = 0;
    private int is_pay_pwd ;
    private NiceDialog payDialog;
    private NiceDialog yueDialog;
    private PayPsdInputView etPwd;

    @Override
    public void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_project_order);
        activity = this;
    }

    @Override
    public void initView() {
        tabPosition = getIntent().getIntExtra("tabPosition",0);
        setStatus(tabPosition);
        txtLeft.setTypeface(StringUtils.getFont(mContext));
        txtTitle.setText("项目订单");
        txtRight.setVisibility(View.VISIBLE);txtRight.setText("售后");
        tvs = new TextView[]{txtAll,txtDaifk,txtDaisy,txtDaipj};
        setHead(tabPosition);
        list = new ArrayList<>();
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

    private void setHead(int postion) {
        for (int i = 0; i < tvs.length; i++){
            if(i == postion){
                tvs[postion].setSelected(true);
            }else {
                tvs[i].setSelected(false);
            }
        }
    }

    private void setStatus(int pos){
        //订单状态：全部、待付款、待使用、待评价
        switch (pos){
            case 0:
                status = "全部";
                break;
            case 1:
                status = "待付款";
                break;
            case 2:
                status = "待使用";
                break;
            case 3:
                status = "待评价";
                break;
        }
    }

    @Override
    public void initData() {
        initData(page);
    }

    private void initData(int pageNum) {
        mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_1, UrlUtils.project_order_list_new_get(LoginUtil.getInfo("token"),LoginUtil.getInfo("uid"),status,pageNum),true);
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
                            List<ProjectOrderBean> lsJson = FastJsonTools.getPersons(object.optString("result"),ProjectOrderBean.class);
                            setAdapter(lsJson);
                            page ++ ;
                        }else {
                            List<ProjectOrderBean> lsJson = new ArrayList<>();
                            setAdapter(lsJson);
                            if(page != 1){
                                ToastUtil.show(mContext,"暂无更多数据");
                            }else {
                                llHas.setVisibility(View.VISIBLE);
                            }
                        }
                        balance = object.optString("balance");
                        is_pay_pwd = object.optInt("is_pay_pwd");
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
                        if(pay_type == 1){
                            JSONObject payObj = new JSONObject(object.optString("result"));
                            doAlipay(payObj.optString("partner"),payObj.optString("seller"),payObj.optString("order_title"),payObj.optString("productName"),
                                    payObj.optString("tradeNO"),payObj.optString("notifyURL"),payObj.optString("amount"));
                        }else if(pay_type == 2){
                            JSONObject payObj = new JSONObject(object.optString("result"));
                            doWeiXinPay(payObj.optString("appid"),payObj.optString("partnerid"),payObj.optString("prepayid"),payObj.optString("noncestr"),
                                    payObj.optString("timestamp"),payObj.optString("package"),payObj.optString("sign"));
                        }else if(pay_type == 3){
                            if(yueDialog != null){
                                yueDialog.dismiss();
                            }
                            if(payDialog != null){
                                payDialog.dismiss();
                            }
                            ToastUtil.show(mContext,"支付成功");
                            page = 1;
                            initData(page);
                        }
                    } else {
                        if(pay_type == 3){
                            NiceDialog.init()
                                    .setLayoutId(R.layout.nd_layout_confirm)
                                    .setConvertListener(new ViewConvertListener() {
                                        @Override
                                        public void convertView(com.othershe.nicedialog.ViewHolder holder, final BaseNiceDialog dialog) {
                                            holder.setText(R.id.title,"提示");
                                            holder.setText(R.id.message,"支付密码不正确");
                                            holder.setText(R.id.cancel,"重新输入");
                                            holder.setText(R.id.ok,"忘记密码");

                                            holder.setOnClickListener(R.id.cancel, new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    dialog.dismiss();
                                                    etPwd.cleanPsd();
                                                }
                                            });

                                            holder.setOnClickListener(R.id.ok, new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    dialog.dismiss();
                                                    it = new Intent(mContext,SetPayPwdActivity.class);
                                                    startActivityForResult(it,AppConfig.ACTIVITY_RESULTCODE);
                                                }
                                            });
                                        }
                                    })
                                    .setWidth(270)
                                    .setOutCancel(false)
                                    .show(getSupportFragmentManager());
                        }else {
                            ToastUtil.show(mContext,object.optString("message"));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case HttpModeBase.HTTP_REQUESTCODE_3:
                try {
                    JSONObject object = new JSONObject((String) msg.obj);
                    if (object.optInt("status") == 1) {
                        ToastUtil.show(mContext,"删除成功");
                        if(delPos != -1){
                            list.remove(delPos);
                            if(list.size() == 0){
                                llHas.setVisibility(View.VISIBLE);
                            }
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
            case HttpModeBase.HTTP_ERROR:
                refreshLayout.finishRefresh();
                refreshLayout.finishLoadMore();
                String result_e = (String) msg.obj;
                ToastUtil.show(mContext,result_e);
                break;
            case PayUitl.SDK_PAY_FLAG:
                PayResult payResult = new PayResult((String) msg.obj);
                // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                String resultInfo = payResult.getResult();
                String resultStatus = payResult.getResultStatus();

                // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                if (TextUtils.equals(resultStatus, "9000")) {
                    ToastUtil.show(mContext,"支付成功");
                    page = 1;
                    initData(page);
                }else if (TextUtils.equals(resultStatus, "8000")) {
                    // 判断resultStatus 为非“9000”则代表可能支付失败
                    // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                    ToastUtil.show(mContext,"支付结果确认中");
                } else {
                    // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                    ToastUtil.show(mContext,"支付失败");
                }
                break;
            case PayUitl.SDK_CHECK_FLAG:
                ToastUtil.show(mContext,"检查结果为：" + msg.obj);
                break;
            case 3: //微信支付成功
                ToastUtil.show(mContext,"支付成功");
                page = 1;
                initData(page);
                break;
            case 4: //微信支付失败
                ToastUtil.show(mContext,"支付取消");
                break;
            case 5: //微信支付失败
                ToastUtil.show(mContext,"支付失败，请重试");
                break;
        }
        return false;
    }

    private void setAdapter(List<ProjectOrderBean> lsJson) {
        if(page == 1)
            list.clear();
        list.addAll(lsJson);
        if(adapter == null){
            adapter = new BaseRecyclerAdapter<ProjectOrderBean>(mContext,list,R.layout.layout_project_order_item) {
                @Override
                public void convert(final BaseRecyclerHolder holder, final ProjectOrderBean bean, final int position, boolean isScrolling) {
                    holder.setText(R.id.txt_shop,bean.getShop_name());
                    holder.setText(R.id.txt_status,bean.getStatus());
                    RecyclerView rvGoods = holder.getView(R.id.recycler_view_goods);
                    rvGoods.setHasFixedSize(true);
                    rvGoods.setLayoutManager(new FullyLinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false));
                    childAdapter = new BaseRecyclerAdapter<OrderChildBean>(mContext,bean.getItems(),R.layout.layout_project_order_item_item) {
                        @Override
                        public void convert(BaseRecyclerHolder cHolder, OrderChildBean child, final int pos, boolean isScrolling) {
                            cHolder.setSimpViewImageUri(R.id.image, Uri.parse(child.getItem_img()));
                            cHolder.setText(R.id.txt_goods_name,child.getItem_name());
                            cHolder.setText(R.id.txt_shop_name,bean.getShop_name());
                            cHolder.setText(R.id.txt_goods_price,"¥"+child.getPrice());
                            cHolder.setText(R.id.txt_yy_time,"下单时间："+bean.getCreate_time_text());
                            if(pos == bean.getItems().size()-1){
                                cHolder.setVisible(R.id.line,false);
                            }else {
                                cHolder.setVisible(R.id.line,true);
                            }
                        }
                    };
                    rvGoods.setAdapter(childAdapter);
                    childAdapter.setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(RecyclerView parent, View view, int pos) {
                            it = new Intent(mContext,OrderProjectDetailActivityV2.class);
                            it.putExtra("oid",list.get(position).getOid());
                            startActivityForResult(it, AppConfig.ACTIVITY_REQUESTCODE);
                        }
                    });
                    holder.setText(R.id.txt_price,"¥"+bean.getPay_amount());
                    switch (list.get(position).getStatus()){
                        case "待付款":
                            holder.setText(R.id.txt_label,"需付款");
                            holder.setVisible(R.id.txt_btn_1,true);
                            holder.setText(R.id.txt_btn_1,"删除订单");
                            holder.setVisible(R.id.txt_btn_2,true);
                            holder.setText(R.id.txt_btn_2,"立即支付");
                            break;
                        case "待使用":
                            holder.setText(R.id.txt_label,"已付款");
                            holder.setVisible(R.id.txt_btn_1,false);
                            holder.setVisible(R.id.txt_btn_2,true);
                            holder.setText(R.id.txt_btn_2,"消费码");
                            if(bean.getIs_code2d() == 1){
                                holder.setTextColor(R.id.txt_btn_2,getResources().getColor(R.color.text_price));
                                holder.setBackgroundRes(R.id.txt_btn_2,R.drawable.bg_solid_stroke_red_radius20);
                            }else {
                                holder.setTextColor(R.id.txt_btn_2,getResources().getColor(R.color.text_d9));
                                holder.setBackgroundRes(R.id.txt_btn_2,R.drawable.bg_solid_stroke_d9_radius20);
                            }
                            break;
                        case "待评价":
                            holder.setText(R.id.txt_label,"已付款");
                            holder.setVisible(R.id.txt_btn_1,false);
                            holder.setVisible(R.id.txt_btn_2,true);
                            holder.setText(R.id.txt_btn_2,"消费码");
                            break;
                        case "已取消":
                            holder.setText(R.id.txt_label,"已付款");
                            holder.setVisible(R.id.txt_btn_1,true);
                            holder.setText(R.id.txt_btn_1,"删除订单");
                            holder.setVisible(R.id.txt_btn_2,false);
                            break;
                        case "已完成":
                            holder.setText(R.id.txt_label,"已付款");
                            holder.setVisible(R.id.txt_btn_1,true);
                            holder.setText(R.id.txt_btn_1,"删除订单");
                            holder.setVisible(R.id.txt_btn_2,false);
                            break;
                    }
//                    holder.setText(R.id.txt_refund_status,bean.getRefund_text());
//                    if (list.get(position).getStatus().equals("待付款")){
//                        holder.setText(R.id.txt_label,"需付款");
//                        holder.setVisible(R.id.txt_btn_1,true);
//                        holder.setText(R.id.txt_btn_1,"删除订单");
//                        holder.setVisible(R.id.txt_btn_2,true);
//                        holder.setText(R.id.txt_btn_2,"立即支付");
//                    }else {
//                        holder.setText(R.id.txt_label,"已付款");
//                        holder.setVisible(R.id.txt_btn_1,false);
//                        holder.setVisible(R.id.txt_btn_2,true);
//                        holder.setText(R.id.txt_btn_2,"消费码");
//                    }
                    holder.setOnClickListener(R.id.txt_btn_1, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertView = new AlertView(null, "确定删除吗？", "取消", null, new String[]{"确定"}, mContext,
                                    AlertView.Style.Alert, new com.bigkoo.alertview.OnItemClickListener() {
                                @Override
                                public void onItemClick(Object o, int pos) {
                                    if (pos == 0) {
                                        delPos = position;
                                        mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_3,UrlUtils.order_delete_set(LoginUtil.getInfo("token"),LoginUtil.getInfo("uid"),
                                                list.get(position).getOid()),true);
                                    }
                                }
                            });
                            alertView.show();
                        }
                    });
                    holder.setOnClickListener(R.id.txt_btn_2, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (list.get(position).getStatus().equals("待付款")){
                                if(Double.parseDouble(list.get(position).getPay_amount()) == 0){
                                    if(is_pay_pwd == 0){
                                        it = new Intent(mContext,SetPayPwdActivity.class);
                                        it.putExtra("oid",list.get(position).getOid());
                                        it.putExtra("order_no",list.get(position).getOrder_no());
                                        startActivityForResult(it,AppConfig.ACTIVITY_RESULTCODE);
                                    }else {
                                        pay_type = 3;
                                        payConfirm(list.get(position).getOid(),list.get(position).getOrder_no());
                                    }
                                }else {
                                    popPay(list.get(position).getPay_amount(),list.get(position).getOid(),list.get(position).getOrder_no());
                                }
                            }else {
                                if(list.get(position).getIs_code2d() == 1){
                                    it = new Intent(mContext,ConsumeCodeDetailActivity.class);
                                    it.putExtra("oid",list.get(position).getOid());
                                    startActivity(it);
                                }
                            }
                        }
                    });
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
                it = new Intent(mContext,OrderProjectDetailActivityV2.class);
                it.putExtra("oid",list.get(position).getOid());
                startActivityForResult(it, AppConfig.ACTIVITY_REQUESTCODE);
            }
        });
    }

    private void popPay(final String pay_amount, final String oid, final String order_no) {
        payDialog = NiceDialog.init();
        payDialog.setLayoutId(R.layout.pop_pay)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    public void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        ImageView ivClose = holder.getView(R.id.iv_close);
                        TextView txtPayAmount = holder.getView(R.id.txt_pay_amount);
                        txtPayAmount.setText("¥ "+pay_amount);
                        final TextView txtAlipay = holder.getView(R.id.txt_alipay);
                        final TextView txtWxpay = holder.getView(R.id.txt_wxpay);
                        final TextView txtYue = holder.getView(R.id.txt_yue);
                        String yue = "余额支付(余额："+balance+")";
                        SpannableStringBuilder builder = new SpannableStringBuilder(yue);
                        ForegroundColorSpan span = new ForegroundColorSpan(Color.parseColor("#ed4272"));
                        builder.setSpan(span, 8, 8+String.valueOf(balance).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        txtYue.setText(builder);
                        TextView txtConfirmPay = holder.getView(R.id.txt_confirm_pay);
                        txtAlipay.setSelected(true);
                        pay_type = 1;
                        txtAlipay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pay_type = 1;
                                txtAlipay.setSelected(true);txtWxpay.setSelected(false);txtYue.setSelected(false);
                            }
                        });
                        txtWxpay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pay_type = 2;
                                txtAlipay.setSelected(false);txtWxpay.setSelected(true);txtYue.setSelected(false);
                            }
                        });
                        txtYue.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pay_type = 3;
                                txtAlipay.setSelected(false);txtWxpay.setSelected(false);txtYue.setSelected(true);
                            }
                        });
                        ivClose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        txtConfirmPay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(pay_type == 3 && Double.parseDouble(balance) < Double.parseDouble(pay_amount)){
                                    ToastUtil.show(mContext,"余额不足！");
                                    return;
                                }
                                if(pay_type == 3){
                                    if(is_pay_pwd == 0){
                                        it = new Intent(mContext,SetPayPwdActivity.class);
                                        it.putExtra("oid",oid);
                                        it.putExtra("order_no",order_no);
                                        startActivityForResult(it,AppConfig.ACTIVITY_RESULTCODE);
                                    }else {
                                        payConfirm(oid,order_no);
                                    }
                                }else {
                                    mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_2, UrlUtils.order_pay_data_get(LoginUtil.getInfo("token"),LoginUtil.getInfo("uid"),
                                            order_no,oid,pay_type),true);
                                }
                            }
                        });
                    }
                })
                .setOutCancel(false).setShowBottom(true).setHeight(300)
                .show(getSupportFragmentManager());
    }

    private void payConfirm(final String oid, final String order_no){
        yueDialog = NiceDialog.init();
        yueDialog.setLayoutId(R.layout.pop_yue_pay)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    public void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        TextView txtClose = holder.getView(R.id.txt_close);
                        txtClose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        txtClose.setTypeface(StringUtils.getFont(mContext));
                        etPwd = holder.getView(R.id.et_pwd);
                        CustomerKeyboard keyboard = holder.getView(R.id.keyboard);
                        keyboard.setOnCustomerKeyboardClickListener(new CustomerKeyboard.CustomerKeyboardClickListener() {
                            @Override
                            public void click(String number) {
                                etPwd.addPassword(number);
                            }

                            @Override
                            public void delete() {
                                etPwd.deleteLastPassword();
                            }
                        });
                        etPwd.setComparePassword(new PayPsdInputView.onPasswordListener() {
                            @Override
                            public void onDifference(String oldPsd, String newPsd) {}

                            @Override
                            public void onEqual(String psd) {}

                            @Override
                            public void inputFinished(String inputPsd) {
                                mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_2, UrlUtils.order_yue_pay(LoginUtil.getInfo("token"),LoginUtil.getInfo("uid"),
                                        oid, order_no,etPwd.getPasswordString()),true);
                            }

                            @Override
                            public void inputUnFinished(String inputPsd) {}
                        });
                    }
                }).setOutCancel(false).setShowBottom(true)
                .show(getSupportFragmentManager());
    }

    @Override
    @OnClick({R.id.txt_left,R.id.txt_right,R.id.txt_all,R.id.txt_daifk,R.id.txt_daisy,R.id.txt_daipj})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.txt_left:
                finish();
                break;
            case R.id.txt_right:
                it  = new Intent(mContext,AfterSalesActivity.class);
                it.putExtra("type",1);
                startActivity(it);
                break;
            case R.id.txt_all:
                setHead(0);status = "全部";
                page = 1;initData(page);
                break;
            case R.id.txt_daifk:
                setHead(1);status = "待付款";
                page = 1;initData(page);
                break;
            case R.id.txt_daisy:
                setHead(2);status = "待使用";
                page = 1;initData(page);
                break;
            case R.id.txt_daipj:
                setHead(3);status = "待评价";
                page = 1;initData(page);
                break;
        }
    }

    /**
     * call alipay sdk pay. 调用SDK支付
     *
     */
    public void doAlipay(String partner,String seller_id,String subject,String body,String out_trade_no,String notify_url,String amount) {
        // 订单
        String orderInfo = PayUitl.getOrderInfo(partner,seller_id,subject,body,out_trade_no, notify_url, amount);
        // 对订单做RSA 签名
        String sign = PayUitl.sign(orderInfo);
        try {
            // 仅需对sign 做URL编码
            sign = URLEncoder.encode(sign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // 完整的符合支付宝参数规范的订单信息
        final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + PayUitl.getSignType();

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                SimpleHUD.dismiss();
                PayTask alipay = new PayTask(OrderProjectActivity.this);
                // 调用支付接口，获取支付结果
                String result = alipay.pay(payInfo,true);

                Message msg = new Message();
                msg.what = PayUitl.SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    public void doWeiXinPay(String appId, String partnerId, String prepayId, String nonceStr,
                            String timeStamp,String packageValue, String sign) {
        SimpleHUD.dismiss();
        api = WXAPIFactory.createWXAPI(mContext,null);
        api.registerApp(appId);
        PayReq req = new PayReq();
        req.appId			= appId;
        req.partnerId		= partnerId;
        req.prepayId		= prepayId;
        req.nonceStr		= nonceStr;
        req.timeStamp		= timeStamp;
        req.packageValue	= packageValue;
        req.sign			= sign;

        Toast.makeText(this, "调起支付...", Toast.LENGTH_SHORT).show();
        api.sendReq(req);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case AppConfig.ACTIVITY_RESULTCODE_PWD:
                is_pay_pwd = 1;
                payConfirm(data.getStringExtra("oid"),data.getStringExtra("order_no"));
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activity = null;
    }
}
