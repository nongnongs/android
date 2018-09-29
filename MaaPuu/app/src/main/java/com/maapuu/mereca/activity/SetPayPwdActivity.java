package com.maapuu.mereca.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.maapuu.mereca.R;
import com.maapuu.mereca.base.AppConfig;
import com.maapuu.mereca.base.BaseActivity;
import com.maapuu.mereca.constant.UrlUtils;
import com.maapuu.mereca.util.HttpModeBase;
import com.maapuu.mereca.util.LoginUtil;
import com.maapuu.mereca.util.StringUtils;
import com.maapuu.mereca.util.ToastUtil;
import com.maapuu.mereca.view.PayPsdInputView;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by dell on 2018/3/1.
 */

public class SetPayPwdActivity extends BaseActivity {
    @BindView(R.id.txt_left)
    TextView txtLeft;
    @BindView(R.id.txt_title)
    TextView txtTitle;
    @BindView(R.id.ll_1)
    LinearLayout ll1;
    @BindView(R.id.txt_code_phone)
    TextView txtCodePhone;
    @BindView(R.id.et_code)
    PayPsdInputView etCode;
    @BindView(R.id.txt_code)
    TextView txtCode;
    @BindView(R.id.ll_2)
    LinearLayout ll2;
    @BindView(R.id.txt_phone)
    TextView txtPhone;
    @BindView(R.id.et_pwd_1)
    PayPsdInputView etPwd1;
    @BindView(R.id.ll_3)
    LinearLayout ll3;
    @BindView(R.id.et_pwd_2)
    PayPsdInputView etPwd2;
    @BindView(R.id.txt_complete)
    TextView txtComplete;

    private TimeCount time;
    private String sms_id = ""; //验证码的id
    private boolean bool = false;

    private String oid;
    private String order_no;

    @Override
    public void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_pay_pwd);
    }

    @Override
    public void initView() {
        txtLeft.setTypeface(StringUtils.getFont(mContext));
        txtTitle.setText("设置安全密码");
        time = new TimeCount(60000, 1000);
        oid = getIntent().getStringExtra("oid");
        order_no = getIntent().getStringExtra("order_no");
        txtCodePhone.setText(LoginUtil.getInfo("phone").substring(0,4)+"****"+LoginUtil.getInfo("phone").substring(7,11));
        txtPhone.setText("请为账号"+LoginUtil.getInfo("phone").substring(0,4)+"****"+LoginUtil.getInfo("phone").substring(7,11));
        etCode.setComparePassword(new PayPsdInputView.onPasswordListener() {
            @Override
            public void onDifference(String oldPsd, String newPsd) {}

            @Override
            public void onEqual(String psd) {}

            @Override
            public void inputFinished(String inputPsd) {
                StringUtils.closeKeyBorder(mContext,etCode);
                mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_2, UrlUtils.user_pay_pwd_sms_set(LoginUtil.getInfo("token"),LoginUtil.getInfo("uid"),
                        sms_id,etCode.getPasswordString()),false);
            }

            @Override
            public void inputUnFinished(String inputPsd) {}
        });
        etPwd1.setComparePassword(new PayPsdInputView.onPasswordListener() {
            @Override
            public void onDifference(String oldPsd, String newPsd) {}

            @Override
            public void onEqual(String psd) {}

            @Override
            public void inputFinished(String inputPsd) {
                StringUtils.closeKeyBorder(mContext,etPwd1);
                ll2.setVisibility(View.GONE);
                ll3.setVisibility(View.VISIBLE);
            }

            @Override
            public void inputUnFinished(String inputPsd) {}
        });
        etPwd2.setComparePassword(new PayPsdInputView.onPasswordListener() {
            @Override
            public void onDifference(String oldPsd, String newPsd) {}

            @Override
            public void onEqual(String psd) {}

            @Override
            public void inputFinished(String inputPsd) {
                bool = true;
                txtComplete.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_btn));
            }

            @Override
            public void inputUnFinished(String inputPsd) {
                bool = false;
                txtComplete.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_refuse_btn));
            }
        });
    }

    @Override
    public void initData() {
        mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_1, UrlUtils.cmm_sms_code_get(LoginUtil.getInfo("phone"),"11"),true);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case HttpModeBase.HTTP_REQUESTCODE_1:
                try {
                    JSONObject object = new JSONObject((String) msg.obj);
                    if (object.optInt("status") == 1) {
                        ToastUtil.show(mContext, "短信发送成功，请注意查收");
                        sms_id = object.optJSONObject("result").optString("sms_id");
                        time.start();
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
                        ll1.setVisibility(View.GONE);
                        ll2.setVisibility(View.VISIBLE);
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
                        ToastUtil.show(mContext,"密码设置成功");
                        it = new Intent();
                        it.putExtra("oid",oid);
                        it.putExtra("order_no",order_no);
                        setResult(AppConfig.ACTIVITY_RESULTCODE_PWD,it);
                        finish();
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
    @OnClick({R.id.txt_left,R.id.txt_code,R.id.txt_complete})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.txt_left:
                setResult(AppConfig.ACTIVITY_RESULTCODE_NO_PWD,it);
                finish();
                break;
            case R.id.txt_code:
                mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_1, UrlUtils.cmm_sms_code_get(LoginUtil.getInfo("phone"),"11"),true);
                break;
            case R.id.txt_complete:
                if(!bool){
                    return;
                }
                if(!etPwd1.getPasswordString().equals(etPwd2.getPasswordString())){
                    ToastUtil.show(mContext,"密码不一致");
                    return;
                }
                mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_3, UrlUtils.user_pay_pwd_set(LoginUtil.getInfo("token"),LoginUtil.getInfo("uid"),
                        etPwd2.getPasswordString(),sms_id,etCode.getPasswordString()),true);
                break;
        }
    }

    /**
     * 短信倒计时
     */
    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }
        @Override
        public void onTick(long millisUntilFinished) {
            txtCode.setClickable(false);
            txtCode.setText(((millisUntilFinished - 1) / 1000) + "秒后重发");
        }
        @Override
        public void onFinish() {
            txtCode.setText("获取验证码");
            txtCode.setClickable(true);
        }
    }

    @Override
    public void onBackPressed() {
        setResult(AppConfig.ACTIVITY_RESULTCODE_NO_PWD,it);
        finish();
        super.onBackPressed();
    }
}
