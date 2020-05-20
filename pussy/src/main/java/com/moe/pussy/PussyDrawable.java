package com.moe.pussy;
import android.graphics.drawable.Drawable;
import android.graphics.ColorFilter;
import android.graphics.Canvas;
import android.graphics.Bitmap;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable.Callback;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Paint;
import java.lang.ref.WeakReference;
import android.graphics.PorterDuff;
import android.os.SystemClock;
import android.graphics.Matrix;
import android.graphics.Rect;

public class PussyDrawable extends Drawable implements Animatable
{
	public WeakReference<Image> bitmap;
	//private WeakReference<Pussy.Refresh> refresh;
	private DrawableAnimator da;
	//private WeakReference<Target> t;
	public PussyDrawable(Image bitmap,DrawableAnimator da)
	{
		if(bitmap==null)throw new NullPointerException("bjtmal 为空");
		this.bitmap =new WeakReference<Image>(bitmap);
		this.da=da;
		if(da!=null)
			da.setCallback(this);
		//this.t=new WeakReference<Target>(t);
		//refresh=new WeakReference<>(r);
	}
	
	/*public void setAnimator(DrawableAnimator da)
	{
		this.da = da;
		if(da!=null)
		da.setCallback(this);
	}*/

	@Override
	public void start()
	{
		if(da!=null)
			da.start();
	}

	@Override
	public void stop()
	{
		if(da!=null)
			da.stop();
	}

	@Override
	public boolean isRunning()
	{
		if(da!=null)
			return da.isRunning();
		return false;
	}

	/*public Pussy.Refresh getRefresh()
	{
		return refresh.get();
	}*/
	@Override
	public void draw(Canvas p1)
	{
		p1.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.DITHER_FLAG|Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
		Image image=this.bitmap.get();
		if (image != null){
			/*if(image.isGif()){
				float scale=Math.max(p1.getWidth()/(float)image.getWidth(),p1.getHeight()/(float)image.getHeight());
				Matrix m=p1.getMatrix();
				m.setScale(scale,scale);
				m.postTranslate((p1.getWidth()-(image.getWidth()*scale))/2f,(p1.getHeight()-image.getHeight()*scale)/2f);
				p1.setMatrix(m);
			}*/
			Bitmap bitmap=image.getBitmap();
			Rect bounds=getBounds();
			Matrix m=new Matrix();
			float scale=Math.max(bounds.width()/(float)image.getWidth(),bounds.height()/(float)image.getHeight());
			m.setScale(scale,scale);
			m.postTranslate((bounds.width()-(image.getWidth()*scale))/2f,(bounds.height()-image.getHeight()*scale)/2f);
			if (da != null)
					da.draw(p1,m, bitmap);
				else
					p1.drawBitmap(bitmap, m, null);
			if(image.isGif()&&isVisible())
				scheduleSelf(Updater,SystemClock.uptimeMillis()+33);
			}else{
				//throw new NullPointerException("图片为空");
			}
	}

	@Override
	public void setAlpha(int p1)
	{
	}

	@Override
	public void setColorFilter(ColorFilter p1)
	{
	}

	@Override
	public boolean setVisible(boolean visible, boolean restart)
	{
		if(!visible)
			unscheduleSelf(Updater);
		return super.setVisible(visible, restart);
	}

	@Override
	public int getOpacity()
	{
		return 0;
	}
	/*public void recycle()
	{
		Bitmap bitmap=this.bitmap.get();
		if (bitmap != null)
			synchronized (bitmap)
			{
				bitmap.recycle();}
		recycle = true;
	}*/

	@Override
	public int getIntrinsicWidth()
	{
		Image bitmap=this.bitmap.get();
		if (bitmap != null)
			return bitmap.getWidth();
		return super.getIntrinsicWidth();
	}

	@Override
	public int getIntrinsicHeight()
	{
		Image bitmap=this.bitmap.get();
		if (bitmap != null)
			return bitmap.getHeight();
		return super.getIntrinsicHeight();
	}
	private Runnable Updater =new Runnable(){

		@Override
		public void run()
		{
			invalidateSelf();
		}
	};

}
