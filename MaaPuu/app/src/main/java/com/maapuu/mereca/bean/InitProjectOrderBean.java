package com.maapuu.mereca.bean;

import java.io.Serializable;

/**
 * Created by dell on 2018/4/7.
 */

public class InitProjectOrderBean implements Serializable {
    private String item_id;
    private String shop_id;
    private String item_name;
    private String item_shop_id;
    private String price;
    private String item_img;
    private String item_desc;
    private String pack_type;
    private String srv_num;
    private String num;

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public String getShop_id() {
        return shop_id;
    }

    public void setShop_id(String shop_id) {
        this.shop_id = shop_id;
    }

    public String getItem_shop_id() {
        return item_shop_id;
    }

    public void setItem_shop_id(String item_shop_id) {
        this.item_shop_id = item_shop_id;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getItem_img() {
        return item_img;
    }

    public void setItem_img(String item_img) {
        this.item_img = item_img;
    }

    public String getItem_desc() {
        return item_desc;
    }

    public void setItem_desc(String item_desc) {
        this.item_desc = item_desc;
    }

    public String getPack_type() {
        return pack_type;
    }

    public void setPack_type(String pack_type) {
        this.pack_type = pack_type;
    }

    public String getSrv_num() {
        return srv_num;
    }

    public void setSrv_num(String srv_num) {
        this.srv_num = srv_num;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }
}
