package com.maapuu.mereca.service;

import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.content.FileProvider;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.maapuu.mereca.R;
import com.maapuu.mereca.activity.MainActivity;
import com.maapuu.mereca.base.AppManager;
import com.maapuu.mereca.base.BaseApplication;
import com.maapuu.mereca.util.ToastUtil;

import java.io.File;

import info.wangchen.simplehud.SimpleHUD;

public class UpdataService extends Service {
    private TelephonyManager tm;
    /**
     * 安卓系统下载类
     **/
    private DownloadManager manager;

    /**
     * 接收下载完的广播
     **/
    private DownloadCompleteReceiver receiver;
    // 文件存储
    private String apkUrl = null;
    private String fileName = null;
    private String DOWNLOADPATH = "/meirong/";//下载路径，如果不定义自己的路径，6.0的手机不自动安装
    /**
     * 初始化下载器
     **/
    private void initDownManager() {
        tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        receiver = new DownloadCompleteReceiver();
        //设置下载地址
        DownloadManager.Request down = new DownloadManager.Request(Uri.parse(apkUrl));
        // 设置允许使用的网络类型，这里是移动网络和wifi都可以
        down.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE
                | DownloadManager.Request.NETWORK_WIFI);
        down.setAllowedOverRoaming(false);
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(apkUrl));
        down.setMimeType(mimeString);
        // 下载时，通知栏显示途中
        down.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        // 显示下载界面
        down.setVisibleInDownloadsUi(true);
        // 设置下载后文件存放的位置
        File fileDir = new File(DOWNLOADPATH);
        if (!fileDir.isDirectory()) {
            fileDir.mkdirs();
        }
        down.setDestinationInExternalPublicDir(Environment.getExternalStorageDirectory().getAbsolutePath()+DOWNLOADPATH,fileName);
        down.setTitle(getApplicationContext().getString(R.string.app_name));
        // 将下载请求放入队列
        manager.enqueue(down);
        //注册下载广播
        registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
//        lastDownloadId = manager.enqueue(down);
        //9.保存id到缓存
//        ACache.get(getApplicationContext()).put(AppConfig.DOWNLOAD_ID,lastDownloadId);
//        mBuilder = new NotificationCompat.Builder(getApplicationContext());
//        noticManager = (NotificationManager)getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
//        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
//        mBuilder.setContentTitle("正在下载"+getApplicationContext().getString(R.string.app_name));
//        startForeground(1, mBuilder.build());

        //10.采用内容观察者模式实现进度
//        downloadObserver = new DownloadChangeObserver(null);
//        getContentResolver().registerContentObserver(CONTENT_URI, true, downloadObserver);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 获取传值
        apkUrl = intent.getStringExtra("url");
        // 创建文件
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            fileName = apkUrl.substring(apkUrl.lastIndexOf('/') + 1);//截取url最后的数据
        }
        String path =Environment.getExternalStorageDirectory().getAbsolutePath()+DOWNLOADPATH+fileName;
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
        try {
            // 调用下载
            SimpleHUD.showInfoMessage(AppManager.getAppManager().currentActivity(), "开始下载");
//            Toast.makeText(getApplicationContext(), "开始下载", Toast.LENGTH_SHORT).show();
            initDownManager();
        } catch (Exception e) {
            e.printStackTrace();
            SimpleHUD.showInfoMessage(AppManager.getAppManager().currentActivity(), "下载失败");
//            Toast.makeText(getApplicationContext(), "下载失败", Toast.LENGTH_SHORT).show();
        }
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        if (receiver != null)
            // 注销下载广播
            unregisterReceiver(receiver);
        super.onDestroy();
    }

    // 接受下载完成后的intent
    class DownloadCompleteReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //判断是否下载完成的广播
            if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                //获取下载的文件id
                long downId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (manager.getUriForDownloadedFile(downId) != null) {
                    //自动安装apk
                    installAPK(manager.getUriForDownloadedFile(downId), context,intent);
                    //installAPK(context);
                } else {
                    SimpleHUD.showInfoMessage(AppManager.getAppManager().currentActivity(), "下载失败");
                }
                //停止服务并关闭广播
                stopSelf();
                stopForeground(true);
            }
        }

        private void installAPK(Uri apk, Context context,Intent intent) {
            if (Build.VERSION.SDK_INT <= 23) {
                Intent intents = new Intent(Intent.ACTION_VIEW);
//                intents.setAction("android.intent.action.VIEW");
//                intents.addCategory("android.intent.category.DEFAULT");
                intents.setType("application/vnd.android.package-archive");
                intents.setData(apk);
                intents.setDataAndType(apk, "application/vnd.android.package-archive");
                intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intents);
            }else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O && Build.VERSION.SDK_INT > Build.VERSION_CODES.M)  {
                Intent install = new Intent(Intent.ACTION_VIEW);
                File apkFile = queryDownloadedApk(context,intent);
                Log.i("abc",apkFile.getPath());
                install.setDataAndType(FileProvider.getUriForFile(context, BaseApplication.getInstance().getPackageName()+".provider", apkFile), "application/vnd.android.package-archive");
                install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(install);
            } else {
                if(context.getPackageManager().canRequestPackageInstalls()){
                    Intent it2 = new Intent(Intent.ACTION_VIEW);
                    File apkFile = queryDownloadedApk(context,intent);
                    it2.setDataAndType(FileProvider.getUriForFile(context, BaseApplication.getInstance().getPackageName()+".provider", apkFile), "application/vnd.android.package-archive");
                    it2.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    it2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(it2);
                }else {
                    ToastUtil.show(context,"请点击设置按钮，允许安装未知来源应用");
                    Intent it = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + getPackageName()));
                    context.startActivity(it);
                }
            }
        }
    }

    //通过downLoadId查询下载的apk，解决6.0以后安装的问题
    public static File queryDownloadedApk(Context context,Intent intent) {
        File targetApkFile = null;
        DownloadManager downloader = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        long downloadId =intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
        if (downloadId != -1) {
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(downloadId);
            query.setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL);
            Cursor cur = downloader.query(query);
            if (cur != null) {
                if (cur.moveToFirst()) {
                    String uriString = cur.getString(cur.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                    if (!uriString.equals("")) {
                        targetApkFile = new File(Uri.parse(uriString).getPath());
                    }
                }
                cur.close();
            }
        }
        return targetApkFile;
    }
}