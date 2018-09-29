package com.maapuu.mereca.activity;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.luck.picture.lib.decoration.RecycleViewDivider;
import com.maapuu.mereca.R;
import com.maapuu.mereca.base.AppConfig;
import com.maapuu.mereca.base.BaseActivity;
import com.maapuu.mereca.bean.PhoneContactBean;
import com.maapuu.mereca.constant.UrlUtils;
import com.maapuu.mereca.recycleviewutil.BaseRecyclerAdapter;
import com.maapuu.mereca.recycleviewutil.BaseRecyclerHolder;
import com.maapuu.mereca.recycleviewutil.FullyLinearLayoutManager;
import com.maapuu.mereca.util.FastJsonTools;
import com.maapuu.mereca.util.HttpModeBase;
import com.maapuu.mereca.util.KeyBoardUtils;
import com.maapuu.mereca.util.LoginUtil;
import com.maapuu.mereca.util.StringUtils;
import com.maapuu.mereca.util.ToastUtil;
import com.maapuu.mereca.util.UIUtils;
import com.maapuu.mereca.view.CircleImgView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 手机通讯录
 * Created by Jia on 2018/5/18.
 */

public class PhoneContactsActivity extends BaseActivity{
    @BindView(R.id.txt_left)
    TextView txtLeft;
    @BindView(R.id.txt_title)
    TextView txtTitle;
    @BindView(R.id.search_ic)
    TextView searchIc;
    @BindView(R.id.search)
    EditText searchEt;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private FullyLinearLayoutManager mLayoutManager;
    private BaseRecyclerAdapter<PhoneContactBean> adapter;

    //int page = 1;//第几页
    boolean isShowProgress = true;
    String keyword = "";

    String phones = "";
    Map<String,String> phoneMap = new HashMap<>();

    @Override
    public void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_phone_contacts);
    }

    @Override
    public void initView() {
        txtTitle.setText("手机通讯录");
        txtLeft.setTypeface(StringUtils.getFont(mContext));
        searchIc.setTypeface(StringUtils.getFont(mContext));

        mLayoutManager = new FullyLinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        //分割线
        recyclerView.addItemDecoration(new RecycleViewDivider(mContext, LinearLayoutManager.HORIZONTAL,
                UIUtils.dip2px(mContext,1),
                getResources().getColor(R.color.background)));
        adapter = new BaseRecyclerAdapter<PhoneContactBean>(mContext, R.layout.item_phone_contact) {
            @Override
            public void convert(BaseRecyclerHolder holder, final PhoneContactBean bean, int position, boolean isScrolling) {
                final TextView txtStatus = holder.getView(R.id.txt_status);
                CircleImgView avatarIv = holder.getView(R.id.avatar);
                UIUtils.loadIcon(mContext,AppConfig.getImagePath(bean.getAvatar()),avatarIv);
                //holder.setText(R.id.nick_name,bean.getNick_name());
                String name = getContactName(bean.getPhone());
                if(TextUtils.isEmpty(name)){
                    holder.setText(R.id.tv_phone,bean.getNick_name());
                } else {
                    holder.setText(R.id.tv_phone,bean.getNick_name()+"("+name+")");
                }

                // 用户状态：1已经注册，可以直接添加；2已经注册，已经是好友(显示已添加，不能做任何操作)；3未注册，可以邀请注册
                switch (bean.getUser_status()){
                    case 1:
                        txtStatus.setVisibility(View.VISIBLE);
                        txtStatus.setText("添加");
                        break;
                    case 2:
                        txtStatus.setVisibility(View.GONE);
                        break;
                    case 3:
                        txtStatus.setVisibility(View.VISIBLE);
                        txtStatus.setText("邀请");
                        break;
                }
                txtStatus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if("添加".equals(txtStatus.getText().toString()) ||
                                "邀请".equals(txtStatus.getText().toString())){
                            invite(bean.getPhone());
                        }
                    }
                });

            }
        };
        recyclerView.setAdapter(adapter);

        searchEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    KeyBoardUtils.closeKeybord(searchEt,mContext);
                    keyword = searchEt.getText().toString().trim();
                    getPhones();

                    return true;
                }
                return false;
            }
        });

    }

    @Override
    public void initData() {
        getPhones();
    }

    private void getContacts(String phones) {
        mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_1,
                UrlUtils.phone_contact_list_get(LoginUtil.getInfo("token"), LoginUtil.getInfo("uid"),
                        phones,keyword),isShowProgress);
    }

    private void invite(String phone) {
        mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_2,
                UrlUtils.phone_contact_invite_set(LoginUtil.getInfo("token"), LoginUtil.getInfo("uid"),
                        phone),true);
    }

    private void setUI(List<PhoneContactBean> list) {
        //没有分页
        adapter.clear();
        adapter.addList(list);
    }

    @Override
    @OnClick({R.id.txt_left})
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.txt_left:
                finish();
                break;
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case HttpModeBase.HTTP_REQUESTCODE_1:
                try {
                    isShowProgress = false;
                    JSONObject object = new JSONObject((String) msg.obj);
                    if (object.optInt("status") == 1) {
                        if (!StringUtils.isEmpty(object.optString("result"))) {
                            List<PhoneContactBean> list = FastJsonTools.getPersons(object.optString("result"), PhoneContactBean.class);;
                            if(list != null){
                                setUI(list);
                            }
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
                        ToastUtil.show(mContext, object.optString("message"));
                        //刷新
                        getPhones();
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

    public void getPhones(){
        if(!TextUtils.isEmpty(phones)){
            getContacts(phones);
            return;
        }
        phoneMap.clear();
        StringBuilder sb = new StringBuilder("");
        //获取联系人
        ContentResolver cr = getContentResolver();
        Uri uri=Uri.parse("content://com.android.contacts/raw_contacts");
        Cursor cursor = cr.query(uri,null,null,null,null);
        while(cursor.moveToNext()){
            int id = cursor.getInt(cursor.getColumnIndex("_id")); //拿到id
            String name= cursor.getString(cursor.getColumnIndex("display_name")); //拿到姓名

            //继续获取相对应的联系人的数据（电话号码）
            Uri uriData=Uri.parse("content://com.android.contacts/raw_contacts/"+id+"/data");//根据id拿到这个对象的所有信息
            Cursor cursorData = cr.query(uriData,null,null,null,null);
            while(cursorData.moveToNext()){
                String number= cursorData.getString(cursorData.getColumnIndex("data1"));
                String type= cursorData.getString(cursorData.getColumnIndex("mimetype"));
                if("vnd.android.cursor.item/phone_v2".equals(type)){
                    if(!TextUtils.isEmpty(number) && number.contains(" ")){
                        number = number.replaceAll(" ","");
                    }
                    if(number.startsWith("1") && number.length() == 11){
                        sb.append(number+",");
                        phoneMap.put(number,name);
                    }
                }
            }
        }

        if(sb.toString().endsWith(",")){
            sb.deleteCharAt(sb.length()-1);
        }

        if(sb.length()>0){
            phones = sb.toString();
            getContacts(phones);
        }
    }

    private String getContactName(String phone){
        for (Map.Entry<String, String> entry : phoneMap.entrySet()) {
            if(phone.equals(entry.getKey())){
                return entry.getValue();
            }
        }
        return "";
    }
}
