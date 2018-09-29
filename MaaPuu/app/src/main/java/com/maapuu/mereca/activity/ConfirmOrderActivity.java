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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.maapuu.mereca.R;
import com.maapuu.mereca.adapter.help.BaseAdapterHelper;
import com.maapuu.mereca.adapter.help.QuickAdapter;
import com.maapuu.mereca.base.AppConfig;
import com.maapuu.mereca.base.BaseActivity;
import com.maapuu.mereca.bean.AddressBean;
import com.maapuu.mereca.bean.InitOrderBean;
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

public class ConfirmOrderActivity extends BaseActivity {
    @BindView(R.id.txt_left)
    TextView txtLeft;
    @BindView(R.id.txt_title)
    TextView txtTitle;
    @BindView(R.id.txt_no_address)
    TextView txtNoAddress;
    @BindView(R.id.ll_has_address)
    LinearLayout llHasAddress;
    @BindView(R.id.txt_name)
    TextView txtName;
    @BindView(R.id.txt_phone)
    TextView txtPhone;
    @BindView(R.id.txt_address)
    TextView txtAddress;
    @BindView(R.id.txt_youhui_amount)
    TextView txtYouhuiAmount;
    @BindView(R.id.txt_coupon_amount)
    TextView txtCouponAmount;
    @BindView(R.id.txt_goods_num)
    TextView txtGoodsNum;
    @BindView(R.id.txt_total_amount)
    TextView txtTotalAmount;
    @BindView(R.id.txt_pay_amount)
    TextView txtPayAmount;
    @BindView(R.id.list_view)
    MyListView listView;

    private List<InitOrderBean> list;
    private QuickAdapter<InitOrderBean> adapter;
    private String item_shop_data;

    private int is_cart;
    private String address_id;
    private String red_id = "0";
    private String order_no;
    private String oid;
    private String round_num;
    private String balance;
    private double order_amount;
    private double red_amount;
    private double pay_amount;

    private int pay_type = 1;
    private IWXAPI api;//微信支付参数
    public static ConfirmOrderActivity activity;
    private boolean isReresh = false;
    private int is_pay_pwd ;
    private NiceDialog payDialog;
    private NiceDialog yueDialog;
    private PayPsdInputView etPwd;

