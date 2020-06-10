package com.moe.pussy.utils;
import android.graphics.drawable.Drawable;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import com.moe.pussy.NetListener;
import com.moe.pussy.Request;
import android.animation.ValueAnimator;
import android.graphics.Paint;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.RectF;

public class ProgressDrawable extends Drawable implements NetListener
{
	public static final int PROGRESS_LOOP=0;
	private int progress=PROGRESS_LOOP,max=100,degress;
	private ValueAnimator anime;
	private Paint paint;
	public ProgressDrawable()
	{
		anime = new ValueAnimator();
		anime.addUpdateListener(new Update());
		paint = new Paint();
		paint.setColor(0xffffffff);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(2);
	}
	public ProgressDrawable(Context context)
	{
		this();
		paint.setColor(StyleUtil.getColor(context, android.R.attr.colorControlNormal));
	}
	public void setProgress(int progress)
	{
		progress = Math.min(Math.max(progress, PROGRESS_LOOP), max);
		anime.cancel();
		if (this.progress != progress)
		{
			if (progress == PROGRESS_LOOP)
			{
				this.progress = PROGRESS_LOOP;
				degress=0;
				invalidateSelf();
			}
			else
			{
				anime.setIntValues(this.progress, progress);
				anime.setDuration(Math.abs(this.progress-progress)*16);
				anime.start();
			}
		}
	}

	@Override
	public void draw(Canvas canvas)
	{
		Rect bounds=getBounds();
		if (anime.isRunning())
		{
			drawProgress(canvas,progress);
		}
		else
		{
			if (progress == PROGRESS_LOOP)
			{
				RectF bound=new RectF(bounds);
				float halfStroke=paint.getStrokeWidth()/2;
				bound.left+=halfStroke;
				bound.top+=halfStroke;
				bound.right-=halfStroke;
				bound.bottom-=halfStroke;
				canvas.drawArc(bound, -90 + degress, 30, false, paint);
				degress++;
				invalidateSelf();
			}else{
				drawProgress(canvas,progress);
			}
		}
	}
	protected void drawProgress(Canvas canvas,int progress){
		Rect bounds=getBounds();
		RectF bound=new RectF(bounds);
		float halfStroke=paint.getStrokeWidth()/2;
		bound.left+=halfStroke;
		bound.top+=halfStroke;
		bound.right-=halfStroke;
		bound.bottom-=halfStroke;
		canvas.drawArc(bound,-90, progress/(float)max*360, false, paint);
		
	}
	@Override
	public void setAlpha(int p1)
	{
		paint.setAlpha(p1);
	}

	@Override
	public void setColorFilter(ColorFilter p1)
	{
		paint.setColorFilter(p1);
	}

	@Override
	public int getOpacity()
	{
		return PixelFormat.TRANSLUCENT;
	}

	@Override
	public int getIntrinsicWidth()
	{
		return 48;
	}

	@Override
	public int getIntrinsicHeight()
	{
		return 48;
	}

	@Override
	public void onProgress(long current, long length, Request r)
	{
		if (length == -1)
			setProgress(PROGRESS_LOOP);
		else
			setProgress((int)(current / (float)length * 100));
	}

	@Override
	public void onStart(Request r)
	{
	}

	@Override
	public void onEnd(Request r, Throwable e)
	{
	}
	class Update implements ValueAnimator.AnimatorUpdateListener
	{

		@Override
		public void onAnimationUpdate(ValueAnimator p1)
		{
			progress = p1.getAnimatedValue();
			invalidateSelf();
		}
	}
}
