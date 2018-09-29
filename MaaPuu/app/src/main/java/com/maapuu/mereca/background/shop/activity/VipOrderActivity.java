package com.maapuu.mereca.background.shop.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.bigkoo.pickerview.listener.CustomListener;
import com.luck.picture.lib.decoration.RecycleViewDivider;
import com.maapuu.mereca.R;
import com.maapuu.mereca.background.shop.bean.MemberOrderBean;
import com.maapuu.mereca.background.shop.bean.MemberOrderInitBean;
import com.maapuu.mereca.background.shop.dialog.ChooseShopFilter;
import com.maapuu.mereca.base.BaseActivity;
import com.maapuu.mereca.bean.ShopBean;
import com.maapuu.mereca.constant.UrlUtils;
import com.maapuu.mereca.recycleviewutil.BaseRecyclerAdapter;
import com.maapuu.mereca.recycleviewutil.BaseRecyclerHolder;
import com.maapuu.mereca.recycleviewutil.FullyLinearLayoutManager;
import com.maapuu.mereca.util.DateUtil;
import com.maapuu.mereca.util.FastJsonTools;
import com.maapuu.mereca.util.HttpModeBase;
import com.maapuu.mereca.util.LoginUtil;
import com.maapuu.mereca.util.StringUtils;
import com.maapuu.mereca.util.ToastUtil;
import com.maapuu.mereca.util.UIUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 会员订单
 * Created by Jia on 2018/3/10.
 */

public class VipOrderActivity extends BaseActivity{
    @BindView(R.id.txt_left)
    TextView txtLeft;
    @BindView(R.id.txt_title)
    TextView txtTitle;
    @BindView(R.id.txt_right)
    TextView txtRight;
    @BindView(R.id.txt_right2)
    TextView txtRight2;

    @BindView(R.id.ao_choose_shop_lt)
    LinearLayout chooseShopLt;
    @BindView(R.id.choose_shop_tv)
    TextView shopTv;

    @BindView(R.id.txt_year_card)
    TextView txtYearCard;
    @BindView(R.id.txt_project_card)
    TextView txtProjectCard;
    @BindView(R.id.txt_charge_card)
    TextView txtChargeCard;
    @BindView(R.id.ll_has)
    LinearLayout llHas;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private FullyLinearLayoutManager mLayoutManager;
    private BaseRecyclerAdapter<MemberOrderBean> adapter;
    private int page = 1;

    private TextView[] tvs;
    boolean isShowProgress = true;
    int card_type = 1;//会员类型：1会籍；2项目卡；3充值卡
    String shop_id = "0";//店铺id，全部店铺传0
    String date = "";//日期过滤，如：2018-04-18
    String member_no = "";//会员编号

    List<ShopBean> shopList;

