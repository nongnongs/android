package com.maapuu.mereca.fragment.goodschild;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.maapuu.mereca.R;
import com.maapuu.mereca.activity.ChooseItemsActivity;
import com.maapuu.mereca.activity.GoodsDetailActivity;
import com.maapuu.mereca.adapter.help.BaseAdapterHelper;
import com.maapuu.mereca.adapter.help.QuickAdapter;
import com.maapuu.mereca.base.BaseFragment;
import com.maapuu.mereca.bean.EvlBean;
import com.maapuu.mereca.bean.ImageTextBean;
import com.maapuu.mereca.bean.ShareBean;
import com.maapuu.mereca.constant.UrlUtils;
import com.maapuu.mereca.recycleviewutil.BaseRecyclerAdapter;
import com.maapuu.mereca.recycleviewutil.BaseRecyclerHolder;
import com.maapuu.mereca.recycleviewutil.FullyGridLayoutManager;
import com.maapuu.mereca.recycleviewutil.RecyclerViewDivider;
import com.maapuu.mereca.util.DisplayUtil;
import com.maapuu.mereca.util.EmojiUtil;
import com.maapuu.mereca.util.FastJsonTools;
import com.maapuu.mereca.util.HttpModeBase;
import com.maapuu.mereca.util.LoginUtil;
import com.maapuu.mereca.util.PictureSelectUtil;
import com.maapuu.mereca.util.StringUtils;
import com.maapuu.mereca.util.ToastUtil;
import com.maapuu.mereca.view.MyListView;
import com.maapuu.mereca.view.TimerTextView;
import com.othershe.nicedialog.BaseNiceDialog;
import com.othershe.nicedialog.NiceDialog;
import com.othershe.nicedialog.ViewConvertListener;
import com.othershe.nicedialog.ViewHolder;
import com.willy.ratingbar.ScaleRatingBar;
import com.ysnows.page.PageBehavior;
import com.ysnows.page.PageContainer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by dell on 2018/3/5.
 */

public class GoodsDetailFragment extends BaseFragment{
    @BindView(R.id.container)
    PageContainer container;
    @BindView(R.id.iv_goods)
    SimpleDraweeView ivGoods;
    @BindView(R.id.ll_qg)
    LinearLayout llQg;
    @BindView(R.id.txt_qg_price)
    TextView txtQgPrice;
    @BindView(R.id.txt_qg_sale_num)
    TextView txtQgSaleNum;
    @BindView(R.id.txt_original_price)
    TextView txtOriginalPrice;
    @BindView(R.id.txt_qg_countdown)
    TimerTextView txtQgCountdown;
    @BindView(R.id.txt_goods_name)
    TextView txtGoodsName;
    @BindView(R.id.txt_goods_desc)
    TextView txtGoodsDpec;
    @BindView(R.id.ll_price)
    LinearLayout llPrice;
    @BindView(R.id.txt_goods_price)
    TextView txtGoodsPrice;
    @BindView(R.id.txt_market_price)
    TextView txtMarketPrice;
    @BindView(R.id.txt_sale_num)
    TextView txtSaleNum;
    @BindView(R.id.txt_evl_good_percent)
    TextView txtEvlGoodPercent;
    @BindView(R.id.txt_all_evl_num)
    TextView txtAllEvlNum;
    @BindView(R.id.txt_pic_evl_num)
    TextView txtPicEvlNum;
    @BindView(R.id.txt_get_coupon)
    TextView txtGetCoupon;
    @BindView(R.id.ll_evl)
    LinearLayout llEvl;
    @BindView(R.id.image)
    SimpleDraweeView image;
    @BindView(R.id.txt_name)
    TextView txtName;
    @BindView(R.id.txt_time)
    TextView txtTime;
    @BindView(R.id.ratingbar)
    ScaleRatingBar ratingBar;
    @BindView(R.id.txt_content)
    TextView txtContent;
    @BindView(R.id.txt_zan)
    TextView txtZan;
    @BindView(R.id.recycler_view_img)
    RecyclerView recyclerViewImg;
    @BindView(R.id.list_view)
    MyListView listView;
    @BindView(R.id.txt_goods_spec)
    TextView txtGoodsSpec;
    @BindView(R.id.txt_reply_content)
    TextView txtReplyContent;

