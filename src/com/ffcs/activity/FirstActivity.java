package com.ffcs.activity;

import com.ffcs.activity.CaptureActivity;
import com.ffcs.activity.FirstActivity;
import com.ffcs.activity.R;
import com.ffcs.activity.FirstActivity.MyButtonListener;
import com.ffcs.grid.DragGridView;
import com.ffcs.mylocation.MyLocationManager;

import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

//该类主要是用于一个启动GridViewActivity的Activity，可以丢弃，更改只要将其设置为第一启动项就可以了
public class FirstActivity extends Activity {
	/** Called when the activity is first created. */
	// 代表按钮对象的引用
	private Button btn_saomiao = null;
	private Button btn_exit = null;
	Context context = this;

	// 复写父类当中的onCreate方法，Activity第一次运行时会调用这个方法
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 为Activity设置布局管理文件
		setContentView(R.layout.first);
		// 以下两行代码是根据控件的ID来得到控件对象
		// TextView myTextView = (TextView) findViewById(R.id.myTextView);
		btn_saomiao = (Button) findViewById(R.id.btn_saomiao);
		btn_exit = (Button) findViewById(R.id.btn_exit);
		// 为按钮对象设置监听器对象
		btn_saomiao.setOnClickListener(new MyButtonListener());
		btn_exit.setOnClickListener(new MyButtonListener());

	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//如果按下的是返回键，并且没有重复
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish();
			overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
			return false;
		}
		return false;
	}
	class MyButtonListener implements OnClickListener {
		// 生成该类的对象，并将其注册到控件上。如果该控件被用户按下，就会执行onClick方法
		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.btn_saomiao) {
				// 生成一个Intent对象
				Intent intent = new Intent();
				// 设置Intent对象要启动的Activity
				intent.setClass(FirstActivity.this, GridLayoutActivity.class);
				// 通过Intent对象启动另外一个Activity
				FirstActivity.this.startActivity(intent);

				// 添加界面切换效果，注意只有Android的2.0(SdkVersion版本号为5)以后的版本才支持
//				int version = Integer.valueOf(android.os.Build.VERSION.SDK);
//				if (version >= 5) {
//					overridePendingTransition(R.anim.push_left_in,
//							R.anim.push_left_out); // 此为自定义的动画效果，下面两个为系统的动画效果
//				}
			} else
				try {
					finish();
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}

	}
}