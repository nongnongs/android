package com.maapuu.mereca.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.maapuu.mereca.R;
import com.maapuu.mereca.adapter.help.BaseAdapterHelper;
import com.maapuu.mereca.adapter.help.QuickAdapter;
import com.maapuu.mereca.base.AppConfig;
import com.maapuu.mereca.base.BaseActivity;
import com.maapuu.mereca.bean.SearchAddress;
import com.maapuu.mereca.util.DisplayUtil;
import com.maapuu.mereca.util.StringUtils;
import com.maapuu.mereca.util.ToastUtil;
import com.maapuu.mereca.view.SearchEditText;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by dell on 2017/8/7.
 */

public class LocateActivity extends BaseActivity implements PoiSearch.OnPoiSearchListener,LocationSource,Animation.AnimationListener,
        AMapLocationListener, AMap.OnCameraChangeListener, GeocodeSearch.OnGeocodeSearchListener {

    @BindView(R.id.et_search)
    SearchEditText etSearch;
    @BindView(R.id.rl_map)
    RelativeLayout rlMap;
    @BindView(R.id.map)
    MapView mapView;
    @BindView(R.id.list_view)
    ListView listView;
    @BindView(R.id.centerMarkerImg)
    ImageView centerImageView;

    private List<SearchAddress> list;
    private QuickAdapter<SearchAddress> adapter;
    private AMap aMap;
    private String keyWord = "";// 要输入的poi搜索关键字
    private String city = "";
    private PoiSearch.Query query;// Poi查询条件类
    private PoiSearch poiSearch;// POI搜索
    private double lat;
    private double lng;
    private String ctgr = "01|02|03|04|05|06|07|08|09|10|11|12|13|14|15|16|17|18|19|20|22|97|99";

    private boolean isSearch = false;

    private LocationSource.OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private Animation centerMarker;

    @Override
    public void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_locate);
        ButterKnife.bind(this);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
    }

    @Override
    public void initView() {
        LinearLayout.LayoutParams linearParams =(LinearLayout.LayoutParams) rlMap.getLayoutParams();
        linearParams.height=((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                DisplayUtil.getWidthDP(mContext)*4/5, getResources().getDisplayMetrics()));
        rlMap.setLayoutParams(linearParams);
        list = new ArrayList<>();
        init();
    }

    /**
     * 初始化AMap对象
     */
    private void init() {
        centerMarker = AnimationUtils.loadAnimation(this, R.anim.anim);
        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }
    }

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        aMap.setLocationSource(this);// 设置定位监听
        centerMarker.setAnimationListener(this);
        centerImageView.startAnimation(centerMarker);
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.setOnCameraChangeListener(this);
        aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //该方法默认为false。
            mLocationOption.setOnceLocation(true);
            //设置定位间隔,单位毫秒,默认为2000ms
            //mLocationOption.setInterval(2000);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            //开始定位
            mlocationClient.startLocation();
        }
    }

    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    @Override
    public void initData() {
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH){
                    StringUtils.closeKeyBorder(mContext,v);
                    isSearch = true;
                    if(StringUtils.isEmpty(etSearch.getText().toString())){
                        doNearSearchQuery(lat,lng);
                    }else {
                        keyWord = etSearch.getText().toString();
                        doSearchQuery();
                    }
                }
                return false;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                it = new Intent();
                it.putExtra("searchAddress",list.get(i));
                setResult(AppConfig.ACTIVITY_RESULTCODE,it);
                finish();
            }
        });
    }

    /**
     * 开始进行关键字搜索
     */
    protected void doSearchQuery() {
        query = new PoiSearch.Query(keyWord, ctgr, city);
        query.setPageSize(30);
        query.setPageNum(1);
        query.setCityLimit(false);
        poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();
    }

    /**
     * 附近搜索
     */
    protected void doNearSearchQuery(double latitude, double longitude) {
        query = new PoiSearch.Query("", ctgr, city);
        query.setPageSize(30);
        query.setPageNum(1);
        query.setCityLimit(true);
        poiSearch = new PoiSearch(this, query);
        poiSearch.setBound(new PoiSearch.SearchBound(new LatLonPoint(latitude, longitude), 1000));//设置周边搜索的中心点以及区域
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();
    }

    @Override
    public void onPoiSearched(PoiResult result, int rCode) {
        if (rCode == 1000) {
            if (result != null && result.getQuery() != null) {// 搜索poi的结果
                if (result.getQuery().equals(query)) {// 是否是同一条
                    List<PoiItem> poiItems = result.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                    list.clear();
                    if(poiItems.size() > 0 && isSearch){
                        LatLng lonPoint = new LatLng(poiItems.get(0).getLatLonPoint().getLatitude(), poiItems.get(0).getLatLonPoint().getLongitude());
                        centerImageView.startAnimation(centerMarker);
                        aMap.moveCamera(CameraUpdateFactory.changeLatLng(lonPoint));
                        aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
                    }
                    for (int i = 0; i < poiItems.size(); i++) {
                        SearchAddress address = new SearchAddress();
                        address.setName(poiItems.get(i).getTitle());
                        address.setAddress(poiItems.get(i).getSnippet());
                        address.setLatitude(poiItems.get(i).getLatLonPoint().getLatitude() + "");
                        address.setLongitude(poiItems.get(i).getLatLonPoint().getLongitude() + "");
                        address.setCity(poiItems.get(i).getCityName() + "");
                        address.setProvince(poiItems.get(i).getProvinceName() + "");
                        address.setArea(poiItems.get(i).getAdName() + "");
                        list.add(address);
                    }
                    setAdapter();
                }
            } else {
                ToastUtil.show(mContext, "暂无数据");
            }
        } else {
            ToastUtil.show(mContext, "" + rCode);
        }
    }

    private void setAdapter() {
        adapter = new QuickAdapter<SearchAddress>(mContext,R.layout.layout_address_search_list_item,list) {
            @Override
            protected void convert(BaseAdapterHelper helper, SearchAddress item) {
                helper.setText(R.id.txt_txt1,item.getName());
                helper.setText(R.id.txt_txt2,item.getAddress());
            }
        };
        listView.setAdapter(adapter);
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    @Override
    public void onAnimationStart(Animation animation) {
        centerImageView.setImageResource(R.mipmap.locate);
    }

    @Override
    public void onAnimationEnd(Animation animation) {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {
        centerImageView.setImageResource(R.mipmap.locate);
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mListener != null && aMapLocation != null) {
            if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(aMapLocation);// 显示系统小蓝点
                city = aMapLocation.getCity();
                lat = aMapLocation.getLatitude();
                lng = aMapLocation.getLongitude();
            } else {
                String errText = "定位失败," + aMapLocation.getErrorCode() + ": " + aMapLocation.getErrorInfo();
                Log.e("AmapErr", errText);
            }
        }
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        if (mListener != null && !isSearch){
            aMap.clear();
            centerImageView.startAnimation(centerMarker);
            LatLonPoint lonPoint = new LatLonPoint(cameraPosition.target.latitude, cameraPosition.target.longitude);
            changeLocationToAddress(lonPoint);
        }
        isSearch = false;
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int pos) {
        list.clear();
        for (int i = 0; i < result.getRegeocodeAddress().getPois().size(); i++) {
            SearchAddress address = new SearchAddress();
            address.setName(result.getRegeocodeAddress().getPois().get(i).getTitle());
            address.setAddress(result.getRegeocodeAddress().getPois().get(i).getSnippet());
            address.setLatitude(result.getRegeocodeAddress().getPois().get(i).getLatLonPoint().getLatitude() + "");
            address.setLongitude(result.getRegeocodeAddress().getPois().get(i).getLatLonPoint().getLongitude() + "");
            address.setCity(result.getRegeocodeAddress().getCity());
            address.setProvince(result.getRegeocodeAddress().getProvince());
            address.setArea(result.getRegeocodeAddress().getDistrict());
            list.add(address);
        }
        setAdapter();
    }

    @Override
    public void onGeocodeSearched(GeocodeResult result, int i) {
        LatLng lonPoint = new LatLng(result.getGeocodeAddressList().get(0).getLatLonPoint().getLatitude(),
                result.getGeocodeAddressList().get(0).getLatLonPoint().getLongitude());
        centerImageView.startAnimation(centerMarker);
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(lonPoint));
        aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
    }

    /**
     * 将经纬度转换成地址
     *
     * @param latlng
     */
    private void changeLocationToAddress(LatLonPoint latlng) {
        GeocodeSearch geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);
        RegeocodeQuery query = new RegeocodeQuery(latlng, 200, GeocodeSearch.AMAP);
        geocoderSearch.getFromLocationAsyn(query);
    }

    /**
     * 将经纬度转换成地址
     */
    private void addressToLocation(String address) {
        GeocodeSearch geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);
        //通过GeocodeQuery设置查询参数,调用getFromLocationNameAsyn(GeocodeQuery geocodeQuery) 方法发起请求。
        //address表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode都ok
        GeocodeQuery query = new GeocodeQuery(address, "86");
        geocoderSearch.getFromLocationNameAsyn(query);
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

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        deactivate();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
}