    @Override
    public void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.shop_activity_vip_order);
    }

    @Override
    public void initView() {
        txtLeft.setTypeface(StringUtils.getFont(mContext));
        txtTitle.setText("会员订单");
        txtRight.setTypeface(StringUtils.getFont(mContext));
        txtRight2.setTypeface(StringUtils.getFont(mContext));
        txtRight2.setVisibility(View.GONE);

        tvs = new TextView[]{txtYearCard,txtProjectCard,txtChargeCard};
        setHead(0);

        refreshLayout.setEnableRefresh(true);//是否启用下拉刷新功能
        refreshLayout.setEnableLoadMore(true);//是否启用上拉加载功能
        refreshLayout.setEnableAutoLoadMore(false);//是否启用自动加载功能
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                page = 1;
                getMemberList();
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(final RefreshLayout refreshLayout) {
                page++;
                getMemberList();
            }
        });
        mLayoutManager = new FullyLinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        //分割线
        recyclerView.addItemDecoration(new RecycleViewDivider(mContext, LinearLayoutManager.HORIZONTAL,
                UIUtils.dip2px(mContext,10),
                getResources().getColor(R.color.background)));

        adapter = new BaseRecyclerAdapter<MemberOrderBean>(mContext, R.layout.shop_item_vip_order) {
            @Override
            public void convert(BaseRecyclerHolder holder, MemberOrderBean bean, int position, boolean isScrolling) {
                ImageView cardIv = holder.getView(R.id.card_img);
                switch (bean.getCard_type()){
                    case 1:
                        cardIv.setImageResource(R.mipmap.nianka);
                        break;
                    case 2:
                        cardIv.setImageResource(R.mipmap.xiangmuka);
                        break;
                    case 3:
                        cardIv.setImageResource(R.mipmap.chongzhika);
                        break;
                }

                holder.setText(R.id.card_name,bean.getCard_name());
                holder.setText(R.id.recharge_amount,"¥"+bean.getRecharge_amount());
                holder.setText(R.id.member_name,bean.getMember_name());
                holder.setText(R.id.member_phone,bean.getMember_phone());
                holder.setText(R.id.order_no,"订单编号："+bean.getOrder_no());
                holder.setText(R.id.member_no,"会员卡："+bean.getMember_no());
                holder.setText(R.id.pay_time,"时间："+bean.getPay_time());
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position) {
                //会员详情
                MemberOrderBean bean = adapter.getList().get(position);
                it = new Intent(mContext, VipDetailActivity.class);
                it.putExtra("member_id",bean.getMember_id());
                startActivity(it);
            }
        });
    }

    private void setHead(int position) {
        //会员类型：1会籍；2项目卡；3充值卡
        card_type = position +1;
        for (int i = 0; i < tvs.length; i++){
            if(i == position){
                tvs[position].setSelected(true);
            } else {
                tvs[i].setSelected(false);
            }
        }

        page = 1;
        getMemberList();
    }

    @Override
    public void initData() {
        getMemberList();
    }

    private void getMemberList() {
        mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_1,
                UrlUtils.s_order_member_list_get(LoginUtil.getInfo("token"), LoginUtil.getInfo("uid"),
                        card_type+"",shop_id,date,member_no,page),isShowProgress);
    }

    private void setUI(MemberOrderInitBean bean) {
        if(bean.getShop_list() != null && bean.getShop_list().size()>0) shopList = bean.getShop_list();

        if(page == 1 && (bean.getMember_order() == null || bean.getMember_order().size() ==0)){
            llHas.setVisibility(View.VISIBLE);
        }else {
            llHas.setVisibility(View.GONE);
        }
        List<MemberOrderBean> list = bean.getMember_order();
        if(page == 1) adapter.clear();
        adapter.addList(list);
        if(page > 1){
            if((list ==null || list.size()==0)){
                page--;
                ToastUtil.show(mContext,"暂无更多数据");
            }
        }
    }

    @Override
    @OnClick({R.id.txt_left,R.id.txt_right,R.id.txt_clear,R.id.ao_choose_shop_lt,R.id.txt_right2,
            R.id.txt_year_card,R.id.txt_project_card,R.id.txt_charge_card})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_left:
                finish();
                break;
            case R.id.txt_clear:
                date ="";
                page = 1;
                getMemberList();
                break;
            case R.id.ao_choose_shop_lt:
                //选择商铺
                if(shopList != null && shopList.size()>0){
                    ChooseShopFilter chooseShopFilter = new ChooseShopFilter(mContext, shopList, new ChooseShopFilter.ChooseCall() {
                        @Override
                        public void onChoose(ShopBean item) {
                            shopTv.setText(item.getShop_name());
                            shop_id = item.getShop_id();
                            page = 1;
                            getMemberList();
                        }
                    });
                    chooseShopFilter.createPopup();
                    chooseShopFilter.showAsDropDown(chooseShopLt);
                } else {
                    ToastUtil.show(mContext,"没有可供选择的商铺");
                }

                break;

            case R.id.txt_right:
                //弹出时间滚轮
                showTimePv();
                break;

            case R.id.txt_right2:
                //ToastUtil.show(mContext,"搜索");

                break;

            case R.id.txt_year_card:
                setHead(0);
                break;
            case R.id.txt_project_card:
                setHead(1);
                break;
            case R.id.txt_charge_card:
                setHead(2);
                break;

        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case HttpModeBase.HTTP_REQUESTCODE_1:
                try {
                    isShowProgress = false;
                    refreshLayout.finishRefresh();
                    refreshLayout.finishLoadMore();
                    JSONObject object = new JSONObject((String) msg.obj);
                    if (object.optInt("status") == 1) {
                        if (!StringUtils.isEmpty(object.optString("result"))) {
                            MemberOrderInitBean bean = FastJsonTools.getPerson(object.optString("result"), MemberOrderInitBean.class);
                            if(bean != null){
                                setUI(bean);
                            }
                        }
                    } else {
                        ToastUtil.show(mContext, object.optString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case HttpModeBase.HTTP_ERROR:
                refreshLayout.finishRefresh();
                refreshLayout.finishLoadMore();
                String result_e = (String) msg.obj;
                ToastUtil.show(mContext, result_e);
                break;
        }
        return false;
    }


    TimePickerView pvCustomTime;
    private void showTimePv() {

        /**
         * 注意事项：
         * 月份是0-11
         * setRangDate方法控制起始终止时间(如果不设置范围，则使用默认时间1900-2100年，此段代码可注释)
         */
        Calendar selectedDate = Calendar.getInstance();//系统当前时间
        Calendar startDate = Calendar.getInstance();
        startDate.set(2000, 0, 1);
        Calendar endDate = Calendar.getInstance();
        //Calendar endDate = selectedDate;
        //时间选择器 ，自定义布局
        pvCustomTime = new TimePickerView.Builder(mContext, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date1, View v) {//选中事件回调
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date1);

                date =DateUtil.calendar2String(calendar, DateUtil.FORMAT_DATE);
                page = 1;
                getMemberList();
            }
        })
                .setDate(selectedDate)
                .setRangDate(startDate, endDate)
                .setLayoutRes(R.layout.my_pickerview_custom_time, new CustomListener() {

                    @Override
                    public void customLayout(View v) {
                        final TextView tvSubmit = (TextView) v.findViewById(R.id.tv_finish);
                        TextView tvCancel = (TextView) v.findViewById(R.id.tv_cancel);
                        tvSubmit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(pvCustomTime != null){
                                    pvCustomTime.returnData();
                                    pvCustomTime.dismiss();
                                }

                            }
                        });
                        tvCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvCustomTime.dismiss();
                            }
                        });
                    }
                })
                .setLineSpacingMultiplier(2.0f)//设置两横线之间的间隔倍数
                .setType(new boolean[]{true, true, true, false, false, false})
                .setLabel("年", "月", "日", "", "", "")
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setTextColorCenter(Color.parseColor("#333333"))//设置选中项的颜色
                .setDividerColor(Color.parseColor("#EEEEEE"))
                .build();

        pvCustomTime.show();
    }

}
