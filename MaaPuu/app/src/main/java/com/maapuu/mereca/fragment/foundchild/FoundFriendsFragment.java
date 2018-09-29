package com.maapuu.mereca.fragment.foundchild;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.maapuu.mereca.R;
import com.maapuu.mereca.activity.LatestCommentActivity;
import com.maapuu.mereca.activity.PersonalHomepageActivity;
import com.maapuu.mereca.adapter.FriendDtRecyclerAdapter;
import com.maapuu.mereca.base.AppConfig;
import com.maapuu.mereca.base.BaseFragment;
import com.maapuu.mereca.bean.MoBean;
import com.maapuu.mereca.bean.MoCommentBean;
import com.maapuu.mereca.bean.PraiseBean;
import com.maapuu.mereca.constant.UrlUtils;
import com.maapuu.mereca.recycleviewutil.FullyLinearLayoutManager;
import com.maapuu.mereca.recycleviewutil.RecyclerViewDivider;
import com.maapuu.mereca.util.FastJsonTools;
import com.maapuu.mereca.util.HttpModeBase;
import com.maapuu.mereca.util.LoginUtil;
import com.maapuu.mereca.util.PictureSelectUtil;
import com.maapuu.mereca.util.StringUtils;
import com.maapuu.mereca.util.ToastUtil;
import com.maapuu.mereca.util.UIUtils;
import com.othershe.nicedialog.BaseNiceDialog;
import com.othershe.nicedialog.NiceDialog;
import com.othershe.nicedialog.ViewConvertListener;
import com.othershe.nicedialog.ViewHolder;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zhouwei.blurlibrary.EasyBlur;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import info.wangchen.simplehud.SimpleHUD;

import static android.app.Activity.RESULT_OK;

/**
 * Created by dell on 2018/2/27.
 */

