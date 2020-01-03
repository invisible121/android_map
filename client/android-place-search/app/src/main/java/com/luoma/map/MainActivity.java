package com.luoma.map;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.SupportMapFragment;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.enums.NaviType;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;

import android.view.View.OnClickListener;
import android.widget.Toast;

import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.amap.map.R;
import com.luoma.map.adapter.InfoWinAdapter;
import com.luoma.map.overlay.PoiOverlay;
import com.luoma.map.overlay.WalkRouteOverlay;
import com.luoma.map.util.Constants;
import com.luoma.map.util.ToastUtil;

import java.util.List;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

public class MainActivity extends FragmentActivity implements
        OnMarkerClickListener, InfoWindowAdapter,
        OnPoiSearchListener, OnClickListener, LocationSource, AMapLocationListener,
        RouteSearch.OnRouteSearchListener, AMap.OnMapClickListener {
    private AMap mAMap;
    private String mKeyWords = "";// 要输入的poi搜索关键字
    private ProgressDialog progDialog = null;// 搜索时进度条

    private PoiResult poiResult; // poi返回的结果
    private int currentPage = 1;
    private PoiSearch.Query query;// Poi查询条件类
    private PoiSearch poiSearch;// POI搜索
    private TextView mKeywordsTextView;
    private Marker mPoiMarker;
    private ImageView mCleanKeyWords;

    public static final int REQUEST_CODE = 100;
    public static final int RESULT_CODE_INPUTTIPS = 101;
    public static final int RESULT_CODE_KEYWORDS = 102;


    private LocationSource.OnLocationChangedListener mListener;
    // 定位
    private AMapLocationClient mlocationClient;

    private AMapLocationClientOption mLocationOption;

    private boolean followMove = true;

    private LatLng latLng;

    private AMapNavi mAMapNavi;


    private RouteSearch mRouteSearch;
    private WalkRouteResult mWalkRouteResult;
    private WalkRouteOverlay walkRouteOverlay;


    private InfoWinAdapter adapter;
    int group;

    String token;

    String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle bundle = this.getIntent().getExtras();
        token = bundle.getString("token");

        userName = bundle.getString("user");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);

            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        mCleanKeyWords = (ImageView) findViewById(R.id.clean_keywords);
        mCleanKeyWords.setOnClickListener(this);

        mKeyWords = "";

        mAMapNavi = AMapNavi.getInstance(getApplicationContext());


        mRouteSearch = new RouteSearch(this);
        mRouteSearch.setRouteSearchListener(this);

        init();
//添加监听回调，用于处理算路成功
//        mAMapNavi.addAMapNaviListener(this);
    }

    /**
     * 初始化AMap对象
     */
    private void init() {
        if (mAMap == null) {
            mAMap = ((SupportMapFragment) this.getSupportFragmentManager()
                    .findFragmentById(R.id.map)).getMap();

            setUpMap();
        }
        mKeywordsTextView = (TextView) findViewById(R.id.main_keywords);
        mKeywordsTextView.setOnClickListener(this);
    }

    /**
     * 设置页面监听
     */
    private void setUpMap() {
        mAMap.setOnMarkerClickListener(this);// 添加点击marker监听事件

        mAMap.getUiSettings().setRotateGesturesEnabled(false);


        mAMap.setLocationSource(this);// 设置定位监听
        //aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        mAMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种

        mAMap.getUiSettings().setZoomControlsEnabled(false);

        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);

        mAMap.setMyLocationStyle(myLocationStyle);
