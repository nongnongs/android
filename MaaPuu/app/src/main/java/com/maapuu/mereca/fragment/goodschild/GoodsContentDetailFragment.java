package com.maapuu.mereca.fragment.goodschild;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.maapuu.mereca.R;
import com.maapuu.mereca.adapter.help.BaseAdapterHelper;
import com.maapuu.mereca.adapter.help.QuickAdapter;
import com.maapuu.mereca.base.BaseFragment;
import com.maapuu.mereca.bean.ImageTextBean;
import com.maapuu.mereca.util.DisplayUtil;
import com.maapuu.mereca.util.FastJsonTools;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by dell on 2018/3/5.
 */

public class GoodsContentDetailFragment extends BaseFragment{
    @BindView(R.id.list_view)
    ListView listView;

    private String item_id;
    private String result;
    private List<ImageTextBean> list = new ArrayList<>();
    private QuickAdapter<ImageTextBean> adapter;
    private static GoodsContentDetailFragment fragment = null;

    public static GoodsContentDetailFragment newInstance() {
        if (fragment == null) {
            fragment = new GoodsContentDetailFragment();
        }
        return fragment;
    }

    @Override
    protected int setContentViewById() {
        Bundle bundle=getArguments();
        //判断需写
        if(bundle!=null) {
            item_id = bundle.getString("item_id");
            result = bundle.getString("result");
        }
        return R.layout.fragment_goods_content_detail;
    }

    @Override
    protected void initView(View v) {

    }

    @Override
    protected void initData() {
        try {
            JSONObject resultObj = new JSONObject(result);

            if(resultObj.has("item_detail") && resultObj.optJSONArray("item_detail").length() > 0){
                list = FastJsonTools.getPersons(resultObj.optString("item_detail"), ImageTextBean.class);
            }
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
                        helper.setText(R.id.txt,item.getContent());
                    }else {
                        helper.setVisible(R.id.image,true);
                        helper.setVisible(R.id.txt,false);
                        helper.setSimpViewImageUri(R.id.image,Uri.parse(item.getContent()));
                    }
                }
            };
            listView.setAdapter(adapter);
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
    public void onClick(View v) {

    }
}
