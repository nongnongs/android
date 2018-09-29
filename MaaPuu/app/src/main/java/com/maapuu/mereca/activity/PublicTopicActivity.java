package com.maapuu.mereca.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.text.InputFilter;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.maapuu.mereca.R;
import com.maapuu.mereca.base.AppConfig;
import com.maapuu.mereca.base.BaseActivity;
import com.maapuu.mereca.constant.UrlUtils;
import com.maapuu.mereca.util.DisplayUtil;
import com.maapuu.mereca.util.EmojiUtil;
import com.maapuu.mereca.util.HttpModeBase;
import com.maapuu.mereca.util.LoginUtil;
import com.maapuu.mereca.util.StringUtils;
import com.maapuu.mereca.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import info.wangchen.simplehud.SimpleHUD;

/**
 * Created by dell on 2018/3/1.
 */

public class PublicTopicActivity extends BaseActivity implements View.OnTouchListener{
    @BindView(R.id.txt_left)
    TextView txtLeft;
    @BindView(R.id.txt_title)
    TextView txtTitle;

    @BindView(R.id.scroll_view)
    ScrollView scrollView;
    @BindView(R.id.et_topic_title)
    EditText etTopicTitle;
    @BindView(R.id.ll_content)
    LinearLayout llContent;
    @BindView(R.id.rl)
    RelativeLayout rl;
    @BindView(R.id.et_topic_content)
    EditText etTopicContent;
    @BindView(R.id.iv_delete)
    ImageView ivDelete;
    @BindView(R.id.txt_add_text)
    TextView txtAddText;
    @BindView(R.id.txt_add_image)
    TextView txtAddImage;
    @BindView(R.id.txt_publish)
    TextView txtPublish;

    private int viewPos = 0;
    private Map<Integer,Object> views = new LinkedHashMap<>();
    private Map<Integer, String> map = new LinkedHashMap<>();
    private List<LocalMedia> selectList = new ArrayList<>();
    private String path; //图片上传服务器后返回的地址
    private Bitmap bm;
    private StringBuffer buffer = new StringBuffer();//文字、图片拼接json字符串
    private int imgListPos = 0;//上传的图片下标

    private AlertView alertView;

