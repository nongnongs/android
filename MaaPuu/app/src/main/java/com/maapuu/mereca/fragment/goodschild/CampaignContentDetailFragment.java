package com.maapuu.mereca.fragment.goodschild;

import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.maapuu.mereca.R;
import com.maapuu.mereca.adapter.help.BaseAdapterHelper;
import com.maapuu.mereca.adapter.help.QuickAdapter;
import com.maapuu.mereca.base.BaseFragment;
import com.maapuu.mereca.bean.ImageTextBean;
import com.maapuu.mereca.constant.UrlUtils;
import com.maapuu.mereca.util.DisplayUtil;
import com.maapuu.mereca.util.FastJsonTools;
import com.maapuu.mereca.util.HttpModeBase;
import com.maapuu.mereca.util.LoginUtil;
import com.maapuu.mereca.util.PictureSelectUtil;
import com.maapuu.mereca.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by dell on 2018/3/5.
 */

public class CampaignContentDetailFragment extends BaseFragment{
    @BindView(R.id.list_view)
    ListView listView;

    private String pack_id;
    private String shop_id;
    private List<ImageTextBean> list = new ArrayList<>();
    private QuickAdapter<ImageTextBean> adapter;
    private static CampaignContentDetailFragment fragment = null;

    public static CampaignContentDetailFragment newInstance() {
        if (fragment == null) {
            fragment = new CampaignContentDetailFragment();
        }
        return fragment;
    }

    @Override
    protected int setContentViewById() {
        Bundle bundle=getArguments();
        //判断需写
        if(bundle!=null) {
            pack_id = bundle.getString("pack_id");
            shop_id = bundle.getString("shop_id");
        }
        return R.layout.fragment_goods_content_detail;
    }

    @Override
    protected void initView(View v) {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PictureSelectUtil.show(getActivity(),position,list);
            }
        });
    }

    @Override
    protected void initData() {
        mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_1, UrlUtils.action_detail_get(LoginUtil.getInfo("token"),LoginUtil.getInfo("uid"),
                pack_id,shop_id),false);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case HttpModeBase.HTTP_REQUESTCODE_1:
                try {
                    JSONObject object = new JSONObject((String) msg.obj);
                    if (object.optInt("status") == 1) {
                        if(object.has("result") && object.optJSONArray("result").length() > 0){
                            list = FastJsonTools.getPersons(object.optString("result"), ImageTextBean.class);
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
    public void onClick(View v) {

    }
}
