package com.maapuu.mereca.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.maapuu.mereca.R;
import com.maapuu.mereca.adapter.help.BaseAdapterHelper;
import com.maapuu.mereca.adapter.help.QuickAdapter;
import com.maapuu.mereca.base.BaseActivity;
import com.maapuu.mereca.bean.CircleCommentBean;
import com.maapuu.mereca.bean.ImageTextBean;
import com.maapuu.mereca.bean.ShareBean;
import com.maapuu.mereca.constant.UrlUtils;
import com.maapuu.mereca.recycleviewutil.BaseRecyclerAdapter;
import com.maapuu.mereca.recycleviewutil.BaseRecyclerHolder;
import com.maapuu.mereca.recycleviewutil.FullyLinearLayoutManager;
import com.maapuu.mereca.util.DisplayUtil;
import com.maapuu.mereca.util.EmojiUtil;
import com.maapuu.mereca.util.FastJsonTools;
import com.maapuu.mereca.util.HttpModeBase;
import com.maapuu.mereca.util.LoginUtil;
import com.maapuu.mereca.util.PictureSelectUtil;
import com.maapuu.mereca.util.ShareUtil;
import com.maapuu.mereca.util.StringUtils;
import com.maapuu.mereca.util.ToastUtil;
import com.maapuu.mereca.view.MyListView;
import com.maapuu.mereca.view.ReplyDialog;
import com.maapuu.mereca.view.SupportPopupWindow;
import com.othershe.nicedialog.BaseNiceDialog;
import com.othershe.nicedialog.NiceDialog;
import com.othershe.nicedialog.ViewConvertListener;
import com.othershe.nicedialog.ViewHolder;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by dell on 2018/3/5.
 */

