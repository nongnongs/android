package com.maapuu.mereca.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import com.maapuu.mereca.R;
import com.maapuu.mereca.activity.AlertActivity;
import com.maapuu.mereca.activity.ChatDetailActivity;
import com.maapuu.mereca.activity.OrderGoodsDetailActivity;
import com.maapuu.mereca.activity.OrderProjectDetailActivity;
import com.maapuu.mereca.activity.SrvDetailActivity;
import com.maapuu.mereca.background.shop.activity.EmployeeCheckActivity;
import com.maapuu.mereca.background.shop.activity.ShopOrderGoodsDetailActivity;
import com.maapuu.mereca.background.shop.activity.ShopOrderProjectDetailActivity;
import com.maapuu.mereca.util.StringUtils;
import com.maapuu.mereca.util.SystemUtils;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengMessageService;
import com.umeng.message.common.UmLog;
import com.umeng.message.entity.UMessage;

import org.android.agoo.common.AgooConstants;
import org.json.JSONObject;

import java.util.Map;
import java.util.Random;

/**
 * Created by dell on 2017/8/29.
 */

public class MyPushIntentService extends UmengMessageService {
    private static final String TAG = MyPushIntentService.class.getName();
    PushAgent mPushAgent = PushAgent.getInstance(this);
    Context mContext;

    @Override
    public void onMessage(Context context, Intent intent) {
        this.mContext = context;
        try {
            //可以通过MESSAGE_BODY取得消息体
            String message = intent.getStringExtra(AgooConstants.MESSAGE_BODY);
            UMessage msg = new UMessage(new JSONObject(message));

            if (Build.VERSION.SDK_INT >= 26) {
                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationChannel channel = new NotificationChannel("channel_id", "channel_name", NotificationManager.IMPORTANCE_HIGH);
                if (manager != null) {
                    manager.createNotificationChannel(channel);
                }
                Notification.Builder builder = new Notification.Builder(context, "channel_id");
                builder.setSmallIcon(R.mipmap.ic_launcher)
                        .setWhen(System.currentTimeMillis())
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                        .setContentTitle(msg.title)
                        .setContentText(msg.text)
                        .setAutoCancel(true);
            }

            int id = new Random(System.nanoTime()).nextInt();
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.cancelAll();
            Notification notification = messageHandler.getNotification(context, msg);
            manager.notify(id, notification);
        } catch (Exception e) {
            UmLog.e(TAG, e.getMessage());
        }
        mPushAgent.setMessageHandler(messageHandler);
    }

    UmengMessageHandler messageHandler = new UmengMessageHandler() {
        @Override
        public Notification getNotification(Context context, UMessage msg) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 1;
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.logo, options);
            Notification.Builder mBuilder = null;
            if (Build.VERSION.SDK_INT >= 26) {
                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationChannel channel = new NotificationChannel("channel_id", "channel_name", NotificationManager.IMPORTANCE_HIGH);
                if (manager != null) {
                    manager.createNotificationChannel(channel);
                }
                mBuilder = new Notification.Builder(MyPushIntentService.this, "channel_id");
            }else {
                mBuilder = new Notification.Builder(MyPushIntentService.this);
            }
            mBuilder.setContentTitle(msg.title)
                    .setContentText(msg.text)
                    .setTicker(msg.ticker)
                    .setWhen(System.currentTimeMillis())
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setLargeIcon(bitmap)
                    .setSmallIcon(R.mipmap.logo)
                    .setAutoCancel(true);
            Notification notification = mBuilder.build();

