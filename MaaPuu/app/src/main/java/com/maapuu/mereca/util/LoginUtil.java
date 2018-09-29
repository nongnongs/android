package com.maapuu.mereca.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.maapuu.mereca.base.AppConfig;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class LoginUtil {

    private static SharedPreferences mPreferences;
    private static final String LOGIN = "isLogin";
    private static final String PHONE = "phone";
    private static final String CITYNAME = "index_city_name";
    private static final String CITYID = "index_city_id";

    public static void init(Context context) {
        mPreferences = context.getSharedPreferences(AppConfig.APP_NAME, Context.MODE_PRIVATE);
    }

    public static void clear(Context context){
        mPreferences = context.getSharedPreferences(AppConfig.APP_NAME, Context.MODE_PRIVATE);
        mPreferences.edit().clear().commit();
    }

    public static boolean getLoginState() {
        return mPreferences.getBoolean(LOGIN, false);
    }

    public static void setLoginState(boolean value) {
        mPreferences.edit().putBoolean(LOGIN, value).commit();
    }

    public static void putCityID(Context context,String value) {
        SharedPreferences preferences = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        preferences.edit().putString(CITYID, value).commit();
    }

    public static String getCityID(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        return preferences.getString(CITYID, "0");
    }

    public static void putCityName(Context context,String value) {
        SharedPreferences preferences = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        preferences.edit().putString(CITYNAME, value).commit();
    }

    public static String getCityName(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        return preferences.getString(CITYNAME, "");
    }

    public static void putPhone(Context context,String value) {
        SharedPreferences preferences = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        preferences.edit().putString(PHONE, value).commit();
    }

    public static String getPhone(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        return preferences.getString(PHONE, "");
    }

    public static boolean saveProperties(Map<String,String> maps){
        boolean flag = false;
        Set<String> keys = maps.keySet();
        Iterator<String> iter = keys.iterator();
        while(iter.hasNext()){
            String key = iter.next();
            String value = maps.get(key);
            flag = mPreferences.edit().putString(key, value).commit();
        }
        return flag;
    }

    /**
     * 读取单条信息
     * @param key SharedPreferences Key
     * @return uid;phone;avatar;nick_name;sex;birthday;city_id;city_namesignature;token;share_code
     */
    public static String getInfo(String key) {
        if(key.equals("uid")|| key.equals("unlogin_shop_id")){
            return mPreferences.getString(key, "0");
        }else {
            return mPreferences.getString(key, "");
        }
    }

    public static void setInfo(String key,String value) {
        mPreferences.edit().putString(key,value).commit();
    }

    /**
     * 写入全部信息
     * @param map 用户信息Map
     * @return
     */
    public static void writeMapInfo(Map<String,String> map) {
        saveProperties(map);
    }

    /**
     * 写入List信息
     * @param spKey SharedPreferences key
     * @param info List信息
     * @return
     */
    public static void writeAllInfo(String spKey, String info) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(spKey,info);
        editor.commit();
    }
    /**
     * 获取List信息
     * @param spKey SharedPreferences key
     * @return
     */
    public static String readAllInfo(String spKey) {
        String userInfo = mPreferences.getString(spKey,null);
        return userInfo;
    }
    /**
     * 获取List信息
     * @param spKey SharedPreferences key
     * @return
     */
    public static String readMapInfo(String spKey) {
        String userInfo = mPreferences.getString(spKey,null);
        return userInfo;
    }
}