public class ArticleDetailActivity extends BaseActivity {
    @BindView(R.id.txt_left)
    TextView txtLeft;
    @BindView(R.id.txt_title)
    TextView txtTitle;
    @BindView(R.id.scroll_view)
    ScrollView scrollView;
    @BindView(R.id.ll)
    LinearLayout ll;
    @BindView(R.id.txt_artical_title)
    TextView txtArticalTitle;
    @BindView(R.id.image)
    SimpleDraweeView image;
    @BindView(R.id.txt_name)
    TextView txtName;
    @BindView(R.id.txt_time)
    TextView txtTime;
    @BindView(R.id.txt_attection)
    TextView txtAttection;
    @BindView(R.id.txt_zan)
    TextView txtZan;
    @BindView(R.id.txt_unlike)
    TextView txtUnlike;
    @BindView(R.id.txt_comment_num)
    TextView txtCommentNum;
    @BindView(R.id.txt_comment_num_1)
    TextView txtCommentNum1;
    @BindView(R.id.list_view)
    MyListView listView;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.iv_shoucang)
    ImageView ivShoucang;
    @BindView(R.id.ll_bottom)
    LinearLayout llBottom;

    private ReplyDialog replyDialog;

    private List<ImageTextBean> list = new ArrayList<>();
    private QuickAdapter<ImageTextBean> adapter;
    private List<CircleCommentBean> commentList = new ArrayList<>();
    private BaseRecyclerAdapter<CircleCommentBean> commentAdapter;
    private LinearLayoutManager mLayoutManager;
    private String u_id;
    private String circle_id;
    private int type = 0;  //  1 关注   2 点赞  3 不喜欢
    private int zan_num = 0;
    private int comment_num = 0;
    private ShareBean shareBean;
    private String article_img = "";

    private int zanPos = -1;
    private int replyPos = -1;

    private View popView;
    private SupportPopupWindow popupWindow;
    private View popViewZan;
    private SupportPopupWindow popupWindowZan;
    TextView txtPopTitle ;
    SimpleDraweeView popImage;
    ImageView ivClose ;
    TextView txtPopName;
    TextView txtPopContent;
    TextView txtPopTime;
    TextView txtPopZanNum;
    TextView txtPopZan;
    LinearLayout llComment;
    RecyclerView rvPopReply;
    BaseRecyclerAdapter<CircleCommentBean> popAdapter;
    List<CircleCommentBean> popList;
    private int not_like_num;

    @Override
    public void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_article_detail);
    }

    @Override
    public void initView() {
        txtLeft.setTypeface(StringUtils.getFont(mContext));
        circle_id = getIntent().getStringExtra("circle_id");
        popList = new ArrayList<>();

        mLayoutManager=new FullyLinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mLayoutManager);
    }

    @Override
    public void initData() {
        mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_1, UrlUtils.circle_detail_get(LoginUtil.getInfo("token"),LoginUtil.getInfo("uid"),circle_id),true);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case HttpModeBase.HTTP_REQUESTCODE_1:
                try {
                    JSONObject object = new JSONObject((String) msg.obj);
                    if (object.optInt("status") == 1) {
                        JSONObject resultObj = object.optJSONObject("result");
                        if(resultObj.has("share_data") && !StringUtils.isEmpty(resultObj.optString("share_data"))){
                            shareBean = FastJsonTools.getPerson(resultObj.optString("share_data"),ShareBean.class);
                        }
                        u_id = resultObj.optString("uid");
                        txtArticalTitle.setText(EmojiUtil.utf8ToString(resultObj.optString("circle_title")));
                        if(!StringUtils.isEmpty(resultObj.optString("avatar"))){
                            image.setImageURI(Uri.parse(resultObj.optString("avatar")));
                        }
                        txtName.setText(resultObj.optString("nick_name"));
                        txtTime.setText(resultObj.optString("create_time"));
                        if(resultObj.optString("is_attention").equals("1")){
                            txtAttection.setSelected(true);
                            txtAttection.setText("已关注");
                        }else {
                            txtAttection.setSelected(false);
                            txtAttection.setText("关注");
                        }
                        if(resultObj.optString("is_not_like").equals("1")){
                            txtUnlike.setSelected(true);
                        }else {
                            txtUnlike.setSelected(false);
                        }
                        not_like_num = resultObj.optInt("not_like_num");
                        txtUnlike.setText(resultObj.optString("not_like_num"));
                        if(resultObj.optString("is_praise").equals("1")){
                            txtZan.setSelected(true);
                        }else {
                            txtZan.setSelected(false);
                        }
                        if(resultObj.optString("is_collect").equals("1")){
                            ivShoucang.setSelected(true);
                        }else {
                            ivShoucang.setSelected(false);
                        }
                        zan_num = resultObj.optInt("praise_num");
                        txtZan.setText(resultObj.optString("praise_num").equals("0")?"赞":resultObj.optString("praise_num"));
                        comment_num = resultObj.optInt("comment_num");
                        txtCommentNum.setText("评论   "+resultObj.optString("comment_num"));
                        if("0".equals(resultObj.optString("comment_num"))){
                            txtCommentNum1.setVisibility(View.GONE);
                        }else {
                            txtCommentNum1.setVisibility(View.VISIBLE);
                            txtCommentNum1.setText(resultObj.optString("comment_num"));
                        }
                        if(resultObj.has("detail") && resultObj.optJSONArray("detail").length() > 0){
                            list = FastJsonTools.getPersons(resultObj.optString("detail"),ImageTextBean.class);
                            for (int i = 0; i < list.size(); i++){
                                if(list.get(i).getContent_type().equals("2")){
                                    article_img = list.get(i).getContent();
                                    break;
                                }
                            }
                        }
                        if(resultObj.has("comment_list") && resultObj.optJSONArray("comment_list").length() > 0){
                            commentList = FastJsonTools.getPersons(resultObj.optString("comment_list"),CircleCommentBean.class);
                        }
                        setAdapter();
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
                        if(type == 1){
                            if(txtAttection.isSelected()){
//                                ToastUtil.show(mContext,"取消关注成功");
                                txtAttection.setSelected(false);
                                txtAttection.setText("关注");
                            }else {
//                                ToastUtil.show(mContext,"关注成功");
                                txtAttection.setSelected(true);
                                txtAttection.setText("已关注");
                            }
                        }else if(type == 2){
                            if(txtZan.isSelected()){
//                                ToastUtil.show(mContext,"取消点赞成功");
                                txtZan.setSelected(false);
                                zan_num --;
                            }else {
//                                ToastUtil.show(mContext,"点赞成功");
                                txtZan.setSelected(true);
                                zan_num ++;
                            }
                            txtZan.setText(zan_num == 0?"赞":zan_num+"");
                        }else if(type == 3){
                            if(txtUnlike.isSelected()){
                                txtUnlike.setSelected(false);
                                not_like_num --;
                            }else {
                                txtUnlike.setSelected(true);
                                not_like_num ++;
                            }
                            txtUnlike.setText(not_like_num+"");
                        }else if(type == 4){
                            if(ivShoucang.isSelected()){
//                                ToastUtil.show(mContext,"取消收藏成功");
                                ivShoucang.setSelected(false);
                            }else {
//                                ToastUtil.show(mContext,"收藏成功");
                                ivShoucang.setSelected(true);
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
                        ToastUtil.show(mContext,"评论成功");
                        if(!StringUtils.isEmpty(object.optString("result"))){
                            CircleCommentBean bean = FastJsonTools.getPerson(object.optString("result"),CircleCommentBean.class);
                            commentList.add(0,bean);
                            commentAdapter.notifyDataSetChanged();
                            comment_num ++ ;
                            txtCommentNum.setText("评论   "+comment_num);
                            txtCommentNum1.setText(comment_num+"");
                        }
                    } else {
                        ToastUtil.show(mContext,object.optString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case HttpModeBase.HTTP_REQUESTCODE_4: //评论点赞
                try {
                    JSONObject object = new JSONObject((String) msg.obj);
                    if (object.optInt("status") == 1) {
                        if(zanPos != -1){
                            if(commentList.get(zanPos).getIs_sub_praise() == 1){
//                                ToastUtil.show(mContext,"取消成功");
                                commentList.get(zanPos).setIs_sub_praise(0);
                                if(!StringUtils.isEmpty(commentList.get(zanPos).getComment_praise_num()) && !commentList.get(zanPos).getComment_praise_num().equals("0")){
                                    if(Integer.parseInt(commentList.get(zanPos).getComment_praise_num())-1 == 0){
                                        commentList.get(zanPos).setComment_praise_num("0");
                                    }else {
                                        commentList.get(zanPos).setComment_praise_num(String.valueOf(Integer.parseInt(commentList.get(zanPos).getComment_praise_num())-1));
                                    }
                                }
                                if(popupWindow != null && popupWindow.isShowing()){
                                    txtPopZanNum.setText(commentList.get(zanPos).getComment_praise_num()+"人赞过");
                                    txtPopZan.setText(commentList.get(zanPos).getComment_praise_num());
                                    txtPopZan.setSelected(false);
                                }
                                for (int i = 0; i < commentList.get(zanPos).getSub_praises().size(); i++){
                                    if(commentList.get(zanPos).getSub_praises().get(i).getUid().equals(LoginUtil.getInfo("uid"))){
                                        commentList.get(zanPos).getSub_praises().remove(i);
                                        break;
                                    }
                                }
                            }else {
//                                ToastUtil.show(mContext,"点赞成功");
                                commentList.get(zanPos).setIs_sub_praise(1);
                                if(!StringUtils.isEmpty(commentList.get(zanPos).getComment_praise_num()) && !commentList.get(zanPos).getComment_praise_num().equals("0")){
                                    commentList.get(zanPos).setComment_praise_num(String.valueOf(Integer.parseInt(commentList.get(zanPos).getComment_praise_num())+1));
                                }else {
                                    commentList.get(zanPos).setComment_praise_num("1");
                                }
                                if(popupWindow != null && popupWindow.isShowing()){
                                    txtPopZanNum.setText(commentList.get(zanPos).getComment_praise_num()+"人赞过");
                                    txtPopZan.setText(commentList.get(zanPos).getComment_praise_num());
                                    txtPopZan.setSelected(true);
                                }
                                if(!StringUtils.isEmpty(object.optString("result"))){
                                    CircleCommentBean.SubPraisesBean bean = FastJsonTools.getPerson(object.optString("result"),CircleCommentBean.SubPraisesBean.class);
                                    if(commentList.get(zanPos).getSub_praises() == null){
                                        commentList.get(zanPos).setSub_praises(new ArrayList<CircleCommentBean.SubPraisesBean>());
                                    }
                                    commentList.get(zanPos).getSub_praises().add(bean);
                                }
                            }
                            commentAdapter.notifyItemChanged(zanPos);
                            zanPos = -1;
                        }
                    } else {
                        ToastUtil.show(mContext,object.optString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case HttpModeBase.HTTP_REQUESTCODE_5: //回复评论
                try {
                    JSONObject object = new JSONObject((String) msg.obj);
                    if (object.optInt("status") == 1) {
                        ToastUtil.show(mContext,"回复成功");
                        if(replyPos != -1){
                            if(!StringUtils.isEmpty(object.optString("result"))){
                                CircleCommentBean bean = FastJsonTools.getPerson(object.optString("result"),CircleCommentBean.class);
                                if(commentList.get(replyPos).getSub_comments() == null){
                                    commentList.get(replyPos).setSub_comments(new ArrayList<CircleCommentBean>());
                                }
                                commentList.get(replyPos).getSub_comments().add(bean);
                                commentAdapter.notifyDataSetChanged();
                                if(popupWindow != null && popupWindow.isShowing()){
                                    txtPopTitle.setText(commentList.get(replyPos).getSub_comments().size()+"回复");
                                    popList.clear();
                                    popList.addAll(commentList.get(replyPos).getSub_comments());
                                    popAdapter.notifyDataSetChanged();
                                }
                            }
                            replyPos = -1;
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

    private void setAdapter() {
        adapter = new QuickAdapter<ImageTextBean>(mContext,R.layout.layout_goods_content_detail_item,list) {
            @Override
            protected void convert(BaseAdapterHelper helper, ImageTextBean item) {
                int position = helper.getPosition();
                RelativeLayout ll = helper.getView(R.id.ll);
                if(position == list.size() - 1){
                    ll.setPadding(DisplayUtil.dip2px(mContext,12),DisplayUtil.dip2px(mContext,10),DisplayUtil.dip2px(mContext,12),DisplayUtil.dip2px(mContext,10));
                }else {
                    ll.setPadding(DisplayUtil.dip2px(mContext,12),DisplayUtil.dip2px(mContext,10),DisplayUtil.dip2px(mContext,12),0);
                }
                if(list.get(position).getHeight() != 0 && list.get(position).getWidth() != 0){
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) helper.getParams(R.id.image);
                    float f = Float.valueOf(list.get(position).getHeight())/list.get(position).getWidth();
                    lp.height = DisplayUtil.getHeight(mContext,f);
                    helper.setLayoutParams(R.id.image,lp);
                }
                if(list.get(position).getContent_type().equals("1")){
                    helper.setVisible(R.id.image,false);
                    helper.setVisible(R.id.txt,true);
                    TextView txt = helper.getView(R.id.txt);
                    txt.setTextSize(18);
                    helper.setText(R.id.txt,EmojiUtil.utf8ToString(item.getContent()));
                }else {
                    helper.setVisible(R.id.image,true);
                    helper.setVisible(R.id.txt,false);
                    helper.setSimpViewImageUri(R.id.image,Uri.parse(item.getContent()));
                }
            }
        };
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PictureSelectUtil.show(ArticleDetailActivity.this,position,list);
            }
        });

        commentAdapter = new BaseRecyclerAdapter<CircleCommentBean>(mContext,commentList,R.layout.layout_article_detail_comment_item) {
            @Override
            public void convert(BaseRecyclerHolder holder, final CircleCommentBean item, final int position, boolean isScrolling) {
                holder.setSimpViewImageUri(R.id.image,Uri.parse(item.getAvatar()));
                holder.setText(R.id.txt_name,item.getNick_name());
                holder.setText(R.id.txt_content,EmojiUtil.utf8ToString(item.getContent()));
                holder.setText(R.id.txt_time,item.getCreate_time());
                holder.setSelected(R.id.txt_zan,1 == item.getIs_sub_praise());
                holder.setText(R.id.txt_zan,item.getComment_praise_num());
                List<CircleCommentBean> childList = new ArrayList<>();
                if(commentList.get(position).getSub_comments() != null && commentList.get(position).getSub_comments().size() > 0){
                    holder.setText(R.id.txt_toltal_reply,commentList.get(position).getSub_comments().size()+"回复");
                    holder.setVisible(R.id.ll_reply,true);
                    if(commentList.get(position).getSub_comments().size() > 2){
                        holder.setVisible(R.id.txt_all_reply,true);
                        holder.setText(R.id.txt_all_reply,"查看全部"+commentList.get(position).getSub_comments().size()+"条回复");
                        childList.addAll(commentList.get(position).getSub_comments().subList(0,2));
                    }else {
                        holder.setVisible(R.id.txt_all_reply,false);
                        childList.addAll(commentList.get(position).getSub_comments());
                    }
                }else {
                    holder.setText(R.id.txt_toltal_reply,"0回复");
                    holder.setVisible(R.id.ll_reply,false);
                    holder.setVisible(R.id.txt_all_reply,false);
                }

                RecyclerView recyclerViewReply = holder.getView(R.id.recycler_view_reply);
                recyclerViewReply.setLayoutManager(new FullyLinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false));
                recyclerViewReply.setHasFixedSize(true);
                recyclerViewReply.setNestedScrollingEnabled(false);
                recyclerViewReply.setAdapter(new BaseRecyclerAdapter<CircleCommentBean>(mContext,childList,R.layout.layout_circle_pinglun_item) {
                    @Override
                    public void convert(BaseRecyclerHolder holder, CircleCommentBean item, int position, boolean isScrolling) {
                        TextView txt= holder.getView(R.id.txt);
                        String str =  item.getNick_name()+":"+EmojiUtil.utf8ToString(item.getContent());
                        SpannableStringBuilder builder = new SpannableStringBuilder(str);
                        ForegroundColorSpan blueSpan = new ForegroundColorSpan(Color.parseColor("#59aaab"));
                        builder.setSpan(blueSpan, 0, item.getNick_name().length()+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        txt.setText(builder);
                    }
                });

                holder.setOnClickListener(R.id.txt_content, new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        if(LoginUtil.getLoginState()){
                            replyDialog = new ReplyDialog(mContext);
                            replyDialog.setCanceledOnTouchOutside(true);
                            replyDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    StringUtils.closeKeyBorder(mContext,v);
                                    replyDialog.dismiss();
                                }
                            });
                            replyDialog.setOnBtnCommitClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(StringUtils.isEmpty(replyDialog.getContent())){
                                        ToastUtil.show(mContext,"请输入评论");
                                        return;
                                    }
                                    StringUtils.closeKeyBorder(mContext,v);
                                    replyDialog.dismiss();
                                    replyPos = position;
                                    mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_5,UrlUtils.circle_sub_comment_set(LoginUtil.getInfo("token"),
                                            LoginUtil.getInfo("uid"), item.getCircle_comment_id(),replyDialog.getContent()),true);
                                }
                            }).show();
                        }else {
                            ToastUtil.show(mContext,"请先登录");
                        }
                    }
                });
                holder.setOnClickListener(R.id.txt_zan, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        zanPos = position;
                        mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_4,UrlUtils.circle_sub_praise_set(LoginUtil.getInfo("token"),LoginUtil.getInfo("uid"),
                                item.getCircle_comment_id(),item.getIs_sub_praise() == 1?"0":"1"),true);
                    }
                });
                holder.setOnClickListener(R.id.txt_all_reply, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showPopupWindow(position,commentList.get(position));
                    }
                });
            }
        };
        recyclerView.setAdapter(commentAdapter);
    }

    private void showPopupWindow(final int position, final CircleCommentBean bean) {
        popView = LayoutInflater.from(mContext).inflate(R.layout.pop_check_all_reply, null);
        popupWindow = new SupportPopupWindow(popView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        txtPopTitle = popView.findViewById(R.id.txt_title);
        popImage = popView.findViewById(R.id.image);
        ivClose = popView.findViewById(R.id.iv_close);
        txtPopName = popView.findViewById(R.id.txt_name);
        txtPopContent = popView.findViewById(R.id.txt_content);
        txtPopTime = popView.findViewById(R.id.txt_time);
        txtPopZanNum = popView.findViewById(R.id.txt_zan_num);
        txtPopZan = popView.findViewById(R.id.txt_zan);
        llComment = popView.findViewById(R.id.ll_comment);
        txtPopTitle.setText(bean.getSub_comments().size()+"回复");
        popImage.setImageURI(Uri.parse(bean.getAvatar()));
        txtPopName.setText(bean.getNick_name());
        txtPopContent.setText(EmojiUtil.utf8ToString(bean.getContent()));
        txtPopTime.setText(bean.getCreate_time());
        txtPopZanNum.setText(bean.getComment_praise_num()+"人赞过");
        txtPopZan.setText(bean.getComment_praise_num());
        txtPopZan.setSelected(bean.getIs_sub_praise() == 1);

        popList.clear();
        popList.addAll(bean.getSub_comments());
        rvPopReply = popView.findViewById(R.id.recycler_view_reply);
        rvPopReply.setLayoutManager(new FullyLinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false));
        rvPopReply.setHasFixedSize(true);
        rvPopReply.setNestedScrollingEnabled(false);
        popAdapter = new BaseRecyclerAdapter<CircleCommentBean>(mContext,popList,R.layout.layout_article_detail_comment_item) {
            @Override
            public void convert(BaseRecyclerHolder holder, CircleCommentBean item, int position, boolean isScrolling) {
                holder.setVisible(R.id.ll_reply,false);
                holder.setVisible(R.id.txt_zan,false);
                holder.setVisible(R.id.txt_dot,false);
                holder.setVisible(R.id.txt_toltal_reply,false);

                holder.setSimpViewImageUri(R.id.image,Uri.parse(item.getAvatar()));
                holder.setText(R.id.txt_name,item.getNick_name());
                holder.setText(R.id.txt_content,EmojiUtil.utf8ToString(item.getContent()));
                holder.setText(R.id.txt_time,item.getCreate_time());
            }
        };
        rvPopReply.setAdapter(popAdapter);

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        txtPopZanNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showZanPopupWindow(commentList.get(position).getSub_praises());
            }
        });
        txtPopZan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zanPos = position;
                mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_4,UrlUtils.circle_sub_praise_set(LoginUtil.getInfo("token"),LoginUtil.getInfo("uid"),
                        bean.getCircle_comment_id(),bean.getIs_sub_praise() == 1?"0":"1"),true);
            }
        });
        llComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if(LoginUtil.getLoginState()){
                    replyDialog = new ReplyDialog(mContext);
                    replyDialog.setCanceledOnTouchOutside(true);
                    replyDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
//                            InputMethodManager inputMgr = (InputMethodManager) mContext
//                                    .getSystemService(Context.INPUT_METHOD_SERVICE);
//                            inputMgr.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0);
                            StringUtils.closeKeyBorder(mContext,v);
                            replyDialog.dismiss();
                        }
                    });
                    replyDialog.setOnBtnCommitClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(StringUtils.isEmpty(replyDialog.getContent())){
                                ToastUtil.show(mContext,"请输入评论");
                                return;
                            }
                            StringUtils.closeKeyBorder(mContext,v);
                            replyDialog.dismiss();
                            replyPos = position;
                            mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_5,UrlUtils.circle_sub_comment_set(LoginUtil.getInfo("token"),
                                    LoginUtil.getInfo("uid"), bean.getCircle_comment_id(),replyDialog.getContent()),true);
                        }
                    }).show();
                }else {
                    ToastUtil.show(mContext,"请先登录");
                }
            }
        });
        popView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK){
                    popupWindow.dismiss();
                }
                return false;
            }
        });
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.5f;
        getWindow().setAttributes(lp);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });
