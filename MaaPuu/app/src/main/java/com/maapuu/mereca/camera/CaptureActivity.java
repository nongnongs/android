package com.maapuu.mereca.camera;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.maapuu.mereca.R;
import com.maapuu.mereca.activity.ChatDetailActivity;
import com.maapuu.mereca.activity.HongBaoActivity;
import com.maapuu.mereca.activity.SrvDetailActivity;
import com.maapuu.mereca.background.shop.activity.ApplyJoinShopActivity;
import com.maapuu.mereca.background.shop.activity.EntryChongzhiActivity;
import com.maapuu.mereca.background.shop.activity.EntryHuijiActivity;
import com.maapuu.mereca.background.shop.activity.EntryTaocanActivity;
import com.maapuu.mereca.background.shop.activity.EntryXiangmuActivity;
import com.maapuu.mereca.base.AppConfig;
import com.maapuu.mereca.base.BaseActivity;
import com.maapuu.mereca.constant.UrlUtils;
import com.maapuu.mereca.util.AbsolutePathUtil;
import com.maapuu.mereca.util.HttpModeBase;
import com.maapuu.mereca.util.LoginUtil;
import com.maapuu.mereca.util.StringUtils;
import com.maapuu.mereca.util.ToastUtil;
import com.maapuu.mereca.util.UIUtils;
import com.othershe.nicedialog.BaseNiceDialog;
import com.othershe.nicedialog.NiceDialog;
import com.othershe.nicedialog.ViewConvertListener;
import com.othershe.nicedialog.ViewHolder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class CaptureActivity extends BaseActivity implements Callback {

    @BindView(R.id.txt_title)
    TextView txtTitle;
    @BindView(R.id.txt_left)
    TextView txtLeft;
    @BindView(R.id.txt_right)
    TextView txtRight;
    @BindView(R.id.preview_view)
    SurfaceView surfaceView;
    @BindView(R.id.viewfinder_view)
    ViewfinderView viewfinderView;
    @BindView(R.id.ll_btn)
    LinearLayout llBtn;

    private CaptureActivityHandler handler;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;
    int ifOpenLight = 0; // 判断是否开启闪光灯
    private static final int CHOOCE_PIC = 0;

    private boolean isAddFriends = false;
    private boolean isEntry = false; //录入

    @Override
    public void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_camera);
        ButterKnife.bind(this);
        CameraManager.init(getApplication());
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
    }

    @Override
    public void initView() {
        isAddFriends = getIntent().getBooleanExtra("isAddFriends",false);
        isEntry = getIntent().getBooleanExtra("isEntry",false);
        if(isAddFriends){
            txtTitle.setText("扫一扫");
            llBtn.setVisibility(View.GONE);
            txtRight.setVisibility(View.VISIBLE);
            txtRight.setText("相册");
        } else {
            txtTitle.setText("二维码/条码");
            llBtn.setVisibility(View.VISIBLE);
            txtRight.setVisibility(View.GONE);
        }
        txtLeft.setTypeface(StringUtils.getFont(mContext));
    }

    @Override
    public void initData() {}

    @Override
    protected void onResume() {
        super.onResume();
//        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    /**
     * 直接扫描二维码
     * @param result
     * @param barcode 获取结果
     */
    public void handleDecode(Result result, Bitmap barcode) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        String resultString = result.getText();
        if (resultString.equals("")) {
            ToastUtil.show(mContext,"扫描失败!");
        } else {
            if(resultString.length() == 6){
                if(isEntry){
                    mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_3, UrlUtils.s_input_code2d_get(LoginUtil.getInfo("token"),
                            LoginUtil.getInfo("uid"),resultString),true);
                }else {
                    it = new Intent(mContext,ChatDetailActivity.class);
                    it.putExtra("friend_uid","");
                    it.putExtra("msg_id","");
                    it.putExtra("share_code",resultString);
                    startActivity(it);
                }
            }else if(resultString.length() == 8){
                mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_2, UrlUtils.s_staff_code_get(LoginUtil.getInfo("token"),
                        LoginUtil.getInfo("uid"),resultString),true);
            }else if(resultString.length() == 12){
                //获取到二维码，根据二维码获取到 消费码id
                getCode2dId(resultString);
            }else {
                ToastUtil.show(mContext,"该二维码无效");
            }
        }
    }

    private void getCode2dId(String code2d){
        mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_1,
                UrlUtils.order2d_detail_get(LoginUtil.getInfo("token"),LoginUtil.getInfo("uid"),code2d),false);
    }

    /*
     * 获取带二维码的相片进行扫描
     */
    public void pickPictureFromAblum(View v) {
//        // 打开手机中的相册
//        Intent innerIntent = new Intent(Intent.ACTION_GET_CONTENT); // "android.intent.action.GET_CONTENT"
//        innerIntent.setType("image/*");
//        Intent wrapperIntent = Intent.createChooser(innerIntent, "选择二维码图片");
//        this.startActivityForResult(wrapperIntent, 1);
        Intent innerIntent = new Intent();//"android.intent.action.GET_CONTENT"
        if (Build.VERSION.SDK_INT < 19) {
            innerIntent.setAction(Intent.ACTION_GET_CONTENT);
        } else {
            // innerIntent.setAction(Intent.ACTION_OPEN_DOCUMENT); 这个方法报 图片地址 空指针；使用下面的方法
            innerIntent.setAction(Intent.ACTION_PICK);
        }
        innerIntent.setType("image/*");
        Intent wrapperIntent = Intent.createChooser(innerIntent, "选择二维码图片");
        startActivityForResult(wrapperIntent, CHOOCE_PIC);
    }

    String photo_path;
    ProgressDialog mProgress;
    Bitmap scanBitmap;

    /*
     * 对相册获取的结果进行分析
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CHOOCE_PIC:
                    try {
                        Uri uri = data.getData();
                        if (!TextUtils.isEmpty(uri.getAuthority())) {
                            Cursor cursor = getContentResolver().query(uri, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
                            if (null == cursor) {
                                ToastUtil.show(mContext,"图片没找到!");
                                return;
                            }
                            cursor.moveToFirst();
                            photo_path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                            if (photo_path == null)
                                photo_path = AbsolutePathUtil.getAbsolutePath((Activity) mContext, uri);
                            cursor.close();
                        } else {
                            photo_path = data.getData().getPath();
                            if (photo_path == null)
                                photo_path = AbsolutePathUtil.getAbsolutePath((Activity) mContext, uri);
                        }
                        mProgress = new ProgressDialog(CaptureActivity.this);
                        mProgress.setMessage("正在扫描...");
                        mProgress.setCancelable(false);
                        mProgress.show();

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Result result = scanningImage(photo_path);
                                if (result != null) {
                                    Message m = mHandler.obtainMessage();
                                    m.what = 1;
                                    m.obj = result.getText();
                                    mHandler.sendMessage(m);
                                } else {
                                    Message m = mHandler.obtainMessage();
                                    m.what = 2;
                                    m.obj = "扫描失败!";
                                    mHandler.sendMessage(m);
                                }
                            }
                        }).start();
                    } catch (Exception e) {
                        ToastUtil.show(mContext,"解析错误!");
                    }
                    break;
                default:
                    break;
            }
        }else if(resultCode == AppConfig.ACTIVITY_RESULTCODE){
            finish();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void entry(final String name,final String image,final String phone,final String custom_uid){
        NiceDialog.init().setLayoutId(R.layout.pop_entry_info)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    public void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        UIUtils.loadImg(mContext,image, (ImageView) holder.getView(R.id.iv_image),true);
                        holder.setText(R.id.txt_name,name);
                        holder.setText(R.id.txt_phone,phone);
                        holder.setOnClickListener(R.id.iv_close, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.txt_1, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                it = new Intent(mContext,EntryHuijiActivity.class);
                                it.putExtra("custom_uid",custom_uid);
                                startActivityForResult(it,AppConfig.ACTIVITY_REQUESTCODE);
                            }
                        });
                        holder.setOnClickListener(R.id.txt_2, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                it = new Intent(mContext,EntryXiangmuActivity.class);
                                it.putExtra("custom_uid",custom_uid);
                                startActivityForResult(it,AppConfig.ACTIVITY_REQUESTCODE);
                            }
                        });
                        holder.setOnClickListener(R.id.txt_3, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                it = new Intent(mContext,EntryChongzhiActivity.class);
                                it.putExtra("custom_uid",custom_uid);
                                startActivityForResult(it,AppConfig.ACTIVITY_REQUESTCODE);
                            }
                        });
                        holder.setOnClickListener(R.id.txt_4, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                it = new Intent(mContext,EntryTaocanActivity.class);
                                it.putExtra("custom_uid",custom_uid);
                                startActivityForResult(it,AppConfig.ACTIVITY_REQUESTCODE);
                            }
                        });
                    }
                }).setHeight(450).setWidth(270)
                .setOutCancel(false).setShowBottom(false)
                .show(getSupportFragmentManager());
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case 1:
                mProgress.dismiss();
                String resultString = msg.obj.toString();
                if (resultString.equals("")) {
                    ToastUtil.show(mContext,"扫描失败!");
                } else {
                    if(resultString.length() == 6){
                        if(isEntry){
                            mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_3, UrlUtils.s_input_code2d_get(LoginUtil.getInfo("token"),
                                    LoginUtil.getInfo("uid"),resultString),true);
                        }else {
                            it = new Intent(mContext,ChatDetailActivity.class);
                            it.putExtra("friend_uid","");
                            it.putExtra("msg_id","");
                            it.putExtra("share_code",resultString);
                            startActivity(it);
                        }
                    }else if(resultString.length() == 8){
                        mHttpModeBase.xPost(HttpModeBase.HTTP_REQUESTCODE_2, UrlUtils.s_staff_code_get(LoginUtil.getInfo("token"),
                                LoginUtil.getInfo("uid"),resultString),true);
                    }else if(resultString.length() == 12){
                        //it = new Intent(mContext,SrvDetailActivity.class);
                        //it.putExtra("code2d",resultString);
                        //it.putExtra("isScan",true);
                        //startActivity(it);

                        //获取到二维码，根据二维码获取到 消费码id
                        getCode2dId(resultString);
                    }else {
                        ToastUtil.show(mContext,"该二维码无效");
                    }
                }
                break;
            case 2:
                mProgress.dismiss();
                ToastUtil.show(mContext,"解析错误!");
                break;

            case HttpModeBase.HTTP_REQUESTCODE_1:
                try {
                    JSONObject object = new JSONObject((String) msg.obj);
                    if (object.optInt("status") == 1) {
                        JSONObject resultObj = object.optJSONObject("result");
                        if(resultObj.has("order_detail") && !StringUtils.isEmpty(resultObj.optString("order_detail"))){
                            final JSONObject detailObj = resultObj.optJSONObject("order_detail");
                            if("1".equals(detailObj.optString("card_type")) || "2".equals(detailObj.optString("card_type"))){
                                NiceDialog.init().setLayoutId(R.layout.pop_service_invite)
                                        .setConvertListener(new ViewConvertListener() {
                                            @Override
                                            public void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                                                SimpleDraweeView image = holder.getView(R.id.iv_image);
                                                image.setImageURI(Uri.parse(detailObj.optString("avatar")));
                                                holder.setText(R.id.txt_name,detailObj.optString("nick_name"));
                                                holder.setOnClickListener(R.id.txt_refuse, new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        dialog.dismiss();
                                                        finish();
                                                    }
                                                });
                                                holder.setOnClickListener(R.id.txt_accept, new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        dialog.dismiss();
                                                        it = new Intent(mContext,SrvDetailActivity.class);
                                                        it.putExtra("code2d_id",detailObj.optString("code2d_id"));
                                                        startActivity(it);
                                                        finish();
                                                    }
                                                });
                                            }
                                        }).setHeight(450).setWidth(270)
                                        .setOutCancel(false).setShowBottom(false)
                                        .show(getSupportFragmentManager());
                            }else {
                                it = new Intent(mContext,SrvDetailActivity.class);
                                it.putExtra("code2d_id",detailObj.optString("code2d_id"));
                                startActivity(it);
                                finish();
                            }
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
                        it = new Intent(mContext,ApplyJoinShopActivity.class);
                        it.putExtra("result",object.optString("result"));
                        startActivityForResult(it, AppConfig.ACTIVITY_REQUESTCODE);
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
                        String name = object.optJSONObject("result").optJSONObject("custom_data").optString("nick_name");
                        String image = object.optJSONObject("result").optJSONObject("custom_data").optString("avatar");
                        String phone = object.optJSONObject("result").optJSONObject("custom_data").optString("phone");
                        String custom_uid = object.optJSONObject("result").optJSONObject("custom_data").optString("uid");
                        entry(name,image,phone,custom_uid);
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

            default:
                break;
        }
        return false;
    }

    /**
     * 扫描二维码图片的方法，目前识别度不高，有待改进
     * @param path
     * @return
     */
    public Result scanningImage(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
        hints.put(DecodeHintType.CHARACTER_SET, "UTF8"); // 设置二维码内容的编码

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 先获取原大小
        scanBitmap = BitmapFactory.decodeFile(path, options);
        options.inJustDecodeBounds = false; // 获取新的大小
        int sampleSize = (int) (options.outHeight / (float) 400);
        if (sampleSize <= 0)
            sampleSize = 1;
        options.inSampleSize = sampleSize;
        scanBitmap = BitmapFactory.decodeFile(path, options);
        RGBLuminanceSource source = new RGBLuminanceSource(scanBitmap);
        BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
        //仅能识别二维码
        //QRCodeReader reader = new QRCodeReader();
        //try {
        //return reader.decode(bitmap1, hints);

        //} catch (NotFoundException e) {
        //e.printStackTrace();
        //} catch (ChecksumException e) {
        //e.printStackTrace();
        //} catch (FormatException e) {
        //e.printStackTrace();
        //}
        MultiFormatReader multiFormatReader = new MultiFormatReader();
        try {
            return multiFormatReader.decode(bitmap1, hints);

        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 是否开启闪光灯
    public void IfOpenLight(View v) {
        ifOpenLight++;
        switch (ifOpenLight % 2) {
            case 0:// 关闭
                CameraManager.get().closeLight();
                break;
            case 1:// 打开
                CameraManager.get().openLight();
                break;
            default:
                break;
        }
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    @Override
    @OnClick({R.id.txt_left,R.id.txt_right, R.id.txt_btn_1,R.id.txt_btn_2})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_left:
                finish();
                break;
            case R.id.txt_right:
            case R.id.txt_btn_1:
                pickPictureFromAblum(view);
                break;
            case R.id.txt_btn_2:
                NiceDialog.init().setLayoutId(R.layout.pop_input_qrcode)
                        .setConvertListener(new ViewConvertListener() {
                            @Override
                            public void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                                final EditText etInput = holder.getView(R.id.et_input);
                                TextView txtCommit = holder.getView(R.id.txt_commit);
                                txtCommit.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(StringUtils.isEmpty(etInput.getText().toString())){
                                            ToastUtil.show(mContext,"请输入条码编号");
                                            return;
                                        }
                                        if(etInput.getText().toString().length() != 12){
                                            ToastUtil.show(mContext,"条码编号不对");
                                            return;
                                        }
                                        dialog.dismiss();
                                        getCode2dId(etInput.getText().toString());
//                                        it = new Intent(mContext,SrvDetailActivity.class);
//                                        it.putExtra("code2d",etInput.getText().toString());
//                                        it.putExtra("isScan",true);
//                                        startActivity(it);
                                    }
                                });
                            }
                        })
                        .setOutCancel(true).setShowBottom(false).setHeight(220).setWidth(350)
                        .show(getSupportFragmentManager());
                break;
        }
    }

}