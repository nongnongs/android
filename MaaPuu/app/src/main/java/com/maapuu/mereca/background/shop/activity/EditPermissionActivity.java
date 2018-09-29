package com.maapuu.mereca.background.shop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.luck.picture.lib.decoration.RecycleViewDivider;
import com.maapuu.mereca.R;
import com.maapuu.mereca.background.shop.bean.FuncBean;
import com.maapuu.mereca.base.BaseActivity;
import com.maapuu.mereca.constant.UrlUtils;
import com.maapuu.mereca.recycleviewutil.BaseRecyclerAdapter;
import com.maapuu.mereca.recycleviewutil.BaseRecyclerHolder;
import com.maapuu.mereca.recycleviewutil.FullyLinearLayoutManager;
import com.maapuu.mereca.util.FastJsonTools;
import com.maapuu.mereca.util.HttpModeBase;
import com.maapuu.mereca.util.LoginUtil;
import com.maapuu.mereca.util.StringUtils;
import com.maapuu.mereca.util.ToastUtil;
import com.maapuu.mereca.util.UIUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 编辑权限
 * Created by Jia on 2018/3/15.
 */

public class EditPermissionActivity extends BaseActivity {

    @BindView(R.id.txt_left)
    TextView txtLeft;
    @BindView(R.id.txt_title)
    TextView txtTitle;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private FullyLinearLayoutManager mLayoutManager;
    private BaseRecyclerAdapter<FuncBean> adapter;

    String title = "";
    String post_id = "";//岗位id，返回时会勾选已选择权限


    @Override
    public void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.shop_activity_edit_permission);
    }

    @Override
    public void initView() {
        getBundle();
        txtLeft.setTypeface(StringUtils.getFont(mContext));
        txtTitle.setText(title);

        mLayoutManager = new FullyLinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        //分割线
        recyclerView.addItemDecoration(new RecycleViewDivider(mContext, LinearLayoutManager.HORIZONTAL,
                UIUtils.dip2px(mContext,1),
                getResources().getColor(R.color.background)));

        adapter = new BaseRecyclerAdapter<FuncBean>(mContext, R.layout.shop_item_edit_permission) {
            @Override
            public void convert(BaseRecyclerHolder holder, FuncBean bean, int position, boolean isScrolling) {
                holder.setText(R.id.ep_name_tv,bean.getFunc_name());
                CheckBox cb = holder.getView(R.id.ep_cb);
                cb.setChecked(bean.getIs_sel()==1);
            }
        };
        adapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position) {
                FuncBean bean = adapter.getList().get(position);
                bean.setIs_sel(bean.getIs_sel()==1?0:1);
                adapter.notifyItemChanged(position);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void getBundle() {
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            title = bundle.getString("title","");
            post_id = bundle.getString("post_id",post_id);
        }
    }

    @Override
    public void initData() {
        getFuncList();
    }

    //权限列表
    private void getFuncList() {
        mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_1, UrlUtils.s_func_list_get(LoginUtil.getInfo("token"),LoginUtil.getInfo("uid"),post_id),true);
    }

    //设置适用店铺
    private void setFunc(String func_ids) {
        //权限可以全不选，即可为空
//        if(TextUtils.isEmpty(func_ids)){
//            ToastUtil.show(mContext,"请选择权限");
//            return;
//        }
        mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_2, UrlUtils.s_post_detail_func_set(LoginUtil.getInfo("token"),LoginUtil.getInfo("uid"),post_id,func_ids),true);
    }

    private String getFuncIds(){
        StringBuilder sb = new StringBuilder("");
        if(adapter != null && adapter.getList() != null && adapter.getList().size()>0){
            for (FuncBean bean:adapter.getList()) {
                if(bean.getIs_sel()==1){
                    sb.append(bean.getFunc_id()+",");
                }
            }
            if(sb.toString().endsWith(",")){
                sb.deleteCharAt(sb.length()-1);
            }
        }

        return sb.toString();
    }

    @Override
    @OnClick({R.id.txt_left,R.id.ep_save_tv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_left:
                finish();
                break;

            case R.id.ep_save_tv:
                //保存
                setFunc(getFuncIds());

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
                            List<FuncBean> list = FastJsonTools.getPersons(object.optString("result"), FuncBean.class);
                            //没有分页
                            adapter.clear();
                            adapter.addList(list);
                        }
                    } else {
                        ToastUtil.show(mContext, object.optString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case HttpModeBase.HTTP_REQUESTCODE_2:
                try {
                    JSONObject object = new JSONObject((String) msg.obj);
                    if (object.optInt("status") == 1) {
                        //通知上一界面刷新
                        setResult(-1,new Intent());
                        finish();

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

}
