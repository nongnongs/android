package com.maapuu.mereca.fragment.homechild;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

import com.maapuu.mereca.R;
import com.maapuu.mereca.activity.GoodsDetailActivity;
import com.maapuu.mereca.activity.ShopDetailActivity;
import com.maapuu.mereca.adapter.LeftListViewAdapter;
import com.maapuu.mereca.base.BaseFragment;
import com.maapuu.mereca.bean.CatalogBean;
import com.maapuu.mereca.bean.GoodsBean;
import com.maapuu.mereca.callback.LeftCallBack;
import com.maapuu.mereca.constant.UrlUtils;
import com.maapuu.mereca.fragment.HomeFragment;
import com.maapuu.mereca.recycleviewutil.BaseRecyclerAdapter;
import com.maapuu.mereca.recycleviewutil.BaseRecyclerHolder;
import com.maapuu.mereca.recycleviewutil.FloatingItemDecoration;
import com.maapuu.mereca.recycleviewutil.RecyclerViewDivider;
import com.maapuu.mereca.util.FastJsonTools;
import com.maapuu.mereca.util.HttpModeBase;
import com.maapuu.mereca.util.LoginUtil;
import com.maapuu.mereca.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * Created by dell on 2018/2/22.
 * 主页——商品 v2
 */

public class HomeGoodsFragment_v2 extends BaseFragment implements LeftCallBack{
    @BindView(R.id.recycler_view_left)
    RecyclerView listViewLeft;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private List<CatalogBean> list;
    private List<GoodsBean> goods;
    private List<Integer> catenums;//每个分类的个数
    private LeftListViewAdapter leftAdapter;
    private BaseRecyclerAdapter<GoodsBean> adapter;
    private FloatingItemDecoration floatingItemDecoration;
    private Map<Integer,String> keys=new HashMap<>();//存放所有key的位置和内容

    private String shop_id;
    private LinearLayoutManager mLinearLayoutManager;

    @Override
    protected int setContentViewById() {
        Bundle bundle=getArguments();
        //判断需写
        if(bundle!=null) {
            shop_id = bundle.getString("shop_id");
        }
        return R.layout.fragment_home_goods_v2;
    }

    public void refresh(String shop_id){
        this.shop_id = shop_id;
        initData();
    }

    @Override
    protected void initView(View v) {
        list = new ArrayList<>();
        catenums = new ArrayList<>();
        goods = new ArrayList<>();

        listViewLeft.setLayoutManager(new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false));
        listViewLeft.addItemDecoration(new RecyclerViewDivider(mContext, LinearLayoutManager.VERTICAL,1
                ,getResources().getColor(R.color.background)));

        mLinearLayoutManager = new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        floatingItemDecoration = new FloatingItemDecoration(mContext,mContext.getResources().getColor(R.color.line),0.5f,0.5f);
        floatingItemDecoration.setmTitleHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,40,getResources().getDisplayMetrics()));
    }

    @Override
    protected void initData() {
        mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_1, UrlUtils.commodity_list_new_get(LoginUtil.getInfo("token"),LoginUtil.getInfo("uid"),shop_id),true);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case HttpModeBase.HTTP_REQUESTCODE_1:
                if(ShopDetailActivity.activity != null){
                    ShopDetailActivity.activity.mHandler.sendEmptyMessage(888);
                }else {
                    if(HomeFragment.homeFragment != null){
                        HomeFragment.homeFragment.mHandler.sendEmptyMessage(888);
                    }
                }
                try {
                    JSONObject object = new JSONObject((String) msg.obj);
                    if (object.optInt("status") == 1) {
                        if(object.optJSONArray("result").length() > 0){
                            list = FastJsonTools.getPersons(object.optString("result"),CatalogBean.class);
                        }
                        setAdapter();
                    } else {
                        ToastUtil.show(mContext,object.optString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case HttpModeBase.HTTP_ERROR:
                if(HomeFragment.homeFragment != null){
                    HomeFragment.homeFragment.mHandler.sendEmptyMessage(888);
                }
                String result_e = (String) msg.obj;
                ToastUtil.show(mContext,result_e);
                break;
        }
        return false;
    }

    private void setAdapter() {
        goods.clear();
        catenums.clear();
        keys.clear();
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < list.size(); i++){
            keys.put(goods.size(),list.get(i).getCatalog_name());
            catenums.add(list.get(i).getItems().size());
            goods.addAll(list.get(i).getItems());
            if(i == 0){
                map.put(i, 1);
            }else {
                map.put(i, 0);
            }
        }
        leftAdapter = new LeftListViewAdapter(getActivity(), list, this);
        leftAdapter.setMap(map);
        listViewLeft.setAdapter(leftAdapter);
        if(adapter == null){
            floatingItemDecoration.setKeys(keys);
            recyclerView.addItemDecoration(floatingItemDecoration);
            adapter = new BaseRecyclerAdapter<GoodsBean>(mContext,goods,R.layout.layout_home_goods_item_v2) {
                @Override
                public void convert(BaseRecyclerHolder holder, GoodsBean bean, int position, boolean isScrolling) {
                    holder.setImage(R.id.iv_goods,bean.getItem_img(),false);
                    holder.setText(R.id.txt_goods_name,bean.getItem_name());
                    holder.setText(R.id.txt_price,"¥"+bean.getPrice());
                    holder.setText(R.id.txt_sale_num,"已售"+bean.getSale_num()+"件");
                    holder.setText(R.id.txt_market_price,"门市价 ¥"+bean.getMarket_price());
                    holder.setTextFlags(R.id.txt_market_price, Paint.STRIKE_THRU_TEXT_FLAG);
                    if(bean.getPrice().equals(bean.getMarket_price()) || Double.parseDouble(bean.getPrice()) == Double.parseDouble(bean.getMarket_price())){
                        holder.setVisible(R.id.txt_market_price,false);
                    }else {
                        holder.setVisible(R.id.txt_market_price,true);
                    }
                }
            };
            recyclerView.setAdapter(adapter);
        }else {
            mLinearLayoutManager.scrollToPositionWithOffset(0, 0);
            adapter.notifyDataSetChanged();
        }
        adapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position) {
                it = new Intent(mContext,GoodsDetailActivity.class);
                it.putExtra("type",1);
                it.putExtra("shop_id",shop_id);
                it.putExtra("item_id",goods.get(position).getItem_id());
                startActivity(it);
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();
                for (int i = 0; i < catenums.size(); i++) {
                    int total = 0;
                    for (int j = 0; j < i + 1; j++) {
                        total += catenums.get(j);
                    }
                    if (total >= (firstVisibleItem + 1)) {
                        Map<Integer, Integer> map = leftAdapter.getMap();
                        for (int z = 0; z < map.size(); z++) {
                            map.put(z, 0);
                        }
                        map.put(i, 1);
                        leftAdapter.notifyDataSetChanged();
                        moveToCenter(i);
                        break;
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void changeLayout(int position) {
        int total = 0;
        for (int i = 0; i < position; i++) {
            total += catenums.get(i);
        }
        mLinearLayoutManager.scrollToPositionWithOffset(total, 0);
    }

    //将当前选中的item居中
    private void moveToCenter(int position) {
        //将点击的position转换为当前屏幕上可见的item的位置以便于计算距离顶部的高度，从而进行移动居中
        View childAt = listViewLeft.getChildAt(position - mLinearLayoutManager.findFirstVisibleItemPosition());
        if (childAt != null) {
            int y = (childAt.getTop() - listViewLeft.getHeight() / 2);
            listViewLeft.smoothScrollBy(0, y);
        }
    }
}
