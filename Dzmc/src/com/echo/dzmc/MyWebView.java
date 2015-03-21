package com.echo.dzmc;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.ViewFlipper;

public class MyWebView extends WebView {

	float downXValue;
	long downTime;
	private ViewFlipper flipper;
	
	private float lastTouchX,lastTouchY;
	private boolean hasMoved = false;
	
	public MyWebView(Context context,ViewFlipper vf) {
		super(context);
		this.flipper = vf;
		// TODO �Զ����ɵĹ��캯�����
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent evt){
		boolean consumed = super.onTouchEvent(evt);
		if (isClickable()) {
            switch (evt.getAction()) {
           case MotionEvent.ACTION_DOWN:
               lastTouchX = evt.getX();

                lastTouchY = evt.getY();
                downXValue = evt.getX();
                downTime = evt.getEventTime();
                hasMoved = false;
                break;
            case MotionEvent.ACTION_MOVE:
                hasMoved = moved(evt);
                break;
            case MotionEvent.ACTION_UP:
               float currentX = evt.getX();
                long currentTime = evt.getEventTime();
                float difference = Math.abs(downXValue - currentX);
                long time = currentTime - downTime;
                Log.i("Touch Event:", "Distance: " + difference + "px Time: "+ time + "ms");
                /** X�Ử���������100������ʱ��С��220ms,������X���ҷ��򻬶�   && (time < 220) */
                if ((downXValue < currentX) && (difference > 100 && (time < 220))) {
                    /** ������һҳ*/
                    this.flipper.setInAnimation(AnimationUtils.loadAnimation(
                            this.getContext(), R.anim.push_right_in));
                    this.flipper.setOutAnimation(AnimationUtils.loadAnimation(
                            this.getContext(), R.anim.push_right_out));
                    flipper.showPrevious();
                }
                /** X�Ử���������100������ʱ��С��220ms,������X�����򻬶�*/
                if ((downXValue > currentX) && (difference > 100) && (time < 220)) {
                    /** ������һҳ*/
                    this.flipper.setInAnimation(AnimationUtils.loadAnimation(
                            this.getContext(), R.anim.push_left_in));
                    this.flipper.setOutAnimation(AnimationUtils.loadAnimation(
                            this.getContext(), R.anim.push_left_out));
                    flipper.showNext();
                }
                break;
            }
        }
        return consumed || isClickable();
	}

	private boolean moved(MotionEvent evt) {
		// TODO �Զ����ɵķ������
		return hasMoved || Math.abs(evt.getX() - lastTouchX) > 10.0 ||Math.abs(evt.getY() - lastTouchY)>10.0;
	}

}
