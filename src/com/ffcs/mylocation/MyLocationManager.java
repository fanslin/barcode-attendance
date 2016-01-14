package com.ffcs.mylocation;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;
import android.widget.Toast;

public class MyLocationManager {
	// 获取定位服务
	private LocationManager mgr;
	// 位置服务提供者
	private String serviceName = Context.LOCATION_SERVICE;
	// 默认为GPS
	private String provider ;
	// 描述位置信息
	private Location l;
	// 保存位置信息（经纬度）
	double lat;
	double lng;
	private Context context;

	// 通过此类获取和调用系统位置服务
	public MyLocationManager(Context context) {
		this.context = context;
		initLocation();// 初始化
	}

	// 初始化
	private void initLocation() {

		ConnectivityManager mConnectivity = (ConnectivityManager)this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
		 NetworkInfo info = mConnectivity.getActiveNetworkInfo();
		 //判断网络是否开启
		 if(info != null && mConnectivity.getBackgroundDataSetting()) {
			// 获取到LocationManager对象
				mgr = (LocationManager) this.context.getSystemService(serviceName);

				// 判断定位方式
				openGPSSettings();

				// 根据当前provider对象获取最后一次位置信息
				Location location = mgr.getLastKnownLocation(provider);
				// 更新位置信息
				updateToNewLocation(location);
				//监听位置变化，2秒一次，距离10米以上
		        mgr.requestLocationUpdates(provider, 2000, 10, mylocationlistener);
		 }else{
			 Toast.makeText(this.context, "请检查网络设置", Toast.LENGTH_SHORT).show();
		 }

//		if(location == null){
//			mgr.requestLocationUpdates(provider, 0, 0, mylocationlistener);
//		}
	

//		// 设置监听器，自动更新的最小时间为间隔N秒(1秒为1*1000，这样写主要为了方便)或最小位移变化超过N米
//		mgr.requestLocationUpdates(provider, 5 * 1000, 500, mylocationlistener);

	}

	// 判断定位方式
	private void openGPSSettings() {	
			if (mgr.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
				Toast.makeText(this.context, "GPS已开启", Toast.LENGTH_SHORT).show();
				 Criteria criteria = new Criteria();
			        criteria.setAccuracy(Criteria.ACCURACY_FINE);//高精度
			        criteria.setAltitudeRequired(false);//不要求海拔
			        criteria.setBearingRequired(false);//不要求方位
			        criteria.setCostAllowed(true);//允许有花费
			        criteria.setPowerRequirement(Criteria.POWER_LOW);//低功耗
			      //从可用的位置提供器中，匹配以上标准的最佳提供器
			        provider = mgr.getBestProvider(criteria, true);			        
			        return;
			}
			Toast.makeText(this.context, "GPS未开启，系统将使用基站定位", Toast.LENGTH_SHORT).show();
			provider = LocationManager.NETWORK_PROVIDER; //若GPS未开启采用基站定位
			return;	
	}

	// 更新坐标
		private void updateToNewLocation(Location l) {

			/*if (provider.equals(LocationManager.GPS_PROVIDER)) {
				try {
					// 真机获取位置信息需要一定的时间，为了保证获取到，采取系统休眠的延迟方法
					Thread.sleep(0);
				} catch (InterruptedException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			}*/
			while(true){
				l = mgr.getLastKnownLocation(provider);
			if (l != null) {
				lat = l.getLatitude();//获取纬度
				lng = l.getLongitude();//获取经度
				// 解析地址并显示
				/*Geocoder geoCoder = new Geocoder(this.context);
				List<Address> list = null;
				try {
					list = geoCoder.getFromLocation(lat, lng, 2);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				for (int i = 0; i < list.size(); i++) {
					Address address = list.get(i);
					Toast.makeText(this.context, address.getAdminArea()+ address.getLocality()+ address.getFeatureName(), Toast.LENGTH_LONG).show();
				}*/
				break;
			} else {
				Toast.makeText(this.context, "无法获取地理信息，请检查设置！", Toast.LENGTH_SHORT).show();
			}
			}
		}

	// 监听位置变化
	private MyLocationListener mylocationlistener = new MyLocationListener();

	private class MyLocationListener implements LocationListener {
		// 当坐标位置改变时触发此函数
		@Override
		public void onLocationChanged(Location loc) {
			// TODO Auto-generated method stub
			updateToNewLocation(loc);
			/*if (count < 5 && l != null) {
				lats[count] = lat;
				lngs[count] = lng;
				count++;
			}
			if (count == 5) {
				getExactly();
				count = 0;
			}*/
		}

		// Provider禁用时触发，比如GPS关闭
		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			updateToNewLocation(l);
		}

		// Provider启用时触发，比如GPS开启
		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		// Provider的转态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
		}
	}

	// 获取精确的经纬度
	double lats[] = { 0, 0, 0, 0, 0 }, lngs[] = { 0, 0, 0, 0, 0 };
	// 记录位置变化的次数
	int count = 0;

	private void getExactly() {
		if (provider.equals(LocationManager.NETWORK_PROVIDER)) {
			double avg_lat = 0.0, avg_lng = 0.0;
			for (int i = 0; i < 5; i++) {
				avg_lat += lats[i];
				avg_lng += lngs[i];
			}
			avg_lat /= 5.0;
			avg_lng /= 5.0;
			double[] flag = { 0, 0, 0, 0, 0 };
			for (int i = 0; i < 5; i++) {
				flag[i] = avg_lat + avg_lng - lats[i] - lngs[i];
			}
			double min = flag[0];
			int pos = 0;
			for (int i = 1; i < 5; i++) {
				if (min < flag[i]) {
					pos = i;
				}
			}
			this.lat = lats[pos];
			this.lng = lngs[pos];
		}
	}
	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}
}