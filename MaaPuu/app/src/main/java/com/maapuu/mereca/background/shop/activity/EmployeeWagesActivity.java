package com.maapuu.mereca.background.shop.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.luck.picture.lib.decoration.RecycleViewDivider;
import com.maapuu.mereca.R;
import com.maapuu.mereca.background.employee.activity.SalaryDetailActivity;
import com.maapuu.mereca.background.shop.bean.StaffBean;
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
import com.maapuu.mereca.view.SearchEditText;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 员工工资
 * Created by Jia on 2018/3/10.
 */

public class EmployeeWagesActivity extends BaseActivity {
    @BindView(R.id.txt_left)
    TextView txtLeft;
    @BindView(R.id.txt_title)
    TextView txtTitle;
    @BindView(R.id.txt_right)
    TextView txtRight;
    @BindView(R.id.et_search)
    SearchEditText etSearch;
    @BindView(R.id.txt_shop_name)
    TextView txtShopName;
    @BindView(R.id.ao_choose_shop_lt)
    LinearLayout chooseShopLt;

    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private FullyLinearLayoutManager mLayoutManager;
    private BaseRecyclerAdapter<StaffBean> adapter;
    private List<StaffBean> list;
    private List<ShopBean> shopList;
    private int page = 1;
    private String shop_id;
    private String wage_month = "";
    private String keyword = "";

