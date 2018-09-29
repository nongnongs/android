package com.maapuu.mereca.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.maapuu.mereca.R;
import com.maapuu.mereca.base.BaseActivity;
import com.maapuu.mereca.bean.SrvStaffBean;
import com.maapuu.mereca.bean.StaffBean;
import com.maapuu.mereca.constant.UrlUtils;
import com.maapuu.mereca.recycleviewutil.BaseRecyclerAdapter;
import com.maapuu.mereca.recycleviewutil.BaseRecyclerHolder;
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
 *
 * Created by dell on 2018/2/24.
 */

public class ChooseHairstylistActivity extends BaseActivity implements BaseRecyclerAdapter.OnItemClickListener{
    @BindView(R.id.txt_left)
    TextView txtLeft;
    @BindView(R.id.txt_title)
    TextView txtTitle;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.txt_menu_1)
    TextView txtMenu1;
    @BindView(R.id.txt_menu_2)
    TextView txtMenu2;

    private TextView[] tvs;

    private LinearLayoutManager mLayoutManager;
    private List<StaffBean> list = new ArrayList<>();
    private List<StaffBean> list_free = new ArrayList<>();
    private List<StaffBean> list_busy = new ArrayList<>();
    private BaseRecyclerAdapter<StaffBean> adapter;
    private int page = 1;
    String appoint_srv_id = "";//预约服务id
    int currentMenu = 0;
    SrvStaffBean bean;

    String srv_id;//用于区分是哪个服务选项
    private AlertView alertView;

    @Override
    public void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_choose_hairstylist);
    }

    @Override
    public void initView() {
        appoint_srv_id = getIntent().getStringExtra("appoint_srv_id");
        srv_id = getIntent().getStringExtra("srv_id");
        txtLeft.setTypeface(StringUtils.getFont(mContext));
        txtTitle.setText("选择服务人员");

        tvs = new TextView[]{txtMenu1, txtMenu2};
        setHead(0);

        mLayoutManager=new FullyLinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mLayoutManager);
        //这句就是添加我们自定义的分隔线
        recyclerView.addItemDecoration(new RecyclerViewDivider(mContext, LinearLayoutManager.VERTICAL,20
                ,getResources().getColor(R.color.background)));

        adapter = new BaseRecyclerAdapter<StaffBean>(mContext,R.layout.item_choose_srv_staff) {
            @Override
            public void convert(BaseRecyclerHolder holder, StaffBean bean, int position, boolean isScrolling) {
                SimpleDraweeView icon = holder.getView(R.id.image);
                TextView tvWaitNum = holder.getView(R.id.tv_wait_num);
                icon.setImageURI(Uri.parse(bean.getStaff_avatar()));
                holder.setText(R.id.tv_staff_name,bean.getStaff_name());
                holder.setText(R.id.tv_post_name,bean.getPost_name());
                holder.setText(R.id.txt_fans_num,"粉丝 " + bean.getFans_num());
                holder.setText(R.id.txt_works_num,"作品 " + bean.getWorks_num());
                tvWaitNum.setText(bean.getNum());
                holder.setVisible(R.id.tv_attention_status,bean.getIs_attention()==1);
            }
        };
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void initData() {
        getStaffList();
    }

    private void getStaffList(){
        mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_1, UrlUtils.order_staff_get(LoginUtil.getInfo("token"),LoginUtil.getInfo("uid"),appoint_srv_id),true);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case HttpModeBase.HTTP_REQUESTCODE_1:

                try {
                    JSONObject object = new JSONObject((String) msg.obj);
                    if (object.optInt("status") == 1) {
                        if(!StringUtils.isEmpty(object.optString("result"))){
                            SrvStaffBean bean = FastJsonTools.getPerson(object.optString("result"),SrvStaffBean.class);
                            if(bean != null) {
                                list_free = bean.getFree_data();
                                list_busy = bean.getBusy_data();
                            }
                            updateUI();
                        }
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

    private void updateUI() {
        if(currentMenu == 0){
            list = list_free;
        } else {
            list = list_busy;
        }

        adapter.clear();
        adapter.addList(list);
    }

    @Override
    @OnClick({R.id.txt_left,R.id.txt_menu_1,R.id.txt_menu_2})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.txt_left:
                finish();
                break;

            case R.id.txt_menu_1:
                setHead(0);
                updateUI();

                break;

            case R.id.txt_menu_2:
                setHead(1);
                updateUI();

                break;
        }
    }

    @Override
    public void onItemClick(RecyclerView parent, View view, final int position) {
        alertView = new AlertView(null, "设定该发型师为您服务吗？", "取消", null, new String[]{"确定"}, mContext,
                AlertView.Style.Alert, new OnItemClickListener() {
            @Override
            public void onItemClick(Object o, int pos) {
                if (pos == 0) {
                    StaffBean bean = adapter.getList().get(position);
                    Intent intent = new Intent();
                    intent.putExtra("srv_id",srv_id);
                    intent.putExtra("staff_id",bean.getStaff_id());
                    intent.putExtra("staff_name",bean.getStaff_name());
                    setResult(-1,intent);

                    finish();
                }
            }
        });
        alertView.show();
    }

    private void setHead(int position) {
        currentMenu = position;
        for (int i = 0; i < tvs.length; i++){
            if(i == position){
                tvs[position].setSelected(true);
            }else {
                tvs[i].setSelected(false);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(alertView != null && alertView.isShowing()){
            alertView.dismiss();
        }else {
            super.onBackPressed();
        }
    }
}