    @Override
    public void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_public_topic);
    }

    @Override
    public void initView() {
        txtLeft.setTypeface(StringUtils.getFont(mContext));
        txtTitle.setText("发布圈子话题");
        views.put(viewPos,etTopicContent);
        map.put(viewPos,etTopicContent.getText().toString());
    }

    @Override
    public void initData() {

    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case HttpModeBase.HTTP_REQUESTCODE_1:
                SimpleHUD.dismiss();
                try {
                    JSONObject object = new JSONObject((String) msg.obj);
                    if (object.optInt("status") == 1) {
                        ToastUtil.show(mContext,"提交成功，等待审核");
                        setResult(AppConfig.ACTIVITY_RESULTCODE);
                        finish();
                    } else {
                        ToastUtil.show(mContext,object.optString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case HttpModeBase.HTTP_REQUESTCODE_IMG:
                if (msg.arg1 == 1) {
                    try {
                        JSONObject object = new JSONObject((String) msg.obj);
                        if (object.has("src") && !object.optString("src").equals("")) {
                            JSONObject resultObj = object.optJSONObject("result");
                            String str = "{\"type\":\"2\",\"content\":\""+resultObj.optString("src")+"\",\"width\":\"" +
                                    resultObj.optString("width") + "\",\"height\":\"" + resultObj.optString("height") + "\"},";
                            map.put(imgListPos,str);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if(msg.arg2 == getLastPos()){
                    publish();
                }else {
                    imgListPos++;
                    if(views.get(imgListPos) instanceof EditText){
                        imgListPos ++;
                    }
                    image_save_set(map.get(imgListPos), imgListPos, false);
                }
                break;
            case HttpModeBase.HTTP_ERROR:
                SimpleHUD.dismiss();
                String result_e = (String) msg.obj;
                ToastUtil.show(mContext,result_e);
                break;
        }
        return false;
    }

    @Override
    @OnClick({R.id.txt_left,R.id.iv_delete,R.id.txt_add_text,R.id.txt_add_image,R.id.txt_publish})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.txt_left:
                finish();
                break;
            case R.id.iv_delete:
                new AlertView(null, "确定要删除吗？", "取消", null, new String[]{"确定"}, mContext,
                        AlertView.Style.Alert, new OnItemClickListener() {
                    @Override
                    public void onItemClick(Object o, int position) {
                        if (position == 0) {
                            views.remove(0);
                            map.remove(0);
                            rl.setVisibility(View.GONE);
                            etTopicContent.setVisibility(View.GONE);
                            ivDelete.setVisibility(View.GONE);
                        }
                    }
                }).show();
                break;
            case R.id.txt_add_text:
                addText();
                break;
            case R.id.txt_add_image:
                PictureSelector.create(PublicTopicActivity.this)
                        .openGallery(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                        .maxSelectNum(1)// 最大图片选择数量
                        .minSelectNum(1)// 最小选择数量
                        .imageSpanCount(3)// 每行显示个数
                        .selectionMode(PictureConfig.SINGLE)// 多选 or 单选
                        .previewImage(true)// 是否可预览图片
                        .freeStyleCropEnabled(true)// 裁剪框是否可拖拽 true or false
                        .isCamera(true)// 是否显示拍照按钮
                        .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                        .circleDimmedLayer(false) // 是否圆形裁剪 true or false
                        .showCropFrame(true)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
                        .showCropGrid(true)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
                        .enableCrop(true)// 是否裁剪
                        .compress(true)// 是否压缩
                        .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
                break;
            case R.id.txt_publish:
                if(StringUtils.isEmpty(etTopicTitle.getText().toString())){
                    ToastUtil.show(mContext,"请输入标题");
                    return;
                }
                if (isNeedUploadImage()) {
                    imgListPos = getFirstPos();
                    image_save_set(map.get(imgListPos), imgListPos, true);
                } else {
                    publish();
                }
                break;
        }
    }

    private void publish() {
        buffer.delete(0,buffer.length());//先清除原来的数据
        buffer.append("[");
        Iterator it = views.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry entry = (Map.Entry) it.next();
            Object key = entry.getKey();
            if(views.get(key) instanceof EditText){
                if(!StringUtils.isEmpty(((EditText) views.get(key)).getText().toString())){
                    buffer.append("{\"type\":\"1\",\"content\":\""+ EmojiUtil.stringToUtf8(((EditText) views.get(key)).getText().toString().trim()).replace("+","%20")+"\"},");
                }
            }else {
                buffer.append(map.get(key));
            }
        }
        if(buffer.toString().endsWith(",")){
            buffer.deleteCharAt(buffer.length()-1);
        }
        buffer.append("]");
        Log.i("abc",buffer.toString());
        if (buffer.toString().isEmpty() || buffer.toString().length() <= 2) {
            ToastUtil.show(mContext, "请输入内容");
            return;
        }
        mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_1, UrlUtils.circle_publish_set(LoginUtil.getInfo("token"),LoginUtil.getInfo("uid"),
                EmojiUtil.stringToUtf8(etTopicTitle.getText().toString()).replace("+","%20"),buffer.toString()),true);
    }

    private void addText(){
        final RelativeLayout rl = new RelativeLayout(mContext);
        RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, DisplayUtil.dip2px(mContext,150));
        lp1.topMargin = DisplayUtil.dip2px(mContext,10);
        rl.setLayoutParams(lp1);
        rl.setBackgroundResource(R.color.white);
        rl.setPadding(DisplayUtil.dip2px(mContext,10),DisplayUtil.dip2px(mContext,10),DisplayUtil.dip2px(mContext,10),DisplayUtil.dip2px(mContext,10));

        viewPos++;
        final EditText editText = new EditText(mContext);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        editText.setLayoutParams(lp);
        editText.setHint("请输入内容");
        editText.setHintTextColor(getResources().getColor(R.color.text_99));
        editText.setTextColor(getResources().getColor(R.color.text_33));
        editText.setGravity(Gravity.TOP);
        editText.setTextSize(13);
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(800)}); //最大输入长度
        editText.setTag(viewPos);
        editText.setBackgroundResource(R.color.white);
        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(canVerticalScroll(editText)){
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                    }
                }
                return false;
            }
        });
        rl.addView(editText);
        views.put(viewPos,editText);
        map.put(viewPos,editText.getText().toString());

        final ImageView imageView = new ImageView(mContext);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        imageView.setLayoutParams(params);
        imageView.setImageResource(R.mipmap.shanchuanniu);
        rl.addView(imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertView = new AlertView(null, "确定要删除吗？", "取消", null, new String[]{"确定"}, mContext,
                        AlertView.Style.Alert, new OnItemClickListener() {
                    @Override
                    public void onItemClick(Object o, int position) {
                        if (position == 0) {
                            views.remove(editText.getTag());
                            map.remove(editText.getTag());
                            rl.setVisibility(View.GONE);
                            editText.setVisibility(View.GONE);
                            imageView.setVisibility(View.GONE);
                        }
                    }
                });
                alertView.show();
            }
        });

        llContent.addView(rl);
        llContent.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    private void addImage(Bitmap bm) {
        final RelativeLayout rl = new RelativeLayout(mContext);
        RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, DisplayUtil.dip2px(mContext,150));
        lp1.topMargin = DisplayUtil.dip2px(mContext,10);
        rl.setLayoutParams(lp1);
        rl.setBackgroundResource(R.color.white);
        rl.setPadding(DisplayUtil.dip2px(mContext,10),DisplayUtil.dip2px(mContext,10),DisplayUtil.dip2px(mContext,10),DisplayUtil.dip2px(mContext,10));

        viewPos++;
        final ImageView imageView = new ImageView(mContext);
        RelativeLayout.LayoutParams lp3 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(lp3);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageBitmap(bm);
        imageView.setTag(viewPos);
        rl.addView(imageView);

        final ImageView ivDel = new ImageView(mContext);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        ivDel.setLayoutParams(params);
        ivDel.setImageResource(R.mipmap.shanchuanniu);
        rl.addView(ivDel);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertView = new AlertView(null, "确定要删除吗？", "取消", null, new String[]{"确定"}, mContext,
                        AlertView.Style.Alert, new OnItemClickListener() {
                    @Override
                    public void onItemClick(Object o, int position) {
                        if (position == 0) {
                            views.remove(imageView.getTag());
                            map.remove(imageView.getTag());
                            rl.setVisibility(View.GONE);
                            ivDel.setVisibility(View.GONE);
                            imageView.setVisibility(View.GONE);
                        }
                    }
                });
                alertView.show();
            }
        });

        llContent.addView(rl);
        llContent.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });

        views.put(viewPos,imageView);
        map.put(viewPos,path);
    }

    private boolean isNeedUploadImage(){
        boolean bool = false;
        Iterator it = views.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            Object key = entry.getKey();
            if(views.get(key) instanceof ImageView){
                bool = true;
                break;
            }
        }
        return bool;
    }

    private int getFirstPos(){
        int pos = 0;
        Iterator it = views.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            Object key = entry.getKey();
            if(views.get(key) instanceof ImageView){
                pos = (int) entry.getKey();
                break;
            }
        }
        return pos;
    }

    private int getLastPos(){
        int pos = 0;
        Iterator it = views.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            Object key = entry.getKey();
            if(views.get(key) instanceof ImageView){
                pos = (int) entry.getKey();
            }
        }
        return pos;
    }

    private void image_save_set(String imagePath, int position, Boolean isProgress) {
        mHttpModeBase.xutilsPostList(HttpModeBase.HTTP_REQUESTCODE_IMG, imagePath, position, isProgress);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PictureConfig.CHOOSE_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    selectList.clear();
                    selectList.addAll(PictureSelector.obtainMultipleResult(data));
                    if(selectList != null && selectList.size() > 0){
                        path = selectList.get(0).getCompressPath();
                        BitmapFactory.Options option = new BitmapFactory.Options();
                        // 压缩图片:表示缩略图大小为原始图片大小的几分之一，1为原图
                        option.inSampleSize = 3;
                        // 得到图片的旋转角度
                        bm = BitmapFactory.decodeFile(selectList.get(0).getCompressPath(), option);
                        // 显示在图片控件上

                        addImage(bm);
//                        image_save_set(selectList.get(0).getCompressPath(),true);
                    }else {
                        ToastUtil.show(mContext,"选择图片出错");
                    }
                }
                break;
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        //触摸的是EditText并且当前EditText可以滚动则将事件交给EditText处理；否则将事件交由其父类处理
        if ((view.getId() == R.id.et_topic_content && canVerticalScroll(etTopicContent))) {
            view.getParent().requestDisallowInterceptTouchEvent(true);
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                view.getParent().requestDisallowInterceptTouchEvent(false);
            }
        }
        return false;
    }

    /**
     * EditText竖直方向是否可以滚动
     * @param editText 需要判断的EditText
     * @return true：可以滚动  false：不可以滚动
     */
    private boolean canVerticalScroll(EditText editText) {
        //滚动的距离
        int scrollY = editText.getScrollY();
        //控件内容的总高度
        int scrollRange = editText.getLayout().getHeight();
        //控件实际显示的高度
        int scrollExtent = editText.getHeight() - editText.getCompoundPaddingTop() -editText.getCompoundPaddingBottom();
        //控件内容总高度与实际显示高度的差值
        int scrollDifference = scrollRange - scrollExtent;

        if(scrollDifference == 0) {
            return false;
        }

        return (scrollY > 0) || (scrollY < scrollDifference - 1);
    }

    @Override
    public void onBackPressed() {
        if(alertView != null && alertView.isShowing()){
            alertView.dismiss();
        }else {
            super.onBackPressed();
        }
    }
}