    @Override
    public void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.shop_activity_employee_wages);
    }

    @Override
    public void initView() {
        txtLeft.setTypeface(StringUtils.getFont(mContext));
        txtTitle.setText("员工工资");
        txtRight.setVisibility(View.VISIBLE);
        txtRight.setTypeface(StringUtils.getFont(mContext));
        txtRight.setText("\ue69a");
        txtRight.setTextSize(18);
//        shop_id = getIntent().getStringExtra("shop_id");
        wage_month = DateUtil.calendar2String(Calendar.getInstance(), DateUtil.FORMAT_YEAR_MONTH);
        shopList = new ArrayList<>();

        mLayoutManager = new FullyLinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        //分割线
        recyclerView.addItemDecoration(new RecycleViewDivider(mContext, LinearLayoutManager.HORIZONTAL,
                UIUtils.dip2px(mContext,1), getResources().getColor(R.color.background)));
        list = new ArrayList<>();

        refreshLayout.setEnableRefresh(true);//是否启用下拉刷新功能
        refreshLayout.setEnableLoadMore(true);//是否启用上拉加载功能
        refreshLayout.setEnableAutoLoadMore(false);//是否启用自动加载功能
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                page = 1;
                initData(page);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(final RefreshLayout refreshLayout) {
                initData(page);
            }
        });

        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH){
                    if(etSearch.getText().toString().isEmpty() || etSearch.getText().toString().equals("")){
                        keyword = "";
                    }else {
                        keyword = etSearch.getText().toString();
                    }
                    page = 1;
                    initData(page);
                }
                return false;
            }
        });
    }

    @Override
    public void initData() {
        mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_5, UrlUtils.s_select_shop_list_get(LoginUtil.getInfo("token"),LoginUtil.getInfo("uid"),0),false);
    }

    private void initData(int pageNum) {
        mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_1, UrlUtils.s_staff_wage_list_get(LoginUtil.getInfo("token"),LoginUtil.getInfo("uid"),
                shop_id,wage_month,keyword,pageNum),true);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case HttpModeBase.HTTP_REQUESTCODE_1:
                refreshLayout.finishRefresh();
                refreshLayout.finishLoadMore();
                try {
                    JSONObject object = new JSONObject((String) msg.obj);
                    if (object.optInt("status") == 1) {
                        JSONObject resultObj = object.optJSONObject("result");
                        if(resultObj.has("shop_list") && !StringUtils.isEmpty(resultObj.optString("shop_list"))){
                            shopList = FastJsonTools.getPersons(resultObj.optString("shop_list"),ShopBean.class);
                        }
                        if(!StringUtils.isEmpty(resultObj.optString("wage_month")) && resultObj.optJSONArray("wage_month").length() > 0){
                            List<StaffBean> lsJson = FastJsonTools.getPersons(resultObj.optString("wage_month"),StaffBean.class);
                            setAdapter(lsJson);
                            page ++ ;
                        }else {
                            List<StaffBean> lsJson = new ArrayList<>();
                            setAdapter(lsJson);
                            if(page != 1){
                                ToastUtil.show(mContext,"暂无更多数据");
                            }
                        }
                    } else {
                        ToastUtil.show(mContext, object.optString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case HttpModeBase.HTTP_REQUESTCODE_5:
                try {
                    JSONObject object = new JSONObject((String) msg.obj);
                    if (object.optInt("status") == 1) {
                        if (!StringUtils.isEmpty(object.optString("result"))) {
                            shopList = FastJsonTools.getPersons(object.optString("result"), ShopBean.class);
                            if(shopList != null && shopList.size() > 0){
                                txtShopName.setText(shopList.get(0).getShop_name());
                                shop_id = shopList.get(0).getShop_id();
                                initData(page);
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

    private void setAdapter(List<StaffBean> lsJson) {
        if(page == 1)
            list.clear();
        list.addAll(lsJson);
        if (adapter == null) {
            adapter = new BaseRecyclerAdapter<StaffBean>(mContext, list, R.layout.shop_item_employee_wages) {
                @Override
                public void convert(BaseRecyclerHolder holder, StaffBean bean, int position, boolean isScrolling) {
                    holder.setSimpViewImageUri(R.id.image, Uri.parse(bean.getStaff_avatar()));
                    holder.setText(R.id.txt_name,bean.getStaff_name());
                    holder.setText(R.id.txt_price,"¥"+bean.getWage_total()+"元");
                }
            };
            recyclerView.setAdapter(adapter);
        } else {
            if (page > 1) {
                adapter.notifyItemRangeInserted(list.size() - lsJson.size(), lsJson.size());
            } else {
                adapter.notifyDataSetChanged();
            }
        }
        adapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position) {
                it = new Intent(mContext, SalaryDetailActivity.class);
                it.putExtra("uid",list.get(position).getUid());
                startActivity(it);
            }
        });
    }

    @Override
    @OnClick({R.id.txt_left,R.id.txt_clear,R.id.txt_right,R.id.ao_choose_shop_lt})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_left:
                finish();
                break;
            case R.id.txt_clear:
                wage_month = "";
                page = 1;
                initData(page);
                break;
            case R.id.ao_choose_shop_lt:
                StringUtils.closeKeyBorder(mContext,chooseShopLt);
                ChooseShopFilter chooseShopFilter = new ChooseShopFilter(mContext, shopList, new ChooseShopFilter.ChooseCall() {
                    @Override
                    public void onChoose(ShopBean item) {
                        txtShopName.setText(item.getShop_name());
                        shop_id = item.getShop_id();
                        page = 1;
                        initData(page);
                    }
                });
                chooseShopFilter.createPopup();
                chooseShopFilter.showAsDropDown(chooseShopLt);
                break;
            case R.id.txt_right:
                StringUtils.closeKeyBorder(mContext,chooseShopLt);
                //弹出时间滚轮
                showTimePv();
                break;
        }
    }

    TimePickerView pvCustomTime;
    private void showTimePv() {
        Calendar selectedDate = Calendar.getInstance();//系统当前时间
        Calendar startDate = Calendar.getInstance();
        startDate.set(1970, 0, 1);
        pvCustomTime = new TimePickerView.Builder(mContext, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                wage_month = getTime(date);
                page = 1;
                initData(page);
            }
        })
                .setRangDate(startDate, selectedDate)
                .setCancelColor(UIUtils.getColor(R.color.text_99))
                .setSubmitColor(UIUtils.getColor(R.color.main_color))
                .setDate(selectedDate)
                .setLineSpacingMultiplier(2.0f)//设置两横线之间的间隔倍数
                .setType(new boolean[]{true, true, false, false, false, false})
                .setLabel("", "", "", "", "", "")
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setTextColorCenter(Color.parseColor("#333333"))//设置选中项的颜色
                .setDividerColor(Color.parseColor("#EEEEEE"))
                .build();
        pvCustomTime.show();
    }

    public String getTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        return format.format(date);
    }

}
