package com.maapuu.mereca.activity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.mobileim.YWAPI;
import com.alibaba.mobileim.conversation.IYWConversationService;
import com.alibaba.mobileim.conversation.IYWConversationUnreadChangeListener;
import com.amap.api.location.AMapLocation;
import com.gyf.barlibrary.ImmersionBar;
import com.maapuu.mereca.R;
import com.maapuu.mereca.base.AppConfig;
import com.maapuu.mereca.base.BaseActivity;
import com.maapuu.mereca.bean.RedBean;
import com.maapuu.mereca.constant.UrlUtils;
import com.maapuu.mereca.event.EventEntity;
import com.maapuu.mereca.fragment.CommunityFragment;
import com.maapuu.mereca.fragment.FoundFragment;
import com.maapuu.mereca.fragment.HomeFragment;
import com.maapuu.mereca.fragment.MessageFragment;
import com.maapuu.mereca.fragment.MineFragment;
import com.maapuu.mereca.service.UpdataService;
import com.maapuu.mereca.util.DisplayUtil;
import com.maapuu.mereca.util.FastJsonTools;
import com.maapuu.mereca.util.HttpModeBase;
import com.maapuu.mereca.util.JumpPermissionManagement;
import com.maapuu.mereca.util.LocationUtils;
import com.maapuu.mereca.util.LoginUtil;
import com.maapuu.mereca.util.StringUtils;
import com.maapuu.mereca.util.ToastUtil;
import com.maapuu.mereca.util.VersionUtil;
import com.othershe.nicedialog.BaseNiceDialog;
import com.othershe.nicedialog.NiceDialog;
import com.othershe.nicedialog.ViewConvertListener;
import com.othershe.nicedialog.ViewHolder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks{

    @BindView(R.id.radiogroup)
    RadioGroup radioGroup;
    @BindView(R.id.rb_home)
    RadioButton rbHome;
    @BindView(R.id.rb_found)
    RadioButton rbFound;
    @BindView(R.id.rb_community)
    RadioButton rbCommunity;
    @BindView(R.id.rb_message)
    RadioButton rbMessage;
    @BindView(R.id.rb_mine)
    RadioButton rbMine;
    @BindView(R.id.txt_unread_num)
    TextView txtUnreadNum;
    @BindView(R.id.dot)
    View dot;

    private FragmentManager fragmentManager;
    private HomeFragment homeFragment;
    private FoundFragment foundFragment;
    private CommunityFragment communityFragment;
    private MessageFragment messageFragment;
    private MineFragment mineFragment;

    private int mTabId = 0;

    private View popView;
    private PopupWindow popupWindow;
    public static final int PERMISSIONCODE = 0x0100;//权限
    private String apkUrl;
    private boolean logout = false;
    private boolean isUpdate = false;
    private boolean isFirst = true;
    private RedBean redBean;
    public static MainActivity activity;

    private IYWConversationService mConversationService;
    private IYWConversationUnreadChangeListener mConversationUnreadChangeListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void initContentView(Bundle savedInstanceState) {
        ImmersionBar.with(this).fitsSystemWindows(false).transparentStatusBar()
                .statusBarDarkFont(true,0f).init();
        setContentView(R.layout.activity_main);
        activity = this;
        logout = getIntent().getBooleanExtra("logout",false);
    }

    @Override
    public void initView() {
        fragmentManager = getSupportFragmentManager();
        if(!logout){
            initLocation();
        }else {
            initFragment();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(LoginUtil.getLoginState()){
            initConversationServiceAndListener();
            //resume时需要检查全局未读消息数并做处理，因为离开此界面时删除了全局消息监听器
            mConversationUnreadChangeListener.onUnreadChange();
            if(isFirst){
                isFirst = false;
                mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_3, UrlUtils.my_info_get(LoginUtil.getInfo("token"),LoginUtil.getInfo("uid"),2),false);
            }
        }
    }

    private void initConversationServiceAndListener() {
        if(AppConfig.mIMKit == null){
            AppConfig.mIMKit = YWAPI.getIMKitInstance(LoginUtil.getInfo("uid"), AppConfig.APP_KEY);
        }
        mConversationService = AppConfig.mIMKit.getConversationService();
        mConversationUnreadChangeListener = new IYWConversationUnreadChangeListener() {

            //当未读数发生变化时会回调该方法，开发者可以在该方法中更新未读数
            @Override
            public void onUnreadChange() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        //获取当前登录用户的所有未读数
                        int unReadCount = mConversationService.getAllUnreadCount();
                        //设置桌面角标的未读数
                        AppConfig.mIMKit.setShortcutBadger(unReadCount);
                        if(unReadCount == 0){
                            txtUnreadNum.setVisibility(View.GONE);
                        }else {
                            txtUnreadNum.setVisibility(View.VISIBLE);
                            txtUnreadNum.setText(unReadCount+"");
                        }
                    }
                });
            }
        };
        mConversationService.addTotalUnreadChangeListener(mConversationUnreadChangeListener);
    }

    private void initLocation() {
        new LocationUtils(MainActivity.this, new LocationUtils.Callback() {
            @Override
            public void click(AMapLocation aMapLocation) {
                if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
                    AppConfig.LAT = aMapLocation.getLatitude()+"";
                    AppConfig.LNG = aMapLocation.getLongitude()+"";
                    AppConfig.CITY = aMapLocation.getCity();
                    initFragment();
                }else {
                    AppConfig.CITY = "未开启";
                    AppConfig.LAT = "0";
                    AppConfig.LNG = "0";
                    if((StringUtils.isEmpty(LoginUtil.getCityID(mContext)) || "0".equals(LoginUtil.getCityID(mContext)))){
                        it = new Intent(mContext, ChooseCityActivity.class);
                        startActivityForResult(it,AppConfig.ACTIVITY_REQUESTCODE);
                    }else {
                        initFragment();
                    }
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    Log.e("AmapError","location Error, ErrCode:" + aMapLocation.getErrorCode() + ", errInfo:" + aMapLocation.getErrorInfo());
                }
            }
        });
    }

    private void initFragment(){
        if(logout){
            mTabId = 4;
            addFragment(mTabId);
            radioGroup.check(rbMine.getId());
        }else {
            addFragment(mTabId);
            checkUpdate();
        }
    }

    @Override
    public void initData() {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_home:
                        mTabId = 0;
                        addFragment(mTabId);
                        break;
                    case R.id.rb_found:
                        mTabId = 1;
                        addFragment(mTabId);
                        break;
                    case R.id.rb_community:
                        mTabId = 2;
                        addFragment(mTabId);
                        break;
                    case R.id.rb_message:
                        if(LoginUtil.getLoginState()){
                            mTabId = 3;
                            addFragment(mTabId);
                        }else {
                            RadioButton btn = (RadioButton) radioGroup.getChildAt(mTabId);
                            btn.setChecked(true);
                            it = new Intent(mContext,LoginActivity.class);
                            startActivity(it);
                        }
                        break;
                    case R.id.rb_mine:
                        mTabId = 4;
                        addFragment(mTabId);
                        break;
                }
            }
        });
    }

    // 检测-更新
    public void checkUpdate() {
        mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_1, UrlUtils.app_update_get(VersionUtil.getAppVersionName(),LoginUtil.getInfo("uid")),false);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case HttpModeBase.HTTP_REQUESTCODE_1:
                try {
                    JSONObject object = new JSONObject((String) msg.obj);
                    if (object.optInt("status") == 1) {
                        if(!StringUtils.isEmpty(object.optString("result"))){
                            JSONObject resultObj = object.optJSONObject("result");
                            isUpdate = true;
                            apkUrl = resultObj.optString("download_url");
                            showPopupWindow(resultObj.optInt("update_type"),resultObj.optString("content"),resultObj.optString("download_url"));
                        }
                        if(object.has("red_data") && !StringUtils.isEmpty(object.optString("red_data"))){
                            redBean = FastJsonTools.getPerson(object.optString("red_data"),RedBean.class);
                            if(!isUpdate){
                                showRedPopupWindow();
                            }
                        }
                    } else {
                        ToastUtil.show(mContext,object.optString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case HttpModeBase.HTTP_REQUESTCODE_3:
                try {
                    JSONObject object = new JSONObject((String) msg.obj);
                    if (object.optInt("status") == 1) {
                        JSONObject resultObj = object.optJSONObject("result");
                        if(resultObj.optInt("system_msg_num") == 0 && resultObj.optInt("friend_msg_num") == 0){
                            dot.setVisibility(View.GONE);
                        }else {
                            dot.setVisibility(View.VISIBLE);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case HttpModeBase.HTTP_ERROR:
                String result_e = (String) msg.obj;
                ToastUtil.show(mContext,result_e);
                break;
            case 98:
                dot.setVisibility(View.GONE);
                break;
            case 99:
                dot.setVisibility(View.VISIBLE);
                break;
            case 999:
                checkUpdate();
                break;
        }
        return false;
    }

    private void showRedPopupWindow(){
        NiceDialog.init().setLayoutId(R.layout.pop_index_red)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    public void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        holder.setText(R.id.txt_red_type,redBean.getRed_type().equals("1")?"项目红包":"商品红包");
                        holder.setText(R.id.txt_cut_amount,"¥"+redBean.getRed_amount());
                        holder.setText(R.id.txt_full_amount,"满"+redBean.getFullcut_amount()+"可用");
                        holder.setOnClickListener(R.id.iv_close, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.iv_check, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                startActivity(new Intent(mContext,HongBaoActivity.class));
                            }
                        });
                    }
                }).setHeight(300).setWidth(330)
                .setOutCancel(true).setShowBottom(false)
                .show(getSupportFragmentManager());
    }

    private void showPopupWindow(int update_type, String content, final String apkUrl) {
        if(mContext == null){
            return;
        }
        popView = LayoutInflater.from(mContext).inflate(R.layout.pop_update, null);
        popupWindow = new PopupWindow(popView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, false);
        LinearLayout llClose = (LinearLayout) popView.findViewById(R.id.ll_close);
        if(update_type == 2){
            llClose.setVisibility(View.GONE);
        }else {
            llClose.setVisibility(View.VISIBLE);
        }
        llClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                if(redBean != null){
                    showRedPopupWindow();
                }
            }
        });
        LinearLayout ll = (LinearLayout) popView.findViewById(R.id.ll);
        RelativeLayout.LayoutParams linearParams =(RelativeLayout.LayoutParams) ll.getLayoutParams();
        linearParams.width=((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DisplayUtil.getWidthDP(mContext)*4/5, getResources().getDisplayMetrics()));
        linearParams.height=linearParams.width*5/3;
        ll.setLayoutParams(linearParams);
        View btn_update = popView.findViewById(R.id.btn_update);
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (!mContext.getPackageManager().canRequestPackageInstalls()) {//没有权限
                        ToastUtil.show(mContext,"请允许安装未知来源应用");
                        Intent it = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + getPackageName()));
                        startActivityForResult(it,1086);
                    } else {
                        requestPermission();
                    }
                }else {
                    requestPermission();
                }
            }
        });
        TextView update_info = (TextView) popView.findViewById(R.id.update_info);
        update_info.setText(content);

        // 设置好参数之后再show
        popupWindow.showAtLocation(popView, Gravity.CENTER,0,0);
    }

    @Subscribe
    public void UpDataMenu(EventEntity event) {
        switch (event.getType()){
            case 0: //首页
                radioGroup.check(rbHome.getId());
                break;
            case 1://发现
                radioGroup.check(rbFound.getId());
                break;
            case 2://圈子
                radioGroup.check(rbCommunity.getId());
                break;
            case 3://消息
                radioGroup.check(rbMessage.getId());
                break;
            case 4://我的
                radioGroup.check(rbMine.getId());
                break;
        }
    }

    // 隐藏所有的Fragment,避免fragment混乱
    private void hideFragments(FragmentTransaction transaction) {
        if (homeFragment != null) {
            transaction.hide(homeFragment);
        }
        if (foundFragment != null) {
            transaction.hide(foundFragment);
        }
        if (communityFragment != null) {
            transaction.hide(communityFragment);
        }
        if (messageFragment != null) {
            transaction.hide(messageFragment);
        }
        if (mineFragment != null) {
            transaction.hide(mineFragment);
        }
    }

    private void addFragment(int i) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        hideFragments(fragmentTransaction);
        switch (i) {
            case 0:
                if (homeFragment == null) {
                    homeFragment = new HomeFragment();
                    fragmentTransaction.add(R.id.fl_container, homeFragment);
                } else {
                    fragmentTransaction.show(homeFragment);
                }
                break;
            case 1:
                if (foundFragment == null) {
                    foundFragment = new FoundFragment();
                    fragmentTransaction.add(R.id.fl_container, foundFragment);
                } else {
                    fragmentTransaction.show(foundFragment);
                }
                break;
            case 2:
                if (communityFragment == null) {
                    communityFragment = new CommunityFragment();
                    fragmentTransaction.add(R.id.fl_container, communityFragment);
                } else {
                    fragmentTransaction.show(communityFragment);
                }
                break;
            case 3:
                if (messageFragment == null) {
                    messageFragment = new MessageFragment();
                    fragmentTransaction.add(R.id.fl_container, messageFragment);
                } else {
                    fragmentTransaction.show(messageFragment);
                }
                break;
            case 4:
                if (mineFragment == null) {
                    mineFragment = new MineFragment();
                    fragmentTransaction.add(R.id.fl_container, mineFragment);
                } else {
                    fragmentTransaction.show(mineFragment);
                }
                break;
        }
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public void onClick(View view) {

    }

    private long timeMills = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - timeMills > 2000) {
                Toast.makeText(this, "请再按一次退出", Toast.LENGTH_SHORT).show();
                timeMills = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activity = null;
        EventBus.getDefault().unregister(this);
        ImmersionBar.with(this).destroy();
    }

    @AfterPermissionGranted(PERMISSIONCODE)
    private void requestPermission() {
        if (EasyPermissions.hasPermissions(mContext, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            if(!StringUtils.isEmpty(apkUrl)){
                Intent it = new Intent(mContext,UpdataService.class);
                it.putExtra("url",apkUrl);
                startService(it);
            }
        }else {
            ToastUtil.show(mContext,"请打开存储权限");
            JumpPermissionManagement.GoToSetting(MainActivity.this);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if(!StringUtils.isEmpty(apkUrl)){
            Intent it = new Intent(mContext,UpdataService.class);
            it.putExtra("url",apkUrl);
            startService(it);
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case AppConfig.ACTIVITY_REQUESTCODE:
                initFragment();
                break;
            case 1086:
                if (resultCode == RESULT_OK) {
                    requestPermission();//再次执行安装流程，包含权限判等
                }
                break;
        }
    }
}
