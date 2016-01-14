package com.ffcs.grid;

import com.ffcs.activity.CaptureActivity;
import com.ffcs.activity.FirstActivity;
import com.ffcs.activity.GridLayoutActivity;
import com.ffcs.activity.Photograph_Sys;
import com.ffcs.activity.Photograph_SysActivity;
import com.ffcs.activity.R;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

public class DragGridView extends GridView {

	// 定义基本的成员变量

	private ImageView dragImageView;
    //拖动的原坐标位置
	private int dragSrcPosition;
    //终点拖放位置
	private int dragPosition;

	// x,y坐标的计算

	private int dragPointX;

	private int dragPointY;
    
	//移动位移
	private int dragOffsetX;

	private int dragOffsetY;

	private WindowManager windowManager;

	private WindowManager.LayoutParams windowParams;

	//手势GestureDetector类
	private GestureDetector mGestureDetector;
	
	//事件监听
	private MyGestureListener mGestureListener;

	public DragGridView(Context context, AttributeSet attrs) {

		super(context, attrs);
		mGestureListener = new MyGestureListener();
		mGestureDetector = new GestureDetector( context,mGestureListener);

	}
	
	// startDrag和stopDrag方法如下：

	public void startDrag(Bitmap bm, int x, int y) {

		stopDrag();

		windowParams = new WindowManager.LayoutParams();

		windowParams.gravity = Gravity.TOP | Gravity.LEFT;

		windowParams.x = x - dragPointX + dragOffsetX;

		windowParams.y = y - dragPointY + dragOffsetY;

		windowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;

		windowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

		windowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE

		| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE

		| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON

		| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

		windowParams.format = PixelFormat.TRANSLUCENT;

		windowParams.windowAnimations = 0;

		ImageView imageView = new ImageView(getContext());

		imageView.setImageBitmap(bm);

		windowManager = (WindowManager) getContext().getSystemService("window");

		windowManager.addView(imageView, windowParams);

		dragImageView = imageView;

	}

	/**
	 * 2 * 停止拖动，去除拖动项的头像
	 * 
	 * 3
	 */

	public void stopDrag() {
		if (dragImageView != null) {
			windowManager.removeView(dragImageView);

			dragImageView = null;
		}
	}

//	 重写onTouchEvent()方法.并将调用GestureDetector的监听事件
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return mGestureDetector.onTouchEvent(event);
	}


	// 其中onDrag方法如下：

	public void onDrag(int x, int y) {

		if (dragImageView != null) {

			windowParams.alpha = 0.8f;

			windowParams.x = x - dragPointX + dragOffsetX;

			windowParams.y = y - dragPointY + dragOffsetY;

			windowManager.updateViewLayout(dragImageView, windowParams);

		}
	}

	// 放下影像，数据更新。
	// 在onDrop方法中实现：

	public void onDrop(int x, int y) {

		// 为了避免滑动到分割线的时候，返回-1的问题

		int tempPosition = pointToPosition(x, y);
		if (tempPosition != INVALID_POSITION) {

			dragPosition = tempPosition;

		}

		// 超出边界处理

		if (y < getChildAt(0).getTop()) {
			// 超出上边界

			dragPosition = 0;

		} else if (y > getChildAt(getChildCount() - 1).getBottom()
				|| (y > getChildAt(getChildCount() - 1).getTop() && x > getChildAt(
						getChildCount() - 1).getRight())) {

			// 超出下边界

			dragPosition = getAdapter().getCount() - 1;

		}

		// 数据交换，主要是交换子Item的数据

		if (dragPosition != dragSrcPosition && dragPosition > -1&& dragSrcPosition > -1
				&& dragPosition < getAdapter().getCount()) {

			GridAdapter adapter = (GridAdapter) getAdapter();
			adapter.exchange(dragPosition, dragSrcPosition);
			ViewGroup itemView1 = (ViewGroup) getChildAt(dragPosition
					- getFirstVisiblePosition());
			ViewGroup itemView2 = (ViewGroup) getChildAt(dragSrcPosition
					- getFirstVisiblePosition());
			itemView1.destroyDrawingCache();
			itemView2.destroyDrawingCache();

		}

	}
