package com.maapuu.mereca.background.shop.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.bigkoo.pickerview.TimePickerView;
import com.bigkoo.pickerview.listener.CustomListener;
import com.luck.picture.lib.decoration.RecycleViewDivider;
import com.maapuu.mereca.R;
import com.maapuu.mereca.background.employee.bean.MyAppointBean;
import com.maapuu.mereca.background.shop.dialog.ChooseShopFilter;
import com.maapuu.mereca.base.BaseActivity;
import com.maapuu.mereca.bean.ShopBean;
import com.maapuu.mereca.bean.SrvBean;
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
import com.maapuu.mereca.view.CircleImgView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 预约单
 * Created by Jia on 2018/3/8.
 */

public class AppointmentOrderActivity extends BaseActivity {
    @BindView(R.id.txt_left)
    TextView txtLeft;
    @BindView(R.id.txt_title)
    TextView txtTitle;
    @BindView(R.id.txt_right)
    TextView txtRight;

    @BindView(R.id.ao_choose_shop_lt)
    LinearLayout chooseShopLt;
    @BindView(R.id.choose_shop_tv)
    TextView shopTv;

    @BindView(R.id.ma_date_tv)
    TextView dateTv;
    @BindView(R.id.ma_num_tv)
    TextView numTv;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private FullyLinearLayoutManager mLayoutManager;
    private BaseRecyclerAdapter<MyAppointBean.BegingSrvBean> adapter;
    //private int page = 1;

    int is_admin = 1;//是否我的店铺。0为工作狂，1为我的店铺
    List<ShopBean> shopList;
    String shop_id = "0";//我的店铺里面需要店铺过滤，工作狂里面不需要店铺过滤。店铺id，全部店铺传0
    private String date = "";//日期，如:2018-04-08
    private String today = "";//日期，如:2018-04-08

    @Override
    public void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.shop_activity_appointment_order);
    }

    @Override
    public void initView() {
        txtLeft.setTypeface(StringUtils.getFont(mContext));
        txtTitle.setText("预约单");
        txtRight.setVisibility(View.VISIBLE);
        txtRight.setTypeface(StringUtils.getFont(mContext));
        txtRight.setText("\ue69a");
        txtRight.setTextSize(17);
        date = StringUtils.getTime(new Date());

        mLayoutManager = new FullyLinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        //分割线
        recyclerView.addItemDecoration(new RecycleViewDivider(mContext, LinearLayoutManager.HORIZONTAL, 2,
                getResources().getColor(R.color.background)));
        adapter = new BaseRecyclerAdapter<MyAppointBean.BegingSrvBean>(mContext, R.layout.em_item_my_appointment) {
            @Override
            public void convert(final BaseRecyclerHolder holder, final MyAppointBean.BegingSrvBean bean, final int position, boolean isScrolling) {
                CircleImgView icon = holder.getView(R.id.ma_icon);
                UIUtils.loadIcon(mContext,bean.getAvatar(),icon);
                holder.setText(R.id.ma_nick_name,bean.getNick_name());
                holder.setText(R.id.ma_phone,bean.getPhone());
                holder.setText(R.id.ma_item_name,bean.getItem_name());
                holder.setText(R.id.ma_pay_amount,"¥"+bean.getPay_amount());
                holder.setText(R.id.ma_srv_name,bean.getSrv_name());
                holder.setText(R.id.ma_staff_name,bean.getStaff_name());
                holder.setText(R.id.ma_appoint_time_text,bean.getAppoint_time_text());
                switch (bean.getCode_status()){
                    case 1:
                        holder.setText(R.id.txt_code_status,"未预约");
                        break;
                    case 2:
                        holder.setText(R.id.txt_code_status,"已预约");
                        break;
                    case 3:
                        holder.setText(R.id.txt_code_status,"服务中");
                        break;
                    case 4:
                        holder.setText(R.id.txt_code_status,"已使用");
                        break;
                    case 5:
                        holder.setText(R.id.txt_code_status,"已完成");
                        break;
                }

                RecyclerView recyclerViewItem = holder.getView(R.id.recycler_view_item);
                recyclerViewItem.setLayoutManager(new FullyLinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
                recyclerViewItem.setHasFixedSize(true);
                recyclerViewItem.setAdapter(new ServiceAdapter(mContext,bean.getSrv_list()));

                holder.setOnClickListener(R.id.ma_call, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!TextUtils.isEmpty(bean.getPhone())){
                            call(bean.getPhone());
                        }
                    }
                });
                recyclerViewItem.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return holder.getView(R.id.ll).onTouchEvent(event);
                    }
                });

                holder.setOnClickListener(R.id.ll, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        it = new Intent(mContext,ShopOrderProjectDetailActivity.class);
                        it.putExtra("oid",bean.getOid());
                        startActivity(it);
                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);
