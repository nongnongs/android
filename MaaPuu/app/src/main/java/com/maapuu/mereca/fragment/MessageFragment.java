package com.maapuu.mereca.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.mobileim.YWAPI;
import com.alibaba.mobileim.conversation.IYWConversationListener;
import com.alibaba.mobileim.conversation.IYWConversationService;
import com.alibaba.mobileim.conversation.YWConversation;
import com.gyf.barlibrary.ImmersionBar;
import com.maapuu.mereca.R;
import com.maapuu.mereca.activity.MainActivity;
import com.maapuu.mereca.base.AppConfig;
import com.maapuu.mereca.base.BaseFragment;
import com.maapuu.mereca.constant.UrlUtils;
import com.maapuu.mereca.fragment.messagechild.NewFriendsFragment;
import com.maapuu.mereca.fragment.messagechild.NoticeFragment;
import com.maapuu.mereca.util.HttpModeBase;
import com.maapuu.mereca.util.LoginUtil;
import com.maapuu.mereca.util.StringUtils;
import com.maapuu.mereca.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by dell on 2018/1/11.
 */

public class MessageFragment extends BaseFragment {

    @BindView(R.id.txt_left)
    TextView txtLeft;
    @BindView(R.id.txt_title)
    TextView txtTitle;
    @BindView(R.id.txt_all)
    TextView txtAll;
    @BindView(R.id.txt_daifk)
    TextView txtDaifk;
    @BindView(R.id.txt_daisy)
    TextView txtDaisy;
    @BindView(R.id.ll_has)
    LinearLayout llHas;
    @BindView(R.id.dot1)
    View dot1;
    @BindView(R.id.dot2)
    View dot2;

    private TextView[] tvs;

    private String[] titles = {"聊天消息","系统通知","新的朋友"};
    private FragmentManager fragmentManager;
    private Fragment fragment = null;
    private NoticeFragment noticeFragment;
    private NewFriendsFragment friendsFragment;
    private int mTab = 0;
    private boolean isFirst = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImmersionBar.with(getActivity()).fitsSystemWindows(true).statusBarColor(R.color.white)
                .statusBarDarkFont(true,0f).init();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
            ImmersionBar.with(getActivity()).fitsSystemWindows(true).statusBarDarkFont(true,0f).init();
        }
    }

    @Override
    protected int setContentViewById() {
        return R.layout.fragment_message;
    }

    @Override
    protected void initView(View v) {
        txtLeft.setVisibility(View.GONE);
        txtLeft.setTypeface(StringUtils.getFont(mContext));
        txtTitle.setText("消息");
        tvs = new TextView[]{txtAll,txtDaifk,txtDaisy};
        setHead(mTab);
        fragmentManager = getChildFragmentManager();
        addFragment(mTab);
    }

    private void setHead(int postion) {
        for (int i = 0; i < tvs.length; i++){
            if(i == postion){
                tvs[postion].setSelected(true);
            }else {
                tvs[i].setSelected(false);
            }
        }
    }

    // 隐藏所有的Fragment,避免fragment混乱
    private void hideFragments(FragmentTransaction transaction) {
        if (fragment != null) {
            transaction.hide(fragment);
        }
        if (noticeFragment != null) {
            transaction.hide(noticeFragment);
        }
        if (friendsFragment != null) {
            transaction.hide(friendsFragment);
        }
    }

    private void addFragment(int i) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        hideFragments(fragmentTransaction);
        switch (i) {
            case 0:
                if(AppConfig.mIMKit == null){
                    AppConfig.mIMKit = YWAPI.getIMKitInstance(LoginUtil.getInfo("uid"), AppConfig.APP_KEY);
                }
                if (fragment == null) {
                    fragment = AppConfig.mIMKit.getConversationFragment();
                    fragmentTransaction.add(R.id.fl_container, fragment);
                } else {
                    fragmentTransaction.show(fragment);
                }
                // 获取会话管理类
                final IYWConversationService conversationService = AppConfig.mIMKit.getConversationService();
                // 添加会话列表变更监听
                conversationService.addConversationListener(new IYWConversationListener() {
                    @Override
                    public void onItemUpdated() {
                        //获取最近会话列表
                        List<YWConversation> list = conversationService.getConversationList();
                        if(mTab == 0){
                            if(list != null && list.size() > 0){
                                llHas.setVisibility(View.GONE);
                            }else {
                                llHas.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
                //获取最近会话列表
                List<YWConversation> list = conversationService.getConversationList();
                if(list != null && list.size() > 0){
                    llHas.setVisibility(View.GONE);
                }else {
                    llHas.setVisibility(View.VISIBLE);
                }
                break;
            case 1:
                if (noticeFragment == null) {
                    noticeFragment = new NoticeFragment();
                    fragmentTransaction.add(R.id.fl_container, noticeFragment);
                } else {
                    fragmentTransaction.show(noticeFragment);
                }
                break;
            case 2:
                if (friendsFragment == null) {
                    friendsFragment = new NewFriendsFragment();
                    fragmentTransaction.add(R.id.fl_container, friendsFragment);
                } else {
                    fragmentTransaction.show(friendsFragment);
                }
                break;
        }
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    protected void initData() {
        new Thread() {
            public void run() {
                try {
                    if (!isFirst){
                        while (true) {
                            Thread.sleep(5000);
                            mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_1, UrlUtils.my_info_get(LoginUtil.getInfo("token"),LoginUtil.getInfo("uid"),2),false);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

        isFirst = false;
        mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_1, UrlUtils.my_info_get(LoginUtil.getInfo("token"),LoginUtil.getInfo("uid"),2),false);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case HttpModeBase.HTTP_REQUESTCODE_1:
                try {
                    JSONObject object = new JSONObject((String) msg.obj);
                    if (object.optInt("status") == 1) {
                        JSONObject resultObj = object.optJSONObject("result");
                        if(resultObj.optInt("system_msg_num") == 0){
                            dot1.setVisibility(View.GONE);
                        }else {
                            dot1.setVisibility(View.VISIBLE);
                        }
                        if(resultObj.optInt("friend_msg_num") == 0){
                            dot2.setVisibility(View.GONE);
                        }else {
                            dot2.setVisibility(View.VISIBLE);
                        }
                        if(resultObj.optInt("system_msg_num") == 0 && resultObj.optInt("friend_msg_num") == 0){
                            MainActivity.activity.mHandler.sendEmptyMessage(98);
                        }else {
                            MainActivity.activity.mHandler.sendEmptyMessage(99);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
        return false;
    }

    @Override
    @OnClick({R.id.txt_all,R.id.txt_daifk,R.id.txt_daisy,})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.txt_all:
                mTab = 0;
                setHead(mTab);
                addFragment(mTab);
                break;
            case R.id.txt_daifk:
                dot1.setVisibility(View.GONE);
                if(dot2.getVisibility() == View.GONE){
                    MainActivity.activity.mHandler.sendEmptyMessage(98);
                }
                llHas.setVisibility(View.GONE);
                mTab = 1;
                setHead(mTab);
                addFragment(mTab);
                break;
            case R.id.txt_daisy:
                dot2.setVisibility(View.GONE);
                if(dot1.getVisibility() == View.GONE){
                    MainActivity.activity.mHandler.sendEmptyMessage(98);
                }
                llHas.setVisibility(View.GONE);
                mTab = 2;
                setHead(mTab);
                addFragment(mTab);
                break;
        }
    }
}
