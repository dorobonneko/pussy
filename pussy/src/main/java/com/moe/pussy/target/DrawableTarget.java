package com.moe.pussy.target;
import android.graphics.drawable.LevelListDrawable;
import com.moe.pussy.Target;
import com.moe.pussy.ContentBuilder;
import android.graphics.Bitmap;
import com.moe.pussy.PussyDrawable;
import com.moe.pussy.Transformer;
import android.graphics.drawable.Drawable;
import android.graphics.Rect;
import com.moe.pussy.Anim;
import android.animation.Animator;
import com.moe.pussy.DrawableAnimator;
import com.moe.pussy.Listener;
import java.io.File;
import android.graphics.drawable.Drawable.Callback;
import com.moe.pussy.Request;
import android.graphics.ColorFilter;
import android.graphics.Canvas;

public class DrawableTarget extends Drawable implements Target,Drawable.Callback
{
	private Drawable cache;
	private ContentBuilder content;
	@Override
	public void onSizeReady(int w, int h)
	{
		content.onSizeReady(w, h);
	}

	@Override
	public Listener getListener()
	{
		return content.getListener();
	}

	@Override
	public void onCancel()
	{
		//setLevel(0);
		//level = 0;
	}

	@Override
	public int getOpacity()
	{
		if(cache!=null)
			return cache.getOpacity();
		return 0;
	}

	@Override
	public void setColorFilter(ColorFilter p1)
	{
		if(cache!=null)
			cache.setColorFilter(p1);
	}

	@Override
	public void setAlpha(int p1)
	{
		if(cache!=null)
			cache.setAlpha(p1);
	}

	@Override
	public void draw(Canvas p1)
	{
		if(cache!=null){
			//cache.setBounds(getBounds());
			cache.draw(p1);
		}
	}

	@Override
	public void setBounds(int left, int top, int right, int bottom)
	{
		super.setBounds(left, top, right, bottom);
		if(cache!=null)
			cache.setBounds(left,top,right,bottom);
	}

	@Override
	public void setBounds(Rect bounds)
	{
		super.setBounds(bounds);
		if(cache!=null)
			cache.setBounds(bounds);
	}




	@Override
	public void placeHolder(Drawable placeHolder)
	{
		if(cache!=null)
			cache.setCallback(null);
		this.cache=placeHolder;
		if(placeHolder!=null){
			placeHolder.setBounds(getBounds());
			placeHolder.setCallback(this);
			}
		Listener l=getListener();
		if (l != null)l.onPlaceHolder(this,placeHolder);
	}

	
	/*@Override
	public void setBounds(int left, int top, int right, int bottom)
	{
		super.setBounds(left, top, right, bottom);
		onSizeReady(right - left, bottom - top);
	}*/

	
	@Override
	public ContentBuilder getContent()
	{
		return content;
	}
	@Override
	public void onResourceReady(String cache,Request request)
	{
		Rect bounds= getBounds();
		//if (getCallback()!=null|| bounds.width() > 0 || bounds.height() > 0)
			onSizeReady(bounds.width(), bounds.height());
	}

	@Override
	public void onSuccess(PussyDrawable pd)
	{
		if(cache!=null)
			cache.setCallback(null);
		if (pd != null)
		{
			pd.stop();
			DrawableAnimator anim= content.getAnim();
			if (anim != null)
				anim.stop();
			//pd.setAnimator(anim);
			this.cache=pd;
			pd.setBounds(getBounds());
			pd.setCallback(this);
			Listener l=getListener();
			if (l != null)l.onSuccess(this,pd);
			//setBounds(0,0,pd.getIntrinsicWidth(),pd.getIntrinsicHeight());
			invalidateSelf();
			
			//pd.start();
		}
		else
		{
			error(null, null);
		}
	}

	@Override
	public void error(Throwable e, Drawable d)
	{
		if(cache!=null)
			cache.setCallback(null);
		DrawableAnimator anim= content.getAnim();
		if (anim != null)
			anim.stop();
		this.cache=d;
		if(d!=null){
			d.setBounds(getBounds());
			d.setCallback(this);
			}
		Listener l=getListener();
		if(l!=null)l.onError(this,d,e);
		
	}

	@Override
	public void onAttachContent(ContentBuilder c)
	{
		content = c;
	}

	@Override
	public void scheduleDrawable(Drawable who, Runnable what, long when)
	{
		if(getCallback()!=null)
		getCallback().scheduleDrawable(who, what, when);
	}

	@Override
	public void invalidateDrawable(Drawable who)
	{
		if(getCallback()!=null)
		getCallback().invalidateDrawable(who);
	}

	@Override
	public void unscheduleDrawable(Drawable who, Runnable what)
	{
		if(getCallback()!=null)
		getCallback().unscheduleDrawable(who, what);
	}

	
}