//        popView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
//        int popupWidth = popView.getMeasuredWidth();    //  获取测量后的宽度
//        int popupHeight = popView.getMeasuredHeight();  //获取测量后的高度
//        //获取需要在其上方显示的控件的位置信息
//        int[] location = new int[2];
//        llBottom.getLocationOnScreen(location);
        //在控件上方显示
//        popupWindow.showAtLocation(llBottom,Gravity.NO_GRAVITY, (location[0] + llBottom.getWidth() / 2) - popupWidth / 2, location[1] - popupHeight);
        popupWindow.showAtLocation(popView,Gravity.BOTTOM, 0,0);
    }

    private void showZanPopupWindow(List<CircleCommentBean.SubPraisesBean> list) {
        popViewZan = LayoutInflater.from(mContext).inflate(R.layout.pop_check_all_zan, null);
        popupWindowZan = new SupportPopupWindow(popViewZan, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        TextView txtPopTitleZan = popViewZan.findViewById(R.id.txt_title);
        TextView close = popViewZan.findViewById(R.id.txt_left);
        close.setTypeface(StringUtils.getFont(mContext));
        txtPopTitleZan.setText(list.size()+"人点赞");
        RecyclerView rvZan = popViewZan.findViewById(R.id.recycler_view_item);
        rvZan.setLayoutManager(new FullyLinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false));
        rvZan.setHasFixedSize(true);
        rvZan.setNestedScrollingEnabled(false);
        rvZan.setAdapter(new BaseRecyclerAdapter<CircleCommentBean.SubPraisesBean>(mContext,list,R.layout.layout_pop_check_all_zan_item) {
            @Override
            public void convert(BaseRecyclerHolder holder, CircleCommentBean.SubPraisesBean item, int position, boolean isScrolling) {
                holder.setImage(R.id.iv_image,item.getAvatar(),true);
                holder.setText(R.id.txt_name,item.getNick_name());
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindowZan.dismiss();
            }
        });

        popViewZan.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK){
                    popupWindowZan.dismiss();
                }
                return false;
            }
        });
