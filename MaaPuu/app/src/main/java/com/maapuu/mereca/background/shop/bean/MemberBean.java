package com.maapuu.mereca.background.shop.bean;

import java.io.Serializable;

/**
 * 会员
 * Created by Jia on 2018/4/18.
 */

public class MemberBean implements Serializable {

    /**
     * member_id : 18
     * nick_name : 趣味儿童与青少年
     * age : 36
     * sex : 1
     * phone : 15972093060
     * card_name : 尊享会员
     * member_end : 2018-07-06 00:00:00
     */

    private String member_id;
    private String nick_name;
    private String age;
    private int sex; //性别：1男；2女；0未知
    private String phone;
    private String card_name;
    private String member_end;
    private String avatar;
    private String remain_amount;
    private String card_type;

    public String getMember_id() {
        return member_id;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCard_name() {
        return card_name;
    }

    public void setCard_name(String card_name) {
        this.card_name = card_name;
    }

    public String getMember_end() {
        return member_end;
    }

    public void setMember_end(String member_end) {
        this.member_end = member_end;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getRemain_amount() {
        return remain_amount;
    }

    public void setRemain_amount(String remain_amount) {
        this.remain_amount = remain_amount;
    }

    public String getCard_type() {
        return card_type;
    }

    public void setCard_type(String card_type) {
        this.card_type = card_type;
    }
}
