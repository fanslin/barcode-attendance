package com.ffcs.activity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Vector;
import com.ffcs.activity.R;
import com.ffcs.camera.CameraManager;
import com.ffcs.decoding.CaptureActivityHandler;
import com.ffcs.decoding.InactivityTimer;
import com.ffcs.mylocation.MyLocationManager;
import com.ffcs.view.ViewfinderView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

public class CaptureActivity extends Activity implements Callback {

	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private boolean hasSurface;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	private TextView txtResult;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.10f;
	private boolean vibrate;

	/** Called when the activity is first created. onCreate()/onResume()完成界面加载*/
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		// 初始化 CameraManager
		CameraManager.init(getApplication());
		// 自定义的view，实现了扫描时界面的刷新，并将识别的一些数据，如定位的点回调显示在界面上。
		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		txtResult = (TextView) findViewById(R.id.txtResult);
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//如果按下的是返回键，并且没有重复
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish();
			//Activity切换动画
			overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
			return false;
		}
		return false;
	}
	//判断这个activity是由什么启动的，可能是其他app触发，也可能是用户直接启动。
	@Override
	protected void onResume() {
		super.onResume();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);//用来加载从底层硬件获取的相机取景的图像
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		decodeFormats = null;   //解码方式
		characterSet = null;    //编码方式
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

	// 初始化摄像头
	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);
		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(this, decodeFormats,
					characterSet);//用于进行扫描解码处理
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

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

	// 处理二维码类
	public void handleDecode(Result obj, Bitmap barcode) {
		inactivityTimer.onActivity();//定时线程
		viewfinderView.drawResultBitmap(barcode);//画结果位图
		playBeepSoundAndVibrate();
/*		txtResult.setText(obj.getBarcodeFormat().toString() + ":"
				+ obj.getText());*/
		MyLocationManager lmg = new MyLocationManager(this);
		Date now = new Date();
		String format = new String("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		String barcodeInfo = obj.getText() + "|" + sdf.format(now) + "|" + lmg.getLat() + "," + lmg.getLng();
		
		// 跳转显示结果
				/*Intent intent = new Intent();
				intent.setClass(CaptureActivity.this, ResultActivity.class);
				CaptureActivity.this.startActivity(intent);*/
		
		if(lmg.getLat()!= 0.0){
			sendInfo(barcodeInfo);//发送信息
//			lmg.setLat(0.0);
//			lmg.setLng(0.0);
		}else{
			Toast.makeText(this, "签到失败！", Toast.LENGTH_SHORT).show();
		}		
		this.finish();
	}

	// 发送二维码中读取的信息
	private void sendInfo(String barcodeInfo) {
//		String urlPost = "http://xiagang.toxm.cn/cip/admin/public/access/cip_sectionInspect/sectionInspect_add.do"; // POST请求的URL
		String urlPost = "http://1.1.1.1:8080/cip/admin/public/access/cip_sectionInspect/sectionInspect_add.do";
		HttpPost httpPostRequest = new HttpPost(urlPost); // 创建HttpPost对象
		List<NameValuePair> httpParams = new ArrayList<NameValuePair>(); // 创建存放参数的ArrayList
		String[] sourceStr = barcodeInfo.split("\\|"); //在每个竖线处进行分解，返回字符串数组
		String[] paramsStr = { "name", "businessId", "signDate", "imgPath" };
//		if (sourceStr.length != paramsStr.length) {
//			Toast.makeText(this, "二维码信息格式不匹配！", Toast.LENGTH_SHORT).show();
//			return;
//		}
		// 设置post参数(参数，值)
		for (int i = 0; i < sourceStr.length; i++)
			httpParams.add(new BasicNameValuePair(paramsStr[i], sourceStr[i]));

		try {
			httpPostRequest.setEntity(new UrlEncodedFormEntity(httpParams,HTTP.UTF_8));
			HttpResponse httpResponse = new DefaultHttpClient().execute(httpPostRequest);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				// 取得返回的字符串
				Toast.makeText(this, "签到成功！", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(this, "签到失败！请重新尝试！", Toast.LENGTH_SHORT).show();
			}

		} catch (Exception e) { // 捕获并打印异常
			// EditText etPost = (EditText)findViewById(R.id.etPost);
			// //获得EditText对象
			// etPost.setText("连接出错:"+e.getMessage()); //为EditText设置出错信息
			Toast.makeText(this, "连接出错！签到失败！", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}

	// 初始化拍照音效
	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			// The volume on STREAM_SYSTEM is not adjustable, and users found it
			// too loud,
			// so we now play on the music stream.
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(
					R.raw.beep);
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

	// 播放声音或震动方法
	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	/**
	 * When the beep has finished playing, rewind to queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};

}