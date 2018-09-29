package com.maapuu.mereca.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.gyf.barlibrary.ImmersionBar;
import com.maapuu.mereca.R;
import com.maapuu.mereca.activity.AddressManageActivity;
import com.maapuu.mereca.activity.AttectionActivity;
import com.maapuu.mereca.activity.CollectionActivity;
import com.maapuu.mereca.activity.ConsumeCodeActivity;
import com.maapuu.mereca.activity.HongBaoActivity;
import com.maapuu.mereca.activity.KaBaoActivity;
import com.maapuu.mereca.activity.LoginActivity;
import com.maapuu.mereca.activity.MyAccountActivity;
import com.maapuu.mereca.activity.MyEwmActivity;
import com.maapuu.mereca.activity.MyOrderActivity;
import com.maapuu.mereca.activity.OpenShopActivity;
import com.maapuu.mereca.activity.SettingActivity;
import com.maapuu.mereca.activity.ShoppingCartActivity;
import com.maapuu.mereca.activity.UserinfoActivity;
import com.maapuu.mereca.activity.YuYueSuccessActivity;
import com.maapuu.mereca.adapter.help.BaseAdapterHelper;
import com.maapuu.mereca.adapter.help.QuickAdapter;
import com.maapuu.mereca.background.employee.activity.WorkaholicActivity;
import com.maapuu.mereca.background.shop.activity.MyShopActivity;
import com.maapuu.mereca.base.BaseFragment;
import com.maapuu.mereca.constant.UrlUtils;
import com.maapuu.mereca.util.HttpModeBase;
import com.maapuu.mereca.util.LoginUtil;
import com.maapuu.mereca.util.StringUtils;
import com.maapuu.mereca.util.ToastUtil;
import com.maapuu.mereca.util.UIUtils;
import com.maapuu.mereca.view.CircleImgView;
import com.maapuu.mereca.view.MyListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by dell on 2018/1/11.
 */

public class MineFragment extends BaseFragment implements AdapterView.OnItemClickListener{
    @BindView(R.id.scroll_view)
    ScrollView scrollView;
    @BindView(R.id.image)
    CircleImgView image;
    @BindView(R.id.txt_nick_name)
    TextView txtNickName;
    @BindView(R.id.txt_shop)
    TextView txtShop;
    @BindView(R.id.iv_position)
    ImageView ivPosition;
    @BindView(R.id.txt_signture)
    TextView txtSignture;
    @BindView(R.id.ll_yuyue)
    LinearLayout llYuyue;
    @BindView(R.id.iv_order)
    SimpleDraweeView ivOrder;
    @BindView(R.id.txt_goods_name)
    TextView txtGoodsName;
    @BindView(R.id.txt_goods_price)
    TextView txtGoodsPrice;
    @BindView(R.id.txt_shop_name)
    TextView txtShopName;
    @BindView(R.id.txt_yy_time)
    TextView txtYyTime;
    @BindView(R.id.list_view)
    MyListView listView;