    private String item_id;
    private String shop_id;
    private String result;

    private List<ImageTextBean> list = new ArrayList<>();
    private QuickAdapter<ImageTextBean> adapter;
    private EvlBean evlBean;
    private BaseRecyclerAdapter<ImageTextBean> imgAdapter;
    private static GoodsDetailFragment fragment = null;

    public static GoodsDetailFragment newInstance() {
        if (fragment == null) {
            fragment = new GoodsDetailFragment();
        }
        return fragment;
    }

    @Override
    protected int setContentViewById() {
        Bundle bundle=getArguments();
        //判断需写
        if(bundle!=null) {
            item_id = bundle.getString("item_id");
            shop_id = bundle.getString("shop_id");
            result = bundle.getString("result");
        }
//        item_id = "1";
        return R.layout.fragment_goods_detail;
    }

    @Override
    protected void initView(View v) {
        LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) ivGoods.getLayoutParams();
        ll.height = ((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DisplayUtil.getWidthDP(mContext), mContext.getResources().getDisplayMetrics()));
        ivGoods.setLayoutParams(ll);
        txtOriginalPrice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        txtMarketPrice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);

        recyclerViewImg.setHasFixedSize(true);
        recyclerViewImg.setNestedScrollingEnabled(false);
        recyclerViewImg.setLayoutManager(new FullyGridLayoutManager(mContext,3, LinearLayoutManager.VERTICAL,false));
        //这句就是添加我们自定义的分隔线
        recyclerViewImg.addItemDecoration(new RecyclerViewDivider(mContext, LinearLayoutManager.VERTICAL,20
                ,mContext.getResources().getColor(R.color.white)));

        container.setOnPageChanged(new PageBehavior.OnPageChanged() {
            @Override
            public void toTop() {
                GoodsDetailActivity.activity.mHandler.sendEmptyMessage(120);
            }

            @Override
            public void toBottom() {
                GoodsDetailActivity.activity.mHandler.sendEmptyMessage(130);
            }
        });
    }

    @Override
    protected void initData() {
        try {
            JSONObject resultObj = new JSONObject(result);
            if(!StringUtils.isEmpty(resultObj.optString("item_img"))){
                ivGoods.setImageURI(Uri.parse(resultObj.optString("item_img")));
            }
            txtGoodsName.setText(resultObj.optString("item_name"));
            if(resultObj.has("item_desc")){
                txtGoodsDpec.setText(resultObj.optString("item_desc"));
            }else {
                txtGoodsDpec.setText(resultObj.optString("item_specification"));
            }
            txtGoodsPrice.setText(resultObj.optString("price"));
            txtMarketPrice.setText("门市价 ¥"+resultObj.optString("market_price"));
            if(resultObj.optDouble("price") == resultObj.optDouble("market_price")){
                txtMarketPrice.setVisibility(View.INVISIBLE);
            }else {
                txtMarketPrice.setVisibility(View.VISIBLE);
            }
            txtSaleNum.setText(resultObj.optString("sale_num")+"人购买过");
            txtEvlGoodPercent.setText(resultObj.optString("evl_good_percent")+"%");
            txtAllEvlNum.setText("全部评价("+resultObj.optString("evl_num")+")");
            txtPicEvlNum.setText("晒单评价("+resultObj.optString("evl_share_num")+")");
            if(resultObj.optInt("is_red_list") == 1){
                txtGetCoupon.setVisibility(View.VISIBLE);
            }else {
                txtGetCoupon.setVisibility(View.GONE);
            }
            if(StringUtils.isEmpty(resultObj.optString("diff_seconds")) || resultObj.optString("diff_seconds").equals("0")){
                llQg.setVisibility(View.GONE);llPrice.setVisibility(View.VISIBLE);
            }else {
                llQg.setVisibility(View.VISIBLE);llPrice.setVisibility(View.GONE);
                txtQgPrice.setText(resultObj.optString("promotion_price"));
                txtOriginalPrice.setText(resultObj.optString("price"));
                txtOriginalPrice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                txtQgSaleNum.setText("已有"+resultObj.optString("sale_num")+"人购买过");
                //获得时间差
                long diff = Long.parseLong(resultObj.optString("diff_seconds"))*1000;
                if(diff > 0){
                    txtQgCountdown.setTimes(diff);//设置时间
                    if(!txtQgCountdown.isRun()){txtQgCountdown.start();}//开始倒计时
                }
                txtQgCountdown.setCall(new TimerTextView.CallBack() {
                    @Override
                    public void onCall() {

                    }

                    @Override
                    public void onCall(int position) {
                        GoodsDetailActivity.activity.mHandler.sendEmptyMessage(999);
                    }
                });
            }
            if(resultObj.has("evl_data") && !(resultObj.opt("evl_data") instanceof Boolean)
                    && !StringUtils.isEmpty(resultObj.optString("evl_data"))){
                llEvl.setVisibility(View.VISIBLE);
                evlBean = FastJsonTools.getPerson(resultObj.optString("evl_data"),EvlBean.class);
                image.setImageURI(Uri.parse(evlBean.getAvatar()));
                txtName.setText(evlBean.getNick_name());
                txtContent.setVisibility(StringUtils.isEmpty(evlBean.getEvl_content())?View.GONE:View.VISIBLE);
                txtContent.setText(EmojiUtil.utf8ToString(evlBean.getEvl_content()));
                if(!StringUtils.isEmpty(evlBean.getCreate_time())){
                    txtTime.setText(evlBean.getCreate_time().substring(0,10));
                }
                if(!StringUtils.isEmpty(evlBean.getIs_evl_praise())){
                    if(evlBean.getIs_evl_praise().equals("1")){
                        txtZan.setSelected(true);
                    }else {
                        txtZan.setSelected(false);
                    }
                }
                txtZan.setText(evlBean.getPraise_num());
                ratingBar.setRating(Float.parseFloat(evlBean.getEvl_level()));
                txtGoodsSpec.setText(evlBean.getOrder_title());
                if("1".equals(evlBean.getIs_reply())){
                    txtReplyContent.setVisibility(View.VISIBLE);
                    txtReplyContent.setText("商家回复："+EmojiUtil.utf8ToString(evlBean.getReply_content()));
                }else {
                    txtReplyContent.setVisibility(View.GONE);
                }
                if(evlBean.getDetail() != null && evlBean.getDetail().size() > 0){
                    recyclerViewImg.setVisibility(View.VISIBLE);
                    imgAdapter = new BaseRecyclerAdapter<ImageTextBean>(mContext,evlBean.getDetail(),R.layout.layout_goods_comment_image_item) {
                        @Override
                        public void convert(BaseRecyclerHolder holder, ImageTextBean item, int position, boolean isScrolling) {
                            holder.setImage(R.id.image,item.getContent(),false);
//                            holder.setSimpViewImageUri(R.id.image,Uri.parse(item.getContent()));
                        }
                    };
                    recyclerViewImg.setAdapter(imgAdapter);
                    imgAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(RecyclerView parent, View view, int position) {
                            PictureSelectUtil.show((Activity)mContext,position,evlBean.getDetail());
                        }
                    });
                }else {
                    recyclerViewImg.setVisibility(View.GONE);
                }
            }else {
                llEvl.setVisibility(View.GONE);
            }

