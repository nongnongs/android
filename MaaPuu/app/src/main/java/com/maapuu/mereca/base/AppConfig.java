package com.maapuu.mereca.base;


import com.alibaba.mobileim.YWIMKit;
import com.maapuu.mereca.util.HttpModeBase;
import com.maapuu.mereca.util.StringUtils;

public class AppConfig {
    public static final boolean isCanCancle = true;

    public static final boolean isEncrypt = true;

    public static String getImagePath(String path){
        String IMAGEPATH = "";
        if(!StringUtils.isEmpty(path)){
            if(path.contains("http")){
                IMAGEPATH = path;
            }else {
                IMAGEPATH = HttpModeBase.BASE_URL + path;
            }
        }
        return IMAGEPATH;
    }

    public static final String APP_NAME = "meirong";
    public static YWIMKit mIMKit = null;
    public static final String APP_KEY = "23472694";//阿里百川key

    public static String LAT = "0"; //纬度
    public static String LNG = "0"; //经度
    public static String CITY = ""; //定位城市

    public static final int PERMISSIONCODE = 0x0100;//权限
    public static final int PERMISSIONCODE1 = 0x0101;//权限

    //Activity请求码
    public static final int ACTIVITY_REQUESTCODE=0X01000;
    public static final int ACTIVITY_REQUESTCODE_1=0X01001;
    public static final int ACTIVITY_REQUESTCODE_2=0X01002;

    //Activity结果码
    public static final int ACTIVITY_RESULTCODE=0X02000;
    public static final int ACTIVITY_RESULTCODE_1=0X02001;
    public static final int ACTIVITY_RESULTCODE_2=0X02002;
    public static final int ACTIVITY_RESULTCODE_3=0X02003;
    public static final int ACTIVITY_RESULTCODE_4=0X02004;
    public static final int ACTIVITY_RESULTCODE_PWD=0X02101;
    public static final int ACTIVITY_RESULTCODE_NO_PWD=0X02102;

    public static final int PERMISSION_LOCATION = 0x0100;//定位权限
    public static final int PERMISSION_CAMERA = 0x0101;//相机权限
    public static final int PERMISSION_SD = 0x0102;//读取SD卡权限
    public static final int PERMISSION_CALL_PHONE = 0x0103;//拨打电话

    //EventBus事件消息
    //ConsumeCodeDetailActivity刷新数据
    public static final String refresh_in_ConsumeCodeDetailActivity = "refresh_in_ConsumeCodeDetailActivity";

}
