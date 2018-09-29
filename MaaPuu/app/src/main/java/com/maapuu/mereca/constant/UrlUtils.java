package com.maapuu.mereca.constant;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by dell on 2017/5/8.
 */

@SuppressWarnings("all")
public class UrlUtils {

    /**
     * 1、短信发送接口
     * @param phone 手机号
     * @param type 1注册 2找回密码 3修改手机号(新手机号) 4第三方登陆绑定手机号 8提现申请验证码 11支付密码验证码(VER2)
     * @return
     */
    public static JSONObject cmm_sms_code_get(String phone,String type) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "cmm_sms_code_get");
            object.put("phone", phone);
            object.put("type", type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 2、注册接口
     * @param phone
     * @param pwd
     * @param sms_id 验证码的id
     * @param sms_code 验证码
     * @return 用户表数据，token在其他接口中可能作为参数，标记用户登录的会话是否有效
     */
    public static JSONObject user_register_set(String phone,String pwd,String sms_id,String sms_code) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "user_register_set");
            object.put("phone", phone);
            object.put("pwd", pwd);
            object.put("sms_id", sms_id);
            object.put("sms_code", sms_code);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 服务条款
     * @return
     */
    public static JSONObject service_agreement_get() {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "service_agreement_get");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 3、密码登陆
     * @param phone
     * @param pwd
     * @return
     */
    public static JSONObject user_login_get(String phone,String pwd,String device_tokens) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "user_login_get");
            object.put("phone", phone);
            object.put("pwd", pwd);
            object.put("device_tokens", device_tokens);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 用户退出，退出后收不到推送信息
     * @param uid
     * @param token
     * @return
     */
    public static JSONObject user_exit_set(String uid,String token) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "user_exit_set");
            object.put("uid", uid);
            object.put("token", token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 4、忘记密码，错误时返回异常信息
     * @param phone
     * @param pwd
     * @param sms_id
     * @param sms_code
     * @return
     */
    public static JSONObject user_pwd_forget_set(String phone,String pwd,String sms_id,String sms_code) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "user_pwd_forget_set");
            object.put("phone", phone);
            object.put("pwd", pwd);
            object.put("sms_id", sms_id);
            object.put("sms_code", sms_code);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 5、修改密码，错误时返回异常信息
     * @param token
     * @param phone
     * @param pwd
     * @param old_pwd
     * @return
     */
    public static JSONObject user_pwd_update_set(String token,String phone,String pwd,String old_pwd) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "user_pwd_update_set");
            object.put("token", token);
            object.put("phone", phone);
            object.put("pwd", pwd);
            object.put("old_pwd", old_pwd);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 6、修改手机号，错误时返回异常信息
     * @param token
     * @param uid
     * @param old_phone
     * @param phone
     * @param sms_id
     * @param sms_code
     * @return
     */
    public static JSONObject user_phone_update_set(String token,String uid,String old_phone,String phone,String sms_id,String sms_code) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "user_phone_update_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("old_phone", old_phone);
            object.put("phone", phone);
            object.put("sms_id", sms_id);
            object.put("sms_code", sms_code);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 10、第三方登陆
     * @param login_type 1微博 2QQ 3微信
     * @param third_userid 第三方用户id
     * @param nick_name 昵称,第三方登录昵称
     * @param avatar 头像
     * @param sex 性别：1男；2女；0未知
     * @param device_tokens 设备号，如果用户不允许推送，传空
     * @param phone 绑定手机号，第一次不需要此参数，接口返回提示需要绑定手机号时(status=1)APP要求用户绑定手机号，然后再调用接口传此参数
     * @param sms_id 验证码的id
     * @param sms_code 验证码
     * @return
     */
    public static JSONObject user_third_login_get(int login_type,String third_userid,String nick_name,String avatar,String sex,String device_tokens,
                                                  String phone,String sms_id,String sms_code) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "user_third_login_get");
            object.put("login_type", login_type);
            object.put("third_userid", third_userid);
            object.put("nick_name", nick_name);
            object.put("avatar", avatar);
            object.put("sex", sex);
            object.put("device_tokens", device_tokens);
            object.put("phone", phone);
            object.put("sms_id", sms_id);
            object.put("sms_code", sms_code);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 获取用户主页最近的5张图数据，个人信息使用
     * @param token
     * @param uid
     * @param data_type;//数据为1返回城市和图片，为2只返回是否是发型师
     * @return
     */
    public static JSONObject user_page_info_get(String token,String uid,int data_type) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "user_page_info_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("data_type", data_type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 7、更新用户信息
     * @param token 用户token,注册或登录时返回的值
     * @param uid
     * @param avatar 头像，头像没有修改时传空
     * @param nick_name 昵称
     * @param signature 签名
     * @param sex 性别：1男；2女；0未知
     * @param birthday 生日
     * @param city_id 城市id
     * @return
     */
    public static JSONObject user_info_set(String token,String uid,String avatar,String nick_name,String signature,int sex,String birthday,String city_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "user_info_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("avatar", avatar);
            object.put("nick_name", nick_name);
            object.put("signature", signature);
            object.put("sex", sex);
            object.put("birthday", birthday);
            object.put("city_id", city_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 8、获取省份和城市数据，用户个人信息修改时选择省份和城市
     * @return
     */
    public static JSONObject cmm_province_city_get() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("action", "cmm_province_city_get");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
    /**
     * 9、开通城市列表
     * @return
     */
    public static JSONObject cmm_open_city_get() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("action", "cmm_open_city_get");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * 10、文件保存(视频、word、excel、ppt)
     * @return
     */
    public static JSONObject cmm_file_save_set() {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "cmm_file_save_set");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 11、图像保存
     * @return
     */
    public static JSONObject cmm_image_save_set() {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "cmm_image_save_set");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 12、用户端首页信息
     * @param token
     * @param uid
     * @param city_id
     * @param city_name
     * @param lat
     * @param lng
     * @return city_data首页的当前城市，如果城市参数(city_name)存在且已开通，返回该城市的信息，如果不存在或未开通，返回默认的城市信息；is_open判断当前定位城市是否开通；
     *          输出:info店铺信息；adv_data轮播图；staff_data明星发型师；promotion_data限时活动商品或项目；hot_item_data热门项目
     */
    public static JSONObject mem_home_get(String token,String uid,String city_id,String city_name,String lat,String lng,String shop_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "mem_home_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("city_id", city_id);
            object.put("city_name", city_name);
            object.put("lat", lat);
            object.put("lng", lng);
            object.put("shop_id", shop_id);
            object.put("sys", "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 13、设置首页店铺
     * @param token
     * @param uid
     * @param shop_id
     * @return
     */
    public static JSONObject home_shop_set(String token,String uid,String shop_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "home_shop_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("shop_id", shop_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 15、选择店铺的列表
     * @param token
     * @param uid
     * @param city_id
     * @param page
     * @return
     */
    public static JSONObject shop_list_get(String token,String uid,String city_id,String search_words,int page) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "shop_list_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("city_id", city_id);
            object.put("search_words", search_words);
            object.put("page", page);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 14、获取门店数据
     * @param token
     * @param uid
     * @param shop_id
     * @return
     */
    public static JSONObject shop_detail_get(String token,String uid,String shop_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "shop_detail_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("shop_id", shop_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 15、获取门店分类数据，项目和商品分类
     * @param token
     * @param uid
     * @param shop_id
     * @param catalog_type 分类：1项目分类；2商品分类
     * @return info店铺信息；adv_data轮播图；staff_data明星发型师；promotion_data限时活动商品或项目；hot_item_data热门项目
     */
    public static JSONObject shop_catalog_get(String token,String uid,String shop_id,String catalog_type) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "shop_catalog_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("shop_id", shop_id);
            object.put("catalog_type", catalog_type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 16、服务项目
     * @param token
     * @param uid
     * @param shop_id
     * @param catalog_id 项目分类id
     * @param order_type 排序方式：1综合排序；2按评分；3按价格
     * @param page
     * @return catalog_img分类图片；catalog_name分类名称；price_region价格区间；item_num商品或服务数量
     */
    public static JSONObject project_list_get(String token,String uid,String shop_id,String catalog_id,String order_type,int page) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "project_list_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("shop_id", shop_id);
            object.put("catalog_id", catalog_id);
            object.put("order_type", order_type);
            object.put("page", page);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 首页服务项目
     * @param token
     * @param uid
     * @param shop_id
     * @return
     */
    public static JSONObject project_list_new_get(String token,String uid,String shop_id,int is_fullcutset) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "project_list_new_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("shop_id", shop_id);
            object.put("is_fullcutset", is_fullcutset);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 首页商品
     * @param token
     * @param uid
     * @param shop_id
     * @return
     */
    public static JSONObject commodity_list_new_get(String token,String uid,String shop_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "commodity_list_new_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("shop_id", shop_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 17、服务项目基本信息
     * @param token
     * @param uid
     * @param shop_id
     * @param item_id 项目id
     * @return shop_service店铺客服id；is_collect是否收藏；evl_good_percent好评率；sale_num购买数；evl_num评价数；evl_share_num晒单数；evl_data评价数据；
     *          detail图文列表，content_type为1文字、2图片；content为文字内容或图片路径；height图片高度；width图片宽度
     */
    public static JSONObject project_info_get(String token,String uid,String shop_id,String item_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "project_info_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("shop_id", shop_id);
            object.put("item_id", item_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 18、服务项目图文详情
     * @param token
     * @param uid
     * @param shop_id
     * @param item_id
     * @return
     */
    public static JSONObject project_detail_get(String token,String uid,String shop_id,String item_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "project_detail_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("shop_id", shop_id);
            object.put("item_id", item_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 19、评价列表
     * @param token
     * @param uid
     * @param shop_id
     * @param item_id
     * @param list_type 列表类型：1全部；2有图；3好评；4差评
     * @param page
     * @return
     */
    public static JSONObject evalution_list_get(String token,String uid,String shop_id,String item_id,String list_type,int page) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "evalution_list_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("shop_id", shop_id);
            object.put("item_id", item_id);
            object.put("list_type", list_type);
            object.put("page", page);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 20、商品列表
     * @param token
     * @param uid
     * @param shop_id
     * @param catalog_id 商品分类id
     * @param order_type 排序方式：1综合排序；2按评分；3按价格
     * @param page
     * @return
     */
    public static JSONObject commodity_list_get(String token,String uid,String shop_id,String catalog_id,String order_type,int page) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "commodity_list_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("shop_id", shop_id);
            object.put("catalog_id", catalog_id);
            object.put("order_type", order_type);
            object.put("page", page);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 21、商品基本信息
     * @param token
     * @param uid
     * @param shop_id
     * @param item_id
     * @return
     */
    public static JSONObject commodity_info_get(String token,String uid,String shop_id,String item_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "commodity_info_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("shop_id", shop_id);
            object.put("item_id", item_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 22、商品图文详情
     * @param token
     * @param uid
     * @param shop_id
     * @param item_id
     * @return
     */
    public static JSONObject s_commodity_detail_get(String token,String uid,String shop_id,String item_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_commodity_detail_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("shop_id", shop_id);
            object.put("item_id", item_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 23、收藏项目或商品
     * @param token
     * @param uid
     * @param shop_id
     * @param item_id
     * @param is_collect 是否收藏，0未收藏，1已收藏
     * @return
     */
    public static JSONObject item_collect_set(String token,String uid,String shop_id,String item_id,String is_collect) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "item_collect_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("shop_id", shop_id);
            object.put("item_id", item_id);
            object.put("is_collect", is_collect);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 24、发型师数据和作品列表
     * @param token
     * @param uid
     * @param staff_id
     * @param page 第几页，仅第一页时返回发型师基本数据
     * @return
     */
    public static JSONObject staff_info_get(String token,String uid,String staff_id,int page) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "staff_info_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("staff_id", staff_id);
            object.put("page", page);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 25、发型师口碑列表
     * @param token
     * @param uid
     * @param staff_id
     * @param page
     * @return
     */
    public static JSONObject staff_koubei_list_get(String token,String uid,String staff_id,int page) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "staff_koubei_list_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("staff_id", staff_id);
            object.put("page", page);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 26、个人主页和作品列表
     * @param token
     * @param uid
     * @param userpage_uid 个人主页uid
     * @param mo_type 为1动态；2作品 。不确定时传0(第1页)，如果是发型师返回作品，如果是用户返回动态
     * @param page 第几页，仅第一页时返回用户基本数据
     * @return
     */
    public static JSONObject userpage_info_get(String token,String uid,String userpage_uid,String mo_type,int page) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "userpage_info_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("userpage_uid", userpage_uid);
            object.put("mo_type", mo_type);
            object.put("page", page);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 27、个人动态列表
     * @param token
     * @param uid
     * @param userpage_uid
     * @param page 第几页，仅第一页时返回用户基本数据
     * @return
     */
    public static JSONObject userpage_moment_get(String token,String uid,String userpage_uid,int page) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "userpage_moment_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("userpage_uid", userpage_uid);
            object.put("page", page);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 28、好友动态列表
     * @param token
     * @param uid
     * @param page
     * @return
     */
    public static JSONObject friend_moment_get(String token,String uid,int page) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "friend_moment_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("page", page);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 更新朋友圈封面图
     * @param token
     * @param uid
     * @param img
     * @return
     */
    public static JSONObject friend_moment_head_set(String token,String uid,String img) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "friend_moment_head_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("img", img);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 删除作品或动态的评论
     * @param token
     * @param uid
     * @param mmt_comment_id 朋友圈评论的id
     * @return
     */
    public static JSONObject mo_comment_delete_set(String token,String uid,String mmt_comment_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "mo_comment_delete_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("mmt_comment_id", mmt_comment_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 28、评价点赞
     * @param token
     * @param uid
     * @param evl_id 评价id
     * @param is_praise 是否点赞评价，0取消点赞，1点赞
     * @return
     */
    public static JSONObject evalution_praise_set(String token,String uid,String evl_id,String is_praise) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "evalution_praise_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("evl_id", evl_id);
            object.put("is_praise", is_praise);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 29、最新的动态列表获取
     * @param token
     * @param uid
     * @param praise_ids 作品或动态的id
     * @param comment_ids
     * @return
     */
    public static JSONObject mo_news_list_get(String token,String uid,String praise_ids,String comment_ids) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "mo_news_list_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("praise_ids", praise_ids);
            object.put("comment_ids", comment_ids);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 30、动态和作品点赞，自己可以给自己点赞
     * @param token
     * @param uid
     * @param mo_id
     * @param is_praise
     * @return
     */
    public static JSONObject mo_praise_set(String token,String uid,String mo_id,String is_praise) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "mo_praise_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("mo_id", mo_id);
            object.put("is_praise", is_praise);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 31、作品和动态的评论
     * @param token
     * @param uid
     * @param mo_id
     * @param content
     * @param reply_uid
     * @return
     */
    public static JSONObject mo_comment_set(String token,String uid,String mo_id,String content,String reply_uid) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "mo_comment_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("mo_id", mo_id);
            object.put("content", content);
            object.put("reply_uid", reply_uid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 32、删除作品或动态
     * @param token
     * @param uid
     * @param mo_id
     * @return
     */
    public static JSONObject mo_delete_set(String token,String uid,String mo_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "mo_delete_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("mo_id", mo_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 33、作品或动态发布
     * @param token
     * @param uid
     * @param content 动态的文字内容，可以为空
     * @param files 文件：type文件类型(2、图片；3、视频)；path文件路径，图片类型传递width宽度和height高度；
                        视频类型传duration播放时长和first_frame第一帧图片和第一帧图片的width宽度和height高度。
                        [{"type":"2","content":"/public/upload/5847972.png","width":"750","height":"290"},
                        {"type":"3","content":"/public/upload/0f779a.mp4","duration":"1201",
                        "first_frame":"/public/upload/0f779.png","width":"750","height":"290"}]
     * @param lng
     * @param lat
     * @param address_detail
     * @param trans_type 转发类型：0原创；1转发圈子；2转发项目；3转发商品；4发型师详情；5我的二维码
     * @param trans_title 转发标题，trans_type不为0时不能为空
     * @param trans_img 转发图片，trans_type不为0时不能为空
     * @param trans_id 转发类型：转发类型trans_type对应的业务id，原创时为0，1圈子类型时为圈子circle_id，2项目和3商品对item_shop_id，4发型师详情为staff_id，5我的二维码为uid
     * @param open_type
     * @param is_syn_works
     * @return
     */
    public static JSONObject mo_publish_set(String token,String uid,String content,String files,String lng,String lat,String address_detail,
                                            int trans_type,String trans_title,String trans_img,String trans_id,int open_type,int is_syn_works) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "mo_publish_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("content", content);
            object.put("files", files);
            object.put("lng", lng);
            object.put("lat", lat);
            object.put("address_detail", address_detail);
            object.put("trans_type", trans_type);
            object.put("trans_title", trans_title);
            object.put("trans_img", trans_img);
            object.put("trans_id", trans_id);
            object.put("open_type", open_type);
            object.put("is_syn_works", is_syn_works);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 34、关注或取消关注：1店铺、2发型师、3圈子作者
     * @param token
     * @param uid
     * @param attention_type
     * @param business_id
     * @param is_attention
     * @return
     */
    public static JSONObject attention_set(String token,String uid,String attention_type,String business_id,String is_attention) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "attention_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("attention_type", attention_type);
            object.put("business_id", business_id);
            object.put("is_attention", is_attention);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 35、店铺团队列表
     * @param token
     * @param uid
     * @param shop_id
     * @param lat
     * @param lng
     * @return
     */
    public static JSONObject shop_team_get(String token,String uid,String shop_id,String lat,String lng) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "shop_team_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("shop_id", shop_id);
            object.put("lat", lat);
            object.put("lng", lng);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 36、店铺活动列表，项目打包活动
     * @param token
     * @param uid
     * @param shop_id
     * @return
     */
    public static JSONObject action_list_get(String token,String uid,String shop_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "action_list_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("shop_id", shop_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 37、活动信息获取
     * @param token
     * @param uid
     * @param pack_id
     * @param shop_id
     * @return
     */
    public static JSONObject action_info_get(String token,String uid,String pack_id,String shop_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "action_info_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("pack_id", pack_id);
            object.put("shop_id", shop_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 38、活动图文详情
     * @param token
     * @param uid
     * @param pack_id
     * @param shop_id
     * @return
     */
    public static JSONObject action_detail_get(String token,String uid,String pack_id,String shop_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "action_detail_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("pack_id", pack_id);
            object.put("shop_id", shop_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 39、附近的店
     * @param token
     * @param uid
     * @param lat
     * @param lng
     * @param page
     * @return
     */
    public static JSONObject around_shop_list_get(String token,String uid,String lat,String lng,int page) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "around_shop_list_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("lat", lat);
            object.put("lng", lng);
            object.put("page", page);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 40、附近的人
     * @param token
     * @param uid
     * @param lat
     * @param lng
     * @param page
     * @return
     */
    public static JSONObject around_user_list_get(String token,String uid,String lat,String lng,int page) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "around_user_list_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("lat", lat);
            object.put("lng", lng);
            object.put("page", page);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 41、好友列表
     * @param token
     * @param uid
     * @return
     */
    public static JSONObject friend_list_get(String token,String uid) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "friend_list_get");
            object.put("token", token);
            object.put("uid", uid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 搜索用户
     * @param token
     * @param uid
     * @param keyword
     * @return
     */
    public static JSONObject search_user_get(String token,String uid,String keyword) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "search_user_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("keyword", keyword);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 42、好友详情
     * @param token
     * @param uid
     * @param friend_uid
     * @return
     */
    public static JSONObject friend_detail_get(String token,String uid,String friend_uid,String share_code) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "friend_detail_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("friend_uid", friend_uid);
            object.put("share_code", share_code);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 43、添加好友，发送通知；如果已经是还有则直接跳转到好友详情
     * @param token
     * @param uid
     * @param share_code
     * @return
     */
    public static JSONObject friend_add_set(String token,String uid,String share_code) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "friend_add_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("share_code", share_code);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 44、添加新朋友的消息列表
     * @param token
     * @param uid
     * @return
     */
    public static JSONObject friend_new_list_get(String token,String uid) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "friend_new_list_get");
            object.put("token", token);
            object.put("uid", uid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 45、删除新朋友的消息
     * @param token
     * @param uid
     * @param msg_id
     * @return
     */
    public static JSONObject friend_new_delete_get(String token,String uid,String msg_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "friend_new_delete_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("msg_id", msg_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 46、处理新朋友的消息，同意或拒绝
     * @param token
     * @param uid
     * @param msg_id
     * @param msg_status
     * @return
     */
    public static JSONObject friend_new_set(String token,String uid,String msg_id,String msg_status) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "friend_new_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("msg_id", msg_id);
            object.put("msg_status", msg_status);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 47、删除好友
     * @param token
     * @param uid
     * @param friend_uid
     * @return
     */
    public static JSONObject friend_delete_set(String token,String uid,String friend_uid) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "friend_delete_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("friend_uid", friend_uid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 48、好友备注
     * @param token
     * @param uid
     * @param friend_uid
     * @param remark
     * @return
     */
    public static JSONObject friend_remark_set(String token,String uid,String friend_uid,String remark) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "friend_remark_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("friend_uid", friend_uid);
            object.put("remark", remark);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 49、不让他看我的朋友圈动态
     * @param token
     * @param uid
     * @param friend_uid
     * @param not_see_me 不让他看我的朋友圈动态，0未选择，1选择
     * @return
     */
    public static JSONObject friend_not_see_me_set(String token,String uid,String friend_uid,String not_see_me) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "friend_not_see_me_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("friend_uid", friend_uid);
            object.put("not_see_me", not_see_me);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 50、不看他的朋友圈动态
     * @param token
     * @param uid
     * @param friend_uid
     * @param not_see_he
     * @return
     */
    public static JSONObject friend_not_see_he_set(String token,String uid,String friend_uid,String not_see_he) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "friend_not_see_he_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("friend_uid", friend_uid);
            object.put("not_see_he", not_see_he);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 53、圈子列表
     * @param token
     * @param uid
     * @param page
     * @return
     */
    public static JSONObject circle_list_get(String token,String uid,String keywords,int page) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "circle_list_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("keywords", keywords);
            object.put("page", page);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 54、圈子详情
     * @param token
     * @param uid
     * @param circle_id
     * @return
     */
    public static JSONObject circle_detail_get(String token,String uid,String circle_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "circle_detail_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("circle_id", circle_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 55、圈子文章点赞，自己可以给自己点赞
     * @param token
     * @param uid
     * @param circle_id
     * @param is_praise
     * @return
     */
    public static JSONObject circle_praise_set(String token,String uid,String circle_id,String is_praise) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "circle_praise_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("circle_id", circle_id);
            object.put("is_praise", is_praise);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 65、删除圈子
     * @param token
     * @param uid
     * @param circle_id
     * @return
     */
    public static JSONObject circle_delete_set(String token,String uid,String circle_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "circle_delete_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("circle_id", circle_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 56、不喜欢圈子文章
     * @param token
     * @param uid
     * @param circle_id
     * @param is_not_like 是否不喜欢：1不喜欢；0喜欢
     * @return
     */
    public static JSONObject circle_not_like_set(String token,String uid,String circle_id,String is_not_like) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "circle_not_like_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("circle_id", circle_id);
            object.put("is_not_like", is_not_like);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 57、圈子文章的评论
     * @param token
     * @param uid
     * @param circle_id
     * @param content
     * @return
     */
    public static JSONObject circle_comment_set(String token,String uid,String circle_id,String content) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "circle_comment_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("circle_id", circle_id);
            object.put("content", content);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 58、圈子文章的评论删除
     * @param token
     * @param uid
     * @param circle_id
     * @return
     */
    public static JSONObject circle_comment_delete_set(String token,String uid,String circle_comment_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "circle_comment_delete_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("circle_comment_id", circle_comment_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 73、圈子文章的评论的评论
     * @param token
     * @param uid
     * @param circle_comment_id
     * @param content
     * @return
     */
    public static JSONObject circle_sub_comment_set(String token,String uid,String circle_comment_id,String content) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "circle_sub_comment_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("circle_comment_id", circle_comment_id);
            object.put("content", content);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 74、圈子文章评论的点赞
     * @param token
     * @param uid
     * @param circle_comment_id
     * @param is_praise 是否点赞：1点赞；0取消点赞
     * @return
     */
    public static JSONObject circle_sub_praise_set(String token,String uid,String circle_comment_id,String is_praise) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "circle_sub_praise_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("circle_comment_id", circle_comment_id);
            object.put("is_praise", is_praise);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 59、圈子文章发布
     * @param token
     * @param uid
     * @param circle_title
     * @param files 文件：type文件类型(1、文本；2、图片；)；path文件路径，图片类型传递width宽度和height高度；
                    [{"type":"1","content":"发型不错，很漂亮"}, {"type":"2","content":"/public/upload/5847972.png","width":"750","height":"290"}]
     * @return
     */
    public static JSONObject circle_publish_set(String token,String uid,String circle_title,String files) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "circle_publish_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("circle_title", circle_title);
            object.put("files", files);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 60、收藏话题
     * @param token
     * @param uid
     * @param circle_id
     * @param is_collect
     * @return
     */
    public static JSONObject circle_collect_set(String token,String uid,String circle_id,String is_collect) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "circle_collect_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("circle_id", circle_id);
            object.put("is_collect", is_collect);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 61、收藏列表
     * @param token
     * @param uid
     * @param collect_type 收藏类型：1项目；2商品；3圈子里面的话题
     * @param page
     * @return
     */
    public static JSONObject collect_list_get(String token,String uid,String collect_type,int page) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "collect_list_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("collect_type", collect_type);
            object.put("page", page);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 62、关注列表
     * @param token
     * @param uid
     * @param attention_type
     * @param page
     * @return
     */
    public static JSONObject attention_list_get(String token,String uid,String attention_type,int page) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "attention_list_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("attention_type", attention_type);
            object.put("page", page);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 63、卡包列表
     * @param token
     * @param uid
     * @param page
     * @return card_type：1年卡；2项目卡；3充值卡，如果是年卡显示已使用天数(cost_days)和剩余天数(remain_days)，如果是项目卡显示已使用次数(cost_times)和剩余次数(remain_times)，
     *                     如果是充值卡显示已消费金额(cost_amount)和剩余金额(remain_amount)；member_end：卡到期时间；last_order：最近的消费记录
     */
    public static JSONObject card_list_get(String token,String uid,int page) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "card_list_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("page", page);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 65、卡详情列表，有效期内和过期是不一样的
     * @param token
     * @param uid
     * @param member_id
     * @return 会员卡明细；card_type：1年卡；2项目卡；3充值卡；年卡显示剩余天数remain_days；项目卡时显示剩余次数remain_times；充值卡时显示剩余金额remain_amount；member_no会员编号；
     *          会员卡在有效期内则显示服务项目item_list；会员卡已用完或过期则显示服务明细srv_list
     */
    public static JSONObject card_detail_get(String token,String uid,String member_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "card_detail_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("member_id", member_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 卡订单列表，使用会员卡下单的列表
     * @param token
     * @param uid
     * @param member_id
     * @return
     */
    public static JSONObject card_order_list_get(String token,String uid,String member_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "card_order_list_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("member_id", member_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 64、新的会员卡申请
     * @param token
     * @param uid
     * @param page
     * @param shop_id 店铺，首次进入页面没有店铺传0
     * @param card_type 卡类型：1年卡；2项目卡；3充值卡
     * @return
     */
    public static JSONObject new_card_list_get(String token,String uid,int page,String shop_id,int card_type) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "new_card_list_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("page", page);
            object.put("shop_id", shop_id);
            object.put("card_type", card_type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 65、新的会员卡详情
     * @param token
     * @param uid
     * @param card_id
     * @param shop_id
     * @return
     */
    public static JSONObject new_card_detail_get(String token,String uid,String card_id,String shop_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "new_card_detail_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("card_id", card_id);
            object.put("shop_id", shop_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 66、会员卡下单
     * @param token
     * @param uid
     * @param card_id
     * @param shop_id
     * @param member_id
     * @param member_name
     * @param member_phone
     * @param member_birthday
     * @return
     */
    public static JSONObject card_order_set(String token,String uid,String card_id,String shop_id,String member_id,String member_name,
                                            String member_phone,String member_birthday,String member_avatar) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "card_order_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("card_id", card_id);
            object.put("shop_id", shop_id);
            object.put("member_id", member_id);
            object.put("member_name", member_name);
            object.put("member_phone", member_phone);
            object.put("member_birthday", member_birthday);
            object.put("member_avatar", member_avatar);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 69、根据订单的支付方式获取支付数据
     * @param token
     * @param uid
     * @param order_no
     * @param oid
     * @param pay_type
     * @return
     */
    public static JSONObject order_pay_data_get(String token,String uid,String order_no,String oid,int pay_type) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "order_pay_data_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("order_no", order_no);
            object.put("oid", oid);
            object.put("pay_type", pay_type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 70、红包列表
     * @param token
     * @param uid
     * @param red_type 红包类型：1项目红包(项目下单)；2商品红包(商品下单)；3全部红包(我的红包)
     * @param is_history 是否历史红包：0正常；1历史(仅我的里面查看历史红包为1)
     * @param item_shop_ids 下单时的参数：店铺项目id，多个半角逗号分隔(1,2,3)，非下单传空字符串
     * @param order_amount 下单时的参数：订单金额，非下单传0
     * @param page
     * @return
     */
    public static JSONObject red_list_get(String token,String uid,int red_type,int is_history,String item_shop_ids,String order_amount,int page) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "red_list_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("red_type", red_type);
            object.put("is_history", is_history);
            object.put("item_shop_ids", item_shop_ids);
            object.put("order_amount", order_amount);
            object.put("page", page);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 71、下单专用会员列表
     * @param token
     * @param uid
     * @param item_shop_ids 店铺项目id，多个半角逗号分隔(1,2,3)
     * @param page
     * @return
     */
    public static JSONObject order_member_list_get(String token,String uid,String item_shop_ids,int page) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "order_member_list_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("item_shop_ids", item_shop_ids);
            object.put("page", page);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 72、项目下单初始化，提取下单页面的数据并计算订单的相关金额
     * @param token
     * @param uid
     * @param lat
     * @param lng
     * @param item_shop_id
     * @return round_num随机码，提交订单时使用；item_data项目数据；shop_data店铺数据；red_data红包数据；member_data会员卡数据
     *          (card_type：1年卡、2项目卡、3充值卡，年卡和项目卡直接免费，充值卡按折扣discount(为0时表示无折扣)
     *          计算订单金额，优惠金额最大为remain_amount)；order_calc_rlt订单计算结果(order_amount:订单金额;red_amount优惠券优惠金额;
     *          pay_amount待支付金额;member_discount会员卡优惠的金额;member_amount会员卡使用的余额)
     */
    public static JSONObject project_order_init_get(String token,String uid,String lat,String lng,String item_shop_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "project_order_init_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("lat", lat);
            object.put("lng", lng);
            object.put("item_shop_id", item_shop_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 97、项目下单初始化，提取下单页面的数据并计算订单的相关金额(VER2)
     * @param token
     * @param uid
     * @param lat
     * @param lng
     * @param is_box_buy 是否组合购买，0不是，1是
     * @param item_shop_data 店铺商品数据:[{"item_shop_id":"1","num":"2"},{"item_shop_id":"2","num":"5"}]，item_shop_id商品id，num为购买数量
     * @return
     */
    public static JSONObject project_order_init_new_get(String token,String uid,String lat,String lng,int is_box_buy,String item_shop_data) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "project_order_init_new_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("lat", lat);
            object.put("lng", lng);
            object.put("is_box_buy", is_box_buy);
            object.put("item_shop_data", item_shop_data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 73、项目订单计算金额，当优惠券或会员卡更改时调用该接口动态计算金额
     * @param token
     * @param uid
     * @param order_amount 订单金额
     * @param red_amount 红包金额，没有传0
     * @param card_type 会员卡类型，没有使用会员传0
     * @param discount 会员折扣，没有使用会员传0
     * @param remain_amount 会员卡剩余金额，没有使用会员传0
     * @return order_amount:订单金额;red_amount优惠券优惠金额;pay_amount待支付金额;member_discount会员卡优惠的金额;member_amount会员卡使用的余额
     */
    public static JSONObject project_order_calc_get(String token,String uid,String order_amount,String red_amount,String card_type,String discount,String remain_amount) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "project_order_calc_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("order_amount", order_amount);
            object.put("red_amount", red_amount);
            object.put("card_type", card_type);
            object.put("discount", discount);
            object.put("remain_amount", remain_amount);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 98、项目订单计算金额，当优惠券或会员卡更改时调用该接口动态计算金额(VER2)
     * @param token
     * @param uid
     * @param is_box_buy
     * @param order_amount
     * @param red_amount
     * @param member_id 会员卡id，没有会员卡传0
     * @param item_shop_data 店铺商品数据:[{"item_shop_id":"1","num":"2"},{"item_shop_id":"2","num":"5"}]，item_shop_id商品id，num为购买数量
     * @return
     */
    public static JSONObject project_order_calc_new_get(String token,String uid,int is_box_buy,String order_amount,String red_amount,
                                                        String member_id,String item_shop_data) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "project_order_calc_new_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("is_box_buy", is_box_buy);
            object.put("order_amount", order_amount);
            object.put("red_amount", red_amount);
            object.put("member_id", member_id);
            object.put("item_shop_data", item_shop_data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 74、项目下单
     * @param token
     * @param uid
     * @param item_shop_id 店铺项目id
     * @param red_id 优惠券id，没有传0
     * @param member_id 会员id，没有传0
     * @param pay_amount 需要支付的金额，大于等于0
     * @param round_num /随机编码
     * @return
     */
    public static JSONObject project_order_set(String token,String uid,String item_shop_id,String red_id,String member_id,String pay_amount,String round_num) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "project_order_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("item_shop_id", item_shop_id);
            object.put("red_id", red_id);
            object.put("member_id", member_id);
            object.put("pay_amount", pay_amount);
            object.put("round_num", round_num);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 99、项目下单(VER2)
     * @param token
     * @param uid
     * @param is_box_buy 是否组合购买，0不是，1是
     * @param item_shop_data 店铺商品数据:[{"item_shop_id":"1","num":"2"},{"item_shop_id":"2","num":"5"}]，item_shop_id商品id，num为购买数量
     * @param red_id
     * @param member_id
     * @param pay_amount
     * @param round_num
     * @return
     */
    public static JSONObject project_order_new_set(String token,String uid,int is_box_buy,int is_cart,String item_shop_data,String red_id,
                                                   String member_id,String pay_amount,String round_num) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "project_order_new_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("is_box_buy", is_box_buy);
            object.put("is_cart", is_cart);
            object.put("item_shop_data", item_shop_data);
            object.put("red_id", red_id);
            object.put("member_id", member_id);
            object.put("pay_amount", pay_amount);
            object.put("round_num", round_num);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 75、订单余额支付
     * @param token
     * @param uid
     * @param oid
     * @param order_no
     * @return
     */
    public static JSONObject order_yue_pay(String token,String uid,String oid,String order_no,String pay_pwd) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "order_yue_pay");
            object.put("token", token);
            object.put("uid", uid);
            object.put("oid", oid);
            object.put("order_no", order_no);
            object.put("pay_pwd", pay_pwd);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 76、购物车列表数据
     * @param token
     * @param uid
     * @return
     */
    public static JSONObject cart_list_get(String token,String uid) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "cart_list_get");
            object.put("token", token);
            object.put("uid", uid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 77、购物车同步数据，退出购物车页面时同步数据
     * @param token
     * @param uid
     * @param item_shop_data 店铺商品数据:[{"item_shop_id":"7","num":"2","is_check":"1"},{"item_shop_id":"8","num":"5","is_check":"2"}]，
     *                       item_shop_id商品id，num为购买数量，is_check是否勾选
     * @return
     */
    public static JSONObject cart_data_syn_set(String token,String uid,String item_shop_data,int cart_type) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "cart_data_syn_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("item_shop_data", item_shop_data);
            object.put("cart_type", cart_type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 78、添加商品数据购物车，商品详情里面操作
     * @param token
     * @param uid
     * @param item_shop_id
     * @return
     */
    public static JSONObject cart_add_set(String token,String uid,String item_shop_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "cart_add_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("item_shop_id", item_shop_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 79、购物车中勾选删除商品数据
     * @param token
     * @param uid
     * @param item_shop_ids 店铺商品id，多个逗号分隔，如：2,3,4
     * @return
     */
    public static JSONObject cart_delete_set(String token,String uid,String item_shop_ids) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "cart_delete_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("item_shop_ids", item_shop_ids);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 80、购物车移入收藏夹
     * @param token
     * @param uid
     * @param item_shop_ids
     * @return
     */
    public static JSONObject cart_to_collect_set(String token,String uid,String item_shop_ids) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "cart_to_collect_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("item_shop_ids", item_shop_ids);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 81、新增或修改地址
     * @param token
     * @param uid
     * @param address_id 地址id，如果是修改传修改的地址id，如果是新增传0
     * @param receiver 收货人
     * @param receiver_phone 收货人电话
     * @param district_name 行政区名称，如：洪山区
     * @param address_detail 详细地址
     * @param lat 纬度，不能为0
     * @param lng 经度，不能为0
     * @param is_default 是否默认地址，0非默认，1默认地址
     * @return
     */
    public static JSONObject address_set(String token,String uid,String address_id,String receiver,String receiver_phone,String district_name,
                                         String address_detail,String lat,String lng,String is_default) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "address_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("address_id", address_id);
            object.put("receiver", receiver);
            object.put("receiver_phone", receiver_phone);
            object.put("district_name", district_name);
            object.put("address_detail", address_detail);
            object.put("lat", lat);
            object.put("lng", lng);
            object.put("is_default", is_default);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 82、删除地址
     * @param token
     * @param uid
     * @param address_id
     * @return
     */
    public static JSONObject address_delete_set(String token,String uid,String address_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "address_delete_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("address_id", address_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 83、设置默认地址，设置某个地址为默认
     * @param token
     * @param uid
     * @param address_id
     * @return
     */
    public static JSONObject address_default_set(String token,String uid,String address_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "address_default_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("address_id", address_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 84、地址列表
     * @param token
     * @param uid
     * @param page
     * @return
     */
    public static JSONObject address_list_get(String token,String uid,int page) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "address_list_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("page", page);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 85、地址详情
     * @param token
     * @param uid
     * @param address_id
     * @return
     */
    public static JSONObject address_detail_get(String token,String uid,String address_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "address_detail_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("address_id", address_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 86、商品下单初始化，提取下单页面的数据并计算订单的相关金额
     * @param token
     * @param uid
     * @param item_shop_data 店铺商品数据:[{"item_shop_id":"1","num":"2"},{"item_shop_id":"2","num":"5"}]，item_shop_id商品id，num为购买数量
     * @return
     */
    public static JSONObject commodity_order_init_get(String token,String uid,String item_shop_data) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "commodity_order_init_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("item_shop_data", item_shop_data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 87、商品下单
     * @param token
     * @param uid
     * @param item_shop_data 店铺商品数据:[{"item_shop_id":"1","num":"2"},{"item_shop_id":"2","num":"5"}]，item_shop_id商品id，num为购买数量
     * @param address_id 收货地址
     * @param red_id 优惠券id，没有传0
     * @param pay_amount 需要支付的金额，大于等于0
     * @param round_num
     * @param is_cart 是否从购物车下单，0不是，1是
     * @return
     */
    public static JSONObject commodity_order_set(String token,String uid,String item_shop_data,String address_id,String red_id,
                                                 String pay_amount,String round_num,int is_cart) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "commodity_order_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("item_shop_data", item_shop_data);
            object.put("address_id", address_id);
            object.put("red_id", red_id);
            object.put("pay_amount", pay_amount);
            object.put("round_num", round_num);
            object.put("is_cart", is_cart);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 88、项目活动下单
     * @param token
     * @param uid
     * @param lat
     * @param lng
     * @param pack_id
     * @param shop_id
     * @return
     */
    public static JSONObject action_order_init_get(String token,String uid,String lat,String lng,String pack_id,String shop_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "action_order_init_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("lat", lat);
            object.put("lng", lng);
            object.put("pack_id", pack_id);
            object.put("shop_id", shop_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 89、活动下单，直接支付，不存在待支付的活动订单，未支付时不用跳转
     * @param token
     * @param uid
     * @param pack_id
     * @param shop_id
     * @param pay_amount
     * @return
     */
    public static JSONObject action_order_set(String token,String uid,String pack_id,String shop_id,String pay_amount) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "action_order_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("pack_id", pack_id);
            object.put("shop_id", shop_id);
            object.put("pay_amount", pay_amount);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 90、我的订单，显示预约订单和各种状态订单的数量
     * @param token
     * @param uid
     * @return
     */
    public static JSONObject my_order_list_get(String token,String uid) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "my_order_list_get");
            object.put("token", token);
            object.put("uid", uid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 91、项目订单列表
     * @param token
     * @param uid
     * @param status
     * @param page
     * @return
     */
    public static JSONObject project_order_list_get(String token,String uid,String status,int page) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "project_order_list_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("status", status);
            object.put("page", page);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 120、项目订单列表，一个列表可能有多个项目(VER2)
     * @param token
     * @param uid
     * @param status
     * @param page
     * @return
     */
    public static JSONObject project_order_list_new_get(String token,String uid,String status,int page) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "project_order_list_new_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("status", status);
            object.put("page", page);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 92、项目订单详情
     * @param token
     * @param uid
     * @param oid
     * @param lat
     * @param lng
     * @return
     */
    public static JSONObject project_order_detail_get(String token,String uid,String oid,String lat,String lng) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "project_order_detail_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("oid", oid);
            object.put("lat", lat);
            object.put("lng", lng);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 122、项目订单详情(VER2)
     * @param token
     * @param uid
     * @param oid
     * @param lat
     * @param lng
     * @return
     */
    public static JSONObject project_order_detail_new_get(String token,String uid,String oid,String lat,String lng) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "project_order_detail_new_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("oid", oid);
            object.put("lat", lat);
            object.put("lng", lng);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 93、删除订单，项目订单或商品订单
     * @param token
     * @param uid
     * @param oid
     * @return
     */
    public static JSONObject order_delete_set(String token,String uid,String oid) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "order_delete_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("oid", oid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 94、商品订单列表
     * @param token
     * @param uid
     * @param status 订单状态：全部、待付款、待发货、待收货、待评价
     * @param page 第几页
     * @return
     */
    public static JSONObject commodity_order_list_get(String token,String uid,String status,int page) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "commodity_order_list_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("status", status);
            object.put("page", page);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 95、商品订单详情
     * @param token
     * @param uid
     * @param oid
     * @return
     */
    public static JSONObject commodity_order_detail_get(String token,String uid,String oid) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "commodity_order_detail_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("oid", oid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 96、商品订单评价
     * @param token
     * @param uid
     * @param oid
     * @param files /图片文件，没有传空：type文件类型(2、图片；)；path文件路径，图片类型传递width宽度和height高度；
    [{"type":"2","content":"/public/upload/5847972.png","width":"750","height":"290"}]
     * @param evl_content 评价内容
     * @param shop_desc_level 描述相符评分
     * @param srv_level 服务态度评分
     * @param staff_logist_level 物流服务评分
     * @param is_hidden 是否匿名
     * @return
     */
    public static JSONObject order_commodity_evl_set(String token,String uid,String oid,String files,String evl_content,String shop_desc_level,
                                                     String srv_level,String staff_logist_level,int is_hidden) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "order_commodity_evl_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("oid", oid);
            object.put("files", files);
            object.put("evl_content", evl_content);
            object.put("shop_desc_level", shop_desc_level);
            object.put("srv_level", srv_level);
            object.put("staff_logist_level", staff_logist_level);
            object.put("is_hidden", is_hidden);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 97、商品订单 提醒发货
     * @param token
     * @param uid
     * @param oid
     * @return
     */
    public static JSONObject order_commodity_remind_set(String token,String uid,String oid) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "order_commodity_remind_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("oid", oid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }
    /**
     * 98、商品订单确认收货
     * @param token
     * @param uid
     * @param oid
     * @return
     */
    public static JSONObject order_receive_set(String token,String uid,String oid) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "order_receive_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("oid", oid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }
    /**
     * 99、订单查看物流，去掉左边的图片，已签收状态改为：订单编号。oid为23的有数据
     * @param token
     * @param uid
     * @param oid
     * @return
     */
    public static JSONObject order_logistics_get(String token,String uid,String oid) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "order_logistics_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("oid", oid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }
    /**
     * 101、售后列表：项目售后和商品售后
     * @param token
     * @param uid
     * @param order_type  1项目，2商品
     * @return
     */
    public static JSONObject refund_list_get(String token,String uid,int order_type) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "refund_list_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("order_type", order_type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 102、退款详情，修改申请功能暂时不要了
     * @param token
     * @param uid
     * @param refund_id
     * @return
     */
    public static JSONObject refund_detail_get(String token,String uid,String refund_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "refund_detail_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("refund_id", refund_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 103、撤销退款申请
     * @param token
     * @param uid
     * @param refund_id
     * @return
     */
    public static JSONObject refund_revoke_set(String token,String uid,String refund_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "refund_revoke_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("refund_id", refund_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 104、申请退款新初始化，项目和商品订单退款
     * @param token
     * @param uid
     * @param order_detail_id
     * @return
     */
    public static JSONObject order_refund_init_set(String token,String uid,String order_detail_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "order_refund_init_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("order_detail_id", order_detail_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 105、申请退款新增，项目和商品共用
     * @param token
     * @param uid
     * @param order_detail_id 订单详情id
     * @param refund_type 退款类型
     * @param refund_reason 退款原因
     * @param refund_amount 退款金额
     * @param refund_desc 退款说明
     * @param files 图片文件，没有传空：type文件类型(2、图片；)；path文件路径，图片类型传递width宽度和height高度；
    [{"type":"2","content":"/public/upload/5847972.png","width":"750","height":"290"}]
     * @return
     */
    public static JSONObject order_refund_set(String token,String uid,String order_detail_id,String refund_type,String refund_reason,
                                              String refund_amount,String refund_desc,String files) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "order_refund_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("order_detail_id", order_detail_id);
            object.put("refund_type", refund_type);
            object.put("refund_reason", refund_reason);
            object.put("refund_amount", refund_amount);
            object.put("refund_desc", refund_desc);
            object.put("files", files);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }
    /**
     * 94、订单消费码列表
     * @param token
     * @param uid
     * @return
     */
    public static JSONObject order_code2d_list_get(String token,String uid) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "order_code2d_list_get");
            object.put("token", token);
            object.put("uid", uid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 95、订单消费码详情
     * @param token
     * @param uid
     * @param oid 订单oid
     * @return
     */
    public static JSONObject order_code2d_detail_get(String token,String uid,String oid,String lat,String lng) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "order_code2d_detail_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("oid", oid);
            object.put("lat", lat);
            object.put("lng", lng);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 96、预约
     * @param token
     * @param uid
     * @param oid
     * @param code2d_id 二维码id，在订单详情里面预约传0，系统默认一个二维码进行预约
     * @param appoint_name
     * @param appoint_phone
     * @param appoint_time 预约时间，格式:2018-04-06 10:30:00，后面补到秒
     * @return
     */
    public static JSONObject appoint_code2d_set(String token,String uid,String oid,String code2d_id,String appoint_name,String appoint_phone,String appoint_time) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "appoint_code2d_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("oid", oid);
            object.put("code2d_id", code2d_id);
            object.put("appoint_name", appoint_name);
            object.put("appoint_phone", appoint_phone);
            object.put("appoint_time", appoint_time);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 96_1、预约初始化界面，未预约时调用初始化界面
     * @param token
     * @param uid
     * @param oid
     * @param code2d_id
     * @return
     */
    public static JSONObject appoint_code2d_init_get(String token,String uid,String oid,String code2d_id,String lat,String lng) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "appoint_code2d_init_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("oid", oid);
            object.put("code2d_id", code2d_id);
            object.put("lat", lat);
            object.put("lng", lng);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 97、预约详情
     * @param token
     * @param uid
     * @param oid
     * @param code2d_id
     * @return
     */
    public static JSONObject appoint_code2d_detail_get(String token,String uid,String oid,String code2d_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "appoint_code2d_detail_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("oid", oid);
            object.put("code2d_id", code2d_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 98_1、预约指定服务人员
     * @param token
     * @param uid
     * @param code2d_id
     * @return
     */
    public static JSONObject code2d_srv_list_get(String token,String uid,String code2d_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "code2d_srv_list_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("code2d_id", code2d_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 98、预约指定服务人员
     * @param token
     * @param uid
     * @param oid
     * @param code2d_id
     * @param srv_data 服务数据:[{"srv_id":"1","staff_id":"2"},{"srv_id":"2","staff_id":"5"}]srv_id服务id，staff_id服务人员id
     * @return
     */
    public static JSONObject appoint_staff_set(String token,String uid,String oid,String code2d_id,String srv_data) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "appoint_staff_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("oid", oid);
            object.put("code2d_id", code2d_id);
            object.put("srv_data", srv_data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 99、取消预约，订单里面的取消预约跳转到验证码页面，让用户选择具体需要取消的
     * @param token
     * @param uid
     * @param oid
     * @param code2d_id
     * @return
     */
    public static JSONObject appoint_cancel_set(String token,String uid,String oid,String code2d_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "appoint_cancel_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("oid", oid);
            object.put("code2d_id", code2d_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 100、评价
     * @param token
     * @param uid
     * @param oid
     * @param code2d_id
     * @param files 文件：type文件类型(2、图片；)；path文件路径，图片类型传递width宽度和height高度；
                        [{"type":"2","content":"/public/upload/5847972.png","width":"750","height":"290"}]
     * @param evl_content 评价内容
     * @param shop_desc_level 项目店铺环境评分
     * @param srv_level 服务态度评分
     * @param staff_logist_level 项目发型师评价
     * @param is_hidden 是否匿名
     * @return
     */
    public static JSONObject order_evl_set(String token,String uid,String oid,String code2d_id,String files,String evl_content,String shop_desc_level,
                                           String srv_level,String staff_logist_level,int is_hidden) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "order_evl_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("oid", oid);
            object.put("code2d_id", code2d_id);
            object.put("files", files);
            object.put("evl_content", evl_content);
            object.put("shop_desc_level", shop_desc_level);
            object.put("srv_level", srv_level);
            object.put("staff_logist_level", staff_logist_level);
            object.put("is_hidden", is_hidden);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 101、投诉
     * @param token
     * @param uid
     * @param oid
     * @param files
     * @param contact_tel
     * @param complaint_desc 投诉内容
     * @return
     */
    public static JSONObject order_complaint_set(String token,String uid,String oid,String files,String contact_tel,String complaint_desc) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "order_complaint_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("oid", oid);
            object.put("files", files);
            object.put("contact_tel", contact_tel);
            object.put("complaint_desc", complaint_desc);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 103、扫消费码，12位长
     * @param token
     * @param uid
     * @param code2d
     * @return
     */
    public static JSONObject order2d_detail_get(String token,String uid,String code2d) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "order2d_detail_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("code2d", code2d);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 104、服务单详情
     * @param token
     * @param uid
     * @param code2d_id
     * @return
     */
    public static JSONObject srv_detail_get(String token,String uid,String code2d_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "srv_detail_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("code2d_id", code2d_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 105、选择服务人员列表
     * @param token
     * @param uid
     * @param appoint_srv_id
     * @return
     */
    public static JSONObject order_staff_get(String token,String uid,String appoint_srv_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "order_staff_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("appoint_srv_id", appoint_srv_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 106、设置服务人员
     * @param token
     * @param uid
     * @param appoint_srv_id
     * @param staff_id
     * @return
     */
    public static JSONObject order_staff_set(String token,String uid,String appoint_srv_id,String staff_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "order_staff_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("appoint_srv_id", appoint_srv_id);
            object.put("staff_id", staff_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 131、在服务单详情页面开始服务，只是通知
     * @param token
     * @param uid
     * @param code2d_id
     * @return
     */
    public static JSONObject appoint_start_set(String token,String uid,String code2d_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "appoint_start_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("code2d_id", code2d_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 132、员工完成服务后跳到服务单页面，可以重新设置服务人员并通知下一个服务人员，仅通知
     * @param token
     * @param uid
     * @param code2d_id
     * @return
     */
    public static JSONObject appoint_next_set(String token,String uid,String code2d_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "appoint_next_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("code2d_id", code2d_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 127、开始服务或一个服务完成后开始下一个服务
     * @param token
     * @param uid
     * @param appoint_srv_id 开始服务的id
     * @return
     */
    public static JSONObject start_srv_set(String token,String uid,String appoint_srv_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "start_srv_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("appoint_srv_id", appoint_srv_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 108、完成一个服务
     * @param token
     * @param uid
     * @param appoint_srv_id
     * @return
     */
    public static JSONObject complete_srv_set(String token,String uid,String appoint_srv_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "complete_srv_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("appoint_srv_id", appoint_srv_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 109、系统消息列表
     * @param token
     * @param uid
     * @return
     */
    public static JSONObject system_new_list_get(String token,String uid) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "system_new_list_get");
            object.put("token", token);
            object.put("uid", uid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 110、新的服务 详情，也是服务消息的详情
     * @param token
     * @param uid
     * @param appoint_srv_id
     * @return
     */
    public static JSONObject srv_new_detail_get(String token,String uid,String appoint_srv_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "srv_new_detail_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("appoint_srv_id", appoint_srv_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 111、工作中详情
     * @param token
     * @param uid
     * @param appoint_srv_id
     * @return
     */
    public static JSONObject srv_work_detail_get(String token,String uid,String appoint_srv_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "srv_work_detail_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("appoint_srv_id", appoint_srv_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 112、工作完成详情
     * @param token
     * @param uid
     * @param appoint_srv_id
     * @return
     */
    public static JSONObject srv_work_complete_get(String token,String uid,String appoint_srv_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "srv_work_complete_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("appoint_srv_id", appoint_srv_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 113、取消工作中的服务
     * @param token
     * @param uid
     * @param appoint_srv_id
     * @return
     */
    public static JSONObject srv_cancel_set(String token,String uid,String appoint_srv_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "srv_cancel_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("appoint_srv_id", appoint_srv_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 114、删除系统消息
     * @param token
     * @param uid
     * @param msg_id
     * @return
     */
    public static JSONObject system_new_delete_get(String token,String uid,String msg_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "system_new_delete_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("msg_id", msg_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 115、我的信息获取，登陆后调用，获取最新的预约订单数据，以及工作狂和我的店铺菜单是否显示
     * @param token
     * @param uid
     * @return
     */
    public static JSONObject my_info_get(String token,String uid,int data_type) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "my_info_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("data_type", data_type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 129、我的账户
     * @param token
     * @param uid
     * @return
     */
    public static JSONObject account_info_get(String token,String uid) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "account_info_get");
            object.put("token", token);
            object.put("uid", uid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 130、我的账户明细列表
     * @param token
     * @param uid
     * @param balance_id 账户id
     * @param data_type 数据类型：1全部；2收入；3支出
     * @param page 第几页
     * @return
     */
    public static JSONObject account_detail_get(String token,String uid,String balance_id,String data_type,int page) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "account_detail_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("balance_id", balance_id);
            object.put("data_type", data_type);
            object.put("page", page);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 131、账户提现申请初始化
     * @param token
     * @param uid
     * @param balance_id
     * @return
     */
    public static JSONObject account_apply_init_get(String token,String uid,String balance_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "account_apply_init_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("balance_id", balance_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 132、绑定微信或支付宝账户
     * @param token
     * @param uid
     * @param balance_id
     * @param account_type 绑定的账户类型：1支付宝；2微信
     * @param real_name 真实姓名
     * @param account 支付宝账户或微信账户
     * @return
     */
    public static JSONObject bind_account_set(String token,String uid,String balance_id,String account_type,String real_name,String account) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "bind_account_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("balance_id", balance_id);
            object.put("account_type", account_type);
            object.put("real_name", real_name);
            object.put("account", account);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 133、账户提现申请提交
     * @param token
     * @param uid
     * @param balance_id
     * @param account_type 绑定的账户类型：1支付宝；2微信
     * @param apply_amount 申请金额
     * @param sms_id 验证码的id
     * @param sms_code
     * @return
     */
    public static JSONObject account_apply_set(String token,String uid,String balance_id,String account_type,String apply_amount,String sms_id,String sms_code) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "account_apply_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("balance_id", balance_id);
            object.put("account_type", account_type);
            object.put("apply_amount", apply_amount);
            object.put("sms_id", sms_id);
            object.put("sms_code", sms_code);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 134、账户充值提交，提交后调用统一的充值参数获取接口order_pay_data_get
     * @param token
     * @param uid
     * @param balance_id
     * @param recharge_amount
     * @return
     */
    public static JSONObject account_recharge_set(String token,String uid,String balance_id,String recharge_amount) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "account_recharge_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("balance_id", balance_id);
            object.put("recharge_amount", recharge_amount);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 116、我的服务-正在服务列表
     * @param token
     * @param uid
     * @return
     */
    public static JSONObject s_being_srv_get(String token,String uid) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_being_srv_get");
            object.put("token", token);
            object.put("uid", uid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 117、我的服务-已完成的服务列表
     * @param token
     * @param uid
     * @return
     */
    public static JSONObject s_completed_srv_get(String token,String uid) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_completed_srv_get");
            object.put("token", token);
            object.put("uid", uid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 118、我的预约服务
     * @param token
     * @param uid
     * @param date
     * @return
     */
    public static JSONObject s_appoint_srv_get(String token,String uid,int is_admin,String shop_id,String date) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_appoint_srv_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("is_admin", is_admin);
            object.put("shop_id", shop_id);
            object.put("date", date);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 119、店铺列表，只有商户、店主和岗位有权限的人才有
     * @param token
     * @param uid
     * @param page
     * @return
     */
    public static JSONObject s_shop_list_get(String token,String uid,int page) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_shop_list_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("page", page);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 121、获取店铺数据
     * @param token
     * @param uid
     * @param shop_id
     * @return
     */
    public static JSONObject s_shop_detail_get(String token,String uid,String shop_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_shop_detail_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("shop_id", shop_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 122、新增或修改店铺数据，只有商家可以增加修改店铺，只有店铺管理员可以修改店铺内容
     * @param token
     * @param uid
     * @param shop_id
     * @param shop_cover 封面，未修改时传空，不要传含http的路径
     * @param shop_logo logo，未修改时传空，不要传含http的路径
     * @param shop_name 店铺名称
     * @param shop_tel 店铺电话
     * @param district_name 店铺区域
     * @param address_detail 店铺详细地址
     * @param lat 纬度，不能为0
     * @param lng 经度，不能为0
     * @param service_phone 客服手机号
     * @param admin_phone 管理员手机号
     * @return
     */
    public static JSONObject s_shop_data_set(String token,String uid,String shop_id,String shop_cover,String shop_logo,String shop_name,String shop_tel,
                                             String district_name,String address_detail,String lat,String lng,String service_phone,String admin_phone,String fullcutset) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_shop_data_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("shop_id", shop_id);
            object.put("shop_cover", shop_cover);
            object.put("shop_logo", shop_logo);
            object.put("shop_name", shop_name);
            object.put("shop_tel", shop_tel);
            object.put("district_name", district_name);
            object.put("address_detail", address_detail);
            object.put("lat", lat);
            object.put("lng", lng);
            object.put("service_phone", service_phone);
            object.put("admin_phone", admin_phone);
            object.put("fullcutset", fullcutset);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 123、删除店铺
     * @param token
     * @param uid
     * @param shop_id
     * @return
     */
    public static JSONObject s_shop_delete_set(String token,String uid,String shop_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_shop_delete_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("shop_id", shop_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 124、店铺轮播图
     * @param token
     * @param uid
     * @param shop_id
     * @return
     */
    public static JSONObject s_shop_adv_get(String token,String uid,String shop_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_shop_adv_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("shop_id", shop_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 125、店铺轮播图新增修改
     * @param token
     * @param uid
     * @param shop_id
     * @param shop_adv_id 广告id，新增时传0
     * @param adv_img 广告图片，未修改时传空
     * @param adv_type 轮播图类型：1发型师，2项目；3商品；4链接
     * @param adv_value 对应的id或链接
     * @return
     */
    public static JSONObject s_shop_adv_set(String token,String uid,String shop_id,String shop_adv_id,String adv_img,int adv_type,String adv_value) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_shop_adv_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("shop_id", shop_id);
            object.put("shop_adv_id", shop_adv_id);
            object.put("adv_img", adv_img);
            object.put("adv_type", adv_type);
            object.put("adv_value", adv_value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 126、店铺轮播图删除
     * @param token
     * @param uid
     * @param shop_adv_id
     * @return
     */
    public static JSONObject s_shop_adv_delete_set(String token,String uid,String shop_adv_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_shop_adv_delete_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("shop_adv_id", shop_adv_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 127、店铺员工选择，轮播图设置、首页发型师等的选择数据
     * @param token
     * @param uid
     * @param shop_id
     * @return
     */
    public static JSONObject s_shop_staff_sel_get(String token,String uid,String shop_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_shop_staff_sel_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("shop_id", shop_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 128、店铺项目列表。供选择
     * @param token
     * @param uid
     * @param shop_id
     * @return
     */
    public static JSONObject s_shop_project_sel_get(String token,String uid,String shop_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_shop_project_sel_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("shop_id", shop_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 129、店铺商品列表，供选择
     * @param token
     * @param uid
     * @param shop_id
     * @return
     */
    public static JSONObject s_shop_commodity_sel_get(String token,String uid,String shop_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_shop_commodity_sel_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("shop_id", shop_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 130、明星发型师列表
     * @param token
     * @param uid
     * @param shop_id
     * @return
     */
    public static JSONObject s_shop_famous_staff_get(String token,String uid,String shop_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_shop_famous_staff_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("shop_id", shop_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 131、明星发型师新增
     * @param token
     * @param uid
     * @param shop_id
     * @param staff_id
     * @return
     */
    public static JSONObject s_shop_famous_set(String token,String uid,String shop_id,String staff_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_shop_famous_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("shop_id", shop_id);
            object.put("staff_id", staff_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 132、删除明星发型师
     * @param token
     * @param uid
     * @param shop_staff_id
     * @return
     */
    public static JSONObject s_shop_famous_delete_set(String token,String uid,String shop_staff_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_shop_famous_delete_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("shop_staff_id", shop_staff_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 133、热门项目列表
     * @param token
     * @param uid
     * @param shop_id
     * @return
     */
    public static JSONObject s_shop_hot_commodity_get(String token,String uid,String shop_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_shop_hot_commodity_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("shop_id", shop_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 134、热门项目新增
     * @param token
     * @param uid
     * @param shop_hot_item_id
     * @param hot_item_img
     * @param item_shop_id
     * @return
     */
    public static JSONObject s_shop_hot_set(String token,String uid,String shop_hot_item_id,String hot_item_img,String item_shop_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_shop_hot_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("shop_hot_item_id", shop_hot_item_id);
            object.put("hot_item_img", hot_item_img);
            object.put("item_shop_id", item_shop_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 135、删除热门项目
     * @param token
     * @param uid
     * @param shop_hot_item_id
     * @return
     */
    public static JSONObject s_shop_hot_delete_set(String token,String uid,String shop_hot_item_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_shop_hot_delete_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("shop_hot_item_id", shop_hot_item_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 135、我要开店，所有参数都不允许为空
     * @param token
     * @param uid
     * @param img_yyzz 营业执照
     * @param img_yyzz_sc 手持营业执照
     * @param img_sfz 身份证
     * @param img_sfz_fm 手持营业执照
     * @param shop_name 店铺名称
     * @param shop_contact 店铺联系人
     * @param shop_tel 店铺联系电话
     * @param shop_address 店铺所在地
     * @return
     */
    public static JSONObject open_shop_apply_set(String token,String uid,String img_yyzz,String img_yyzz_sc,String img_sfz,String img_sfz_fm,
                                                 String shop_name,String shop_contact,String shop_tel,String shop_address) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "open_shop_apply_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("img_yyzz", img_yyzz);
            object.put("img_yyzz_sc", img_yyzz_sc);
            object.put("img_sfz", img_sfz);
            object.put("img_sfz_fm", img_sfz_fm);
            object.put("shop_name", shop_name);
            object.put("shop_contact", shop_contact);
            object.put("shop_tel", shop_tel);
            object.put("shop_address", shop_address);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 136、搜索初始化，获取热门关键词，做多不超过10个
     * @param token
     * @param uid
     * @return
     */
    public static JSONObject search_keyword_init_get(String token,String uid) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "search_keyword_init_get");
            object.put("token", token);
            object.put("uid", uid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 137、搜索
     * @param token
     * @param uid
     * @param keyword 搜索词
     * @param data_type 数据类型：0首次进入页面；1门店；2发型师；3项目；4商品
     * @param page
     * @return
     */
    public static JSONObject search_keyword_get(String token,String uid,String keyword,String data_type,int page) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "search_keyword_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("keyword", keyword);
            object.put("data_type", data_type);
            object.put("page", page);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 136、岗位模板列表，只允许商家和店铺管理设置
     * @param token
     * @param uid
     * @return
     */
    public static JSONObject s_post_temp_get(String token,String uid) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_post_temp_get");
            object.put("token", token);
            object.put("uid", uid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 137、岗位模板详情
     * @param token
     * @param uid
     * @param post_temp_id
     * @return
     */
    public static JSONObject s_post_temp_detail_get(String token,String uid,String post_temp_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_post_temp_detail_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("post_temp_id", post_temp_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 138、岗位模板详情
     * @param token
     * @param uid
     * @param post_id
     * @return
     */
    public static JSONObject s_post_detail_get(String token,String uid,String post_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_post_detail_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("post_id", post_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 139、权限列表，供选择
     * @param token
     * @param uid
     * @param post_id 权限id，已经选择了的，返回时会勾选
     * @return
     */
    public static JSONObject s_func_list_get(String token,String uid,String post_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_func_list_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("post_id", post_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 140、服务列表，供选择
     * @param token
     * @param uid
     * @param post_id
     * @return
     */
    public static JSONObject s_srv_list_get(String token,String uid,String post_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_srv_list_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("post_id", post_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 141、新增修改岗位模板
     * @param token
     * @param uid
     * @param post_temp_id 岗位模板id
     * @param post_temp_name 岗位模板名称
     * @return
     */
    public static JSONObject s_post_temp_set(String token,String uid,String post_temp_id,String post_temp_name) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_post_temp_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("post_temp_id", post_temp_id);
            object.put("post_temp_name", post_temp_name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 142、往岗位模板里面新增或修改岗位
     * @param token
     * @param uid
     * @param post_id
     * @param post_name
     * @param post_temp_id
     * @return
     */
    public static JSONObject s_post_set(String token,String uid,String post_id,String post_name,String post_temp_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_post_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("post_id", post_id);
            object.put("post_name", post_name);
            object.put("post_temp_id", post_temp_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 144、删除模板，没有店铺使用时才允许删除
     * @param token
     * @param uid
     * @return
     */
    public static JSONObject s_post_temp_delete_set(String token,String uid,String post_temp_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_post_temp_delete_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("post_temp_id", post_temp_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 143、岗位基本工资设置
     * @param token
     * @param uid
     * @param post_id 岗位id
     * @param wage_base 岗位基本工资
     * @return
     */
    public static JSONObject s_post_base_wage_set(String token,String uid,String post_id,String wage_base) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_post_base_wage_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("post_id", post_id);
            object.put("wage_base", wage_base);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 146、我的店铺菜单
     * @param token
     * @param uid
     * @return
     */
    public static JSONObject s_shop_menu_get(String token,String uid) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_shop_menu_get");
            object.put("token", token);
            object.put("uid", uid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 146、解除绑定，服务端和客户端共用
     * @param token
     * @param uid
     * @param balance_id //账户id
     * @param account_type //绑定的账户类型：1支付宝；2微信
     * @return
     */
    public static JSONObject remove_account_set(String token,String uid,String balance_id,String account_type) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "remove_account_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("balance_id", balance_id);
            object.put("account_type", account_type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 147、删除岗位，岗位没有员工时才允许删除
     * @param token
     * @param uid
     * @return
     */
    public static JSONObject s_post_delete_set(String token,String uid,String post_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_post_delete_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("post_id", post_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 149、岗位模板适用店铺列表
     * @param token
     * @param uid
     * @return
     */
    public static JSONObject s_temp_shop_get(String token,String uid,String post_temp_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_temp_shop_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("post_temp_id", post_temp_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 149、岗位详情设置-权限
     * @param token
     * @param uid
     * @param func_ids //权限id，多个逗号分隔
     * @return
     */
    public static JSONObject s_post_detail_func_set(String token,String uid,String post_id,String func_ids) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_post_detail_func_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("post_id", post_id);
            object.put("func_ids", func_ids);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 150、岗位详情设置-服务
     * @param token
     * @param uid
     * @return
     */
    public static JSONObject s_post_detail_srv_set(String token,String uid,String post_id,String srv_ids) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_post_detail_srv_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("post_id", post_id);
            object.put("srv_ids", srv_ids);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 151、废弃（岗位详情设置-工资）
     * @param token
     * @param uid
     * @param wage_data //工资，价格阶梯是有顺序的:[{"srv_id":"1", "is_num":"2","calc_type":"1","detail":[{"condition":"5","commission":"1"},{"condition":"8","commission":"5"}]}, {"srv_id":"2", "is_num":"1","calc_type":"1","detail":[{"condition":"5","commission":"1"},{"condition":"8","commission":"5"}]}]}
     * @return
     */
    public static JSONObject s_post_detail_wage_set_feiqi(String token,String uid,String post_id,String wage_data) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_post_detail_wage_set_feiqi");
            object.put("token", token);
            object.put("uid", uid);
            object.put("post_id", post_id);
            object.put("wage_data", wage_data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 152、岗位详情设置-工资，单独设置
     * @param token
     * @param uid
     * @param post_id //岗位id
     * @param wage_id //工资id
     * @param srv_id //服务id
     * @param is_num //计费方式: 1按次数计费，2按比例计费
     * @param calc_type //计算方式：1累进计算；2累计计算
     * @param detail //工资，价格阶梯是有顺序的:[{"condition":"5","commission":"1"},{"condition":"8","commission":"5"}]
     * @return
     */
    public static JSONObject s_post_detail_wage_set(String token,String uid,String post_id,String wage_id,String srv_id,int is_num,int calc_type,String detail) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_post_detail_wage_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("post_id", post_id);
            object.put("wage_id", wage_id);
            object.put("srv_id", srv_id);
            object.put("is_num", is_num);
            object.put("calc_type", calc_type);
            object.put("detail", detail);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }


    /**
     * 150、岗位模板中删除某个门店，那么门店里面所有员工的岗位也同时被清空，删除时要提示确认
     * @param token
     * @param uid
     * @param shop_id
     * @param post_temp_id
     * @return
     */
    public static JSONObject s_temp_shop_delete_set(String token,String uid,String shop_id,String post_temp_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_temp_shop_delete_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("shop_id", shop_id);
            object.put("post_temp_id", post_temp_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 151、岗位模板适用店铺选择列表，店铺只能是自己有权限的店铺
     * @param token
     * @param uid
     * @param post_temp_id
     * @return
     */
    public static JSONObject s_temp_shop_sel_get(String token,String uid,String post_temp_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_temp_shop_sel_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("post_temp_id", post_temp_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 152、岗位模板适用店铺
     * @param token
     * @param uid
     * @param post_temp_id
     * @param shop_ids 店铺id，多个店铺用逗号分隔，如：1,2,3
     * @return
     */
    public static JSONObject s_temp_shop_set(String token,String uid,String post_temp_id,String shop_ids) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_temp_shop_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("post_temp_id", post_temp_id);
            object.put("shop_ids", shop_ids);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 153、员工管理
     * @param token
     * @param uid
     * @param shop_id 店铺id，0表示全部店铺
     * @return
     */
    public static JSONObject s_staff_list_get(String token,String uid,String shop_id,int page) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_staff_list_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("shop_id", shop_id);
            object.put("page", page);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 154、扫码加入店铺，返回界面初始化数据，普通用户扫店铺的二维码(长度8位)升级加入店铺，如果是不是店铺员工显示申请信息，已是店铺员工给出提示
     * @param token
     * @param uid
     * @param shop_code
     * @return
     */
    public static JSONObject s_staff_code_get(String token,String uid,String shop_code) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_staff_code_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("shop_code", shop_code);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 155、提交加入店铺申请
     * @param token
     * @param uid
     * @param shop_id
     * @return
     */
    public static JSONObject s_staff_apply_set(String token,String uid,String shop_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_staff_apply_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("shop_id", shop_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 156、员工审核列表
     * @param token
     * @param uid
     * @param shop_id 店铺id，0表示全部店铺
     * @return
     */
    public static JSONObject s_staff_apply_list_get(String token,String uid,String shop_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_staff_apply_list_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("shop_id", shop_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 157、删除审核信息
     * @param token
     * @param uid
     * @param staff_apply_id 员工申请记录
     * @return
     */
    public static JSONObject s_staff_apply_delete_set(String token,String uid,String staff_apply_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_staff_apply_delete_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("staff_apply_id", staff_apply_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 120、通过店铺过滤列表，很多页面共用的顶部条件。只有有权限的店铺才会展示出来
     * @param token
     * @param uid
     * @param is_all
     * @return 是否包含全部店铺的选项，0不包含，1包含
     */
    public static JSONObject s_select_shop_list_get(String token,String uid,int is_all) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_select_shop_list_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("is_all", is_all);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 158、岗位列表，通用接口，通过店铺过滤岗位
     * @param token
     * @param uid
     * @param shop_id
     * @return
     */
    public static JSONObject s_select_post_list_get(String token,String uid,String shop_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_select_post_list_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("shop_id", shop_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 159、员工审核-同意(店铺不能选择，申请加入的时候已经定了)
     * @param token
     * @param uid
     * @param staff_apply_id 员工申请记录
     * @param staff_avatar 员工头像
     * @param staff_name 员工姓名
     * @param card_id 员工身份证
     * @param post_id 员工岗位id
     * @param shop_entry_time 入职时间
     * @param staff_intro 员工简介
     * @param staff_memo 员工备注
     * @return
     */
    public static JSONObject s_staff_agree_set(String token,String uid,String staff_apply_id,String staff_avatar,String staff_name,String card_id,
                                               String post_id,String shop_entry_time,String staff_intro,String staff_memo) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_staff_agree_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("staff_apply_id", staff_apply_id);
            object.put("staff_avatar", staff_avatar);
            object.put("staff_name", staff_name);
            object.put("card_id", card_id);
            object.put("post_id", post_id);
            object.put("shop_entry_time", shop_entry_time);
            object.put("staff_intro", staff_intro);
            object.put("staff_memo", staff_memo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 160、员工详情，查看后修改
     * @param token
     * @param uid
     * @param staff_id
     * @return
     */
    public static JSONObject s_staff_info_get(String token,String uid,String staff_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_staff_info_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("staff_id", staff_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 161、员工信息修改
     * @param token
     * @param uid
     * @param staff_id
     * @param shop_id
     * @param staff_avatar
     * @param staff_name
     * @param card_id
     * @param post_id
     * @param shop_entry_time
     * @param staff_intro
     * @param staff_memo
     * @return
     */
    public static JSONObject s_staff_info_set(String token,String uid,String staff_id,String shop_id,String staff_avatar,String staff_name,String card_id,
                                               String post_id,String shop_entry_time,String staff_intro,String staff_memo) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_staff_info_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("staff_id", staff_id);
            object.put("staff_avatar", staff_avatar);
            object.put("shop_id", shop_id);
            object.put("staff_name", staff_name);
            object.put("card_id", card_id);
            object.put("post_id", post_id);
            object.put("shop_entry_time", shop_entry_time);
            object.put("staff_intro", staff_intro);
            object.put("staff_memo", staff_memo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 162、员工审核-拒绝
     * @param token
     * @param uid
     * @param staff_apply_id
     * @return
     */
    public static JSONObject s_staff_reject_set(String token,String uid,String staff_apply_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_staff_reject_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("staff_apply_id", staff_apply_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 163、员工删除
     * @param token
     * @param uid
     * @param staff_id
     * @return
     */
    public static JSONObject s_staff_delete_set(String token,String uid,String staff_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_staff_delete_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("staff_id", staff_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 164、项目管理列表
     * @param token
     * @param uid
     * @param shop_id 店铺id，0表示全部店铺
     * @param page
     * @param project_type 项目类型，多个逗号分隔(1促销项目；2单次项目；3单项套餐；4多选套餐)
     * @param other_type 其他类型(1上架；2下架；3置顶项目；4非置顶项目)
     * @return
     */
    public static JSONObject s_projmgr_list_get(String token,String uid,String shop_id,int page,String project_type,String other_type) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_projmgr_list_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("shop_id", shop_id);
            object.put("page", page);
            object.put("project_type", project_type);
            object.put("other_type", other_type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 168、项目管理-置顶项目
     * @param token
     * @param uid
     * @param item_id
     * @param is_top 是否置顶，0取消置顶；1置顶
     * @return
     */
    public static JSONObject s_projmgr_top_set(String token,String uid,String item_id,String is_top) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_projmgr_top_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("item_id", item_id);
            object.put("is_top", is_top);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 169、项目管理-上下架项目
     * @param token
     * @param uid
     * @param item_id
     * @param shelf_status 1上架项目；2下架项目
     * @return
     */
    public static JSONObject s_projmgr_shelf_set(String token,String uid,String item_id,String shelf_status) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_projmgr_shelf_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("item_id", item_id);
            object.put("shelf_status", shelf_status);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 170、项目管理-删除项目
     * @param token
     * @param uid
     * @param item_id
     * @return
     */
    public static JSONObject s_projmgr_delete_set(String token,String uid,String item_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_projmgr_delete_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("item_id", item_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 171、新增或修改项目或商品分类
     * @param token
     * @param uid
     * @param catalog_id 分类id，新增时传0
     * @param catalog_type 1项目分类；2商品分类
     * @param catalog_name 分类名称
     * @param catalog_img 分类图片，修改时传空或原始链接
     * @return
     */
    public static JSONObject s_catalog_set(String token,String uid,String catalog_id,String catalog_type,String catalog_name,String catalog_img) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_catalog_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("catalog_id", catalog_id);
            object.put("catalog_type", catalog_type);
            object.put("catalog_name", catalog_name);
            object.put("catalog_img", catalog_img);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 172、删除项目或商品分类
     * @param token
     * @param uid
     * @param catalog_id
     * @return
     */
    public static JSONObject s_catalog_delete_set(String token,String uid,String catalog_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_catalog_delete_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("catalog_id", catalog_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 173、项目和商品分类，供选择
     * @param token
     * @param uid
     * @param catalog_type
     * @return
     */
    public static JSONObject s_catalog_sel_get(String token,String uid,String catalog_type) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_catalog_sel_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("catalog_type", catalog_type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 174、服务列表，公用接口
     * @param token
     * @param uid
     * @return
     */
    public static JSONObject s_srv_sel_get(String token,String uid) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_srv_sel_get");
            object.put("token", token);
            object.put("uid", uid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 175、项目管理-单次项目、单项套餐、多项套餐详情，三种类型公用一个接口
     * @param token
     * @param uid
     * @param item_id
     * @return
     */
    public static JSONObject s_projmgr_detail_get(String token,String uid,String item_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_projmgr_detail_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("item_id", item_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 176、项目管理-单次项目
     * @param token
     * @param uid
     * @param item_id 项目id，如果新增传0
     * @param item_img 项目图片
     * @param item_name 项目名
     * @param item_desc 描述
     * @param catalog_id 分类id
     * @param srv_ids 包含服务，多个逗号分隔，且有先后顺序之分，如：1,2,3
     * @param shop_ids 应用店铺，多个逗号分隔，如：1,2,3
     * @param cost_price 成本
     * @param price 现价
     * @param market_price 市场价
     * @param promotion_begin_time 促销开始时间
     * @param promotion_end_time 促销结束时间
     * @param promotion_price 促销价格
     * @param project_type 项目类型：1小项目；2大项目
     * @param files 详情，文字和图片：type文件类型(1、文本；2、图片；)；path文件路径，图片类型传递width宽度和height高度；
    [{"type":"1","content":"发型不错，很漂亮"}, {"type":"2","content":"/public/upload/5847972.png","width":"750","height":"290"}]
     * @return
     */
    public static JSONObject s_projmgr_once_set(String token,String uid,String item_id,String item_img,String item_name,String item_desc,String catalog_id,
                                                String srv_ids,String shop_ids,String cost_price,String price,String market_price,String promotion_begin_time,
                                                String promotion_end_time,String promotion_price,String project_type,String files) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_projmgr_once_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("item_id", item_id);
            object.put("item_img", item_img);
            object.put("item_name", item_name);
            object.put("item_desc", item_desc);
            object.put("catalog_id", catalog_id);
            object.put("srv_ids", srv_ids);
            object.put("shop_ids", shop_ids);
            object.put("cost_price", cost_price);
            object.put("price", price);
            object.put("market_price", market_price);
            object.put("promotion_begin_time", promotion_begin_time);
            object.put("promotion_end_time", promotion_end_time);
            object.put("promotion_price", promotion_price);
            object.put("project_type", project_type);
            object.put("files", files);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 177、项目管理-单项套餐
     * @param token
     * @param uid
     * @param item_id 项目id，如果新增传0
     * @param item_img 项目图片
     * @param item_name 项目名
     * @param item_desc 描述
     * @param catalog_id 分类id
     * @param srv_ids 包含服务，多个逗号分隔，且有先后顺序之分，如：1,2,3
     * @param shop_ids 应用店铺，多个逗号分隔，如：1,2,3
     * @param cost_price 成本
     * @param price 现价
     * @param market_price 市场价
     * @param promotion_begin_time 促销开始时间
     * @param promotion_end_time 促销结束时间
     * @param promotion_price 促销价格
     * @param project_type 项目类型：1小项目；2大项目
     * @param files 详情，文字和图片：type文件类型(1、文本；2、图片；)；path文件路径，图片类型传递width宽度和height高度；
    [{"type":"1","content":"发型不错，很漂亮"}, {"type":"2","content":"/public/upload/5847972.png","width":"750","height":"290"}]
     * @return
     */
    public static JSONObject s_projmgr_single_set(String token,String uid,String item_id,String item_img,String item_name,String item_desc,String catalog_id,
                                                String srv_ids,String shop_ids,String srv_num,String cost_price,String price,String market_price,String promotion_begin_time,
                                                String promotion_end_time,String promotion_price,String project_type,String files) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_projmgr_single_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("item_id", item_id);
            object.put("item_img", item_img);
            object.put("item_name", item_name);
            object.put("item_desc", item_desc);
            object.put("catalog_id", catalog_id);
            object.put("srv_ids", srv_ids);
            object.put("shop_ids", shop_ids);
            object.put("srv_num", srv_num);
            object.put("cost_price", cost_price);
            object.put("price", price);
            object.put("market_price", market_price);
            object.put("promotion_begin_time", promotion_begin_time);
            object.put("promotion_end_time", promotion_end_time);
            object.put("promotion_price", promotion_price);
            object.put("project_type", project_type);
            object.put("files", files);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 178、多店铺项目列表。供选择，店铺列表接口：s_select_shop_list_get
     * @param token
     * @param uid
     * @param shop_ids
     * @return
     */
    public static JSONObject s_multi_project_sel_get(String token,String uid,String item_id,String shop_ids) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_multi_project_sel_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("item_id", item_id);
            object.put("shop_ids", shop_ids);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 179、项目管理-多项套餐
     * @param token
     * @param uid
     * @param item_id 项目id，如果新增传0
     * @param item_img 项目图片
     * @param item_name 项目名
     * @param item_desc 描述
     * @param catalog_id 分类id
     * @param srv_ids 包含服务，多个逗号分隔，且有先后顺序之分，如：1,2,3
     * @param shop_ids 应用店铺，多个逗号分隔，如：1,2,3
     * @param cost_price 成本
     * @param price 现价
     * @param market_price 市场价
     * @param promotion_begin_time 促销开始时间
     * @param promotion_end_time 促销结束时间
     * @param promotion_price 促销价格
     * @param project_type 项目类型：1小项目；2大项目
     * @param files 详情，文字和图片：type文件类型(1、文本；2、图片；)；path文件路径，图片类型传递width宽度和height高度；
    [{"type":"1","content":"发型不错，很漂亮"}, {"type":"2","content":"/public/upload/5847972.png","width":"750","height":"290"}]
     * @return
     */
    public static JSONObject s_projmgr_multi_set(String token,String uid,String item_id,String item_img,String item_name,String item_desc,String catalog_id,
                                                String srv_ids,String shop_ids,String sub_item_ids,String cost_price,String price,String market_price,String promotion_begin_time,
                                                String promotion_end_time,String promotion_price,String project_type,String files) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_projmgr_multi_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("item_id", item_id);
            object.put("item_img", item_img);
            object.put("item_name", item_name);
            object.put("item_desc", item_desc);
            object.put("catalog_id", catalog_id);
            object.put("srv_ids", srv_ids);
            object.put("shop_ids", shop_ids);
            object.put("sub_item_ids", sub_item_ids);
            object.put("cost_price", cost_price);
            object.put("price", price);
            object.put("market_price", market_price);
            object.put("promotion_begin_time", promotion_begin_time);
            object.put("promotion_end_time", promotion_end_time);
            object.put("promotion_price", promotion_price);
            object.put("project_type", project_type);
            object.put("files", files);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 180、商品管理列表
     * @param token
     * @param uid
     * @param shop_id 店铺id，0表示全部店铺
     * @param commodity_type 商品类型，多个逗号分隔(1促销项目；2普通商品)
     * @param other_type 其他类型(1上架；2下架；3置顶项目；4非置顶项目)
     * @param page
     * @return
     */
    public static JSONObject s_commgr_list_get(String token,String uid,String shop_id,String commodity_type,String other_type,int page) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_commgr_list_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("shop_id", shop_id);
            object.put("commodity_type", commodity_type);
            object.put("other_type", other_type);
            object.put("page", page);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 181、商品管理-置顶商品
     * @param token
     * @param uid
     * @param item_id
     * @param is_top 是否置顶，0取消置顶；1置顶
     * @return
     */
    public static JSONObject s_commgr_top_set(String token,String uid,String item_id,int is_top) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_commgr_top_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("item_id", item_id);
            object.put("is_top", is_top);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 182、商品管理-上下架商品
     * @param token
     * @param uid
     * @param item_id
     * @param shelf_status 1上架项目；2下架项目
     * @return
     */
    public static JSONObject s_commgr_shelf_set(String token,String uid,String item_id,int shelf_status) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_commgr_shelf_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("item_id", item_id);
            object.put("shelf_status", shelf_status);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 183、商品管理-删除商品
     * @param token
     * @param uid
     * @param item_id
     * @return
     */
    public static JSONObject s_commgr_delete_set(String token,String uid,String item_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_commgr_delete_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("item_id", item_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 187、商品管理-商品详情
     * @param token
     * @param uid
     * @param item_id
     * @return
     */
    public static JSONObject s_commgr_detail_get(String token,String uid,String item_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_commgr_detail_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("item_id", item_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 188、商品管理-商品新增或修改
     * @param token
     * @param uid//用户id，未登录时传0
     * @param item_id//商品id，如果新增传0
     * @param item_img
     * @param item_name
     * @param item_specification
     * @param catalog_id//分类id
     * @param shop_ids//应用店铺，多个逗号分隔，如：1,2,3
     * @param cost_price
     * @param price
     * @param market_price
     * @param promotion_begin_time
     * @param promotion_end_time
     * @param promotion_price
     * @param files  //详情，文字和图片：type文件类型(1、文本；2、图片；)；path文件路径，图片类型传递width宽度和height高度；
                    [{"type":"1","content":"发型不错，很漂亮"}, {"type":"2","content":"/public/upload/5847972.png","width":"750","height":"290"}]
     * @return
     */
    public static JSONObject s_commgr_set(String token,String uid,String item_id,String item_img,String item_name,String item_specification,String catalog_id,
                                          String shop_ids,String cost_price,String price,String market_price,String promotion_begin_time,String promotion_end_time,
                                          String promotion_price,String files) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_commgr_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("item_id", item_id);
            object.put("item_img", item_img);
            object.put("item_name", item_name);
            object.put("item_specification", item_specification);
            object.put("catalog_id", catalog_id);
            object.put("shop_ids", shop_ids);
            object.put("cost_price", cost_price);
            object.put("price", price);
            object.put("market_price", market_price);
            object.put("promotion_begin_time", promotion_begin_time);
            object.put("promotion_end_time", promotion_end_time);
            object.put("promotion_price", promotion_price);
            object.put("files", files);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 185、项目管理-单项套餐
     * @param token
     * @param uid
     * @param item_id 项目id，如果新增传0
     * @param item_img 项目图片
     * @param item_name 项目名
     * @param item_desc 描述
     * @param catalog_id 分类id
     * @param srv_ids 包含服务，多个逗号分隔，且有先后顺序之分，如：1,2,3
     * @param shop_ids 应用店铺，多个逗号分隔，如：1,2,3
     * @param cost_price 成本
     * @param price 现价
     * @param market_price 市场价
     * @param promotion_begin_time 促销开始时间
     * @param promotion_end_time 促销结束时间
     * @param promotion_price 促销价格
     * @param files 详情，文字和图片：type文件类型(1、文本；2、图片；)；path文件路径，图片类型传递width宽度和height高度；
    [{"type":"1","content":"发型不错，很漂亮"}, {"type":"2","content":"/public/upload/5847972.png","width":"750","height":"290"}]
     * @return
     */
    public static JSONObject s_commgr_single_set(String token,String uid,String item_id,String item_img,String item_name,String item_desc,String catalog_id,
                                                 String srv_ids,String shop_ids,String cost_price,String price,String market_price,String promotion_begin_time,
                                                 String promotion_end_time,String promotion_price,String files) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_commgr_single_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("item_id", item_id);
            object.put("item_img", item_img);
            object.put("item_name", item_name);
            object.put("item_desc", item_desc);
            object.put("catalog_id", catalog_id);
            object.put("srv_ids", srv_ids);
            object.put("shop_ids", shop_ids);
            object.put("cost_price", cost_price);
            object.put("price", price);
            object.put("market_price", market_price);
            object.put("promotion_begin_time", promotion_begin_time);
            object.put("promotion_end_time", promotion_end_time);
            object.put("promotion_price", promotion_price);
            object.put("files", files);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 198、营销活动
     * @param token
     * @param uid
     * @param act_type //活动类型：1会员卡；2套餐活动；3红包
     * @param shop_id //店铺id，0表示全部店铺
     * @param page //第几页
     * @return
     */
    public static JSONObject s_act_list_get(String token,String uid,int act_type,String shop_id,int page) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_act_list_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("act_type", act_type);
            object.put("shop_id", shop_id);
            object.put("page", page);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 199、营销活动-删除
     * @param token
     * @param uid
     * @param act_type
     * @param business_id //对应活动类型的业务id
     * @return
     */
    public static JSONObject s_act_delete_set(String token,String uid,int act_type,String business_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_act_delete_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("act_type", act_type);
            object.put("business_id", business_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 200、营销活动-详情
     * @param token
     * @param uid
     * @param act_type
     * @param business_id
     * @return
     */
    public static JSONObject s_act_detail_get(String token,String uid,int act_type,String business_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_act_detail_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("act_type", act_type);
            object.put("business_id", business_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 201、会员卡、套餐和优惠券选择，多店铺项目列表。供选择，店铺列表接口：s_select_shop_list_get
     * @param token
     * @param uid
     * @param act_type
     * @param business_id
     * @param shop_ids
     * @param item_type
     * @return
     */
    public static JSONObject s_multi_act_sel_get(String token,String uid,int act_type,String business_id,String shop_ids,String item_type) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_multi_act_sel_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("act_type", act_type);
            object.put("business_id", business_id);
            object.put("shop_ids", shop_ids);
            object.put("item_type", item_type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 202、营销活动-新增或修改会员卡。店铺选择调整为单项
     * @param token
     * @param uid
     * @param card_id
     * @param card_type //会员类型：1年卡；2项目卡；3充值卡
     * @param card_img
     * @param card_name
     * @param card_desc
     * @param shop_ids //应用店铺，多个逗号分隔，如：1,2,3
     * @param item_ids //项目id，多个逗号分隔，如：1,2,3
     * @param limit_months //使用期限，单位为月
     * @param recharge_amount //充值金额
     * @param give_amount//充值卡--赠送金额，年卡和项目卡传0
     * @param times //项目卡--次数，充值卡和年卡传0
     * @param discount //充值卡--折扣比例，年卡和项目卡传0
     * @param deadline_begin //活动开始时间
     * @param deadline_end
     * @param files //详情，文字和图片：type文件类型(1、文本；2、图片；)；path文件路径，图片类型传递width宽度和height高度；
                        [{"type":"1","content":"发型不错，很漂亮"}, {"type":"2","content":"/public/upload/5847972.png","width":"750","height":"290"}]
     * @return
     */
    public static JSONObject s_act_card_set(String token,String uid,String card_id,String card_type,String card_img,String card_name,String card_desc,
                  String shop_ids,String item_ids,String limit_months,String recharge_amount,String give_amount,String times,String discount,
                  String deadline_begin,String deadline_end,String files) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_act_card_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("card_id", card_id);
            object.put("card_type", card_type);
            object.put("card_img", card_img);
            object.put("card_name", card_name);
            object.put("card_desc", card_desc);
            object.put("shop_ids", shop_ids);
            object.put("item_ids", item_ids);
            object.put("limit_months", limit_months);
            object.put("recharge_amount", recharge_amount);
            object.put("give_amount", give_amount);
            object.put("times", times);
            object.put("discount", discount);
            object.put("deadline_begin", deadline_begin);
            object.put("deadline_end", deadline_end);
            object.put("files", files);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 203、营销活动-新增或修改套餐活动。店铺选择调整为单项
     * @param token
     * @param uid
     * @param pack_id //套餐id
     * @param pack_img
     * @param pack_name
     * @param price //现价
     * @param market_price //市场价
     * @param shop_ids
     * @param item_ids //项目id，多个逗号分隔，如：1,2,3
     * @param deadline_begin
     * @param deadline_end
     * @param files
     * @return
     */
    public static JSONObject s_act_pack_set(String token,String uid,String pack_id,String pack_img,String pack_name,String price,String market_price,
                                            String shop_ids,String item_ids,String deadline_begin,String deadline_end,String files) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_act_pack_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("pack_id", pack_id);
            object.put("pack_img", pack_img);
            object.put("pack_name", pack_name);
            object.put("price", price);
            object.put("market_price", market_price);
            object.put("shop_ids", shop_ids);
            object.put("item_ids", item_ids);
            object.put("deadline_begin", deadline_begin);
            object.put("deadline_end", deadline_end);
            object.put("files", files);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 204、营销活动-新增红包活动。此规则不能修改，红包已经发放了。店铺选择调整为单项
     * @param token
     * @param uid
     * @param red_act_id //红包活动id
     * @param red_type //红包类型:1项目红包；2商品红包
     * @param red_amount //红包金额
     * @param fullcut_amount //满多少钱可以使用，0不限制
     * @param limit_days //使用期限，天
     * @param delivery_mode //投放方式，选项由接口返回
     * @param shop_ids
     * @param item_ids
     * @param deadline_begin
     * @param deadline_end
     * @return
     */
    public static JSONObject s_act_red_set(String token,String uid,String red_act_id,String red_type,String red_amount,String fullcut_amount,String limit_days,
                                            String delivery_mode,String shop_ids,String item_ids,String deadline_begin,String deadline_end) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_act_red_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("red_act_id", red_act_id);
            object.put("red_type", red_type);
            object.put("red_amount", red_amount);
            object.put("fullcut_amount", fullcut_amount);
            object.put("limit_days", limit_days);
            object.put("delivery_mode", delivery_mode);
            object.put("shop_ids", shop_ids);
            object.put("item_ids", item_ids);
            object.put("deadline_begin", deadline_begin);
            object.put("deadline_end", deadline_end);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 214、会员管理
     * @param token
     * @param uid
     * @param shop_id 店铺id，全部店铺传0
     * @param page
     * @return
     */
    public static JSONObject s_member_list_get(String token,String uid,String shop_id,int page) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_member_list_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("shop_id", shop_id);
            object.put("page", page);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 215、会员卡详情
     * @param token
     * @param uid
     * @param member_id //会员id
     * @param page
     * @return
     */
    public static JSONObject s_member_detail_get(String token,String uid,String member_id,int page) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_member_detail_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("member_id", member_id);
            object.put("page", page);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 216、会员卡切换，查询该用户的其他会员卡
     * @param token
     * @param uid
     * @param member_id //会员id
     * @param page
     * @return
     */
    public static JSONObject s_member_change_get(String token,String uid,String member_id,int page) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_member_change_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("member_id", member_id);
            object.put("page", page);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 217、财务管理
     * @param token
     * @param uid
     * @param shop_id 店铺id，全部店铺传0
     * @param date 查询日期，格式：2018-04
     * @return
     */
    public static JSONObject s_account_info_get(String token,String uid,String shop_id,String date) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_account_info_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("shop_id", shop_id);
            object.put("date", date);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 218、我的账户明细列表
     * @param token
     * @param uid
     * @param shop_id 店铺id，全部店铺传0
     * @param date 查询日期，格式：2018-04
     * @param page
     * @return
     */
    public static JSONObject s_account_detail_get(String token,String uid,String shop_id,String date,int page) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_account_detail_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("shop_id", shop_id);
            object.put("date", date);
            object.put("page", page);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 219、账户提现申请初始化，提现时用户必须选择具体的店铺
     * @param token
     * @param uid
     * @param shop_id 店铺id，必须是具体的店铺id
     * @return
     */
    public static JSONObject s_account_apply_init_get(String token,String uid,String shop_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_account_apply_init_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("shop_id", shop_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 220、绑定微信或支付宝账户
     * @param token
     * @param uid
     * @param shop_id 店铺id，必须是具体的店铺id
     * @param account_type 绑定的账户类型：1支付宝；2微信
     * @param real_name 真实姓名
     * @param account 支付宝账户或微信账户
     * @return
     */
    public static JSONObject s_bind_account_set(String token,String uid,String shop_id,String account_type,String real_name,String account) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_bind_account_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("shop_id", shop_id);
            object.put("account_type", account_type);
            object.put("real_name", real_name);
            object.put("account", account);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 221、账户提现申请提交
     * @param token
     * @param uid
     * @param shop_id 店铺id，必须是具体的店铺id
     * @param account_type 绑定的账户类型：1支付宝；2微信
     * @param apply_amount 申请金额
     * @param sms_id 验证码的id
     * @param sms_code
     * @return
     */
    public static JSONObject s_account_apply_set(String token,String uid,String shop_id,String account_type,String apply_amount,String sms_id,String sms_code) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_account_apply_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("shop_id", shop_id);
            object.put("account_type", account_type);
            object.put("apply_amount", apply_amount);
            object.put("sms_id", sms_id);
            object.put("sms_code", sms_code);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 222、项目订单列表
     * @param token
     * @param uid
     * @param shop_id 店铺id，全部店铺传0
     * @param date 日期过滤，如：2018-04-18
     * @param status 订单状态：待使用；待评价；已完成
     * @param complaint 1投诉建议,2已处理
     * @param refund 退款，1退款申请,2退款成功,3退款失败
     * @param page
     * @return
     */
    public static JSONObject s_project_list_get(String token,String uid,String shop_id,String date,String status,String complaint,String refund,int page) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_project_list_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("shop_id", shop_id);
            object.put("date", date);
            object.put("status", status);
            object.put("complaint", complaint);
            object.put("refund", refund);
            object.put("page", page);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     *223、项目订单详情
     * @param token
     * @param uid
     * @param oid //订单oid
     * @return
     */
    public static JSONObject s_project_detail_get(String token,String uid,String oid) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_project_detail_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("oid", oid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 224、项目订单评价列表，每个项目订单可能评价多次
     * @param token
     * @param uid
     * @param oid //订单oid
     * @return
     */
    public static JSONObject s_project_evl_list_get(String token,String uid,String oid) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_project_evl_list_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("oid", oid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 225、订单评价详情，项目商品共用
     * @param token
     * @param uid
     * @param evl_id //评价id
     * @return
     */
    public static JSONObject s_evl_detail_get(String token,String uid,String evl_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_evl_detail_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("evl_id", evl_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 226、订单退款信息，项目商品共用
     * @param token
     * @param uid
     * @param refund_id //退款单id
     * @return
     */
    public static JSONObject s_refund_get(String token,String uid,String refund_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_refund_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("refund_id", refund_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 227、订单退款确认或拒绝，项目商品共用
     * @param token
     * @param uid
     * @param refund_id //退款单id
     * @param refund_status  //2退款成功；4商家拒绝退款
     * @return
     */
     public static JSONObject s_refund_set(String token,String uid,String refund_id,String refund_status) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_refund_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("refund_id", refund_id);
            object.put("refund_status", refund_status);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 228、订单投诉信息，项目商品共用
     * @param token
     * @param uid
     * @param complaint_id  //投诉id
     * @return
     */
    public static JSONObject s_complaint_get(String token,String uid,String complaint_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_complaint_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("complaint_id", complaint_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 229、订单投诉处理，项目商品共用
     * @param token
     * @param uid
     * @param complaint_id  //投诉id
     * @return
     */
    public static JSONObject s_complaint_set(String token,String uid,String complaint_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_complaint_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("complaint_id", complaint_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 230、商品订单列表
     * @param token
     * @param uid
     * @param shop_id //店铺id，全部店铺传0
     * @param date  //日期过滤，如：2018-04-18
     * @param status  //订单状态：未发货,已发货,未评价,已完成，状态传中文
     * @param complaint  //投诉：1投诉建议,2已处理
     * @param refund  //退款：1退款申请,2退款成功,4商家拒绝
     * @param page
     * @return
     */
    public static JSONObject s_commodity_order_list_get(String token,String uid,String shop_id,String date,String status,String complaint,String refund,int page) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_commodity_order_list_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("shop_id", shop_id);
            object.put("date", date);
            object.put("status", status);
            object.put("complaint", complaint);
            object.put("refund", refund);
            object.put("page", page);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 231、商品订单详情
     * @param token
     * @param uid
     * @param oid
     * @return
     */
    public static JSONObject s_commodity_order_detail_get(String token,String uid,String oid) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_commodity_order_detail_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("oid", oid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 232、商品订单退款列表
     * @param token
     * @param uid
     * @param oid
     * @return
     */
    public static JSONObject s_commodity_refund_list_get(String token,String uid,String oid) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_commodity_refund_list_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("oid", oid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 233、商品订单提醒发货信息
     * @param token
     * @param uid
     * @param remind_id //提醒发货id
     * @return
     */
    public static JSONObject s_commodity_remind_get(String token,String uid,String remind_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_commodity_remind_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("remind_id", remind_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 234、商品订单发货信息
     * @param token
     * @param uid
     * @param oid  //订单id
     * @return
     */
    public static JSONObject s_commodity_logistics_get(String token,String uid,String oid) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_commodity_logistics_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("oid", oid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 235、商品订单发货
     * @param token
     * @param uid
     * @param oid  //订单id
     * @param logistics_type  //提货方式：1物流；2自提
     * @param logistics_no  //物流单号
     * @param logistics_company_id  //物流公司
     * @return
     */
    public static JSONObject s_commodity_logistics_set(String token,String uid,String oid,int logistics_type,String logistics_no,String logistics_company_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_commodity_logistics_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("oid", oid);
            object.put("logistics_type", logistics_type);
            object.put("logistics_no", logistics_no);
            object.put("logistics_company_id", logistics_company_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 241、会员订单列表
     * @param token
     * @param uid
     * @param card_type //会员类型：1年卡；2项目卡；3充值卡
     * @param shop_id //店铺id，全部店铺传0
     * @param date //日期过滤，如：2018-04-18
     * @param member_no //会员编号
     * @param page //第几页
     * @return
     */
    public static JSONObject s_order_member_list_get(String token,String uid,String card_type,String shop_id,String date,String member_no,int page) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_order_member_list_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("card_type", card_type);
            object.put("shop_id", shop_id);
            object.put("date", date);
            object.put("member_no", member_no);
            object.put("page", page);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     *  242、会员详情
     * @param token
     * @param uid
     * @param member_id //卡号
     * @return
     */
    public static JSONObject s_order_member_detail_get(String token,String uid,String member_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_order_member_detail_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("member_id", member_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 243、套餐订单列表
     * @param token
     * @param uid
     * @param shop_id //店铺id，全部店铺传0
     * @param date //日期过滤，如：2018-04-18
     * @param page
     * @return
     */
    public static JSONObject s_order_pack_list_get(String token,String uid,String shop_id,String date,int page) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_order_pack_list_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("shop_id", shop_id);
            object.put("date", date);
            object.put("page", page);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 244、套餐详情，一个套餐购买后会拆分成多个子订单，一个种项目一个子订单
     * @param token
     * @param uid
     * @param oid //订单id
     * @return
     */
    public static JSONObject s_order_pack_detail_get(String token,String uid,String oid) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_order_pack_detail_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("oid", oid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 245、红包列表
     * @param token
     * @param uid
     * @param shop_id //店铺id，全部店铺传0
     * @param date //日期过滤，如：2018-04-18
     * @param page
     * @return
     */
    public static JSONObject s_red_list_get(String token,String uid,String shop_id,String date,int page) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_red_list_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("shop_id", shop_id);
            object.put("date", date);
            object.put("page", page);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 246、客户列表：我的店铺和工作狂里面共用客户列表
     * @param token
     * @param uid
     * @param is_admin  //是否我的店铺。0为工作狂，1为我的店铺
     * @param shop_id  //我的店铺里面需要店铺过滤，工作狂里面不需要店铺过滤。店铺id，全部店铺传0
     * @param page
     * @return
     */
    public static JSONObject s_custom_list_get(String token,String uid,String is_admin,String shop_id,String keywords,int page) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_custom_list_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("is_admin", is_admin);
            object.put("shop_id", shop_id);
            object.put("keywords", keywords);
            object.put("page", page);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 247、总收入
     * @param token
     * @param uid
     * @param date //月份过滤，如：2018-04
     * @param page
     * @return
     */
    public static JSONObject s_wage_total_get(String token,String uid,String date,int page) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_wage_total_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("date", date);
            object.put("page", page);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 248、工资明细
     * @param token
     * @param uid
     * @param date //月份过滤，如：2018-04
     * @param page
     * @return
     */
    public static JSONObject s_wage_detail_get(String token,String uid,String date) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_wage_detail_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("date", date);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * app升级接口，返回接口有2种：1提示升级，用户可以取消；2强制升级，不能取消，否则不能使用。苹果的点击升级后跳转到对应的appstore地址
     * @param version
     * @return 升级信息，content升级的具体内容；update_type更新类型：1提示升级、2强制升级；如果已经是最新版本则直接返回异常提示信息;url更新的路径
     */
    public static JSONObject app_update_get(String version,String uid) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "app_update_get");
            object.put("version", version);
            object.put("uid", uid);
            object.put("sys", "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 259、员工报表数据
     * @param token
     * @param uid
     * @return
     */
    public static JSONObject staff_report_data_get(String token,String uid) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "staff_report_data_get");
            object.put("token", token);
            object.put("uid", uid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 260、员工报表设置本月目标金额
     * @param token
     * @param uid
     * @param target_wage
     * @return
     */
    public static JSONObject staff_report_target_set(String token,String uid,String target_wage) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "staff_report_target_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("target_wage", target_wage);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 261、员工报表获取本月目标金额
     * @param token
     * @param uid
     * @return
     */
    public static JSONObject staff_report_target_get(String token,String uid) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "staff_report_target_get");
            object.put("token", token);
            object.put("uid", uid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 262、店铺报表数据
     * @param token
     * @param uid
     * @return
     */
    public static JSONObject shop_report_data_get(String token,String uid,String shop_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "shop_report_data_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("shop_id", shop_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 263、店铺报表目标金额
     * @param token
     * @param uid
     * @return
     */
    public static JSONObject shop_report_target_get(String token,String uid) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "shop_report_target_get");
            object.put("token", token);
            object.put("uid", uid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 264、店铺报表目标金额
     * @param token
     * @param uid
     * @param target_wage
     * @return
     */
    public static JSONObject shop_report_target_set(String token,String uid,String target_income,String shop_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "shop_report_target_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("target_income", target_income);
            object.put("shop_id", shop_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 266、店内报表
     * @param token
     * @param uid
     * @param shop_id
     * @return
     */
    public static JSONObject inshop_report_data_get(String token,String uid,String shop_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "inshop_report_data_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("shop_id", shop_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 267、店铺查看员工数据的报表
     * @param token
     * @param uid
     * @param shop_id
     * @return
     */
    public static JSONObject shop_staff_report_data_get(String token,String uid,String shop_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "shop_staff_report_data_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("shop_id", shop_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 265、评价列表
     * @param token
     * @param uid
     * @param shop_id
     * @param list_type
     * @param page
     * @return
     */
    public static JSONObject s_evalution_list_get(String token,String uid,String shop_id,int list_type,int page) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_evalution_list_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("shop_id", shop_id);
            object.put("list_type", list_type);
            object.put("page", page);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 266、商家回复评价
     * @param token
     * @param uid
     * @param evl_id
     * @param content
     * @return
     */
    public static JSONObject s_evalution_reply_set(String token,String uid,String evl_id,String content) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_evalution_reply_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("evl_id", evl_id);
            object.put("content", content);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 删除商家回复评价
     * @param token
     * @param uid
     * @param evl_reply_id
     * @return
     */
    public static JSONObject s_evalution_reply_delete_set(String token,String uid,String evl_reply_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_evalution_reply_delete_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("evl_reply_id", evl_reply_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 店铺员工工资
     * @param token
     * @param uid
     * @param shop_id 店铺id，全部传0
     * @param wage_month 月度，如2018-05，没有传空
     * @param keyword 搜索的员工姓名，没有传空
     * @param page
     * @return
     */
    public static JSONObject s_staff_wage_list_get(String token,String uid,String shop_id,String wage_month,String keyword,int page) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_staff_wage_list_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("shop_id", shop_id);
            object.put("wage_month", wage_month);
            object.put("keyword", keyword);
            object.put("page", page);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 150、获取手机联系人信息
     * @param token
     * @param uid
     * @param phones  //手机号列表，逗号分隔
     * @return
     */
    public static JSONObject phone_contact_list_get(String token,String uid,String phones,String keyword) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "phone_contact_list_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("phones", phones);
            object.put("keyword", keyword);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 151、手机联系人邀请或添加
     * @param token
     * @param uid
     * @param phone //某个手机号
     * @return
     */
    public static JSONObject phone_contact_invite_set(String token,String uid,String phone) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "phone_contact_invite_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("phone", phone);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 158、发送生日短信
     * @param token
     * @param uid
     * @param phone
     * @return
     */
    public static JSONObject phone_birthday_set(String token,String uid,String phone) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "phone_birthday_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("phones", phone);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 22、支付密码短信验证(VER2)
     * @param token
     * @param uid
     * @param sms_id
     * @param sms_code
     * @return
     */
    public static JSONObject user_pay_pwd_sms_set(String token,String uid,String sms_id,String sms_code) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "user_pay_pwd_sms_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("sms_id", sms_id);
            object.put("sms_code", sms_code);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 23、支付密码设置(VER2)
     * @param token
     * @param uid
     * @param pay_pwd
     * @param sms_id
     * @param sms_code
     * @return
     */
    public static JSONObject user_pay_pwd_set(String token,String uid,String pay_pwd,String sms_id,String sms_code) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "user_pay_pwd_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("pay_pwd", pay_pwd);
            object.put("sms_id", sms_id);
            object.put("sms_code", sms_code);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 231、项目选择服务列表，供选择，公用接口，服务分成3种分类，美发、头皮和高科(VER2)
     * @param token
     * @param uid
     * @return
     */
    public static JSONObject s_srv_sel_new_get(String token,String uid) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_srv_sel_new_get");
            object.put("token", token);
            object.put("uid", uid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 293、服务添加和修改(VER2)
     * @param token
     * @param uid
     * @param srv_id 服务id，如果是新增传0，修改传对应的srv_id
     * @param srv_name
     * @param srv_type 服务分类：1美发；2头皮；3高科美颜
     * @param srv_img
     * @param srv_duration 服务时长，单位分钟
     * @return
     */
    public static JSONObject s_service_set(String token,String uid,String srv_id,String srv_name,String srv_type,String srv_img,String srv_duration) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_service_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("srv_id", srv_id);
            object.put("srv_name", srv_name);
            object.put("srv_type", srv_type);
            object.put("srv_img", srv_img);
            object.put("srv_duration", srv_duration);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 294、服务删除(VER2)
     * @param token
     * @param uid
     * @param srv_id
     * @return
     */
    public static JSONObject s_service_delete_set(String token,String uid,String srv_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_service_delete_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("srv_id", srv_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 102、购物车列表数据，分成项目和商品(VER2)
     * @param token
     * @param uid
     * @param cart_type 购车车类型：1项目；2商品
     * @return
     */
    public static JSONObject cart_list_new_get(String token,String uid,int cart_type) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "cart_list_new_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("cart_type", cart_type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 295、录入会员卡：店铺员工扫用户二维码，判断二维码是否有效，返回客户id(VER2)
     * @param token
     * @param uid
     * @param share_code
     * @return
     */
    public static JSONObject s_input_code2d_get(String token,String uid,String share_code) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_input_code2d_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("share_code", share_code);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 296、录入会员卡初始化(VER2)
     * @param token
     * @param uid
     * @param custom_uid
     * @return
     */
    public static JSONObject s_input_member_init_get(String token,String uid,String custom_uid) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_input_member_init_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("custom_uid", custom_uid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 297、录入会员卡列表选择(VER2)
     * @param token
     * @param uid
     * @param shop_id
     * @param card_type 卡的类型，card_type：1年卡、2项目卡、3充值卡
     * @return
     */
    public static JSONObject s_input_card_list_get(String token,String uid,String shop_id,int card_type) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_input_card_list_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("shop_id", shop_id);
            object.put("card_type", card_type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 298、录入会员卡项目列表选择(VER2)
     * @param token
     * @param uid
     * @param card_id
     * @return
     */
    public static JSONObject s_input_item_list_get(String token,String uid,String card_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_input_item_list_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("card_id", card_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 299、录入会员卡会籍(VER2)
     * @param token
     * @param uid
     * @param custom_uid 客户uid
     * @param shop_id 店铺id，切换店铺后清空选择的会籍
     * @param member_name 会员姓名
     * @param member_phone 会员电话
     * @param member_birthday 会员生日
     * @param card_id 会员卡id
     * @param member_avatar 会员头像，会籍和项目卡参数
     * @param member_begin 会员开始时间，如：2018-08-08
     * @param member_end 会员结束时间，如：2018-08-08
     * @param remain_amount 剩余金额，充值卡参数
     * @param item_data 项目卡项目:[{"item_id":"1","num":"2"},{"item_id":"2","num":"5"}]，num为购买数量
     * @return
     */
    public static JSONObject s_input_member_set(String token,String uid,String custom_uid,String shop_id,String member_name,String member_phone,
                                                String member_birthday,String card_id,String member_avatar,String member_begin,String member_end,
                                                String remain_amount,String item_data) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_input_member_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("custom_uid", custom_uid);
            object.put("shop_id", shop_id);
            object.put("member_name", member_name);
            object.put("member_phone", member_phone);
            object.put("member_birthday", member_birthday);
            object.put("card_id", card_id);
            object.put("member_avatar", member_avatar);
            object.put("member_begin", member_begin);
            object.put("member_end", member_end);
            object.put("remain_amount", remain_amount);
            object.put("item_data", item_data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 300、录入套餐项目列表选择(VER2)
     * @param token
     * @param uid
     * @param shop_id
     * @param pack_type
     * @return
     */
    public static JSONObject s_input_item_pack_list_get(String token,String uid,String shop_id,int pack_type) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_input_item_pack_list_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("shop_id", shop_id);
            object.put("pack_type", pack_type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 301、获取套餐里面的项目，pack_type为3时调用此接口(VER2)
     * @param token
     * @param uid
     * @param shop_id
     * @param item_id
     * @return
     */
    public static JSONObject s_input_item_sub_list_get(String token,String uid,String shop_id,String item_id) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_input_item_sub_list_get");
            object.put("token", token);
            object.put("uid", uid);
            object.put("shop_id", shop_id);
            object.put("item_id", item_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 299、录入会员卡会籍(VER2)
     * @param token
     * @param uid
     * @param custom_uid 客户uid
     * @param shop_id 店铺id，切换店铺后清空选择的会籍
     * @param member_name 会员姓名
     * @param member_phone 会员电话
     * @param member_birthday 会员生日
     * @param sel_item_id 使用的套餐item_id
     * @param item_data 项目卡项目:[{"item_id":"1","num":"2"},{"item_id":"2","num":"5"}]，num为购买数量
     * @return
     */
    public static JSONObject s_input_pack_set(String token,String uid,String custom_uid,String shop_id,String member_name,String member_phone,
                                                String member_birthday,String sel_item_id,String item_data) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "s_input_pack_set");
            object.put("token", token);
            object.put("uid", uid);
            object.put("custom_uid", custom_uid);
            object.put("shop_id", shop_id);
            object.put("member_name", member_name);
            object.put("member_phone", member_phone);
            object.put("member_birthday", member_birthday);
            object.put("sel_item_id", sel_item_id);
            object.put("item_data", item_data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }
}
