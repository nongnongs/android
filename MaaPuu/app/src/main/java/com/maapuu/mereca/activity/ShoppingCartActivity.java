package com.maapuu.mereca.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.maapuu.mereca.R;
import com.maapuu.mereca.adapter.ShoppingCartRecyclerAdapter;
import com.maapuu.mereca.base.AppConfig;
import com.maapuu.mereca.base.BaseActivity;
import com.maapuu.mereca.bean.CartBean;
import com.maapuu.mereca.bean.CartGoodsBean;
import com.maapuu.mereca.constant.UrlUtils;
import com.maapuu.mereca.recycleviewutil.FullyLinearLayoutManager;
import com.maapuu.mereca.recycleviewutil.RecyclerViewDivider;
import com.maapuu.mereca.util.FastJsonTools;
import com.maapuu.mereca.util.HttpModeBase;
import com.maapuu.mereca.util.LoginUtil;
import com.maapuu.mereca.util.StringUtils;
import com.maapuu.mereca.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by dell on 2018/2/24.
 */

public class ShoppingCartActivity extends BaseActivity {
    @BindView(R.id.txt_left)
    TextView txtLeft;
    @BindView(R.id.txt_title)
    TextView txtTitle;
    @BindView(R.id.txt_right)
    TextView txtRight;
    @BindView(R.id.txt_1)
    TextView txt1;
    @BindView(R.id.txt_2)
    TextView txt2;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.ll_has)
    LinearLayout llHas;
    @BindView(R.id.txt_label)
    TextView txtLabel;
    @BindView(R.id.iv_choose)
    ImageView ivChoose;
    @BindView(R.id.txt_total_price)
    TextView txtTotalPrice;
    @BindView(R.id.ll_edit)
    LinearLayout llEdit;
    @BindView(R.id.txt_settlement)
    TextView txtSettlement;

    private LinearLayoutManager mLayoutManager;
    private List<CartBean> list;
    private ShoppingCartRecyclerAdapter adapter;
    private boolean isEdit = false;//是否编辑（删除）
    private boolean isJieSuan = false;

    private int cart_type = 1;//购车车类型：0全部(默认)；1项目；2商品(VER2)

    @Override
    public void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_shopping_cart);
    }

    @Override
    public void initView() {
        txtLeft.setTypeface(StringUtils.getFont(mContext));
        txtTitle.setText("购物车");
        txtRight.setText("编辑");txtRight.setVisibility(View.VISIBLE);
        list = new ArrayList<>();
        txt1.setSelected(true);
        mLayoutManager=new FullyLinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mLayoutManager);
        //这句就是添加我们自定义的分隔线
        recyclerView.addItemDecoration(new RecyclerViewDivider(mContext, LinearLayoutManager.VERTICAL,20
                ,getResources().getColor(R.color.background)));
    }

    @Override
    public void initData() {
        mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_1, UrlUtils.cart_list_new_get(LoginUtil.getInfo("token"),LoginUtil.getInfo("uid"),cart_type),true);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case HttpModeBase.HTTP_REQUESTCODE_1:
                try {
                    JSONObject object = new JSONObject((String) msg.obj);
                    if (object.optInt("status") == 1) {
                        if(!StringUtils.isEmpty(object.optString("result")) && object.optJSONArray("result").length() > 0){
                            llHas.setVisibility(View.GONE);
                            List<CartBean> lsJson = FastJsonTools.getPersons(object.optString("result"),CartBean.class);
                            setAdapter(lsJson);
                        }else {
                            llHas.setVisibility(View.VISIBLE);
                            txtLabel.setText(cart_type == 1?"购物车没有项目~":"购物车没有商品~");
                            List<CartBean> lsJson = new ArrayList<>();
                            setAdapter(lsJson);
                        }
                        txtTotalPrice.setText("合计：¥"+StringUtils.formatDouble(getTotalPrice()));
                    } else {
                        ToastUtil.show(mContext,object.optString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case HttpModeBase.HTTP_REQUESTCODE_2:
                try {
                    JSONObject object = new JSONObject((String) msg.obj);
                    if (object.optInt("status") == 1) {
                        if(isJieSuan){
                            if(cart_type == 1){
                                it = new Intent(mContext,ConfirmProjectOrderActivityV2.class);
                                it.putExtra("is_box_buy",0);
                                it.putExtra("is_cart",1);
                                it.putExtra("item_shop_data",getOrderData());
                                startActivityForResult(it, AppConfig.ACTIVITY_RESULTCODE);
                            }else {
                                it = new Intent(mContext,ConfirmOrderActivity.class);
                                it.putExtra("item_shop_data",getOrderData());
                                it.putExtra("is_cart",1);
                                startActivityForResult(it, AppConfig.ACTIVITY_REQUESTCODE);
                                isJieSuan = false;
                            }
                        }
                    } else {
                        ToastUtil.show(mContext,object.optString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case HttpModeBase.HTTP_REQUESTCODE_3://加入收藏夹
                try {
                    JSONObject object = new JSONObject((String) msg.obj);
                    if (object.optInt("status") == 1) {
                        ToastUtil.show(mContext,object.optString("message"));
                    } else {
                        ToastUtil.show(mContext,object.optString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case HttpModeBase.HTTP_ERROR:
                String result_e = (String) msg.obj;
                ToastUtil.show(mContext,result_e);
                break;
        }
        return false;
    }

    private void setAdapter(List<CartBean> lsJson) {
        list.clear();
        list.addAll(lsJson);
        for (int i = 0; i < list.size(); i++){
            list.get(i).setBool(isChildAllSelected(i));
        }
        ivChoose.setSelected(isAllSelected());
        if(adapter == null){
            adapter = new ShoppingCartRecyclerAdapter(mContext,list);
            recyclerView.setAdapter(adapter);
        }else {
            adapter.notifyDataSetChanged();
        }
        adapter.setOnItemClickListener(new ShoppingCartRecyclerAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onChooseClick(int position) {
                list.get(position).setBool(!list.get(position).isBool());
                if(isChildAllSelected(position)){
                    for (CartGoodsBean cartGoodsBean : list.get(position).getDetail()){
                        cartGoodsBean.setIs_check(0);
                    }
                }else {
                    for (CartGoodsBean cartGoodsBean : list.get(position).getDetail()){
                        cartGoodsBean.setIs_check(1);
                    }
                }
                ivChoose.setSelected(isAllSelected());
                txtTotalPrice.setText("合计：¥"+StringUtils.formatDouble(getTotalPrice()));
                adapter.notifyItemChanged(position);
            }

            @Override
            public void onChooseItemClick(int position, int pos) {
                list.get(position).getDetail().get(pos).setIs_check(list.get(position).getDetail().get(pos).getIs_check() == 1?0:1);
                list.get(position).setBool(isChildAllSelected(position));
                ivChoose.setSelected(isAllSelected());
                txtTotalPrice.setText("合计：¥"+StringUtils.formatDouble(getTotalPrice()));
                adapter.notifyItemChanged(position);
            }

            @Override
            public void onMinusClick(int position, int pos) {
                list.get(position).getDetail().get(pos).setNum(list.get(position).getDetail().get(pos).getNum()-1);
                txtTotalPrice.setText("合计：¥"+StringUtils.formatDouble(getTotalPrice()));
                adapter.notifyItemChanged(position);
            }

            @Override
            public void onPlusClick(int position, int pos) {
                list.get(position).getDetail().get(pos).setNum(list.get(position).getDetail().get(pos).getNum()+1);
                txtTotalPrice.setText("合计：¥"+StringUtils.formatDouble(getTotalPrice()));
                adapter.notifyItemChanged(position);
            }

            @Override
            public void onLlClick(int position, int pos) {
                saveCart();
                it = new Intent(mContext,GoodsDetailActivity.class);
                it.putExtra("type",cart_type == 1 ? 2 : 1);
                it.putExtra("shop_id",list.get(position).getDetail().get(pos).getShop_id());
                it.putExtra("item_id",list.get(position).getDetail().get(pos).getItem_id());
                it.putExtra("position",position);
                it.putExtra("pos",pos);
                startActivityForResult(it,AppConfig.ACTIVITY_REQUESTCODE);
            }
        });
    }

    /**
     * 判断是否选中
     * @return	true有条目被选中
     */
    private boolean isSelected(){
        boolean flag = false;
        for(int i=0 ; i < list.size() && !flag ; i++){
            for(int j=0 ; j < list.get(i).getDetail().size() ; j++) {
                if (list.get(i).getDetail().get(j).getIs_check()==1) {
                    flag = true;
                    break;
                }
            }
        }
        return flag;
    }
    /**
     * 判断是否选中
     * @return	true 店铺有条目被选中
     */
    private boolean isChildSelected(int pos){
        boolean flag = false;
        for(int i=0 ; i < list.get(i).getDetail().size() ; i++){
            if (list.get(pos).getDetail().get(i).getIs_check()==1) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    /**
     * 判断是否全部被选中
     * @return	true 所有条目全部被选中
     */
    private boolean isAllSelected(){
        boolean flag = true;
        for(int i=0 ; i < list.size() && flag ; i++){
            for(int j=0 ; j < list.get(i).getDetail().size() ; j++) {
                if (!(list.get(i).getDetail().get(j).getIs_check()==1)) {
                    flag = false;
                    break;
                }
            }
        }
        return flag;
    }
    /**
     * 判断是否全部被选中
     * @return	true 店铺所有条目全部被选中
     */
    private boolean isChildAllSelected(int pos){
        boolean flag = true;
        for(int i=0;i<list.get(pos).getDetail().size();i++){
            if(list.get(pos).getDetail().get(i).getIs_check()!=1){
                flag = false;
                break;
            }
        }
        return flag;
    }

    /**
     * 下单的商品数据
     * @return
     */
    private String getOrderData(){
        String jsonString ="";
        for (int i = 0 ; i < list.size() ; i++){
            for (CartGoodsBean bean : list.get(i).getDetail()){
                if(bean.getIs_check() == 1){
                    jsonString += "{" + "\"" + "item_shop_id" + "\"" + ":" + "\"" + bean.getItem_shop_id()+ "\"" + ","
                            + "\"" + "num" + "\"" + ":" + "\"" + bean.getNum() + "\"" + "}" + ",";
                }
            }
        }
        jsonString = "[" + jsonString.substring(0,jsonString.length()-1) +"]";
        return jsonString;
    }
    /**
     * 下单的商品id
     * @return
     */
    private String getItemIds(){
        String jsonString ="";
        for (int i = 0 ; i < list.size() ; i++){
            for (CartGoodsBean bean : list.get(i).getDetail()){
                if(bean.getIs_check() == 1){
                    jsonString +=  bean.getItem_shop_id()+ ",";
                }
            }
        }
        if(jsonString.endsWith(",")){
            jsonString = jsonString.substring(0,jsonString.length()-1);
        }
        return jsonString;
    }

    private double getTotalPrice(){
        double amount =0;
        for (int i = 0 ; i < list.size() ; i++){
            for (CartGoodsBean bean : list.get(i).getDetail()){
                if(bean.getIs_check() == 1){
                    amount +=  Double.parseDouble(bean.getPrice())*bean.getNum();
                }
            }
        }
        return amount;
    }

    @Override
    @OnClick({R.id.txt_left,R.id.txt_1,R.id.txt_2,R.id.txt_right,R.id.iv_choose,R.id.txt_add_collect,R.id.txt_delete,R.id.txt_settlement})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.txt_left:
                saveCart();
                finish();
                break;
            case R.id.txt_1:
                txt1.setSelected(true);txt2.setSelected(false);
                saveCart();cart_type = 1;initData();
                break;
            case R.id.txt_2:
                txt1.setSelected(false);txt2.setSelected(true);
                saveCart();cart_type = 2;initData();
                break;
            case R.id.txt_right:
                if(isEdit){
                    isEdit = false;txtRight.setText("编辑");
                    txtTotalPrice.setVisibility(View.VISIBLE);
                    llEdit.setVisibility(View.GONE);
                    txtSettlement.setVisibility(View.VISIBLE);
                }else {
                    isEdit = true;txtRight.setText("完成");
                    txtTotalPrice.setVisibility(View.GONE);
                    llEdit.setVisibility(View.VISIBLE);
                    txtSettlement.setVisibility(View.GONE);
                }
                break;
            case R.id.iv_choose:
                if(isAllSelected()){
                    for (int i = 0; i < list.size(); i++){
                        list.get(i).setBool(false);
                        for (CartGoodsBean cartGoodsBean : list.get(i).getDetail()){
                            cartGoodsBean.setIs_check(0);
                        }
                    }
                }else {
                    for (int i = 0; i < list.size(); i++){
                        list.get(i).setBool(true);
                        for (CartGoodsBean cartGoodsBean : list.get(i).getDetail()){
                            cartGoodsBean.setIs_check(1);
                        }
                    }
                }
                if(adapter != null){
                    adapter.notifyDataSetChanged();
                }
                ivChoose.setSelected(isAllSelected());
                break;
            case R.id.txt_add_collect:
                if(!isSelected()){
                    ToastUtil.show(mContext,"请选择要收藏的商品");
                }else {
                    mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_3,UrlUtils.cart_to_collect_set(LoginUtil.getInfo("token"),
                            LoginUtil.getInfo("uid"),getItemIds()),true);
                }
                break;
            case R.id.txt_delete:
                if(!isSelected()){
                    ToastUtil.show(mContext,"请选择要删除的商品");
                }else {
                    new AlertView(null, "确定要删除选中商品吗？", "取消", new String[]{"删除"}, null, mContext,
                            AlertView.Style.Alert, new OnItemClickListener() {
                        @Override
                        public void onItemClick(Object o, int position) {
                            if (position == 0) {
                                for (int i = 0; i < list.size(); i++){
                                    for (int j = 0 ;j < list.get(i).getDetail().size(); j++){
                                        if(list.get(i).getDetail().get(j).getIs_check() == 1){
                                            list.get(i).getDetail().remove(j);
                                            j --;
                                        }
                                    }
                                    if(list.get(i).getDetail().size() == 0){
                                        list.remove(i);
                                        i --;
                                    }
                                }
                                adapter.notifyDataSetChanged();
                                txtTotalPrice.setText("合计：¥"+StringUtils.formatDouble(getTotalPrice()));
                                if(list.size() == 0){
                                    llHas.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }).show();
                }
                break;
            case R.id.txt_settlement:
                if(!isSelected()){
                    ToastUtil.show(mContext,"请选择要下单的商品");
                }else {
                    isJieSuan = true;
                    saveCart();
                }
                break;
        }
    }

    private void saveCart() {
        String jsonString ="";
        for (int i = 0 ; i < list.size() ; i++){
            for (CartGoodsBean bean : list.get(i).getDetail()){
                jsonString += "{" + "\"" + "item_shop_id" + "\"" + ":" + "\"" + bean.getItem_shop_id()+ "\"" + ","
                        + "\"" + "num" + "\"" + ":" + "\"" + bean.getNum() + "\"" + ","
                        + "\"" + "is_check" + "\"" + ":" + "\"" + bean.getIs_check() + "\"" + "}" + ",";
            }
        }
        if(list.size() > 0){
            jsonString = "[" + jsonString.substring(0,jsonString.length()-1) +"]";
        }else {
            jsonString = "[]";
        }
        mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_2,UrlUtils.cart_data_syn_set(LoginUtil.getInfo("token"),
                LoginUtil.getInfo("uid"),jsonString,cart_type),isJieSuan);
    }

    @Override
    public void onBackPressed() {
        saveCart();
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case AppConfig.ACTIVITY_RESULTCODE:
                initData();
                break;
            case AppConfig.ACTIVITY_RESULTCODE_4:
                int position = data.getIntExtra("position",-1);
                int pos = data.getIntExtra("pos",-1);
                int num = data.getIntExtra("num",0);
                if(position != -1 && pos != -1){
                    list.get(position).getDetail().get(pos).setNum(list.get(position).getDetail().get(pos).getNum()+num);
                    txtTotalPrice.setText("合计：¥"+StringUtils.formatDouble(getTotalPrice()));
                    adapter.notifyItemChanged(position);
                }
                break;
        }
    }
}
