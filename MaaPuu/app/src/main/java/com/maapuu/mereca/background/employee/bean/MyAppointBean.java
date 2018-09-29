package com.maapuu.mereca.background.employee.bean;

import com.maapuu.mereca.bean.ShopBean;
import com.maapuu.mereca.bean.SrvBean;

import java.util.List;

/**
 * 我的预约
 * Created by Jia on 2018/4/11.
 */

public class MyAppointBean {

    /**
     * beging_srv : [{"uid":"1","nick_name":"鬼使白兵","avatar":"http://beauty.whhxrc.com/./public/upload/appphotos/2018/03/29/889d0b8066c118a65392a83f93af2b78Bo7ojB.jpg","item_name":"时尚剪发","phone":"15972093060","pay_amount":"1.00","appoint_srv_id":"10","appoint_id":"3","srv_id":"1","srv_name":"洗发","appoint_time_text":"2018-04-07 10:00","srv_status":"4","staff_name":"郑美美"},{"uid":"1","nick_name":"鬼使白兵","avatar":"http://beauty.whhxrc.com/./public/upload/appphotos/2018/03/29/889d0b8066c118a65392a83f93af2b78Bo7ojB.jpg","item_name":"时尚剪发","phone":"15972093060","pay_amount":"1.00","appoint_srv_id":"12","appoint_id":"3","srv_id":"3","srv_name":"剪发","appoint_time_text":"2018-04-07 10:00","srv_status":"4","staff_name":"郑美美"},{"uid":"1","nick_name":"鬼使白兵","avatar":"http://beauty.whhxrc.com/./public/upload/appphotos/2018/03/29/889d0b8066c118a65392a83f93af2b78Bo7ojB.jpg","item_name":"时尚剪发","phone":"15972093060","pay_amount":"1.00","appoint_srv_id":"13","appoint_id":"3","srv_id":"4","srv_name":"染发","appoint_time_text":"2018-04-07 10:00","srv_status":"4","staff_name":"郑美美"}]
     * count : 3
     */

    private int count;
    private List<BegingSrvBean> beging_srv;
    private List<ShopBean> shop_list;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<BegingSrvBean> getBeging_srv() {
        return beging_srv;
    }

    public void setBeging_srv(List<BegingSrvBean> beging_srv) {
        this.beging_srv = beging_srv;
    }

    public List<ShopBean> getShop_list() {
        return shop_list;
    }

    public void setShop_list(List<ShopBean> shop_list) {
        this.shop_list = shop_list;
    }

    public static class BegingSrvBean {
        /**
         * uid : 1
         * nick_name : 鬼使白兵
         * avatar : http://beauty.whhxrc.com/./public/upload/appphotos/2018/03/29/889d0b8066c118a65392a83f93af2b78Bo7ojB.jpg
         * item_name : 时尚剪发
         * phone : 15972093060
         * pay_amount : 1.00
         * appoint_srv_id : 10
         * appoint_id : 3
         * srv_id : 1
         * srv_name : 洗发
         * appoint_time_text : 2018-04-07 10:00
         * srv_status : 4
         * staff_name : 郑美美
         */

        private String uid;
        private String nick_name;
        private String avatar;
        private String item_name;
        private String phone;
        private String pay_amount;
        private String appoint_srv_id;
        private String appoint_id;
        private String srv_id;
        private String srv_name;
        private String appoint_time_text;
        private String srv_status;
        private String staff_name;
        private String oid;
        private int code_status;
        private List<SrvBean> srv_list;

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getNick_name() {
            return nick_name;
        }

        public void setNick_name(String nick_name) {
            this.nick_name = nick_name;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getItem_name() {
            return item_name;
        }

        public void setItem_name(String item_name) {
            this.item_name = item_name;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getPay_amount() {
            return pay_amount;
        }

        public void setPay_amount(String pay_amount) {
            this.pay_amount = pay_amount;
        }

        public String getAppoint_srv_id() {
            return appoint_srv_id;
        }

        public void setAppoint_srv_id(String appoint_srv_id) {
            this.appoint_srv_id = appoint_srv_id;
        }

        public String getAppoint_id() {
            return appoint_id;
        }

        public void setAppoint_id(String appoint_id) {
            this.appoint_id = appoint_id;
        }

        public String getSrv_id() {
            return srv_id;
        }

        public void setSrv_id(String srv_id) {
            this.srv_id = srv_id;
        }

        public String getSrv_name() {
            return srv_name;
        }

        public void setSrv_name(String srv_name) {
            this.srv_name = srv_name;
        }

        public String getAppoint_time_text() {
            return appoint_time_text;
        }

        public void setAppoint_time_text(String appoint_time_text) {
            this.appoint_time_text = appoint_time_text;
        }

        public String getSrv_status() {
            return srv_status;
        }

        public void setSrv_status(String srv_status) {
            this.srv_status = srv_status;
        }

        public String getStaff_name() {
            return staff_name;
        }

        public void setStaff_name(String staff_name) {
            this.staff_name = staff_name;
        }

        public List<SrvBean> getSrv_list() {
            return srv_list;
        }

        public void setSrv_list(List<SrvBean> srv_list) {
            this.srv_list = srv_list;
        }

        public String getOid() {
            return oid;
        }

        public void setOid(String oid) {
            this.oid = oid;
        }

        public int getCode_status() {
            return code_status;
        }

        public void setCode_status(int code_status) {
            this.code_status = code_status;
        }
    }
}