//        WindowManager.LayoutParams lp = getWindow().getAttributes();
//        lp.alpha = 0.5f;
//        getWindow().setAttributes(lp);
//        popupWindowZan.setOnDismissListener(new PopupWindow.OnDismissListener() {
//            @Override
//            public void onDismiss() {
//                WindowManager.LayoutParams lp = getWindow().getAttributes();
//                lp.alpha = 1f;
//                getWindow().setAttributes(lp);
//            }
//        });
//        popView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
//        int popupWidth = popView.getMeasuredWidth();    //  获取测量后的宽度
//        int popupHeight = popView.getMeasuredHeight();  //获取测量后的高度
//        //获取需要在其上方显示的控件的位置信息
//        int[] location = new int[2];
//        llBottom.getLocationOnScreen(location);
        //在控件上方显示
//        popupWindow.showAtLocation(llBottom,Gravity.NO_GRAVITY, (location[0] + llBottom.getWidth() / 2) - popupWidth / 2, location[1] - popupHeight);
        popupWindowZan.showAtLocation(popViewZan,Gravity.BOTTOM, 0,0);
    }

    @Override
    @OnClick({R.id.txt_left,R.id.iv_share,R.id.iv_share_bottom,R.id.txt_attection,R.id.txt_zan,R.id.txt_unlike,R.id.iv_shoucang,R.id.ll_comment,R.id.rl_all_comment})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.txt_left:
                finish();
                break;
            case R.id.txt_attection:
                if(LoginUtil.getLoginState()){
                    type = 1;
                    mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_2,UrlUtils.attention_set(LoginUtil.getInfo("token"),LoginUtil.getInfo("uid"),"3",
                            u_id, txtAttection.isSelected()?"0":"1"),true);
                }else {
                    startActivity(new Intent(mContext, LoginActivity.class));
                }
                break;
            case R.id.txt_zan:
                if(LoginUtil.getLoginState()){
                    type = 2;
                    mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_2,UrlUtils.circle_praise_set(LoginUtil.getInfo("token"),LoginUtil.getInfo("uid"),circle_id,
                            txtZan.isSelected()?"0":"1"),true);
                }else {
                    startActivity(new Intent(mContext, LoginActivity.class));
                }
                break;
            case R.id.txt_unlike:
                if(LoginUtil.getLoginState()){
                    type = 3;
                    mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_2,UrlUtils.circle_not_like_set(LoginUtil.getInfo("token"),LoginUtil.getInfo("uid"),
                            circle_id,txtUnlike.isSelected()?"0":"1"),true);
                }else {
                    startActivity(new Intent(mContext, LoginActivity.class));
                }
                break;
            case R.id.iv_shoucang:
                if(LoginUtil.getLoginState()){
                    type = 4;
                    mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_2,UrlUtils.circle_collect_set(LoginUtil.getInfo("token"),LoginUtil.getInfo("uid"),circle_id,
                            ivShoucang.isSelected()?"0":"1"),true);
                }else {
                    startActivity(new Intent(mContext, LoginActivity.class));
                }
                break;
            case R.id.iv_share:
            case R.id.iv_share_bottom:
                if(shareBean != null){
                    share();
                }
                break;
            case R.id.rl_all_comment:
