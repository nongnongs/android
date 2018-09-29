package com.maapuu.mereca.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by dell on 2018/4/7.
 */

public class ProjectOrderBean implements Serializable {
    private String oid;
    private String order_no;
    private String status;
    private String item_name;
    private String item_img;
    private String shop_name;
    private String pay_amount;
    private String create_time_text;
    private String item_num;
    private String price;
    private int is_delete;
    private int is_pay;
    private int is_appoint;
    private int is_cancel_appoint;
    private int is_evl;
    private int is_code2d;
    private String refund_text;

    private List<OrderChildBean> items;

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public String getItem_img() {
        return item_img;
    }

    public void setItem_img(String item_img) {
        this.item_img = item_img;
    }

    public String getShop_name() {
        return shop_name;
    }

    public void setShop_name(String shop_name) {
        this.shop_name = shop_name;
    }

    public String getPay_amount() {
        return pay_amount;
    }

    public void setPay_amount(String pay_amount) {
        this.pay_amount = pay_amount;
    }

    public String getCreate_time_text() {
        return create_time_text;
    }

    public void setCreate_time_text(String create_time_text) {
        this.create_time_text = create_time_text;
    }

    public String getItem_num() {
        return item_num;
    }

    public void setItem_num(String item_num) {
        this.item_num = item_num;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getIs_delete() {
        return is_delete;
    }

    public void setIs_delete(int is_delete) {
        this.is_delete = is_delete;
    }

    public int getIs_pay() {
        return is_pay;
    }

    public void setIs_pay(int is_pay) {
        this.is_pay = is_pay;
    }

    public int getIs_appoint() {
        return is_appoint;
    }

    public void setIs_appoint(int is_appoint) {
        this.is_appoint = is_appoint;
    }

    public int getIs_cancel_appoint() {
        return is_cancel_appoint;
    }

    public void setIs_cancel_appoint(int is_cancel_appoint) {
        this.is_cancel_appoint = is_cancel_appoint;
    }

    public int getIs_evl() {
        return is_evl;
    }

    public void setIs_evl(int is_evl) {
        this.is_evl = is_evl;
    }

    public int getIs_code2d() {
        return is_code2d;
    }

    public void setIs_code2d(int is_code2d) {
        this.is_code2d = is_code2d;
    }

    public String getRefund_text() {
        return refund_text;
    }

    public void setRefund_text(String refund_text) {
        this.refund_text = refund_text;
    }

    public List<OrderChildBean> getItems() {
        return items;
    }

    public void setItems(List<OrderChildBean> items) {
        this.items = items;
    }
}
