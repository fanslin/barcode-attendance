package com.ffcs.activity;

import java.io.File;

import android.app.Activity;

import android.content.Intent;

import android.net.Uri;

import android.os.Bundle;

import android.os.Environment;

import android.provider.MediaStore;

import android.util.Log;

public class Photograph_SysActivity extends Activity {

	/** Called when the activity is first created. */

	private String logTag = "Exception";

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		// setContentView(R.layout.main);

		try {

			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

			File out = new File(Environment.getExternalStorageDirectory(),
					"camera.jpg");

			Uri uri = Uri.fromFile(out);

			intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

			startActivityForResult(intent, 0);

		} catch (Exception e) {

			e.printStackTrace();

		}

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data)

	{

		try {

			Log.v("", "requestCode = " + requestCode + " resultCode =  "
					+ resultCode);

			if (requestCode != 0) {

				return;

			}

			if (resultCode == 0) {

				finish();

				return;

			}

			// Bundle extras = data.getExtras();

			// Bitmap b = (Bitmap) extras.get("data");

			Intent intent = new Intent();

			intent.setClass(this, ShowImageActivity.class);

			// intent.putExtra("image",b);

			this.startActivity(intent);

		} catch (Exception e) {

			// TODO: handle exception

			Log.v(logTag, e.getMessage());

		}

	}

}