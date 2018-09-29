package com.maapuu.mereca.activity;

import android.content.Intent;
import android.os.Bundle;
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
import com.maapuu.mereca.view.SearchEditText;
import com.umeng.message.PushAgent;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.util.LogUtil;

import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by dell on 2018/3/5.
 */

public class LoginPhoneActivity extends BaseActivity {
    @BindView(R.id.txt_left)
    TextView txtLeft;
    @BindView(R.id.txt_title)
    TextView txtTitle;
    @BindView(R.id.et_phone)
    SearchEditText etPhone;
    @BindView(R.id.et_pwd)
    EditText etPwd;

    private PushAgent mPushAgent;
    private int times = 0;

    @Override
    public void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_login_phone);
        mPushAgent = PushAgent.getInstance(this);
    }

    @Override
    public void initView() {
        txtLeft.setTypeface(StringUtils.getFont(mContext));
        txtTitle.setText("登录");
        etPhone.setText(LoginUtil.getPhone(mContext));
        etPhone.setSelection(LoginUtil.getPhone(mContext).length());
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
                        ToastUtil.show(mContext, "登录成功");
                        LoginUtil.setLoginState(true);
                        LoginUtil.putPhone(mContext,object.optJSONObject("result").optString("phone"));
                        if (!StringUtils.isEmpty(object.optString("result"))) {
                            Map<String, String> userMap = JsonUtils.jsonStrToMap(object.optString("result"));
                            userMap.put(AppConfig.APP_NAME, object.optString("result"));
                            LoginUtil.writeMapInfo(userMap);
                            initChat();
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
            case HttpModeBase.HTTP_ERROR:
                String result_e = (String) msg.obj;
                ToastUtil.show(mContext,result_e);
                break;
        }
        return false;
    }

    @Override
    @OnClick({R.id.txt_left,R.id.txt_forget,R.id.txt_login})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.txt_left:
                finish();
                break;
            case R.id.txt_forget:
                startActivity(new Intent(mContext,ForgetPwdActivity.class));
                break;
            case R.id.txt_login:
                if (StringUtils.isEmpty(etPhone.getText().toString())) {
                    ToastUtil.show(mContext, "请输入手机号码");
                    return;
                }
                if (etPhone.getText().toString().length() != 11) {
                    ToastUtil.show(mContext, "手机号格式错误");
                    return;
                }
                if (StringUtils.isEmpty(etPwd.getText().toString())) {
                    ToastUtil.show(mContext, "请输入密码");
                    return;
                }
                mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_1, UrlUtils.user_login_get(etPhone.getText().toString(),etPwd.getText().toString(),
                        mPushAgent.getRegistrationId()),true);
                break;
        }
    }

    /**
     * 阿里百川的聊天
     */
    private void initChat() {
        AppConfig.mIMKit = YWAPI.getIMKitInstance(LoginUtil.getInfo("uid"), AppConfig.APP_KEY);
        IYWLoginService loginService = AppConfig.mIMKit.getLoginService();
        YWLoginParam loginParam = YWLoginParam.createLoginParam(LoginUtil.getInfo("uid"), "e10adc3949ba59abbe56e057f20f883e");
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