//        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);


        mAMap.setOnMyLocationChangeListener(new AMap.OnMyLocationChangeListener() {

            @Override
            public void onMyLocationChange(Location location) {

                double latitude = location.getLatitude();

                double longitude = location.getLongitude();

                latLng = new LatLng(latitude, longitude);
                adapter.setLatLng(latLng);
                mAMap.setInfoWindowAdapter(adapter);// 添加显示infowindow监听事件

                if (followMove) {

                    mAMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                }
            }
        });


        mAMap.setOnMapTouchListener(new AMap.OnMapTouchListener() {

            @Override

            public void onTouch(MotionEvent motionEvent) {

                //用户拖动地图后，不再跟随移动，直到用户点击定位按钮

                followMove = false;

            }

        });

        mAMap.setOnMapClickListener(this);

        adapter = new InfoWinAdapter(latLng, mRouteSearch);

        mAMap.setInfoWindowAdapter(adapter);// 添加显示infowindow监听事件

    }

    @Override
    public void onMapClick(LatLng latLng) {

        if (getBuildingByXY(latLng).equals("sx")) {
            mAMap.clear();
            final Marker marker = mAMap.addMarker(new MarkerOptions().position(latLng).title("思源西楼自习室").snippet("sx201\nsx303\nsx502"));
            marker.showInfoWindow();
        }

        if (getBuildingByXY(latLng).equals("sy")) {
            mAMap.clear();
            final Marker marker = mAMap.addMarker(new MarkerOptions().position(latLng).title("思源楼自习室").snippet("sy301\nsy302\nsy506"));
            marker.showInfoWindow();
        }

        if (getBuildingByXY(latLng).equals("yf")) {
            mAMap.clear();
            final Marker marker = mAMap.addMarker(new MarkerOptions().position(latLng).title("逸夫楼自习室").snippet("yf312\nyf411\nyf508"));
            marker.showInfoWindow();

        }

//        Toast.makeText(this, latLng.latitude + " " + latLng.longitude, Toast.LENGTH_SHORT).show();
    }

    public String getBuildingByXY(LatLng latLng) {
        String place = "";
        if (39.95139366517645 > latLng.latitude &&
                latLng.latitude > 39.9510635179935 &&
                116.34025747231811 > latLng.longitude &&
                latLng.longitude > 116.3394528096274) {
            return "sx";
        }
        if (39.951780220876834 > latLng.latitude &&
                latLng.latitude > 39.95125281854049 &&
                116.3412364785918 > latLng.longitude &&
                latLng.longitude > 116.34035000852755) {
            return "sy";
        }
        if (39.95190975767071 > latLng.latitude &&
                latLng.latitude > 39.951606013819605 &&
                116.34578818721221 > latLng.longitude &&
                latLng.longitude > 116.34443098947389) {
            return "yf";
        }
        if (39.952353881958715 > latLng.latitude &&
                latLng.latitude > 39.952330236525334 &&
                116.34382749245587 > latLng.longitude &&
                latLng.longitude > 116.34301612424272) {
            return  "tsg";
        }
        return place;
    }

    /**
     * 显示进度框
     */
    private void showProgressDialog() {
        if (progDialog == null)
            progDialog = new ProgressDialog(this);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(false);
        progDialog.setMessage("正在搜索:\n" + mKeyWords);
        progDialog.show();
    }

    /**
     * 隐藏进度框
     */
    private void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }

    /**
     * 开始进行poi搜索
     */
    protected void doSearchQuery(String keywords) {
        showProgressDialog();// 显示进度框
        currentPage = 1;
        // 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query = new PoiSearch.Query(keywords, "", Constants.DEFAULT_CITY);
        // 设置每页最多返回多少条poiitem
        query.setPageSize(10);
        // 设置查第一页
        query.setPageNum(currentPage);

        poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        return false;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public View getInfoWindow(final Marker marker) {
        View view = getLayoutInflater().inflate(R.layout.poikeywordsearch_uri,
                null);
        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(marker.getTitle());

        TextView snippet = (TextView) view.findViewById(R.id.snippet);
        snippet.setText(marker.getSnippet());

        showWalkResult(marker);

        return view;
    }

    void showWalkResult(Marker marker) {
        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
                new LatLonPoint(latLng.latitude, latLng.longitude),
                new LatLonPoint(marker.getPosition().latitude, marker.getPosition().longitude));

        RouteSearch.WalkRouteQuery query = new RouteSearch.WalkRouteQuery(fromAndTo);
        mRouteSearch.calculateWalkRouteAsyn(query);// 异步路径规划步行模式查询
    }

    /**
     * poi没有搜索到数据，返回一些推荐城市的信息
     */
    private void showSuggestCity(List<SuggestionCity> cities) {
        String infomation = "推荐城市\n";
        for (int i = 0; i < cities.size(); i++) {
            infomation += "城市名称:" + cities.get(i).getCityName() + "城市区号:"
                    + cities.get(i).getCityCode() + "城市编码:"
                    + cities.get(i).getAdCode() + "\n";
        }
        ToastUtil.show(MainActivity.this, infomation);

    }


    /**
     * POI信息查询回调方法
     */
    @Override
    public void onPoiSearched(PoiResult result, int rCode) {
        dissmissProgressDialog();// 隐藏对话框
        if (rCode == 1000) {
            if (result != null && result.getQuery() != null) {// 搜索poi的结果
                if (result.getQuery().equals(query)) {// 是否是同一条
                    poiResult = result;
                    // 取得搜索到的poiitems有多少页
                    List<PoiItem> poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                    List<SuggestionCity> suggestionCities = poiResult
                            .getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息

                    if (poiItems != null && poiItems.size() > 0) {
                        mAMap.clear();// 清理之前的图标
                        PoiOverlay poiOverlay = new PoiOverlay(mAMap, poiItems);
                        poiOverlay.removeFromMap();
                        poiOverlay.addToMap();
                        poiOverlay.zoomToSpan();
                    } else if (suggestionCities != null
                            && suggestionCities.size() > 0) {
                        showSuggestCity(suggestionCities);
                    } else {
                        ToastUtil.show(MainActivity.this,
                                R.string.no_result);
                    }
                }
            } else {
                ToastUtil.show(MainActivity.this,
                        R.string.no_result);
            }
        } else {
            ToastUtil.showerror(this, rCode);
        }

    }

    @Override
    public void onPoiItemSearched(PoiItem item, int rCode) {
        // TODO Auto-generated method stub

    }

    /**
     * 输入提示activity选择结果后的处理逻辑
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CODE_INPUTTIPS && data
                != null) {
            mAMap.clear();
            Tip tip = data.getParcelableExtra(Constants.EXTRA_TIP);
            if (tip.getPoiID() == null || tip.getPoiID().equals("")) {
                doSearchQuery(tip.getName());
            } else {
                addTipMarker(tip);
            }
            mKeywordsTextView.setText(tip.getName());
            if (!tip.getName().equals("")) {
                mCleanKeyWords.setVisibility(View.VISIBLE);
            }
        } else if (resultCode == RESULT_CODE_KEYWORDS && data != null) {
            mAMap.clear();
            String keywords = data.getStringExtra(Constants.KEY_WORDS_NAME);
            if (keywords != null && !keywords.equals("")) {
                doSearchQuery(keywords);
            }
            mKeywordsTextView.setText(keywords);
            if (!keywords.equals("")) {
                mCleanKeyWords.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 用marker展示输入提示list选中数据
     *
     * @param tip
     */
    private void addTipMarker(Tip tip) {
        if (tip == null) {
            return;
        }
        mPoiMarker = mAMap.addMarker(new MarkerOptions());
        LatLonPoint point = tip.getPoint();
        if (point != null) {
            LatLng markerPosition = new LatLng(point.getLatitude(), point.getLongitude());
            mPoiMarker.setPosition(markerPosition);
            mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerPosition, 17));
        }
        mPoiMarker.setTitle(tip.getName());
        mPoiMarker.setSnippet(tip.getAddress());
    }

    /**
     * 点击事件回调方法
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_keywords:
                Intent intent = new Intent(this, InputTipsActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.clean_keywords:
                mKeywordsTextView.setText("");
                mAMap.clear();
                mCleanKeyWords.setVisibility(View.GONE);
            default:
                break;
        }
    }


    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null
                    && amapLocation.getErrorCode() == 0) {
                // mLocationErrText.setVisibility(View.GONE);
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
            } else {
                String errText = "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo();
                System.out.println(errText);
//                Log.e("AmapErr",errText);
//                mLocationErrText.setVisibility(View.VISIBLE);
//                mLocationErrText.setText(errText);
            }
        }
    }

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();
        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    public void clickAvator(View view) {
        Intent intent = new Intent(this, Setting.class);

        Bundle bundle = new Bundle();

//        bundle.putInt("group", group);
        bundle.putString("token", token);

        bundle.putString("user", userName);

        intent.putExtras(bundle);
        startActivity(intent);
//        Toast.makeText(this, "点击了！", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {

    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult result, int errorCode) {
        dissmissProgressDialog();
        mAMap.clear();// 清理地图上的所有覆盖物
        if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size() > 0) {
                    mWalkRouteResult = result;
                    final WalkPath walkPath = mWalkRouteResult.getPaths()
                            .get(0);
                    if (walkRouteOverlay != null) {
                        walkRouteOverlay.removeFromMap();
                    }
                    walkRouteOverlay = new WalkRouteOverlay(
                            this, mAMap, walkPath,
                            mWalkRouteResult.getStartPos(),
                            mWalkRouteResult.getTargetPos());
                    walkRouteOverlay.addToMap();
                    walkRouteOverlay.zoomToSpan();
//                    mBottomLayout.setVisibility(View.VISIBLE);
//                    int dis = (int) walkPath.getDistance();
//                    int dur = (int) walkPath.getDuration();
//                    String des = AMapUtil.getFriendlyTime(dur)+"("+AMapUtil.getFriendlyLength(dis)+")";
//                    mRotueTimeDes.setText(des);
//                    mRouteDetailDes.setVisibility(View.GONE);
//                    mBottomLayout.setOnClickListener(new OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Intent intent = new Intent(mContext,
//                                    WalkRouteDetailActivity.class);
//                            intent.putExtra("walk_path", walkPath);
//                            intent.putExtra("walk_result",
//                                    mWalkRouteResult);
//                            startActivity(intent);
//                        }
//                    });
//                } else if (result != null && result.getPaths() == null) {
//                    ToastUtil.show(mContext, R.string.no_result);
//                }
//            } else {
//                ToastUtil.show(mContext, R.string.no_result);
//            }
//        } else {
//            ToastUtil.showerror(this.getApplicationContext(), errorCode);
//        }
                }
            }
        }
    }

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

    }
}