//自定义的手势事件监听器
	class MyGestureListener extends SimpleOnGestureListener {
		
		//单击
		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			return false;
		}
         //长按
		@Override
		public void onLongPress(MotionEvent e) {
			
		}

		/**
		 * @param e1
		 *            The first down motion event that started the scrolling.
		 * @param e2
		 *            The move motion event that triggered the current onScroll.
		 * @param distanceX
		 *            The distance along the X axis(轴) that has been scrolled
		 *            since the last call to onScroll. This is NOT the distance
		 *            between e1 and e2.
		 * @param distanceY
		 *            The distance along the Y axis that has been scrolled since
		 *            the last call to onScroll. This is NOT the distance
		 *            between e1 and e2. 无论是用手拖动view，或者是以抛的动作滚动，都会多次触发
		 *            ,这个方法在ACTION_MOVE动作发生时就会触发
		 *            参看GestureDetector的onTouchEvent方法源码
		 * */
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			int x = (int) e1.getX();

			int y = (int) e1.getY();

			dragSrcPosition = dragPosition = pointToPosition(x, y);

			if (dragPosition == AdapterView.INVALID_POSITION) {

				return mGestureDetector.onTouchEvent(e1);

			}
			ViewGroup itemView = (ViewGroup) getChildAt(dragPosition
					- getFirstVisiblePosition());
			dragPointX = x - itemView.getLeft();

			dragPointY = y - itemView.getTop();

			dragOffsetX = (int) (e1.getRawX() - x);

			dragOffsetY = (int) (e1.getRawY() - y);

			// 每次都销毁一次cache，重新生成一个bitmap
			itemView.destroyDrawingCache();
			itemView.setDrawingCacheEnabled(true);

			Bitmap bm = Bitmap.createBitmap(itemView.getDrawingCache());
			startDrag(bm, x, y);
			int moveX = (int) e2.getX();

			int moveY = (int) e2.getY();
			onDrag(moveX, moveY);
			return false;
		}

		/**
		 * @param e1
		 *            第1个ACTION_DOWN MotionEvent 并且只有一个
		 * @param e2
		 *            最后一个ACTION_MOVE MotionEvent
		 * @param velocityX
		 *            X轴上的移动速度，像素/秒
		 * @param velocityY
		 *            Y轴上的移动速度，像素/秒 这个方法发生在ACTION_UP时才会触发
		 *            参看GestureDetector的onTouchEvent方法源码
		 * 
		 * */
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			if (dragImageView != null && dragPosition != INVALID_POSITION&& dragSrcPosition != INVALID_POSITION) {

				int upX = (int) e2.getX();

				int upY = (int) e2.getY();

				stopDrag();
				onDrop(upX, upY);

			}
			return false;
		}
    
		@Override
		public void onShowPress(MotionEvent e) {

		}
        //按下
		@Override
		public boolean onDown(MotionEvent e) {
			return true;
		}
        //双击
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			return true;
		}
       
		@Override
		public boolean onDoubleTapEvent(MotionEvent e) {
			return false;
		}

		/**
		 * 这个方法不同于onSingleTapUp，他是在GestureDetector确信用户在第一次触摸屏幕后，没有紧跟着第二次触摸屏幕，
		 * 也就是不是“双击”的时候触发
		 * */
		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			int x = (int) e.getX();

			int y = (int) e.getY();

			int position =pointToPosition(x, y);
			
			GridAdapter adapter = (GridAdapter) getAdapter();
			
			final View view=(View)getChildAt(position);
			
			final GridInfo item=(GridInfo)adapter.getItem(position);
			
            //设置子Item的背景，图片来自drawable
			view.setBackgroundDrawable(getResources().getDrawable(R.drawable.focused_application_background_static));
            
			//调用线程睡眠0.5秒后将背景在设置为空，以达到闪烁一下的效果
			new Handler().postDelayed(new Runnable(){

				@Override
				public void run() {
					// TODO Auto-generated method stub
					view.setBackgroundDrawable(null);
				}
            	
            }, 500);
			
            //根据图片id响应相应的应用功能
			new Handler().postDelayed(new Runnable(){

				@Override
				public void run() {
					// TODO Auto-generated method stub
					Intent intent = new Intent();
					Context context = getContext();
					switch(item.getImage_id())
					{
					case 1:intent.setClass(context, CaptureActivity.class);context.startActivity(intent);break;
					case 2:intent.setClass(context, Photograph_Sys.class);context.startActivity(intent);break;
					default : break;
					}
				}
				
			},500);
		
			return false;
		}

	}
}