    private QuickAdapter<String> adapter;
    private List<String> list;
    private String oid;
    private String code2d_id;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImmersionBar.with(getActivity()).fitsSystemWindows(true).statusBarColor(R.color.white)
                .statusBarDarkFont(true,0f).init();
    }

    @Override
    protected int setContentViewById() {
        return R.layout.fragment_mine;
    }

    @Override
    protected void initView(View v) {
        list = new ArrayList<>();
        scrollView.smoothScrollTo(0,0);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
            ImmersionBar.with(getActivity()).fitsSystemWindows(true).statusBarDarkFont(true,0f).init();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(LoginUtil.getLoginState()){
            if(!StringUtils.isEmpty(LoginUtil.getInfo("avatar"))){
                UIUtils.loadImg(mContext,LoginUtil.getInfo("avatar"),image,true,R.mipmap.morentouxiang);
            }
            txtNickName.setText(LoginUtil.getInfo("nick_name"));
            txtSignture.setVisibility(View.VISIBLE);
            txtSignture.setText(LoginUtil.getInfo("signature"));
            mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_1, UrlUtils.my_info_get(LoginUtil.getInfo("token"),LoginUtil.getInfo("uid"),1),false);
        }else {
            image.setImageResource(R.mipmap.morentouxiang);
            txtNickName.setText("请登录");
            ivPosition.setVisibility(View.GONE);
            txtShop.setVisibility(View.GONE);
            txtSignture.setVisibility(View.GONE);
            llYuyue.setVisibility(View.GONE);
            setAdapter(0);
        }
        listView.setOnItemClickListener(this);
    }

    @Override
    protected void initData() {}

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case HttpModeBase.HTTP_REQUESTCODE_1:
                try {
                    JSONObject object = new JSONObject((String) msg.obj);
                    if (object.optInt("status") == 1) {
                        JSONObject resultObj = object.optJSONObject("result");
                        if("8".equals(resultObj.optString("u_level"))){
                            txtShop.setVisibility(View.GONE);
                            ivPosition.setVisibility(View.GONE);
                        }else {
                            txtShop.setVisibility(View.VISIBLE);
                            ivPosition.setVisibility(View.VISIBLE);
                            txtShop.setText(resultObj.optString("shop_name"));
                        }
                        if(resultObj.has("order_appoint") && !StringUtils.isEmpty(resultObj.optString("order_appoint"))
                                && !(resultObj.opt("order_appoint") instanceof Boolean)){
                            llYuyue.setVisibility(View.VISIBLE);
                            JSONObject appointObj = resultObj.optJSONObject("order_appoint");
                            oid = appointObj.optString("oid");
                            code2d_id = appointObj.optString("code2d_id");
                            ivOrder.setImageURI(Uri.parse(appointObj.optString("item_img")));
                            txtGoodsName.setText(appointObj.optString("item_name"));
                            txtGoodsPrice.setText("¥"+appointObj.optString("pay_amount"));
                            txtShopName.setText(appointObj.optString("shop_name"));
                            txtYyTime.setText("预约时间："+appointObj.optString("appoint_time_text"));
                        }else {
                            llYuyue.setVisibility(View.GONE);
                        }
                        setAdapter(resultObj.optInt("u_level"));
                    } else {
                        ToastUtil.show(mContext,object.optString("message"));
                        setAdapter(0);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case HttpModeBase.HTTP_ERROR:
                String result_e = (String) msg.obj;
//                ToastUtil.show(mContext,result_e);
                break;
        }
        return false;
    }

    private void setAdapter(int level){
        list.clear();
        list.add("消费验证码");list.add("我的账户");list.add("我的红包");list.add("我的二维码");list.add("地址管理");
        list.add("设置");list.add("我要开店");
        switch (level){
            case 1:
            case 2:
            case 3:
                list.add("工作狂");list.add("我的店铺");
                break;
            case 5:
                list.add("我的店铺");
                break;
            case 4:
                list.add("工作狂");
                break;
            case 8:
            default:
                break;
        }
        adapter = new QuickAdapter<String>(mContext,R.layout.layout_mine_item,list) {
            @Override
            protected void convert(BaseAdapterHelper helper, String item) {
                int position = helper.getPosition();
                helper.setText(R.id.txt,item);
                if(position == list.size()-1){
                    helper.setVisible(R.id.line,false);;
                }else {
                    helper.setVisible(R.id.line,true);;
                }
            }
        };
        listView.setAdapter(adapter);
    }

    @Override
    @OnClick({R.id.ll_head,R.id.txt_collection,R.id.txt_attection,R.id.txt_kabao,R.id.txt_cart,R.id.ll_order,R.id.ll_yuyue})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ll_head:
                if(LoginUtil.getLoginState()){
                    startActivity(new Intent(getActivity(),UserinfoActivity.class));
                }else {
                    startActivity(new Intent(getActivity(),LoginActivity.class));
                }
                break;
            case R.id.txt_collection:
                if(LoginUtil.getLoginState()){
                    startActivity(new Intent(getActivity(),CollectionActivity.class));
                }else {
                    startActivity(new Intent(getActivity(),LoginActivity.class));
                }
                break;
            case R.id.txt_attection:
                if(LoginUtil.getLoginState()){
                    startActivity(new Intent(getActivity(),AttectionActivity.class));
                }else {
                    startActivity(new Intent(getActivity(),LoginActivity.class));
                }
                break;
            case R.id.txt_kabao:
                if(LoginUtil.getLoginState()){
                    startActivity(new Intent(getActivity(),KaBaoActivity.class));
                }else {
                    startActivity(new Intent(getActivity(),LoginActivity.class));
                }
                break;
            case R.id.txt_cart:
                if(LoginUtil.getLoginState()){
                    startActivity(new Intent(getActivity(),ShoppingCartActivity.class));
                }else {
                    startActivity(new Intent(getActivity(),LoginActivity.class));
                }
                break;
            case R.id.ll_order:
                if(LoginUtil.getLoginState()){
                    startActivity(new Intent(getActivity(),MyOrderActivity.class));
                }else {
                    startActivity(new Intent(getActivity(),LoginActivity.class));
                }
                break;
            case R.id.ll_yuyue:
                if(LoginUtil.getLoginState()){
                    if(!TextUtils.isEmpty(oid) && !TextUtils.isEmpty(code2d_id)){
                        it = new Intent(mContext,YuYueSuccessActivity.class);
                        it.putExtra("oid",oid);
                        it.putExtra("code2d_id",code2d_id);
                        startActivity(it);
                    } else {
                        ToastUtil.show(mContext,"未获取到订单信息");
                    }
                }else {
                    startActivity(new Intent(getActivity(),LoginActivity.class));
                }
                break;
        }
    }

    //listview 条目点击
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(LoginUtil.getLoginState()){
            if(list != null && list.size()>0){
                switch (list.get(position)){
                    case "消费验证码":
                        startActivity(new Intent(getActivity(),ConsumeCodeActivity.class));
                        break;
                    case "我的账户":
                        startActivity(new Intent(getActivity(),MyAccountActivity.class));
                        break;
                    case "我的红包":
                        startActivity(new Intent(getActivity(),HongBaoActivity.class));
                        break;
                    case "我的二维码":
                        startActivity(new Intent(getActivity(),MyEwmActivity.class));
                        break;
                    case "地址管理":
                        startActivity(new Intent(getActivity(),AddressManageActivity.class));
                        break;
                    case "设置":
                        startActivity(new Intent(getActivity(),SettingActivity.class));
                        break;
                    case "我要开店":
                        startActivity(new Intent(getActivity(),OpenShopActivity.class));
                        break;
                    case "工作狂":
                        startActivity(new Intent(getActivity(),WorkaholicActivity.class));
                        break;
                    case "我的店铺":
                        startActivity(new Intent(getActivity(),MyShopActivity.class));
                        break;
                }
            }
        }else {
            startActivity(new Intent(getActivity(),LoginActivity.class));
        }
    }
}