    @Override
    public void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_confirm_order);
        activity = this;
    }

    @Override
    public void initView() {
        txtLeft.setTypeface(StringUtils.getFont(mContext));
        txtTitle.setText("确认下单");
        list = new ArrayList<>();
        is_cart = getIntent().getIntExtra("is_cart",0);
        item_shop_data = getIntent().getStringExtra("item_shop_data");
    }

    @Override
    public void initData() {
        mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_1, UrlUtils.commodity_order_init_get(LoginUtil.getInfo("token"),LoginUtil.getInfo("uid"),
                item_shop_data),true);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case HttpModeBase.HTTP_REQUESTCODE_1:
                try {
                    JSONObject object = new JSONObject((String) msg.obj);
                    if (object.optInt("status") == 1) {
                        JSONObject resultObj = object.optJSONObject("result");
                        if(resultObj.has("default_address") && !StringUtils.isEmpty(resultObj.optString("default_address")) && !(resultObj.opt("default_address") instanceof Boolean)){
                            txtNoAddress.setVisibility(View.GONE);
                            llHasAddress.setVisibility(View.VISIBLE);
                            JSONObject addressObj = resultObj.optJSONObject("default_address");
                            address_id = addressObj.optString("address_id");
                            txtName.setText(addressObj.optString("receiver"));
                            txtPhone.setText(addressObj.optString("receiver_phone"));
                            txtAddress.setText("收货地址："+addressObj.optString("city_district_name")+addressObj.optString("address_detail"));
                        }else {
                            txtNoAddress.setVisibility(View.VISIBLE);
                            llHasAddress.setVisibility(View.GONE);
                        }
                        if(!StringUtils.isEmpty(resultObj.optString("lst_item")) && resultObj.optJSONArray("lst_item").length() > 0){
                            list = FastJsonTools.getPersons(resultObj.optString("lst_item"),InitOrderBean.class);
                            setAdapter();
                        }
                        txtGoodsNum.setText("共"+list.size()+"件商品");
                        if(resultObj.has("red_data") && !StringUtils.isEmpty(resultObj.optString("red_data")) && !(resultObj.opt("red_data") instanceof Boolean)){
                            JSONObject redObj = resultObj.optJSONObject("red_data");
                            red_id = redObj.optString("red_id");
                            txtCouponAmount.setText("-"+redObj.optString("red_amount")+"元");
                        }else {
                            txtCouponAmount.setText("");
                        }
                        round_num = resultObj.optString("round_num");
                        if(resultObj.has("order_calc_rlt") && !StringUtils.isEmpty(resultObj.optString("order_calc_rlt"))){
                            JSONObject amountObj = resultObj.optJSONObject("order_calc_rlt");
                            order_amount = amountObj.optDouble("order_amount");
                            red_amount = amountObj.optDouble("red_amount");
                            pay_amount = amountObj.optDouble("pay_amount");
                            txtTotalAmount.setText("¥"+StringUtils.formatDouble(pay_amount));
                            txtYouhuiAmount.setText(StringUtils.formatDouble(red_amount));
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
                        isReresh = true;
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
                            it = new Intent(mContext,OrderGoodsDetailActivity.class);
                            it.putExtra("oid",oid);
                            startActivity(it);
                            setResult(AppConfig.ACTIVITY_RESULTCODE);
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
                    it = new Intent(mContext,OrderGoodsDetailActivity.class);
                    it.putExtra("oid",oid);
                    startActivity(it);
                    setResult(AppConfig.ACTIVITY_RESULTCODE);
                    finish();
                }else if (TextUtils.equals(resultStatus, "8000")) {
                    // 判断resultStatus 为非“9000”则代表可能支付失败
                    // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                    ToastUtil.show(mContext,"支付结果确认中");
                    it = new Intent(mContext,OrderGoodsDetailActivity.class);
                    it.putExtra("oid",oid);
                    startActivity(it);
                    setResult(AppConfig.ACTIVITY_RESULTCODE);
                    finish();
                } else {
                    // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                    ToastUtil.show(mContext,"支付失败");
                    it = new Intent(mContext,OrderGoodsDetailActivity.class);
                    it.putExtra("oid",oid);
                    startActivity(it);
                    setResult(AppConfig.ACTIVITY_RESULTCODE);
                    finish();
                }
                break;
            case PayUitl.SDK_CHECK_FLAG:
                ToastUtil.show(mContext,"检查结果为：" + msg.obj);
                it = new Intent(mContext,OrderGoodsDetailActivity.class);
                it.putExtra("oid",oid);
                startActivity(it);
                setResult(AppConfig.ACTIVITY_RESULTCODE);
                finish();
                break;
            case 3: //微信支付成功
                ToastUtil.show(mContext,"支付成功");
                it = new Intent(mContext,OrderGoodsDetailActivity.class);
                it.putExtra("oid",oid);
                startActivity(it);
                setResult(AppConfig.ACTIVITY_RESULTCODE);
                finish();
                break;
            case 4: //微信支付失败
                ToastUtil.show(mContext,"支付取消");
                it = new Intent(mContext,OrderGoodsDetailActivity.class);
                it.putExtra("oid",oid);
                startActivity(it);
                setResult(AppConfig.ACTIVITY_RESULTCODE);
                finish();
                break;
            case 5: //微信支付失败
                ToastUtil.show(mContext,"支付失败，请重试");
                it = new Intent(mContext,OrderGoodsDetailActivity.class);
                it.putExtra("oid",oid);
                startActivity(it);
                setResult(AppConfig.ACTIVITY_RESULTCODE);
                finish();
                break;
        }
        return false;
    }

    /**
     * 下单的商品id
     * @return
     */
    private String getItemIds(){
        String jsonString ="";
        for (int i = 0 ; i < list.size() ; i++){
            jsonString +=  list.get(i).getItem_shop_id()+ ",";
        }
        if(jsonString.endsWith(",")){
            jsonString = jsonString.substring(0,jsonString.length()-1);
        }
        return jsonString;
    }

    private void setAdapter(){
        adapter = new QuickAdapter<InitOrderBean>(mContext,R.layout.layout_confirm_order_item,list) {
            @Override
            protected void convert(BaseAdapterHelper helper, InitOrderBean item) {
                helper.setSimpViewImageUri(R.id.iv_goods, Uri.parse(item.getItem_img()));
                helper.setText(R.id.txt_goods_name, item.getItem_name());
                helper.setText(R.id.txt_goods_spec, item.getItem_specification());
                helper.setText(R.id.txt_price, "¥"+item.getPrice());
                helper.setText(R.id.txt_goods_num, "×"+item.getNum());
            }
        };
        listView.setAdapter(adapter);
    }

    @Override
    @OnClick({R.id.txt_left,R.id.ll_has_address,R.id.txt_no_address,R.id.ll_coupon,R.id.txt_commit})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.txt_left:
                finish();
                break;
            case R.id.ll_has_address:
            case R.id.txt_no_address:
                it = new Intent(mContext,AddressManageActivity.class);
                it.putExtra("isChoose",true);
                startActivityForResult(it,AppConfig.ACTIVITY_REQUESTCODE);
                break;
            case R.id.ll_coupon:
                it = new Intent(mContext,HongBaoActivity.class);
                it.putExtra("red_type",2);
                it.putExtra("isOrder",true);
                it.putExtra("item_shop_ids",getItemIds());
                it.putExtra("order_amount",StringUtils.formatDouble(order_amount));
                startActivityForResult(it, AppConfig.ACTIVITY_REQUESTCODE);
                break;
            case R.id.txt_commit:
                if(StringUtils.isEmpty(address_id)){
                    ToastUtil.show(mContext,"请添加地址");
                    return;
                }
                mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_2, UrlUtils.commodity_order_set(LoginUtil.getInfo("token"),LoginUtil.getInfo("uid"),
                        item_shop_data,address_id,red_id,StringUtils.formatDouble(pay_amount),round_num,is_cart),true);
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
                                it = new Intent(mContext,OrderGoodsDetailActivity.class);
                                it.putExtra("oid",oid);
                                startActivity(it);
                                setResult(AppConfig.ACTIVITY_RESULTCODE);
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
                PayTask alipay = new PayTask(ConfirmOrderActivity.this);
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
            case AppConfig.ACTIVITY_RESULTCODE:
                RedBean redBean = (RedBean) data.getSerializableExtra("red");
                red_id = redBean.getRed_id();
                red_amount = Double.parseDouble(redBean.getRed_amount());
                txtCouponAmount.setText("-"+StringUtils.formatDouble(red_amount));
                if(red_amount > order_amount){
                    txtYouhuiAmount.setText(StringUtils.formatDouble(order_amount));
                }else {
                    txtYouhuiAmount.setText(StringUtils.formatDouble(red_amount));
                }
                pay_amount = order_amount - red_amount;
                if(pay_amount < 0){
                    pay_amount = 0;
                }
                txtTotalAmount.setText("¥"+StringUtils.formatDouble(pay_amount));
                txtPayAmount.setText(StringUtils.formatDouble(pay_amount));
                break;
            case AppConfig.ACTIVITY_RESULTCODE_4:
                red_id = "0";
                red_amount = 0;
                txtCouponAmount.setText("");
                pay_amount = order_amount - red_amount;
                txtTotalAmount.setText("¥"+StringUtils.formatDouble(pay_amount));
                txtPayAmount.setText(StringUtils.formatDouble(pay_amount));
                break;
            case AppConfig.ACTIVITY_RESULTCODE_2:
                AddressBean addressBean = (AddressBean) data.getSerializableExtra("addressBean");
                address_id = addressBean.getAddress_id();
                txtNoAddress.setVisibility(View.GONE);
                llHasAddress.setVisibility(View.VISIBLE);
                txtName.setText(addressBean.getReceiver());
                txtPhone.setText(addressBean.getReceiver_phone());
                txtAddress.setText("收货地址："+addressBean.getAdress());
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
