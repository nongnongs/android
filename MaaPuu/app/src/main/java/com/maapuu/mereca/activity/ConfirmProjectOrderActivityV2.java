package com.maapuu.mereca.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.facebook.drawee.view.SimpleDraweeView;
import com.maapuu.mereca.R;
import com.maapuu.mereca.adapter.help.BaseAdapterHelper;
import com.maapuu.mereca.adapter.help.QuickAdapter;
import com.maapuu.mereca.base.AppConfig;
import com.maapuu.mereca.base.BaseActivity;
import com.maapuu.mereca.bean.InitProjectOrderBean;
import com.maapuu.mereca.bean.MyCardBean;
import com.maapuu.mereca.bean.RedBean;
import com.maapuu.mereca.constant.UrlUtils;
import com.maapuu.mereca.util.FastJsonTools;
import com.maapuu.mereca.util.HttpModeBase;
import com.maapuu.mereca.util.LoginUtil;
import com.maapuu.mereca.util.PayUitl;
import com.maapuu.mereca.util.StringUtils;
import com.maapuu.mereca.util.ToastUtil;
import com.maapuu.mereca.util.alipay.PayResult;
import com.maapuu.mereca.view.CustomerKeyboard;
import com.maapuu.mereca.view.MyListView;
import com.maapuu.mereca.view.PayPsdInputView;
import com.othershe.nicedialog.BaseNiceDialog;
import com.othershe.nicedialog.NiceDialog;
import com.othershe.nicedialog.ViewConvertListener;
import com.othershe.nicedialog.ViewHolder;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.json.JSONArray;
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
 * Created by dell on 2018/3/5.
 */

public class ConfirmProjectOrderActivityV2 extends BaseActivity {
    @BindView(R.id.txt_left)
    TextView txtLeft;
    @BindView(R.id.txt_title)
    TextView txtTitle;
    @BindView(R.id.iv_shop)
    SimpleDraweeView ivShop;
    @BindView(R.id.txt_shop_name)
    TextView txtShopName;
    @BindView(R.id.txt_shop_address)
    TextView txtShopAddress;
    @BindView(R.id.txt_distance)
    TextView txtDistance;
    @BindView(R.id.txt_coupon_amount)
    TextView txtCouponAmount;
    @BindView(R.id.txt_membercard_amount)
    TextView txtMembercardAmount;
    @BindView(R.id.txt_goods_num)
    TextView txtGoodsNum;
    @BindView(R.id.txt_youhui_amount)
    TextView txtYouhuiAmount;
    @BindView(R.id.txt_member_youhui_amount)
    TextView txtMemberYouhuiAmount;
    @BindView(R.id.txt_total_amount)
    TextView txtTotalAmount;
    @BindView(R.id.txt_pay_amount)
    TextView txtPayAmount;
    @BindView(R.id.list_view)
    MyListView listView;

    private List<InitProjectOrderBean> list;
    private QuickAdapter<InitProjectOrderBean> adapter;

    private int is_box_buy;
    private int is_cart;
    private String item_shop_data;
    private String red_id = "0";
    private String member_id = "0";
    private String order_no;
    private String oid;
    private String round_num;
    private String balance;
    private double order_amount;
    private double red_amount;
    private double pay_amount;
    private double member_discount;
    private double member_amount;
    private double fullcut_amount;

    private RedBean redBean;
    private MyCardBean cardBean;

    private int pay_type = 1;
    private IWXAPI api;//微信支付参数
    public static ConfirmProjectOrderActivityV2 activity;
    private int is_pay_pwd ;
    private NiceDialog payDialog;
    private NiceDialog yueDialog;
    private PayPsdInputView etPwd;