//            if(resultObj.has("item_detail") && resultObj.optJSONArray("item_detail").length() > 0){
//                list = FastJsonTools.getPersons(resultObj.optString("item_detail"), ImageTextBean.class);
//            }
//            adapter = new QuickAdapter<ImageTextBean>(mContext,R.layout.layout_goods_content_detail_item,list) {
//                @Override
//                protected void convert(BaseAdapterHelper helper, ImageTextBean item) {
//                    int position = helper.getPosition();
//                    RelativeLayout ll = helper.getView(R.id.ll);
//                    if(position == list.size() - 1){
//                        ll.setPadding(DisplayUtil.dip2px(mContext,12),DisplayUtil.dip2px(mContext,10),DisplayUtil.dip2px(mContext,12),DisplayUtil.dip2px(mContext,10));
//                    }else {
//                        ll.setPadding(DisplayUtil.dip2px(mContext,12),DisplayUtil.dip2px(mContext,10),DisplayUtil.dip2px(mContext,12),0);
//                    }
//                    if(list.get(position).getHeight() != 0 && list.get(position).getWidth() != 0){
//                        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) helper.getParams(R.id.image);
//                        float f = Float.valueOf(list.get(position).getHeight())/list.get(position).getWidth();
//                        lp.height = DisplayUtil.getHeight(mContext,f);
//                    }
//                    if(list.get(position).getContent_type().equals("1")){
//                        helper.setVisible(R.id.image,false);
//                        helper.setVisible(R.id.txt,true);
//                        helper.setText(R.id.txt,item.getContent());
//                    }else {
//                        helper.setVisible(R.id.image,true);
//                        helper.setVisible(R.id.txt,false);
//                        helper.setSimpViewImageUri(R.id.image,Uri.parse(item.getContent()));
//                    }
//                }
//            };
//            listView.setAdapter(adapter);
//            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    PictureSelectUtil.show(getActivity(),position,list);
//                }
//            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case 300:
                container.backToTop();//滑到头部
                break;
            case HttpModeBase.HTTP_REQUESTCODE_6:
                try {
                    JSONObject object = new JSONObject((String) msg.obj);
                    if (object.optInt("status") == 1) {
                        if(evlBean.getIs_evl_praise().equals("1")){
                            ToastUtil.show(mContext,"取消成功");
                            evlBean.setIs_evl_praise("0");
                            if(!StringUtils.isEmpty(evlBean.getPraise_num()) && !evlBean.getPraise_num().equals("0")){
                                if(Integer.parseInt(evlBean.getPraise_num())-1 == 0){
                                    evlBean.setPraise_num("0");
                                }else {
                                    evlBean.setPraise_num(String.valueOf(Integer.parseInt(evlBean.getPraise_num())-1));
                                }
                            }
                        }else {
                            ToastUtil.show(mContext,"点赞成功");
                            evlBean.setIs_evl_praise("1");
                            if(!StringUtils.isEmpty(evlBean.getPraise_num()) && !evlBean.getPraise_num().equals("0")){
                                evlBean.setPraise_num(String.valueOf(Integer.parseInt(evlBean.getPraise_num())+1));
                            }else {
                                evlBean.setPraise_num("1");
                            }
                        }
                        if(evlBean.getIs_evl_praise().equals("1")){
                            txtZan.setSelected(true);
                            txtZan.setText(evlBean.getPraise_num());
                        }else {
                            txtZan.setSelected(false);
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

    @Override
    @OnClick({R.id.txt_get_coupon,R.id.rl_zuhe,R.id.txt_zan,R.id.txt_all_evl_num,R.id.txt_pic_evl_num})
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.txt_get_coupon:
                getCoupon();
                break;
            case R.id.rl_zuhe:
                it = new Intent(mContext,ChooseItemsActivity.class);
                it.putExtra("shop_id",shop_id);
                startActivity(it);
                break;
            case R.id.txt_zan:
                mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_6, UrlUtils.evalution_praise_set(LoginUtil.getInfo("token"),
                        LoginUtil.getInfo("uid"),evlBean.getEvl_id(),evlBean.getIs_evl_praise().equals("1")?"0":"1"),true);
                break;
            case R.id.txt_all_evl_num:
                Message msg = new Message();
                msg.what = 101;
                msg.arg1 = 0;
                GoodsDetailActivity.activity.mHandler.sendMessage(msg);
                break;
            case R.id.txt_pic_evl_num:
                Message msg2 = new Message();
                msg2.what = 101;
                msg2.arg1 = 1;
                GoodsDetailActivity.activity.mHandler.sendMessage(msg2);
                break;
        }
    }

    private void getCoupon() {
        final List<String> list = new ArrayList<>();
        list.add("");list.add("");list.add("");
        NiceDialog.init().setLayoutId(R.layout.pop_get_coupon)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    public void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        TextView txtCommit = holder.getView(R.id.txt_commit);
                        MyListView listView = holder.getView(R.id.list_view);
                        listView.setAdapter(new QuickAdapter<String>(mContext,R.layout.layout_coupon_item,list) {
                            @Override
                            protected void convert(BaseAdapterHelper helper, String item) {
                                int  position = helper.getPosition();
                                if(position == list.size() - 1){
                                    helper.setMargins(R.id.ll_item,mContext,12,10,12,10);
                                }else {
                                    helper.setMargins(R.id.ll_item,mContext,12,10,12,0);
                                }
                            }
                        });
                        txtCommit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                    }
                })
                .setOutCancel(true).setShowBottom(true).setHeight(450)
                .show(getChildFragmentManager());
    }

}
