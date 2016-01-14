package com.ffcs.activity;

import java.util.ArrayList;
import java.util.List;

import com.ffcs.activity.R;
import com.ffcs.grid.GridAdapter;
import com.ffcs.grid.GridInfo;
import com.ffcs.listenerHelper.MyOnGestureListener;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.GridView;
import android.widget.Toast;

public class GridLayoutActivity extends Activity {
	private GridView gridview;
	
	private List<GridInfo> list;
	
	private GridAdapter adapter;

	public ViewGroup container_gridview;
	
	public ViewGroup container_2;
	
	private GestureDetector gestureDetector;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.gridlayout);
		//这里定义了两个界面，container_2，container_gridview
		container_2 = (ViewGroup) findViewById(R.id.container_2);
		
		container_gridview = (ViewGroup) findViewById(R.id.container_gridview);
		
		//这个主要是监听屏幕的滑动事件，用于左右拉动屏幕切换
		MyOnGestureListener myOnGestureListener = new MyOnGestureListener(this);
		
		gestureDetector = new GestureDetector(this,myOnGestureListener);
		
		gridview = (GridView) findViewById(R.id.gridview);
		
		//初始化数据
		list = new ArrayList<GridInfo>();
		list.add(new GridInfo("段长巡察", 1));
		list.add(new GridInfo("拍照上传", 2));
		list.add(new GridInfo("地图定位", 3));
		list.add(new GridInfo("在线会话", 4));
		list.add(new GridInfo("日历", 5));
		list.add(new GridInfo("电子邮件", 6));
		list.add(new GridInfo("音乐", 7));
		list.add(new GridInfo("电子时钟", 8));
		list.add(new GridInfo("浏览器", 9));
		list.add(new GridInfo("摄像机", 10));
		list.add(new GridInfo("联系人", 11));
		list.add(new GridInfo("短信", 12));
		
		adapter = new GridAdapter(this);
		
		adapter.setList(list);
		
		gridview.setAdapter(adapter);
		

		//监听手机键盘的单击确定操作
		gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView parent, View view,
					int position, long id) {
				{
					
					GridInfo item = (GridInfo) adapter.getItem(position);
					Intent intent = new Intent();
					switch (item.getImage_id()) {
					case 1:
						intent.setClass(GridLayoutActivity.this,
								CaptureActivity.class);
						GridLayoutActivity.this.startActivity(intent);
						break;
					case 2:
						intent.setClass(GridLayoutActivity.this,
								Photograph_Sys.class);
						GridLayoutActivity.this.startActivity(intent);
						break;
					default:
						break;
					}

				}
			}

		});

	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//如果按下的是返回键，并且没有重复
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish();
			
			//设置界面的切换效果
			overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
			
			return false;
		}
		return false;
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (gestureDetector.onTouchEvent(event))
			return true;
		else
			return false;
	}
}