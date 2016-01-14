package com.ffcs.activity;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.ffcs.activity.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.ImageView;

public class ShowImageActivity extends Activity {
	private String logTag = "exception";
	private AlertDialog alertDialog;
	private ImageView view;
	private Camera camera;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.surfaceview);

		try {
			view = (ImageView) findViewById(R.id.image);
			// Bundle bundle = this.getIntent().getExtras();
			// Bitmap b = bundle.getParcelable("image");
			Date now = new Date();
			String format = new String("yyyy-MM-dd HH-mm-ss");
			// String format = new String("yyyy/MM/dd-HH_mm_ss");
			TelephonyManager tm = (TelephonyManager) getSystemService(this.TELEPHONY_SERVICE);
			// String sid = tm.getSimSerialNumber();
			SimpleDateFormat sdf = new SimpleDateFormat(format);

			// String fileName = sdf.format(now)+".jpg" ;

			final String fileName = tm.getDeviceId() + "_" + sdf.format(now)
					+ ".jpg";
			String pathString = Environment.getExternalStorageDirectory()
					.toString() + "/" + fileName;

			// Log.v("", "pathString = " + pathString);
			// Bitmap b = BitmapFactory.decodeFile(pathString);
			// view.setImageBitmap(b);
			// setContentView(view);

			alertDialog = new AlertDialog.Builder(ShowImageActivity.this)
					.setMessage("确定上传 ?")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									/* User clicked OK so do some stuff */
									uploadFile(fileName);
//									Intent intent = new Intent();
//
//									intent.setClass(ShowImageActivity.this,
//											ActMain.class);
//
//									ShowImageActivity.this
//											.startActivity(intent);

								}
							})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									/* User clicked Cancel so do some stuff */
									Intent intent = new Intent();

									intent.setClass(ShowImageActivity.this,
											GridLayoutActivity.class);

									ShowImageActivity.this
											.startActivity(intent);
								}
							}).create();
			alertDialog.show();
		} catch (Exception e) {
			Log.v(logTag, e.getMessage());
			throw new RuntimeException(e);

		}
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

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		Log.v("", "event = " + event.getX());
		return super.onTouchEvent(event);
	}

	/* 上传文件至Server的方法 */
	private void uploadFile(String name) {
		String newName = name;
		String uploadFile = "/mnt/sdcard/" + name;
		String actionUrl = "http://192.168.20.222:8080/FileUploadDemo/servlet/MyServlet";
		String end = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		try {
			URL url = new URL(actionUrl);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			/* 允许Input、Output，不使用Cache */
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false);
			/* 设置传送的method=POST */
			con.setRequestMethod("POST");
			/* setRequestProperty */
			con.setRequestProperty("Connection", "Keep-Alive");
			con.setRequestProperty("Charset", "UTF-8");
			con.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);
			/* 设置DataOutputStream */
			DataOutputStream ds = new DataOutputStream(con.getOutputStream());
			ds.writeBytes(twoHyphens + boundary + end);
			ds.writeBytes("Content-Disposition: form-data; "
					+ "name=\"file1\";filename=\"" + newName + "\"" + end);
			ds.writeBytes(end);

			/* 取得文件的FileInputStream */
			FileInputStream fStream = new FileInputStream(uploadFile);
			/* 设置每次写入1024bytes */
			int bufferSize = 1024;
			byte[] buffer = new byte[bufferSize];

			int length = -1;
			/* 从文件读取数据至缓冲区 */
			while ((length = fStream.read(buffer)) != -1) {
				/* 将资料写入DataOutputStream中 */
				ds.write(buffer, 0, length);
			}
			ds.writeBytes(end);
			ds.writeBytes(twoHyphens + boundary + twoHyphens + end);

			/* close streams */
			fStream.close();
			ds.flush();

			/* 取得Response内容 */
			InputStream is = con.getInputStream();
			int ch;
			StringBuffer b = new StringBuffer();
			while ((ch = is.read()) != -1) {
				b.append((char) ch);
			}
			/* 将Response显示于Dialog */
			showDialog("上传成功" + b.toString().trim());
			/* 关闭DataOutputStream */
			ds.close();
		} catch (Exception e) {
			showDialog("上传失败" + e);
		}
	}

	/* 显示Dialog的method */
	private void showDialog(String mess) {
		new AlertDialog.Builder(this).setTitle("Message").setMessage(mess)
				.setNegativeButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					}
				}).show();
	}
}