//        adapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(RecyclerView parent, View view, int position) {
//                it = new Intent(mContext,ShopOrderProjectDetailActivity.class);
//                it.putExtra("oid",adapter.getList().get(position).getOid());
//                startActivity(it);
//            }
//        });
    }

    //服务套餐列表
    class ServiceAdapter extends BaseRecyclerAdapter<SrvBean> {

        public ServiceAdapter(Context context, List list) {
            super(context, list, R.layout.em_item_my_appointment_item);
        }

        @Override
        public void convert(BaseRecyclerHolder holder, SrvBean item, int position, boolean isScrolling) {
            holder.setText(R.id.ma_srv_name,item.getSrv_name());
            holder.setText(R.id.ma_staff_name,item.getStaff_name());
        }
    }

    @Override
    public void initData() {
        today = DateUtil.getNowDate();
        date = today;
        getAppointSrv();
    }

    private void getAppointSrv(){
        mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_1, UrlUtils.s_appoint_srv_get(LoginUtil.getInfo("token"),LoginUtil.getInfo("uid"),is_admin,shop_id,date),true);
    }

    @Override
    @OnClick({R.id.txt_left,R.id.txt_clear,R.id.txt_right,R.id.ao_choose_shop_lt})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_left:
                finish();
                break;
            case R.id.txt_clear:
                date = "";
                dateTv.setText("全部预约人数");
                getAppointSrv();
                break;
            case R.id.txt_right:
                //弹出时间滚轮
                showTimePv();
                break;

            case R.id.ao_choose_shop_lt:
                //选择商铺
                if(shopList != null && shopList.size()>0){
                    ChooseShopFilter chooseShopFilter = new ChooseShopFilter(mContext, shopList, new ChooseShopFilter.ChooseCall() {
                        @Override
                        public void onChoose(ShopBean item) {
                            shopTv.setText(item.getShop_name());
                            shop_id = item.getShop_id();
                            //page = 1;
                            getAppointSrv();
                        }
                    });
                    chooseShopFilter.createPopup();
                    chooseShopFilter.showAsDropDown(chooseShopLt);
                } else {
                    ToastUtil.show(mContext,"没有可供选择的商铺");
                }


                break;
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case HttpModeBase.HTTP_REQUESTCODE_1:
                try {
                    JSONObject object = new JSONObject((String) msg.obj);
                    if (object.optInt("status") == 1) {
                        if (!StringUtils.isEmpty(object.optString("result"))) {
                            MyAppointBean bean = FastJsonTools.getPerson(object.optString("result"), MyAppointBean.class);
                            if (bean != null) {
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

                String result_e = (String) msg.obj;
                ToastUtil.show(mContext, result_e);
                break;
        }
        return false;
    }

    private void setUI(MyAppointBean bean) {
        if(shopList == null || shopList.size() == 0){
            shopList = bean.getShop_list();
        }

        numTv.setText(bean.getCount()+"人");
        List<MyAppointBean.BegingSrvBean> list = bean.getBeging_srv();
        if(list != null){
            adapter.clear();
            adapter.addList(list);
        }
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

                date = DateUtil.calendar2String(calendar, DateUtil.FORMAT_DATE);
                dateTv.setText(today.equals(date)?"今天预约人数":date+"预约人数");
                getAppointSrv();
            }
        })
                .setDate(selectedDate)
//                .setRangDate(startDate, endDate)
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

    private void call(final String phone){
        new AlertView(null, "TEL:"+phone, "取消", null, new String[]{"拨打"}, mContext,
                AlertView.Style.Alert, new OnItemClickListener() {
            @Override
            public void onItemClick(Object o, int position) {
                if (position == 0) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + phone));
                    startActivity(intent);
                }
            }
        }).show();
    }

}
