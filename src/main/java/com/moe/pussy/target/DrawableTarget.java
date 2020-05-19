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

public class DrawableTarget extends LevelListDrawable implements Target
{
	private ContentBuilder content;
	private int level;
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
		setLevel(0);
		level = 0;
	}



	@Override
	public void placeHolder(Drawable placeHolder)
	{
		addLevel(1, 1, placeHolder);
		setLevel(1);
		level = 1;
		Listener l=getListener();
		if (l != null)l.onPlaceHolder(this,placeHolder);
	}

	@Override
	public boolean setVisible(boolean visible, boolean restart)
	{
		boolean f= super.setVisible(visible, restart);
		if (getLevel() == 0)
		{
			setLevel(level);
		}
		/*if(restart)
		 {
		 PussyDrawable pd=(PussyDrawable) getCurrent();
		 if(pd!=null)
		 pd.start();
		 }*/
		return f;
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
	public void onResourceReady(String cache)
	{
		Rect bounds= getBounds();
		//if (getCallback()!=null|| bounds.width() > 0 || bounds.height() > 0)
			onSizeReady(bounds.width(), bounds.height());
	}

	@Override
	public void onSuccess(PussyDrawable pd)
	{
		level = 2;
		if (pd != null)
		{
			pd.stop();
			DrawableAnimator anim= content.getAnim();
			if (anim != null)
				anim.stop();
			//pd.setAnimator(anim);
			addLevel(2, 2, pd);
			setLevel(2);
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
		level = 3;
		DrawableAnimator anim= content.getAnim();
		if (anim != null)
			anim.stop();
		addLevel(3, 3, d);
		setLevel(3);
		Listener l=getListener();
		if(l!=null)l.onError(this,d);
		
	}

	@Override
	public void onAttachContent(ContentBuilder c)
	{
		content = c;
	}

	
}