    @Override
    public void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_confirm_project_order);
        activity = this;
    }

    @Override
    public void initView() {
        txtLeft.setTypeface(StringUtils.getFont(mContext));
        txtTitle.setText("确认下单");
        is_box_buy = getIntent().getIntExtra("is_box_buy",0);
        is_cart = getIntent().getIntExtra("is_cart",0);
        item_shop_data = getIntent().getStringExtra("item_shop_data");
        list = new ArrayList<>();
    }

    @Override
    public void initData() {
        mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_1, UrlUtils.project_order_init_new_get(LoginUtil.getInfo("token"),LoginUtil.getInfo("uid"),
                AppConfig.LAT,AppConfig.LNG,is_box_buy,item_shop_data),true);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case HttpModeBase.HTTP_REQUESTCODE_1:
                try {
                    JSONObject object = new JSONObject((String) msg.obj);
                    if (object.optInt("status") == 1) {
                        JSONObject resultObj = object.optJSONObject("result");
                        if(resultObj.has("shop_data") && !StringUtils.isEmpty(resultObj.optString("shop_data")) && !(resultObj.opt("shop_data") instanceof Boolean)){
                            JSONObject shopObj = resultObj.optJSONObject("shop_data");
                            ivShop.setImageURI(Uri.parse(shopObj.optString("shop_logo")));
                            txtShopName.setText(shopObj.optString("shop_name"));
                            txtShopAddress.setText(shopObj.optString("address_detail"));
                            txtDistance.setText(shopObj.optString("distance"));
                        }
                        if(resultObj.has("lst_item") && !StringUtils.isEmpty(resultObj.optString("lst_item"))){
                            list = FastJsonTools.getPersons(resultObj.optString("lst_item"),InitProjectOrderBean.class);
                            txtGoodsNum.setText("共"+list.size()+"个项目");
                            setAdapter();
                        }
                        if(resultObj.has("red_data") && !StringUtils.isEmpty(resultObj.optString("red_data")) && !(resultObj.opt("red_data") instanceof Boolean)){
                            JSONObject redObj = resultObj.optJSONObject("red_data");
                            red_id = redObj.optString("red_id");
                            if(red_id.equals("0")){
                                txtCouponAmount.setText("");
                            }else {
                                txtCouponAmount.setText("-"+redObj.optString("red_amount")+"元");
                            }
                        }
                        if(resultObj.has("member_data") && !StringUtils.isEmpty(resultObj.optString("member_data")) && !(resultObj.opt("member_data") instanceof Boolean)){
                            JSONObject memberObj = resultObj.optJSONObject("member_data");
                            cardBean = FastJsonTools.getPerson(resultObj.optString("member_data"),MyCardBean.class);
                            member_id = memberObj.optString("member_id");
                            txtMembercardAmount.setText(memberObj.optString("card_name"));
                        }else {
                            txtMembercardAmount.setText("");
                        }
                        round_num = resultObj.optString("round_num");
                        if(resultObj.has("order_calc_rlt") && !StringUtils.isEmpty(resultObj.optString("order_calc_rlt"))){
                            JSONObject amountObj = resultObj.optJSONObject("order_calc_rlt");
                            order_amount = amountObj.optDouble("order_amount");
                            red_amount = amountObj.optDouble("red_amount");
                            pay_amount = amountObj.optDouble("pay_amount");
                            member_discount = amountObj.optDouble("member_discount");
                            member_amount = amountObj.optDouble("member_amount");
                            fullcut_amount = amountObj.optDouble("fullcut_amount");

                            txtTotalAmount.setText("¥"+StringUtils.formatDouble(pay_amount));
                            if(red_amount+member_discount+fullcut_amount > order_amount){
                                txtYouhuiAmount.setText(StringUtils.formatDouble(order_amount));
                            }else {
                                txtYouhuiAmount.setText(StringUtils.formatDouble(member_discount+red_amount+fullcut_amount));
                            }
                            if(member_id.equals("0")){
                                txtMemberYouhuiAmount.setText("0");
                            }else {
                                txtMemberYouhuiAmount.setText(StringUtils.formatDouble(member_amount));
                            }
                            txtPayAmount.setText("¥"+StringUtils.formatDouble(pay_amount));
                        }
                        balance = resultObj.optString("balance");
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
                        order_no = object.optJSONObject("result").optString("order_no");
                        oid = object.optJSONObject("result").optString("oid");
                        is_pay_pwd = object.optJSONObject("result").optInt("is_pay_pwd");
                        if(pay_amount == 0){
                            if(is_pay_pwd == 0){
                                it = new Intent(mContext,SetPayPwdActivity.class);
                                startActivityForResult(it,AppConfig.ACTIVITY_RESULTCODE);
                            }else {
                                pay_type = 3;
                                payConfirm();
                            }
                        }else {
                            popPay();
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
                            it = new Intent(mContext,OrderProjectActivity.class);
//                            it.putExtra("oid",oid);
                            startActivity(it);
                            finish();
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
            case HttpModeBase.HTTP_REQUESTCODE_4:
                try {
                    JSONObject object = new JSONObject((String) msg.obj);
                    if (object.optInt("status") == 1) {
                        if(object.has("result") && !StringUtils.isEmpty(object.optString("result"))){
                            JSONObject resultObj = object.optJSONObject("result");
                            order_amount = resultObj.optDouble("order_amount");
                            red_amount = resultObj.optDouble("red_amount");
                            pay_amount = resultObj.optDouble("pay_amount");
                            member_discount = resultObj.optDouble("member_discount");
                            member_amount = resultObj.optDouble("member_amount");
                            fullcut_amount = resultObj.optDouble("fullcut_amount");

                            if(red_amount+member_discount+fullcut_amount > order_amount){
                                txtYouhuiAmount.setText(StringUtils.formatDouble(order_amount));
                            }else {
                                txtYouhuiAmount.setText(StringUtils.formatDouble(member_discount+red_amount+fullcut_amount));
                            }
                            if(member_id.equals("0")){
                                txtMemberYouhuiAmount.setText("0");
                            }else {
                                txtMemberYouhuiAmount.setText(StringUtils.formatDouble(member_amount));
                            }
                            txtTotalAmount.setText("¥"+StringUtils.formatDouble(pay_amount));
                            txtPayAmount.setText("¥"+StringUtils.formatDouble(pay_amount));
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
            case PayUitl.SDK_PAY_FLAG:
                PayResult payResult = new PayResult((String) msg.obj);
                // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                String resultInfo = payResult.getResult();
                String resultStatus = payResult.getResultStatus();

                // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                if (TextUtils.equals(resultStatus, "9000")) {
                    ToastUtil.show(mContext,"支付成功");
                    it = new Intent(mContext,OrderProjectActivity.class);
                    startActivity(it);
                    finish();
                }else if (TextUtils.equals(resultStatus, "8000")) {
                    // 判断resultStatus 为非“9000”则代表可能支付失败
                    // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                    ToastUtil.show(mContext,"支付结果确认中");
                    it = new Intent(mContext,OrderProjectDetailActivityV2.class);
                    it.putExtra("oid",oid);
                    startActivity(it);
                    finish();
                } else {
                    // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                    ToastUtil.show(mContext,"支付失败");
                    it = new Intent(mContext,OrderProjectDetailActivityV2.class);
                    it.putExtra("oid",oid);
                    startActivity(it);
                    finish();
                }
                break;
            case PayUitl.SDK_CHECK_FLAG:
                ToastUtil.show(mContext,"检查结果为：" + msg.obj);
                it = new Intent(mContext,OrderProjectDetailActivityV2.class);
                it.putExtra("oid",oid);
                startActivity(it);
                finish();
                break;
            case 3: //微信支付成功
                ToastUtil.show(mContext,"支付成功");
                it = new Intent(mContext,OrderProjectActivity.class);
                startActivity(it);
                finish();
                break;
            case 4: //微信支付失败
                ToastUtil.show(mContext,"支付取消");
                it = new Intent(mContext,OrderProjectDetailActivityV2.class);
                it.putExtra("oid",oid);
                startActivity(it);
                finish();
                break;
            case 5: //微信支付失败
                ToastUtil.show(mContext,"支付失败，请重试");
                it = new Intent(mContext,OrderProjectDetailActivityV2.class);
                it.putExtra("oid",oid);
                startActivity(it);
                finish();
                break;
        }
        return false;
    }

    private String getIds(){
        String ids = "";
        for (int i = 0; i < list.size(); i++){
            ids += list.get(i).getItem_shop_id()+",";
        }
        if(ids.endsWith(",")){
            ids = ids.substring(0,ids.length()-1);
        }
        return ids;
    }

    private void setAdapter(){
        adapter = new QuickAdapter<InitProjectOrderBean>(mContext,R.layout.layout_confirm_order_item,list) {
            @Override
            protected void convert(BaseAdapterHelper helper, InitProjectOrderBean item) {
                helper.setSimpViewImageUri(R.id.iv_goods,Uri.parse(item.getItem_img()));
                helper.setText(R.id.txt_goods_name,item.getItem_name());
                helper.setText(R.id.txt_goods_spec,item.getItem_desc());
                helper.setText(R.id.txt_price,"¥"+item.getPrice());
                helper.setText(R.id.txt_goods_num,"×"+item.getNum());
            }
        };
        listView.setAdapter(adapter);
    }

    @Override
    @OnClick({R.id.txt_left,R.id.ll_coupon,R.id.ll_membership_card,R.id.txt_commit})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.txt_left:
                finish();
                break;
            case R.id.ll_coupon:
                it = new Intent(mContext,HongBaoActivity.class);
                it.putExtra("isOrder",true);
                it.putExtra("red_type",1);
                it.putExtra("item_shop_ids",getIds());
                it.putExtra("order_amount",StringUtils.formatDouble(order_amount));
                startActivityForResult(it, AppConfig.ACTIVITY_REQUESTCODE);
                break;
            case R.id.ll_membership_card:
                it = new Intent(mContext,MyMembershipCardActivity.class);
                it.putExtra("item_shop_ids",getIds());
                it.putExtra("member_id",member_id);
                startActivityForResult(it, AppConfig.ACTIVITY_REQUESTCODE);
                break;
            case R.id.txt_commit:
                mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_2, UrlUtils.project_order_new_set(LoginUtil.getInfo("token"),LoginUtil.getInfo("uid"),
                        is_box_buy,is_cart,item_shop_data,red_id,member_id,StringUtils.formatDouble(pay_amount),round_num),true);
                break;
        }
    }

    private void popPay(){
        payDialog = NiceDialog.init();
        payDialog.setLayoutId(R.layout.pop_pay)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    public void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        ImageView ivClose = holder.getView(R.id.iv_close);
                        TextView txtPayAmount = holder.getView(R.id.txt_pay_amount);
                        txtPayAmount.setText("¥ "+StringUtils.formatDouble(pay_amount));
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
                                it = new Intent(mContext,OrderProjectDetailActivityV2.class);
                                it.putExtra("oid",oid);
                                startActivity(it);
                                finish();
                            }
                        });
                        txtConfirmPay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(pay_type == 3 && Double.parseDouble(balance) < pay_amount){
                                    ToastUtil.show(mContext,"余额不足！");
                                    return;
                                }
                                if(pay_type == 3){
                                    if(is_pay_pwd == 0){
                                        it = new Intent(mContext,SetPayPwdActivity.class);
                                        startActivityForResult(it,AppConfig.ACTIVITY_RESULTCODE);
                                    }else {
                                        payConfirm();
                                    }
                                }else {
                                    mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_3, UrlUtils.order_pay_data_get(LoginUtil.getInfo("token"),LoginUtil.getInfo("uid"),
                                            order_no,oid,pay_type),true);
                                }
                            }
                        });
                    }
                })
                .setOutCancel(false).setShowBottom(true).setHeight(300)
                .show(getSupportFragmentManager());
    }

    private void payConfirm(){
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
                                if(!StringUtils.isEmpty(oid) && pay_amount == 0){
                                    it = new Intent(mContext,OrderProjectDetailActivityV2.class);
                                    it.putExtra("oid",oid);
                                    startActivity(it);
                                    finish();
                                }
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
                                mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_3, UrlUtils.order_yue_pay(LoginUtil.getInfo("token"),LoginUtil.getInfo("uid"),
                                        oid,order_no,etPwd.getPasswordString()),true);
                            }

                            @Override
                            public void inputUnFinished(String inputPsd) {}
                        });
                    }
                }).setOutCancel(false).setShowBottom(true)
                .show(getSupportFragmentManager());
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
                PayTask alipay = new PayTask(ConfirmProjectOrderActivityV2.this);
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

    private void calculate(){
        mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_4,UrlUtils.project_order_calc_new_get(LoginUtil.getInfo("token"),LoginUtil.getInfo("uid"),is_box_buy,
                StringUtils.formatDouble(order_amount),StringUtils.formatDouble(red_amount),cardBean == null?"0":(cardBean.getMember_id()+""),item_shop_data),true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case AppConfig.ACTIVITY_RESULTCODE:
                redBean = (RedBean) data.getSerializableExtra("red");
                red_id = redBean.getRed_id();
                red_amount = Double.parseDouble(redBean.getRed_amount());
                txtCouponAmount.setText("-"+redBean.getRed_amount()+"元");
                calculate();
                break;
            case AppConfig.ACTIVITY_RESULTCODE_1:
                cardBean = (MyCardBean) data.getSerializableExtra("membercard");
                member_id = cardBean.getMember_id();
                txtMembercardAmount.setText(cardBean.getCard_name());
                calculate();
                break;
            case AppConfig.ACTIVITY_RESULTCODE_3://不使用会员卡
                txtMembercardAmount.setText("");
                cardBean = null;
                member_id = "0";
                calculate();
                break;
            case AppConfig.ACTIVITY_RESULTCODE_4://不使用优惠券
                red_id = "0";
                txtCouponAmount.setText("");
                red_amount = 0;
                calculate();
                break;
            case AppConfig.ACTIVITY_RESULTCODE_PWD:
                is_pay_pwd = 1;
                payConfirm();
                break;
            case AppConfig.ACTIVITY_RESULTCODE_NO_PWD:
                it = new Intent(mContext,OrderProjectDetailActivityV2.class);
                it.putExtra("oid",oid);
                startActivity(it);
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activity = null;
    }
}
