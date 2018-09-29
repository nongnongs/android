package com.maapuu.mereca.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.maapuu.mereca.R;
import com.maapuu.mereca.base.BaseActivity;
import com.maapuu.mereca.recycleviewutil.BaseRecyclerAdapter;
import com.maapuu.mereca.recycleviewutil.BaseRecyclerHolder;
import com.maapuu.mereca.recycleviewutil.FullyLinearLayoutManager;
import com.maapuu.mereca.recycleviewutil.RecyclerViewDivider;
import com.maapuu.mereca.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by dell on 2018/3/5.
 * 圈子详情 全部评论
 */

public class ArticleAllCommentActivity extends BaseActivity {
    @BindView(R.id.txt_left)
    TextView txtLeft;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private List<String> commentList = new ArrayList<>();
    private BaseRecyclerAdapter<String> commentAdapter;
    private LinearLayoutManager mLayoutManager;
    private int page = 1;

    @Override
    public void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_article_all_comment);
    }

    @Override
    public void initView() {
        txtLeft.setTypeface(StringUtils.getFont(mContext));

        commentList.add("");commentList.add("");
        commentList.add("");commentList.add("");
        mLayoutManager=new FullyLinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mLayoutManager);
        setAdapter(commentList);
    }

    @Override
    public void initData() {

    }

    private void setAdapter(List<String> lsJson) {
        if(commentAdapter == null){
            commentAdapter = new BaseRecyclerAdapter<String>(mContext,commentList,R.layout.layout_article_detail_comment_item) {
                @Override
                public void convert(BaseRecyclerHolder holder, String item, int position, boolean isScrolling) {
                    RecyclerView rvReply = holder.getView(R.id.recycler_view_reply);

                    rvReply.setHasFixedSize(true);
                    rvReply.setNestedScrollingEnabled(false);
                    rvReply.setLayoutManager(new FullyLinearLayoutManager(mContext, LinearLayoutManager.VERTICAL,false));
                    //这句就是添加我们自定义的分隔线
                    rvReply.addItemDecoration(new RecyclerViewDivider(mContext, LinearLayoutManager.VERTICAL,10
                            ,mContext.getResources().getColor(R.color.background)));
                    rvReply.setAdapter(new BaseRecyclerAdapter<String>(mContext,commentList,R.layout.layout_dongtai_pinglun_item) {
                        @Override
                        public void convert(BaseRecyclerHolder holder, String item, int position, boolean isScrolling) {
                            TextView txt= (TextView)holder.getView(R.id.txt);
                            txt.setBackgroundColor(getResources().getColor(R.color.background));
                            txt.setTextColor(getResources().getColor(R.color.text_33));
                            String str = txt.getText().toString();
                            SpannableStringBuilder builder = new SpannableStringBuilder(str);
                            ForegroundColorSpan blueSpan = new ForegroundColorSpan(Color.parseColor("#59aaab"));
                            ForegroundColorSpan blueSpan1 = new ForegroundColorSpan(Color.parseColor("#333333"));
                            ForegroundColorSpan blueSpan2 = new ForegroundColorSpan(Color.parseColor("#59aaab"));
                            builder.setSpan(blueSpan, 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            builder.setSpan(blueSpan1, 5, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            builder.setSpan(blueSpan2, 7, 9, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            txt.setText(builder);
                        }
                    });
                }
            };
            recyclerView.setAdapter(commentAdapter);
        }else {
            if(page > 1){
                commentAdapter.notifyItemRangeInserted(commentList.size()-lsJson.size(),lsJson.size());
                recyclerView.smoothScrollToPosition(commentList.size()-lsJson.size()-1);
            }else {
                commentAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    @OnClick({R.id.txt_left})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.txt_left:
                finish();
                break;
        }
    }
}
