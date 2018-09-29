package com.maapuu.mereca.base;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.alibaba.mobileim.YWAPI;
import com.alibaba.mobileim.aop.AdviceBinder;
import com.alibaba.mobileim.aop.PointCutEnum;
import com.alibaba.wxlib.util.SysUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.maapuu.mereca.openim.ChattingOperationCustomSample;
import com.maapuu.mereca.openim.ChattingUICustomSample;
import com.maapuu.mereca.openim.ConversationListUICustomSample;
import com.maapuu.mereca.openim.UserProfileSampleHelper;
import com.maapuu.mereca.service.MyPushIntentService;
import com.maapuu.mereca.util.LoginUtil;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.socialize.PlatformConfig;

import org.xutils.x;

/**
 * Created by dell on 2016/10/17.
 */
public class BaseApplication extends MultiDexApplication {
    private static BaseApplication application;
    private PushAgent mPushAgent;

    public static BaseApplication getInstance() {
        return application;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        init();
    }

    private void init() {
        x.Ext.init(this);
        x.Ext.setDebug(true);
//        ButterKnife.setDebug(true);
//        LoginUtil.init(getBaseContext());
//        Fresco.initialize(getBaseContext());
//        UMConfigure.setLogEnabled(true);
//        //初始化组件化基础库, 统计SDK/推送SDK/分享SDK都必须调用此初始化接口
        UMConfigure.init(this, "5adcc507f29d987c84000447", "Umeng", UMConfigure.DEVICE_TYPE_PHONE,
                "ad424ff5a97d2806466a54745faf3664");

        mPushAgent = PushAgent.getInstance(this);
        new registerPushThread().start();

        SysUtil.setApplication(this);
        if (SysUtil.isTCMSServiceProcess(this)) {
            return;
        }
        //第一个参数是Application Context
        //这里的APP_KEY即应用创建时申请的APP_KEY，同时初始化必须是在主进程中
        if (SysUtil.isMainProcess()) {
            YWAPI.init(this, AppConfig.APP_KEY);
            //自定义头像和昵称回调初始化(如果不需要自定义头像和昵称，则可以省去)
            UserProfileSampleHelper.initProfileCallback();
        }
        AdviceBinder.bindAdvice(PointCutEnum.CHATTING_FRAGMENT_POINTCUT,ChattingOperationCustomSample.class);
        AdviceBinder.bindAdvice(PointCutEnum.CONVERSATION_FRAGMENT_UI_POINTCUT, ConversationListUICustomSample.class);
        AdviceBinder.bindAdvice(PointCutEnum.CHATTING_FRAGMENT_UI_POINTCUT, ChattingUICustomSample.class);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    class registerPushThread extends Thread{
        @Override
        public void run() {
            LoginUtil.init(getBaseContext());
            Fresco.initialize(getBaseContext());
            //注册推送服务，每次调用register方法都会回调该接口
            mPushAgent.register(new IUmengRegisterCallback() {
                @Override
                public void onSuccess(String deviceToken) {
                    //注册成功会返回device token
                    Log.i("abc",deviceToken);
                }
                @Override
                public void onFailure(String s, String s1) {
                    Log.i("abc",s+s1);
                }
            });
            mPushAgent.setPushIntentServiceClass(MyPushIntentService.class);
        }
    }

    {
        PlatformConfig.setWeixin("wx787a13e07988e5f8", "e60d94151efce0e91f5f4d5dca0c8884");
        PlatformConfig.setSinaWeibo("3921700954", "04b48b094faeb16683c32669824ebdad", "http://sns.whalecloud.com");
        PlatformConfig.setQQZone("1106832697", "eEmybnOWGQJkonKb");
    }
}
