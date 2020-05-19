package com.moe.pussy;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.animation.ValueAnimator;
import android.view.animation.LinearInterpolator;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.PorterDuffXfermode;
import android.graphics.PorterDuff;
import android.graphics.Matrix;

public class Anim
{
	public static DrawableAnimator fade(final long time){
		return new DrawableAnimator(){
			private ValueAnimator va=new ValueAnimator();
			private Paint paint=new Paint();
			private Drawable drawable;
			private Drawable.Callback callback;
			{
				va.setDuration(time);
				va.setInterpolator(new LinearInterpolator());
				va.setIntValues(0,255);
				va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){

						@Override
						public void onAnimationUpdate(ValueAnimator p1)
						{
							invalidate();
							/*if(drawable!=null){
								if(callback==null)
									callback=drawable.getCallback();
								if(callback==drawable.getCallback())
								callback.invalidateDrawable(drawable);
								}*/
						}
					});
			}
			@Override
			public void setCallback(Drawable drawable){
				this.drawable=drawable;
				callback=drawable.getCallback();
			}
			@Override
			public void start()
			{
				va.start();
			}

			@Override
			public void stop()
			{
				va.cancel();
				invalidate();
				/*if(drawable!=null){
					if(callback==drawable.getCallback())
						callback.invalidateDrawable(drawable);
				}*/
			}

			@Override
			public boolean isRunning()
			{
				return va.isRunning();
			}

			@Override
			public void draw(Canvas canvas,Matrix m, Bitmap bitmap)
			{
				paint.setAlpha(va.isRunning()?va.getAnimatedValue():255);
				canvas.drawBitmap(bitmap,m,paint);
			}
			private void invalidate(){
				if(drawable!=null){
					Drawable.Callback dc=drawable.getCallback();
					if(dc!=null)dc.invalidateDrawable(drawable);
					}
			}
		};
	}
	public static DrawableAnimator cicle(final long time){
		return new DrawableAnimator(){
			private ValueAnimator va=new ValueAnimator();
			private Paint paint=new Paint();
			private Drawable drawable;
			private Drawable.Callback callback;
			{
				va.setDuration(time);
				va.setInterpolator(new LinearInterpolator());
				va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){

						@Override
						public void onAnimationUpdate(ValueAnimator p1)
						{
							invalidate();
							/*if(drawable!=null){
								if(callback==drawable.getCallback())
									callback.invalidateDrawable(drawable);
							}*/
						}
					});
			}
			@Override
			public void setCallback(Drawable drawable){
				this.drawable=drawable;
				callback=drawable.getCallback();
				va.setFloatValues(0,(float)Math.sqrt(Math.pow(drawable.getIntrinsicWidth(),2)+Math.pow(drawable.getIntrinsicHeight(),2)));
			}
			@Override
			public void start()
			{
				va.start();
			}

			@Override
			public void stop()
			{
				va.cancel();
				/*if(drawable!=null){
					if(callback==drawable.getCallback())
						callback.invalidateDrawable(drawable);
				}*/
				invalidate();
			}

			@Override
			public boolean isRunning()
			{
				return va.isRunning();
			}

			@Override
			public void draw(Canvas canvas, Matrix m,Bitmap bitmap)
			{
				canvas.drawCircle(bitmap.getWidth()/2,bitmap.getHeight()/2,va.getAnimatedValue(),paint);

				paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
				canvas.drawBitmap(bitmap,m,paint);
				paint.setXfermode(null);
			}
			private void invalidate(){
				if(drawable!=null){
					Drawable.Callback dc=drawable.getCallback();
					if(dc!=null)dc.invalidateDrawable(drawable);
				}
			}
		};
	}
}
