package com.ffcs.listenerHelper;

import com.ffcs.activity.GridLayoutActivity;
import com.ffcs.activity.R;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.animation.AnimationUtils;

public class MyOnGestureListener implements OnGestureListener {

    GridLayoutActivity first;

    private static final int SWIPE_MAX_OFF_PATH = 100;

    private static final int SWIPE_MIN_DISTANCE = 100;

    private static final int SWIPE_THRESHOLD_VELOCITY = 100;

    public MyOnGestureListener( GridLayoutActivity first) {
        this.first = first;
    }

    // 用户轻触触摸屏，由1个MotionEvent ACTION_DOWN触发
    public boolean onDown(MotionEvent e) {
        return true;
    }

    // 用户按下触摸屏、快速移动后松开,由1个MotionEvent ACTION_DOWN,
    // 多个ACTION_MOVE, 1个ACTION_UP触发
    // e1：第1个ACTION_DOWN MotionEvent
    // e2：最后一个ACTION_MOVE MotionEvent
    // velocityX：X轴上的移动速度，像素/秒
    // velocityY：Y轴上的移动速度，像素/秒
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
            float velocityY) {
    	if(e1!=null&&e2!=null){
        if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
            return false;

        if ((e1.getX() - e2.getX()) > SWIPE_MIN_DISTANCE
                && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
            first.container_gridview.setAnimation(AnimationUtils.loadAnimation(first,
                    R.anim.push_left_out1));
            first.container_2.setVisibility(View.VISIBLE);
            first.container_2.setAnimation(AnimationUtils.loadAnimation(first,
                    R.anim.push_right_in));
            first.container_gridview.setVisibility(View.GONE);

        } else if ((e2.getX() - e1.getX()) > SWIPE_MIN_DISTANCE
                && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
        	first.container_gridview.setVisibility(View.VISIBLE);
        	first.container_gridview.setAnimation(AnimationUtils.loadAnimation(first,
                    R.anim.push_left_in1));
        	first.container_2.setAnimation(AnimationUtils.loadAnimation(first,
                    R.anim.push_right_out));
        	first.container_2.setVisibility(View.GONE);
        }
        }
        return true;
    	
    }

    // 用户长按触摸屏，由多个MotionEvent ACTION_DOWN触发
    public void onLongPress(MotionEvent e) {
     
    }

    // 用户按下触摸屏，并拖动，由1个MotionEvent ACTION_DOWN, 多个ACTION_MOVE触发
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
            float distanceY) {
        return true;
    }

    // 用户轻触触摸屏，尚未松开或拖动，由一个1个MotionEvent ACTION_DOWN触发
    // 注意和onDown()的区别，强调的是没有松开或者拖动的状态
    public void onShowPress(MotionEvent e) {

    }

    // 用户（轻触触摸屏后）松开，由一个1个MotionEvent ACTION_UP触发
    public boolean onSingleTapUp(MotionEvent e) {
        return true;
    }

}