            Intent it = null;
            //判断app进程是否存活
            Map<String, String> map = msg.extra;
            Log.i("abc","bb:"+msg.extra.toString());
            if (SystemUtils.isAppAlive(context, "com.maapuu.mereca")) {
                it = new Intent();
                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                switch (Integer.valueOf(map.get("type"))) {
                    case 0://通知
                        break;
                    case 100: //打开订单，value为订单oid；
                        if(map.get("value").contains(",")){
                            String[] splits = map.get("value").split(",");
                            if(splits[1].equals("1")){
                                it = new Intent(context, ShopOrderProjectDetailActivity.class);
                                it.putExtra("oid",splits[0]);
                            }else if(splits[1].equals("2")){
                                it = new Intent(context,ShopOrderGoodsDetailActivity.class);
                                it.putExtra("oid",splits[0]);
                            }
                        }
                        break;
                    case 101: //添加好友
                        it = new Intent(mContext,ChatDetailActivity.class);
                        it.putExtra("friend_uid",map.get("value").split(",")[1]);
                        it.putExtra("msg_id",map.get("value").split(",")[0]);
                        it.putExtra("share_code","");
                        it.putExtra("isVerify",true);
                        break;
                    case 200: //员工审核
                        it = new Intent(mContext,EmployeeCheckActivity.class);
                        it.putExtra("shop_id",map.get("value"));
                        break;
                    case 201: //用于预约之后发送给对应的预约人员，告知服务单详情，value为消费码的code2d_id，打开服务单详情(srv_detail_get)
                        if(map.containsKey("value") && !StringUtils.isEmpty(map.get("value"))) {
                            it = new Intent(mContext,SrvDetailActivity.class);
                            it.putExtra("code2d_id",map.get("value"));
                        }
                        break;
                    case 202: //服务详情，可以打开新的服务 详情(srv_new_detail_get)
                        if(map.containsKey("value") && !StringUtils.isEmpty(map.get("value"))) {
                            it = new Intent(context, AlertActivity.class);
                            it.putExtra("business_id",map.get("value"));
                        }
                        break;
                }
            } else {
                it = context.getPackageManager().getLaunchIntentForPackage("com.maapuu.mereca");
                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                switch (Integer.valueOf(map.get("type"))) {
                    case 0://通知
                        break;
                    case 100: //打开订单，value为订单oid；
                        if(map.get("value").contains(",")){
                            String[] splits = map.get("value").split(",");
                            if(splits[1].equals("1")){
                                it = new Intent(context, OrderProjectDetailActivity.class);
                                it.putExtra("oid",map.get("value"));
                            }else if(splits[1].equals("2")){
                                it = new Intent(context,OrderGoodsDetailActivity.class);
                                it.putExtra("oid",map.get("value"));
                            }
                        }
                        break;
                    case 101: //添加好友
                        it = new Intent(mContext,ChatDetailActivity.class);
                        it.putExtra("friend_uid",map.get("value").split(",")[1]);
                        it.putExtra("msg_id",map.get("value").split(",")[0]);
                        it.putExtra("share_code","");
                        break;
                    case 200: //员工审核
                        it = new Intent(mContext,EmployeeCheckActivity.class);
                        it.putExtra("shop_id",map.get("value"));
                        break;
                    case 201: //用于预约之后发送给对应的预约人员，告知服务单详情，value为消费码的code2d_id，打开服务单详情(srv_detail_get)
                        if(map.containsKey("value") && !StringUtils.isEmpty(map.get("value"))) {
                            it = new Intent(mContext,SrvDetailActivity.class);
                            it.putExtra("code2d_id",map.get("value"));
                        }
                        break;
                    case 202: //服务详情，可以打开新的服务 详情(srv_new_detail_get)
                        if(map.containsKey("value") && !StringUtils.isEmpty(map.get("value"))) {
                            it = new Intent(context, AlertActivity.class);
                            it.putExtra("business_id",map.get("value"));
                        }
                        break;
                }
            }
            PendingIntent intent_paly = PendingIntent.getActivity(MyPushIntentService.this, 0x001, it, PendingIntent.FLAG_UPDATE_CURRENT);
//            notification.deleteIntent = intent_paly;
            notification.contentIntent = intent_paly;
            return notification;
        }
    };

}