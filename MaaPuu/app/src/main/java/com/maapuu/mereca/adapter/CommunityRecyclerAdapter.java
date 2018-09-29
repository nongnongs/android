package com.maapuu.mereca.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.maapuu.mereca.R;
import com.maapuu.mereca.bean.CircleBean;
import com.maapuu.mereca.util.EmojiUtil;
import com.maapuu.mereca.util.UIUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by dell on 2017/6/15.
 */

public class CommunityRecyclerAdapter extends RecyclerView.Adapter implements View.OnClickListener {
    private Context mContext;
    private List<CircleBean> list;

    //定义三种常量  表示三种条目类型
    public static final int TYPE_PULL_IMAGE = 0;
    public static final int TYPE_RIGHT_IMAGE = 1;
    public static final int TYPE_THREE_IMAGE = 2;

    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view);
        void onDelClick(int position);
        void onZanClick(int position);
    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public CommunityRecyclerAdapter(Context context, List<CircleBean> list){
        this.mContext = context;
        this.list = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        RecyclerView.ViewHolder holder = null;
        switch (viewType) {
            case TYPE_PULL_IMAGE: //3张图
                View v = mInflater.inflate(R.layout.layout_community_item1, parent, false);
                holder = new ViewHolder1(v);
                v.setOnClickListener(this);
                break;
            case TYPE_RIGHT_IMAGE:  //1张图
                View v1 = mInflater.inflate(R.layout.layout_community_item2, parent, false);
                holder = new ViewHolder2(v1);
                v1.setOnClickListener(this);
                break;
            case TYPE_THREE_IMAGE:  //2张图
                View v2 = mInflater.inflate(R.layout.layout_community_item3, parent, false);
                holder = new ViewHolder3(v2);
                v2.setOnClickListener(this);
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ViewHolder1){
            ((ViewHolder1) holder).txtTitle.setText(EmojiUtil.utf8ToString(list.get(position).getCircle_title()));
            UIUtils.loadImg(mContext,list.get(position).getDetail().get(0).getContent(),((ViewHolder1) holder).ivImage1);
            UIUtils.loadImg(mContext,list.get(position).getDetail().get(1).getContent(),((ViewHolder1) holder).ivImage2);
            UIUtils.loadImg(mContext,list.get(position).getDetail().get(2).getContent(),((ViewHolder1) holder).ivImage3);
//            ((ViewHolder1) holder).ivImage1.setImageURI(Uri.parse(list.get(position).getDetail().get(0).getContent()));
//            ((ViewHolder1) holder).ivImage2.setImageURI(Uri.parse(list.get(position).getDetail().get(1).getContent()));
//            ((ViewHolder1) holder).ivImage3.setImageURI(Uri.parse(list.get(position).getDetail().get(2).getContent()));
            ((ViewHolder1) holder).txtCommentNum.setText(list.get(position).getComment_num()+"评论");
            ((ViewHolder1) holder).txtTime.setText(list.get(position).getTime_text());
            ((ViewHolder1) holder).txtZan.setText(list.get(position).getPraise_num());
            if(list.get(position).getIs_delete().equals("1")){
                ((ViewHolder1) holder).txtDel.setVisibility(View.VISIBLE);
            }else {
                ((ViewHolder1) holder).txtDel.setVisibility(View.GONE);
            }
            if(list.get(position).getIs_praise() == 1){
                ((ViewHolder1) holder).txtZan.setSelected(true);
            }else {
                ((ViewHolder1) holder).txtZan.setSelected(false);
            }
            ((ViewHolder1) holder).txtDel.setOnClickListener(new MyListener(position));
            ((ViewHolder1) holder).txtZan.setOnClickListener(new MyListener(position));
        }else if(holder instanceof ViewHolder2){
            ((ViewHolder2) holder).txtTitle.setText(EmojiUtil.utf8ToString(list.get(position).getCircle_title()));
            UIUtils.loadImg(mContext,list.get(position).getDetail().get(0).getContent(),((ViewHolder2) holder).ivImage1);
//            ((ViewHolder2) holder).ivImage1.setImageURI(Uri.parse(list.get(position).getDetail().get(0).getContent()));
            ((ViewHolder2) holder).txtCommentNum.setText(list.get(position).getComment_num()+"评论");
            ((ViewHolder2) holder).txtTime.setText(list.get(position).getTime_text());
            ((ViewHolder2) holder).txtZan.setText(list.get(position).getPraise_num());
            if(list.get(position).getIs_delete().equals("1")){
                ((ViewHolder2) holder).txtDel.setVisibility(View.VISIBLE);
            }else {
                ((ViewHolder2) holder).txtDel.setVisibility(View.GONE);
            }
            if(list.get(position).getIs_praise() == 1){
                ((ViewHolder2) holder).txtZan.setSelected(true);
            }else {
                ((ViewHolder2) holder).txtZan.setSelected(false);
            }
            ((ViewHolder2) holder).txtDel.setOnClickListener(new MyListener(position));
            ((ViewHolder2) holder).txtZan.setOnClickListener(new MyListener(position));
        }else {
            ((ViewHolder3) holder).txtTitle.setText(EmojiUtil.utf8ToString(list.get(position).getCircle_title()));
            if(list.get(position).getDetail().size() != 0){
                ((ViewHolder3) holder).llImage.setVisibility(View.VISIBLE);
                UIUtils.loadImg(mContext,list.get(position).getDetail().get(0).getContent(),((ViewHolder3) holder).ivImage1);
                UIUtils.loadImg(mContext,list.get(position).getDetail().get(1).getContent(),((ViewHolder3) holder).ivImage2);
//                ((ViewHolder3) holder).ivImage1.setImageURI(Uri.parse(list.get(position).getDetail().get(0).getContent()));
//                ((ViewHolder3) holder).ivImage2.setImageURI(Uri.parse(list.get(position).getDetail().get(1).getContent()));
            }else {
                ((ViewHolder3) holder).llImage.setVisibility(View.GONE);
            }
            ((ViewHolder3) holder).txtCommentNum.setText(list.get(position).getComment_num()+"评论");
            ((ViewHolder3) holder).txtTime.setText(list.get(position).getTime_text());
            ((ViewHolder3) holder).txtZan.setText(list.get(position).getPraise_num());
            if(list.get(position).getIs_delete().equals("1")){
                ((ViewHolder3) holder).txtDel.setVisibility(View.VISIBLE);
            }else {
                ((ViewHolder3) holder).txtDel.setVisibility(View.GONE);
            }
            if(list.get(position).getIs_praise() == 1){
                ((ViewHolder3) holder).txtZan.setSelected(true);
            }else {
                ((ViewHolder3) holder).txtZan.setSelected(false);
            }
            ((ViewHolder3) holder).txtDel.setOnClickListener(new MyListener(position));
            ((ViewHolder3) holder).txtZan.setOnClickListener(new MyListener(position));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    //根据条件返回条目的类型
    @Override
    public int getItemViewType(int position) {
        if (list.get(position).getDetail().size() == 0) {
            return TYPE_THREE_IMAGE;
        } else if (list.get(position).getDetail().size()== 1) {
            return TYPE_RIGHT_IMAGE;
        } else if (list.get(position).getDetail().size()== 2) {
            return TYPE_THREE_IMAGE;
        } else{
            return TYPE_PULL_IMAGE;
        }
    }


    @Override
    public void onClick(View view) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(view);
        }
    }

    class MyListener implements View.OnClickListener {
        private int position;
        public MyListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(final View v) {
            switch (v.getId()) {
                case R.id.txt_zan:
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onZanClick(position);
                    }
                    break;
                case R.id.txt_del:
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onDelClick(position);
                    }
                    break;
            }
        }

    }

    class ViewHolder1 extends RecyclerView.ViewHolder{
        @BindView(R.id.txt_title)
        TextView txtTitle;
        @BindView(R.id.iv_image_1)
        ImageView ivImage1;
        @BindView(R.id.iv_image_2)
        ImageView ivImage2;
        @BindView(R.id.iv_image_3)
        ImageView ivImage3;
        @BindView(R.id.txt_comment_num)
        TextView txtCommentNum;
        @BindView(R.id.txt_time)
        TextView txtTime;
        @BindView(R.id.txt_zan)
        TextView txtZan;
        @BindView(R.id.txt_del)
        TextView txtDel;
        public ViewHolder1(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
    class ViewHolder2 extends RecyclerView.ViewHolder{
        @BindView(R.id.txt_title)
        TextView txtTitle;
        @BindView(R.id.iv_image_1)
        ImageView ivImage1;
        @BindView(R.id.txt_comment_num)
        TextView txtCommentNum;
        @BindView(R.id.txt_time)
        TextView txtTime;
        @BindView(R.id.txt_zan)
        TextView txtZan;
        @BindView(R.id.txt_del)
        TextView txtDel;
        public ViewHolder2(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
    class ViewHolder3 extends RecyclerView.ViewHolder{
        @BindView(R.id.txt_title)
        TextView txtTitle;
        @BindView(R.id.ll_image)
        LinearLayout llImage;
        @BindView(R.id.iv_image_1)
        ImageView ivImage1;
        @BindView(R.id.iv_image_2)
        ImageView ivImage2;
        @BindView(R.id.txt_comment_num)
        TextView txtCommentNum;
        @BindView(R.id.txt_time)
        TextView txtTime;
        @BindView(R.id.txt_zan)
        TextView txtZan;
        @BindView(R.id.txt_del)
        TextView txtDel;
        public ViewHolder3(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
