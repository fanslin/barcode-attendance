package com.ffcs.grid;

import android.widget.ImageView;
import android.widget.TextView;

//用于保存GridView中子項的数据的类
public class GridInfo {

	private String name;

	// 子Item的图片id，初始化的时候固定指定值，也可作为唯一标识
	private int image_id;

	private String img;

	// 保存imageview实例
	private ImageView imgView;

	// 保存textview实例
	private TextView txtView;

	public GridInfo(String name, int image_id) {

		super();

		this.name = name;

		this.image_id = image_id;

		// 这个是必须的，图标的命名格式：grid_integer;
		this.img = "grid_" + image_id;
	}

	public String getName() {
		return name;
	}

	public void setImgView(ImageView imgView) {
		this.imgView = imgView;
	}

	public ImageView getImgView() {
		return imgView;
	}

	public void setTxtView(TextView txtView) {
		this.txtView = txtView;
	}

	public TextView getTxtView() {
		return txtView;
	}

	public void setImage_id(int id) {
		this.image_id = id;
	}

	public int getImage_id() {
		return image_id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}
}
