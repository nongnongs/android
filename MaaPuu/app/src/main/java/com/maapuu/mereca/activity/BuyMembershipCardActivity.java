package com.maapuu.mereca.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.bigkoo.pickerview.TimePickerView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.maapuu.mereca.R;
import com.maapuu.mereca.base.AppConfig;
import com.maapuu.mereca.base.BaseActivity;
import com.maapuu.mereca.constant.UrlUtils;
import com.maapuu.mereca.util.HttpModeBase;
import com.maapuu.mereca.util.LoginUtil;
import com.maapuu.mereca.util.PayUitl;
import com.maapuu.mereca.util.PictureSelectUtil;
import com.maapuu.mereca.util.StringUtils;
import com.maapuu.mereca.util.ToastUtil;
import com.maapuu.mereca.util.UIUtils;
import com.maapuu.mereca.util.alipay.PayResult;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.compress.Luban;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.maapuu.mereca.view.CircleImgView;
import com.maapuu.mereca.view.CustomerKeyboard;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import info.wangchen.simplehud.SimpleHUD;

/**
 * Created by dell on 2018/3/5.
 */

public class BuyMembershipCardActivity extends BaseActivity {
    @BindView(R.id.txt_left)
    TextView txtLeft;
    @BindView(R.id.txt_title)
    TextView txtTitle;
    @BindView(R.id.iv_icon)
    CircleImgView ivIcon;
    @BindView(R.id.iv_shop)
    SimpleDraweeView ivShop;
    @BindView(R.id.txt_shop_name)
    TextView txtShopName;
    @BindView(R.id.txt_card_type)
    TextView txtCardType;
    @BindView(R.id.txt_card_no)
    TextView txtCardNo;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.txt_birth)
    TextView txtBirth;
    @BindView(R.id.txt_price)
    TextView txtPrice;
    @BindView(R.id.ll_icon)
    LinearLayout llIcon;

    private TimePickerView pvCustomTime;
    private String shop_id;
    private String card_id;
    private String card_name;
    private String shop_name;
    private String shop_logo;
    private int card_type;
    private String price;

    private String order_no;
    private String oid;
    private String balance;
    private List<LocalMedia> selectList = new ArrayList<>();
    private Bitmap bm;
    private String member_avatar = "";

    private int pay_type = 1;
    private IWXAPI api;//微信支付参数
    public static BuyMembershipCardActivity activity;
    private int is_pay_pwd ;
    private NiceDialog payDialog;
    private NiceDialog yueDialog;
    private PayPsdInputView etPwd;

    @Override
    public void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_buy_membership_card);
        activity = this;
    }

    @Override
    public void initView() {
        txtLeft.setTypeface(StringUtils.getFont(mContext));
        txtTitle.setText("办理会员卡");
        shop_id = getIntent().getStringExtra("shop_id");
        card_id = getIntent().getStringExtra("card_id");
        card_name = getIntent().getStringExtra("card_name");
        shop_name = getIntent().getStringExtra("shop_name");
        shop_logo = getIntent().getStringExtra("shop_logo");
        card_type = getIntent().getIntExtra("card_type",1);
        price = getIntent().getStringExtra("price");

        switch (card_type){
            case 1:
                txtCardType.setText("会籍");
                llIcon.setVisibility(View.VISIBLE);
                break;
            case 2:
                txtCardType.setText("项目卡");
                llIcon.setVisibility(View.VISIBLE);
                break;
            case 3:
                txtCardType.setText("充值卡");
                llIcon.setVisibility(View.GONE);
                break;
        }
        if(!StringUtils.isEmpty(shop_logo)){
            ivShop.setImageURI(Uri.parse(shop_logo));
        }
        txtShopName.setText(shop_name);
        txtPrice.setText("¥"+price);
    }

    @Override
    public void initData() {

    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case HttpModeBase.HTTP_REQUESTCODE_1:
                try {
                    JSONObject object = new JSONObject((String) msg.obj);
                    if (object.optInt("status") == 1) {
                        order_no = object.optJSONObject("result").optString("order_no");
                        oid = object.optJSONObject("result").optString("oid");
                        balance = object.optJSONObject("result").optString("balance");
                        is_pay_pwd = object.optJSONObject("result").optInt("is_pay_pwd");
                        popPay();
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
                            it = new Intent(mContext,BanKaSuccessActivity.class);
                            it.putExtra("shop_name",shop_name);
                            it.putExtra("card_name",card_name);
                            it.putExtra("card_type",txtCardType.getText().toString());
                            it.putExtra("price",price);
                            it.putExtra("name",etName.getText().toString());
                            it.putExtra("phone",etPhone.getText().toString());
                            it.putExtra("birth",txtBirth.getText().toString());
                            it.putExtra("order_no",order_no);
                            switch (pay_type){
                                case 1:
                                    it.putExtra("pay_type","支付宝支付");
                                    break;
                                case 2:
                                    it.putExtra("pay_type","微信支付");
                                    break;
                                case 3:
                                    it.putExtra("pay_type","余额支付");
                                    break;
                            }
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
            case HttpModeBase.HTTP_REQUESTCODE_IMG:
                try {
                    JSONObject object = new JSONObject((String) msg.obj);
                    if (object.optInt("status") == 1) {
                        if (object.has("src") && !object.optString("src").equals("")) {
                            member_avatar = object.optString("src");
                        }
                        mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_1, UrlUtils.card_order_set(LoginUtil.getInfo("token"),LoginUtil.getInfo("uid"),
                                card_id,shop_id,"0",etName.getText().toString(),etPhone.getText().toString(),txtBirth.getText().toString(),member_avatar),true);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                SimpleHUD.dismiss();
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
                    it = new Intent(mContext,BanKaSuccessActivity.class);
                    it.putExtra("shop_name",shop_name);
                    it.putExtra("card_name",card_name);
                    it.putExtra("card_type",txtCardType.getText().toString());
                    it.putExtra("price",price);
                    it.putExtra("name",etName.getText().toString());
                    it.putExtra("phone",etPhone.getText().toString());
                    it.putExtra("birth",txtBirth.getText().toString());
                    it.putExtra("order_no",order_no);
                    switch (pay_type){
                        case 1:
                            it.putExtra("pay_type","支付宝支付");
                            break;
                        case 2:
                            it.putExtra("pay_type","微信支付");
                            break;
                        case 3:
                            it.putExtra("pay_type","余额支付");
                            break;
                    }
                    startActivity(it);
                    finish();
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
                it = new Intent(mContext,BanKaSuccessActivity.class);
                it.putExtra("shop_name",shop_name);
                it.putExtra("card_name",card_name);
                it.putExtra("card_type",txtCardType.getText().toString());
                it.putExtra("price",price);
                it.putExtra("name",etName.getText().toString());
                it.putExtra("phone",etPhone.getText().toString());
                it.putExtra("birth",txtBirth.getText().toString());
                it.putExtra("order_no",order_no);
                switch (pay_type){
                    case 1:
                        it.putExtra("pay_type","支付宝支付");
                        break;
                    case 2:
                        it.putExtra("pay_type","微信支付");
                        break;
                    case 3:
                        it.putExtra("pay_type","余额支付");
                        break;
                }
                startActivity(it);
                finish();
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
                PayTask alipay = new PayTask(BuyMembershipCardActivity.this);
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
    @OnClick({R.id.txt_left,R.id.iv_icon,R.id.txt_birth,R.id.txt_pay})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.txt_left:
                finish();
                break;
            case R.id.iv_icon:
                PictureSelector.create(BuyMembershipCardActivity.this)
                        .openGallery(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                        .maxSelectNum(1)// 最大图片选择数量
                        .minSelectNum(1)// 最小选择数量
                        .imageSpanCount(3)// 每行显示个数
                        .selectionMode(PictureConfig.SINGLE)// 多选 or 单选
                        .previewImage(true)// 是否可预览图片
                        .withAspectRatio(1,1)// int 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                        .isCamera(true)// 是否显示拍照按钮
                        .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                        .circleDimmedLayer(false) // 是否圆形裁剪 true or false
                        .showCropFrame(false)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
                        .showCropGrid(false)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
                        //.setOutputCameraPath("/CustomPath")// 自定义拍照保存路径
                        .enableCrop(true)// 是否裁剪
                        .compress(true)// 是否压缩
                        .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
                break;
            case R.id.txt_birth:
                showTimePv();
                break;
            case R.id.txt_pay:
                if(StringUtils.isEmpty(etName.getText().toString())){
                    ToastUtil.show(mContext,"请输入姓名");
                    return;
                }
                if(StringUtils.isEmpty(etPhone.getText().toString())){
                    ToastUtil.show(mContext,"请输入联系方式");
                    return;
                }
                if(StringUtils.isEmpty(txtBirth.getText().toString())){
                    ToastUtil.show(mContext,"请选择生日");
                    return;
                }
                if(card_type == 3){
                    mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_1, UrlUtils.card_order_set(LoginUtil.getInfo("token"),LoginUtil.getInfo("uid"),
                            card_id,shop_id,"0",etName.getText().toString(),etPhone.getText().toString(),txtBirth.getText().toString(),member_avatar),true);
                }else {
                    if(bm == null){
                        ToastUtil.show(mContext,"请选择头像");
                        return;
                    }
                    mHttpModeBase.uploadImage(HttpModeBase.HTTP_REQUESTCODE_IMG, selectList.get(0).getCutPath(),true);
                }
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
                        txtPayAmount.setText("¥ "+price);
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
                                if(pay_type == 3 && Double.parseDouble(balance) < Double.parseDouble(price)){
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
                                        oid,order_no,etPwd.getPasswordString()),true);
                            }

                            @Override
                            public void inputUnFinished(String inputPsd) {}
                        });
                    }
                }).setOutCancel(false).setShowBottom(true)
                .show(getSupportFragmentManager());
    }

    private void showTimePv() {
        Calendar selectedDate = Calendar.getInstance();//系统当前时间
        pvCustomTime = new TimePickerView.Builder(mContext, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                txtBirth.setText(StringUtils.getTime(date));
            }
        }).setCancelColor(UIUtils.getColor(R.color.text_99))
                .setSubmitColor(UIUtils.getColor(R.color.main_color))
                .setDate(selectedDate)
                .setLineSpacingMultiplier(2.0f)//设置两横线之间的间隔倍数
                .setType(new boolean[]{true, true, true, false, false, false})
                .setLabel("", "", "", "", "", "")
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setTextColorCenter(Color.parseColor("#333333"))//设置选中项的颜色
                .setDividerColor(Color.parseColor("#EEEEEE"))
                .build();

        pvCustomTime.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case RESULT_OK:
                if(requestCode == PictureConfig.CHOOSE_REQUEST){
                    selectList.clear();
                    selectList.addAll(PictureSelector.obtainMultipleResult(data));
                    if(selectList != null && selectList.size() > 0){
                        BitmapFactory.Options option = new BitmapFactory.Options();
                        // 压缩图片:表示缩略图大小为原始图片大小的几分之一，1为原图
                        option.inSampleSize = 3;
                        // 得到图片的旋转角度
                        bm = BitmapFactory.decodeFile(selectList.get(0).getCutPath(), option);
                        // 显示在图片控件上
                        ivIcon.setImageBitmap(bm);
                    }else {
                        ToastUtil.show(mContext,"选择图片出错");
                    }
                }
                break;
            case AppConfig.ACTIVITY_RESULTCODE_PWD:
                is_pay_pwd = 1;
                payConfirm();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activity = null;
        PictureSelectUtil.clearCache(this);
    }
}
