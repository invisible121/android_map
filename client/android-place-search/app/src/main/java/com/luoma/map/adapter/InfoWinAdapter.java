package com.luoma.map.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.RouteSearch;
import com.amap.map.R;
import com.luoma.map.base.BaseApplication;
import com.luoma.map.utils.NavigationUtils;
import com.luoma.map.utils.PhoneCallUtils;

/**
 * Created by Teprinciple on 2016/8/23.
 * 地图上自定义的infowindow的适配器
 */
public class InfoWinAdapter implements AMap.InfoWindowAdapter, View.OnClickListener {
    private Context mContext = BaseApplication.getIntance().getBaseContext();
    private LatLng latLng;
    private LatLng placeLatLng;
//    private LinearLayout call;
    private LinearLayout navigation;
    private TextView nameTV;
    private String agentName;
    private TextView addrTV;
    private String snippet;
    private Marker marker;

    private RouteSearch mRouteSearch;

    public InfoWinAdapter(LatLng latLng, RouteSearch mRouteSearch) {
        this.latLng = latLng;
        this.mRouteSearch = mRouteSearch;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        initData(marker);
        View view = initView();
        return view;
    }
    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    private void initData(Marker marker) {
        this.marker = marker;
        placeLatLng = marker.getPosition();
        snippet = marker.getSnippet();
        agentName = marker.getTitle();
    }

    @NonNull
    private View initView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_infowindow, null);
        navigation = (LinearLayout) view.findViewById(R.id.navigation_LL);
//        call = (LinearLayout) view.findViewById(R.id.call_LL);
        nameTV = (TextView) view.findViewById(R.id.name);
        addrTV = (TextView) view.findViewById(R.id.addr);

        nameTV.setText(agentName);
        addrTV.setText(String.format(mContext.getString(R.string.agent_addr),snippet));

        navigation.setOnClickListener(this);
//        call.setOnClickListener(this);
        return view;
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.navigation_LL:  //点击导航
//                NavigationUtils.Navigation(placeLatLng);
                showWalkResult(this.marker);
                break;

//            case R.id.call_LL:  //点击打电话
//                PhoneCallUtils.call("028-"); //TODO 处理电话号码
//                break;
        }
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    void showWalkResult(Marker marker) {
        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
                new LatLonPoint(latLng.latitude, latLng.longitude),
                new LatLonPoint(marker.getPosition().latitude, marker.getPosition().longitude));

        RouteSearch.WalkRouteQuery query = new RouteSearch.WalkRouteQuery(fromAndTo);
        mRouteSearch.calculateWalkRouteAsyn(query);// 异步路径规划步行模式查询
    }

}
