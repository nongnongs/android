package com.maapuu.mereca.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.mobileim.IYWLoginService;
import com.alibaba.mobileim.YWAPI;
import com.alibaba.mobileim.YWLoginParam;
import com.alibaba.mobileim.channel.event.IWxCallback;
import com.maapuu.mereca.R;
import com.maapuu.mereca.base.AppConfig;
import com.maapuu.mereca.base.BaseActivity;
import com.maapuu.mereca.constant.UrlUtils;
import com.maapuu.mereca.util.HttpModeBase;
import com.maapuu.mereca.util.JsonUtils;
import com.maapuu.mereca.util.LoginUtil;
import com.maapuu.mereca.util.StringUtils;
import com.maapuu.mereca.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.util.LogUtil;

import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by dell on 2018/3/5.
 */

public class RegisterActivity extends BaseActivity {
    @BindView(R.id.txt_left)
    TextView txtLeft;
    @BindView(R.id.txt_title)
    TextView txtTitle;
    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.et_pwd)
    EditText etPwd;
    @BindView(R.id.et_code)
    EditText etCode;
    @BindView(R.id.txt_code)
    TextView txtCode;

    private TimeCount time;
    private String sms_id = ""; //验证码的id
    private int times = 0;

    @Override
    public void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_register);
    }

    @Override
    public void initView() {
        txtLeft.setTypeface(StringUtils.getFont(mContext));
        txtTitle.setText("注册");
        time = new TimeCount(60000, 1000);
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
                        ToastUtil.show(mContext, "注册成功");
                        LoginUtil.setLoginState(true);
                        if (!StringUtils.isEmpty(object.optString("result"))) {
                            Map<String, String> userMap = JsonUtils.jsonStrToMap(object.optString("result"));
                            userMap.put(AppConfig.APP_NAME, object.optString("result"));
                            LoginUtil.writeMapInfo(userMap);
                            initChat();
                        }
                        if(MainActivity.activity != null){
                            MainActivity.activity.mHandler.sendEmptyMessage(999);
                        }
                        setResult(AppConfig.ACTIVITY_RESULTCODE);
                        finish();
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
            case HttpModeBase.HTTP_ERROR:
                String result_e = (String) msg.obj;
                ToastUtil.show(mContext,result_e);
                break;
        }
        return false;
    }

    @Override
    @OnClick({R.id.txt_left,R.id.txt_code,R.id.txt_register,R.id.txt_rule})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.txt_left:
                finish();
                break;
            case R.id.txt_code:
                if (StringUtils.isEmpty(etPhone.getText().toString())) {
                    ToastUtil.show(mContext, "请输入手机号码");
                    return;
                }
                if (etPhone.getText().toString().length() != 11) {
                    ToastUtil.show(mContext, "手机号格式错误");
                    return;
                }
                mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_2, UrlUtils.cmm_sms_code_get(etPhone.getText().toString(),"1"),true);
                break;
            case R.id.txt_register:
                if (StringUtils.isEmpty(etPhone.getText().toString())) {
                    ToastUtil.show(mContext, "请输入手机号码");
                    return;
                }
                if (etPhone.getText().toString().length() != 11) {
                    ToastUtil.show(mContext, "手机号格式错误");
                    return;
                }
                if (etPwd.getText().toString().length() < 6) {
                    ToastUtil.show(mContext, "密码至少6位");
                    return;
                }
                if (StringUtils.isEmpty(etCode.getText().toString())) {
                    ToastUtil.show(mContext, "请输入验证码");
                    return;
                }
                if (StringUtils.isEmpty(sms_id)) {
                    ToastUtil.show(mContext, "验证码无效");
                    return;
                }
                mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_1,UrlUtils.user_register_set(etPhone.getText().toString(),
                        etPwd.getText().toString(),sms_id,etCode.getText().toString()),true);
                break;
            case R.id.txt_rule:
                it = new Intent(mContext, WebViewActivity.class);
                it.putExtra("title","服务条款");
                it.putExtra("isHtml",false);
                it.putExtra("content","");
                startActivity(it);
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
            txtCode.setText(((millisUntilFinished - 1) / 1000) + "s");
        }
        @Override
        public void onFinish() {
            txtCode.setText("获取验证码");
            txtCode.setClickable(true);
        }
    }

    /**
     * 阿里百川的聊天
     */
    private void initChat() {
        AppConfig.mIMKit = YWAPI.getIMKitInstance(LoginUtil.getInfo("uid"), AppConfig.APP_KEY);
        IYWLoginService loginService = AppConfig.mIMKit.getLoginService();
        YWLoginParam loginParam = YWLoginParam.createLoginParam(LoginUtil.getInfo("uid"), "e10adc3949ba59abbe56e057f20f883e", AppConfig.APP_KEY);
        loginService.login(loginParam, new IWxCallback() {
            @Override
            public void onSuccess(Object... arg0) {
                LogUtil.e("------登录成功");
            }

            @Override
            public void onProgress(int arg0) {}

            @Override
            public void onError(int errCode, String description) {
                //如果登录失败，errCode为错误码,description是错误的具体描述信息
                if(times <= 3 && errCode == -2){
                    times ++;
                    initChat();
                }
                LogUtil.e("------登陆失败" + description);
            }
        });
    }
}