//                startActivity(new Intent(mContext,ArticleAllCommentActivity.class));
                int height = ll.getHeight();
                scrollView.smoothScrollTo(0,height);
                break;
            case R.id.ll_comment:
                if(LoginUtil.getLoginState()){
                    replyDialog = new ReplyDialog(mContext);
                    replyDialog.setCanceledOnTouchOutside(true);
                    replyDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
//                            InputMethodManager inputMgr = (InputMethodManager) mContext
//                                    .getSystemService(Context.INPUT_METHOD_SERVICE);
//                            inputMgr.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0);
                            StringUtils.closeKeyBorder(mContext,txtCommentNum1);
                            replyDialog.dismiss();
                        }
                    });
                    replyDialog.setOnBtnCommitClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(StringUtils.isEmpty(replyDialog.getContent())){
                                ToastUtil.show(mContext,"请输入评论");
                                return;
                            }
                            StringUtils.closeKeyBorder(mContext,v);
                            replyDialog.dismiss();
                            mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_3,UrlUtils.circle_comment_set(LoginUtil.getInfo("token"),LoginUtil.getInfo("uid"),
                                    circle_id,replyDialog.getContent()),true);
                        }
                    }).show();
                }else {
                    startActivity(new Intent(mContext, LoginActivity.class));
                }
                break;
        }
    }

    private void share() {
        NiceDialog.init().setLayoutId(R.layout.pop_share)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    public void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        TextView txtDt = holder.getView(R.id.txt_dt);
                        txtDt.setVisibility(View.VISIBLE);
                        holder.setOnClickListener(R.id.txt_pyq, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new ShareUtil(ArticleDetailActivity.this, SHARE_MEDIA.WEIXIN_CIRCLE,shareBean);
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.txt_wx, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new ShareUtil(ArticleDetailActivity.this, SHARE_MEDIA.WEIXIN,shareBean);
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.txt_qq, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new ShareUtil(ArticleDetailActivity.this, SHARE_MEDIA.QQ,shareBean);
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.txt_wb, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new ShareUtil(ArticleDetailActivity.this, SHARE_MEDIA.SINA,shareBean);
                                dialog.dismiss();
                            }
                        });
                        txtDt.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                it = new Intent(mContext,PublishTrendsActivity.class);
                                it.putExtra("is_share",true);
                                it.putExtra("trans_type",1);
                                it.putExtra("trans_id",circle_id);
                                it.putExtra("trans_title",txtArticalTitle.getText().toString());
                                it.putExtra("trans_img",article_img);
                                startActivity(it);
                            }
                        });
                    }
                })
                .setOutCancel(true).setShowBottom(true)
                .show(getSupportFragmentManager());
    }

    @Override
    public void onBackPressed() {
        if(popupWindowZan != null && popupWindowZan.isShowing()){
            popupWindowZan.dismiss();
        }else {
            if(popupWindow != null && popupWindow.isShowing()){
                popupWindow.dismiss();
            }else {
                super.onBackPressed();
            }
        }
    }
}