public class FoundFriendsFragment extends BaseFragment {
    @BindView(R.id.bg_image)
    ImageView bgImage;
    @BindView(R.id.txt_name)
    TextView txtName;
    @BindView(R.id.iv_icon)
    SimpleDraweeView ivIcon;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.ll_latest_comment)
    LinearLayout llLatestComment;
    @BindView(R.id.txt_news_num)
    TextView txtNewsNum;
    @BindView(R.id.iv_icon_small)
    SimpleDraweeView ivIconAmall;
    @BindView(R.id.txt_more)
    TextView txtMore;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.line)
    View line;
    @BindView(R.id.ll_has)
    LinearLayout llHas;

    private LinearLayoutManager mLayoutManager;
    private List<MoBean> list;
    private FriendDtRecyclerAdapter adapter;
    private int page = 1;
    private Bitmap bitmap = null;
    private String is_staff;
    private int delPos = -1;
    private int zanPos = -1;
    private int comPos = -1;
    private String praise_ids;
    private String comment_ids;
    private int plPos = -1,plPos1 = -1;

    private String upload_path = "";

    @Override
    protected int setContentViewById() {
        return R.layout.fragment_found_friends;
    }

    @Override
    protected void initView(View v) {
        txtMore.setTypeface(StringUtils.getFont(mContext));
        list = new ArrayList<>();
        mLayoutManager=new FullyLinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(mLayoutManager);
        //这句就是添加我们自定义的分隔线
        recyclerView.addItemDecoration(new RecyclerViewDivider(mContext, LinearLayoutManager.VERTICAL,20
                ,getResources().getColor(R.color.background)));

//        txtName.setText(LoginUtil.getInfo("nick_name"));
//        if(!StringUtils.isEmpty(LoginUtil.getInfo("avatar"))){
//            ivIcon.setImageURI(Uri.parse(LoginUtil.getInfo("avatar")));
//            new AsyncTask<String, Void, Bitmap>() {
//                @Override
//                protected Bitmap doInBackground(String... params) {
//                    return returnBitMap(params[0]);
//                }
//                @Override
//                protected void onPostExecute(Bitmap bitmap) {
//                    // 主线程处理view
//                    if (bitmap != null) {
//                        bgImage.setScaleType(ImageView.ScaleType.CENTER);
//                        bgImage.setImageBitmap(bitmap);
//                    }else {
//                        bgImage.setBackgroundColor(getResources().getColor(R.color.text_dd));
//                    }
//                }
//            }.execute(LoginUtil.getInfo("avatar"));
//        }

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
    }

    public Bitmap returnBitMap(final String url){
        try {
            URL path = new URL(url);
            HttpURLConnection conn = (HttpURLConnection)path.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(bitmap != null){
            bitmap = EasyBlur.with(getActivity())
                    .bitmap(bitmap) //要模糊的图片
                    .scale(1)
                    .radius(15)//模糊半径
                    .blur();
        }
        return bitmap;
    }

    @Override
    protected void initData() {
        initData(page);
    }

    public void refresh(){
        page = 1;
        initData(page);
    }

    private void initData(int pageNum) {
        mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_1, UrlUtils.friend_moment_get(LoginUtil.getInfo("token"),LoginUtil.getInfo("uid"),pageNum),true);
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
                        if(resultObj.has("mo_head_img")){
                            UIUtils.loadImg(mContext,resultObj.optString("mo_head_img"),bgImage);
                        }
                        if(resultObj.has("avatar")){
                            ivIcon.setImageURI(Uri.parse(resultObj.optString("avatar")));
                        }
                        if(resultObj.has("nick_name")){
                            txtName.setText(resultObj.optString("nick_name"));
                        }
                        if(!StringUtils.isEmpty(resultObj.optString("mo_data")) && resultObj.optJSONArray("mo_data").length() > 0){
                            llHas.setVisibility(View.GONE);
                            line.setVisibility(View.VISIBLE);
                            List<MoBean> lsJson = FastJsonTools.getPersons(resultObj.optString("mo_data"),MoBean.class);
                            setAdapter(lsJson);
                            page ++ ;
                        }else {
                            List<MoBean> lsJson = new ArrayList<>();
                            setAdapter(lsJson);
                            if(page != 1){
                                llHas.setVisibility(View.GONE);
                                line.setVisibility(View.VISIBLE);
                                ToastUtil.show(mContext,"暂无更多数据");
                            }else {
                                llHas.setVisibility(View.VISIBLE);
                                line.setVisibility(View.GONE);
                            }
                        }
                        is_staff = resultObj.optString("is_staff");
                        if(resultObj.has("news_data") && !StringUtils.isEmpty(resultObj.optString("news_data"))){
                            JSONObject newsObj = resultObj.optJSONObject("news_data");
                            praise_ids = newsObj.optString("praise_ids");
                            comment_ids = newsObj.optString("comment_ids");
                            if(StringUtils.isEmpty(newsObj.optString("count")) || newsObj.optInt("count") == 0){
                                llLatestComment.setVisibility(View.GONE);
                            }else {
                                llLatestComment.setVisibility(View.VISIBLE);
                                txtNewsNum.setText(newsObj.optString("count")+"条新消息");
                            }
                            if(!StringUtils.isEmpty(newsObj.optString("last_mo_user"))){
                                JSONObject userObj = newsObj.optJSONObject("last_mo_user");
                                ivIconAmall.setImageURI(Uri.parse(userObj.optString("avatar")));
                            }
                        }else {
                            llLatestComment.setVisibility(View.GONE);
                        }
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
                        if(zanPos != -1){
                            if(list.get(zanPos).getIs_praise() == 1){
//                                ToastUtil.show(mContext,"取消成功");
                                list.get(zanPos).setIs_praise(0);
                                if(!StringUtils.isEmpty(list.get(zanPos).getPraise_num()) && !list.get(zanPos).getPraise_num().equals("0")){
                                    if(Integer.parseInt(list.get(zanPos).getPraise_num())-1 == 0){
                                        list.get(zanPos).setPraise_num("0");
                                    }else {
                                        list.get(zanPos).setPraise_num(String.valueOf(Integer.parseInt(list.get(zanPos).getPraise_num())-1));
                                    }
                                }
                                for (int i = 0; i < list.get(zanPos).getPraise_users().size(); i++){
                                    if(list.get(zanPos).getPraise_users().get(i).getUid().equals(LoginUtil.getInfo("uid"))){
                                        list.get(zanPos).getPraise_users().remove(i);
                                        break;
                                    }
                                }
                            }else {
//                                ToastUtil.show(mContext,"点赞成功");
                                list.get(zanPos).setIs_praise(1);
                                if(!StringUtils.isEmpty(list.get(zanPos).getPraise_num()) && !list.get(zanPos).getPraise_num().equals("0")){
                                    list.get(zanPos).setPraise_num(String.valueOf(Integer.parseInt(list.get(zanPos).getPraise_num())+1));
                                }else {
                                    list.get(zanPos).setPraise_num("1");
                                }
                                if(!StringUtils.isEmpty(object.optString("result"))){
                                    PraiseBean bean = FastJsonTools.getPerson(object.optString("result"),PraiseBean.class);
                                    list.get(zanPos).getPraise_users().add(bean);
                                }
                            }
                            adapter.notifyDataSetChanged();
                            zanPos = -1;
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
//                        ToastUtil.show(mContext,"评论成功");
                        if(comPos != -1){
                            if(!StringUtils.isEmpty(object.optString("result"))){
                                MoCommentBean bean = FastJsonTools.getPerson(object.optString("result"),MoCommentBean.class);
                                list.get(comPos).getComments().add(bean);
                                adapter.notifyDataSetChanged();
                                comPos = -1;
                            }
                        }
                    } else {
                        ToastUtil.show(mContext,object.optString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case HttpModeBase.HTTP_REQUESTCODE_4:
                try {
                    JSONObject object = new JSONObject((String) msg.obj);
                    if (object.optInt("status") == 1) {
                        ToastUtil.show(mContext,"删除成功");
                        if(delPos != -1){
                            list.remove(delPos);
                            adapter.notifyDataSetChanged();
                            delPos = -1;
                            if(list.size() == 0){
                                llHas.setVisibility(View.VISIBLE);
                                line.setVisibility(View.GONE);
                            }
                        }
                    } else {
                        ToastUtil.show(mContext,object.optString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case HttpModeBase.HTTP_REQUESTCODE_5:
                try {
                    JSONObject object = new JSONObject((String) msg.obj);
                    if (object.optInt("status") == 1) {
//                        ToastUtil.show(mContext,"修改成功");
                        UIUtils.loadImg(mContext,object.optString("result"),bgImage);
                    } else {
                        ToastUtil.show(mContext,object.optString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case HttpModeBase.HTTP_REQUESTCODE_7:
                try {
                    JSONObject object = new JSONObject((String) msg.obj);
                    if (object.optInt("status") == 1) {
//                        ToastUtil.show(mContext,"删除成功");
                        if(plPos != -1){
                            list.get(plPos).getComments().remove(plPos1);
                            adapter.notifyDataSetChanged();
                            plPos = -1;
                            plPos1 = -1;
                        }
                    } else {
                        ToastUtil.show(mContext,object.optString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case HttpModeBase.HTTP_REQUESTCODE_IMG:
                try {
                    JSONObject object = new JSONObject((String) msg.obj);
                    if (object.optInt("status") == 1) {
                        if (object.has("src") && !object.optString("src").equals("")) {
                            save(object.optString("src"));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                SimpleHUD.dismiss();
                break;
            case HttpModeBase.HTTP_ERROR:
                refreshLayout.finishRefresh();
                refreshLayout.finishLoadMore();
                String result_e = (String) msg.obj;
                ToastUtil.show(mContext,result_e);
                break;
        }
        return false;
    }

    private void save(String src) {
        mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_5, UrlUtils.friend_moment_head_set(LoginUtil.getInfo("token"),LoginUtil.getInfo("uid"),src),true);
    }

    private void setAdapter(List<MoBean> lsJson) {
        if(page == 1)
            list.clear();
        list.addAll(lsJson);
        if(adapter == null){
            adapter = new FriendDtRecyclerAdapter(mContext,list);
            recyclerView.setAdapter(adapter);
        }else {
            if(page > 1){
                adapter.notifyItemRangeInserted(list.size()-lsJson.size(),lsJson.size());
                recyclerView.smoothScrollToPosition(list.size()-lsJson.size()-1);
            }else {
                adapter.notifyDataSetChanged();
//                adapter = new FriendDtRecyclerAdapter(mContext,list);
//                recyclerView.setAdapter(adapter);
            }
        }
        adapter.setOnItemClickListener(new FriendDtRecyclerAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view) {}

            @Override
            public void onIconClick(int position) {
                it = new Intent(mContext,PersonalHomepageActivity.class);
                it.putExtra("userpage_uid",list.get(position).getUid());
                it.putExtra("is_staff",list.get(position).getIs_staff()+"");
                startActivityForResult(it, AppConfig.ACTIVITY_REQUESTCODE);
            }

            @Override
            public void onZanClick(int position) {
                zanPos = position;
                mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_2,UrlUtils.mo_praise_set(LoginUtil.getInfo("token"),
                        LoginUtil.getInfo("uid"),list.get(position).getMo_id(),list.get(position).getIs_praise()==1?"0":"1"),true);
            }

            @Override
            public void onDelClick(final int position) {
                new AlertView(null, "确定删除吗？", "取消", null, new String[]{"确定"}, mContext,
                        AlertView.Style.Alert, new OnItemClickListener() {
                    @Override
                    public void onItemClick(Object o, int pos) {
                        if (pos == 0) {
                            delPos = position;
                            mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_4,UrlUtils.mo_delete_set(LoginUtil.getInfo("token"),
                                    LoginUtil.getInfo("uid"),list.get(position).getMo_id()),true);
                        }
                    }
                }).show();
            }

            @Override
            public void onCommentClick(int position,String content) {
                comPos = position;
                mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_3,UrlUtils.mo_comment_set(LoginUtil.getInfo("token"),
                        LoginUtil.getInfo("uid"),list.get(position).getMo_id(),content,"0"),true);
            }

            @Override
            public void onChildCommentDelClick(final int position, final int pos) {
                NiceDialog.init().setLayoutId(R.layout.pop_ewm_more)
                        .setConvertListener(new ViewConvertListener() {
                            @Override
                            public void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                                holder.setText(R.id.txt_share,"删除评论");
                                TextView txt = holder.getView(R.id.txt_save_pic);
                                txt.setVisibility(View.GONE);
                                holder.setOnClickListener(R.id.txt_cancel, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                    }
                                });
                                holder.setOnClickListener(R.id.txt_share, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                        plPos = position;plPos1 = pos;
                                        mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_7,
                                                UrlUtils.mo_comment_delete_set(LoginUtil.getInfo("token"),LoginUtil.getInfo("uid"),
                                                        list.get(position).getComments().get(pos).getMmt_comment_id()),true);
                                    }
                                });
                            }
                        })
                        .setOutCancel(true).setShowBottom(true)
                        .show(getChildFragmentManager());
            }

            @Override
            public void onChildCommentClick(int position, int pos, String content) {
                comPos = position;
                mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_3,UrlUtils.mo_comment_set(LoginUtil.getInfo("token"),
                        LoginUtil.getInfo("uid"),list.get(position).getMo_id(),content,list.get(position).getComments().get(pos).getComment_uid()),true);
            }
        });
    }

    @Override
    @OnClick({R.id.iv_icon,R.id.bg_image,R.id.ll_latest_comment})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_icon:
                it = new Intent(mContext, PersonalHomepageActivity.class);
                it.putExtra("userpage_uid",LoginUtil.getInfo("uid"));
                it.putExtra("is_staff",is_staff);
                startActivityForResult(it, AppConfig.ACTIVITY_REQUESTCODE);
                break;
            case R.id.bg_image:
                NiceDialog.init().setLayoutId(R.layout.pop_ewm_more)
                        .setConvertListener(new ViewConvertListener() {
                            @Override
                            public void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                                holder.setText(R.id.txt_share,"更换相册封面");
                                TextView txt = holder.getView(R.id.txt_save_pic);
                                txt.setVisibility(View.GONE);
                                holder.setOnClickListener(R.id.txt_cancel, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                    }
                                });
                                holder.setOnClickListener(R.id.txt_share, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                        PictureSelector.create(FoundFriendsFragment.this)
                                                .openGallery(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                                                .maxSelectNum(1)// 最大图片选择数量
                                                .minSelectNum(1)// 最小选择数量
                                                .imageSpanCount(3)// 每行显示个数
                                                .selectionMode(PictureConfig.SINGLE)// 多选 or 单选
                                                .previewImage(true)// 是否可预览图片
                                                .isCamera(true)// 是否显示拍照按钮
                                                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                                                .circleDimmedLayer(false) // 是否圆形裁剪 true or false
                                                .showCropFrame(false)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
                                                .showCropGrid(false)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
                                                .enableCrop(true)// 是否裁剪
                                                .compress(true)// 是否压缩
                                                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
                                    }
                                });
                            }
                        })
                        .setOutCancel(true).setShowBottom(true)
                        .show(getChildFragmentManager());
                break;
            case R.id.ll_latest_comment:
                it = new Intent(mContext, LatestCommentActivity.class);
                it.putExtra("praise_ids",praise_ids);
                it.putExtra("comment_ids",comment_ids);
                startActivity(it);
                llLatestComment.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case PictureConfig.CHOOSE_REQUEST:
                if(resultCode == RESULT_OK){
                    if(PictureSelector.obtainMultipleResult(data) != null && PictureSelector.obtainMultipleResult(data).size() > 0){
                        upload_path = PictureSelector.obtainMultipleResult(data).get(0).getCutPath();
                        mHttpModeBase.uploadImage(HttpModeBase.HTTP_REQUESTCODE_IMG, upload_path,true);
                        // 显示在图片控件上
//                        UIUtils.loadImg(mContext,upload_path,bgImage);
                    }else {
                        ToastUtil.show(mContext,"选择图片出错");
                    }
                }
                break;
            case AppConfig.ACTIVITY_REQUESTCODE:
                if(resultCode == AppConfig.ACTIVITY_RESULTCODE_4){
                    UIUtils.loadImg(mContext,data.getStringExtra("edit_path"),bgImage);
                }
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        PictureSelectUtil.clearCache(getActivity());
    }
}
