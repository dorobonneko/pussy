package com.moe.pussy.target;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewTreeObserver;
import com.moe.pussy.DrawableAnimator;
import com.moe.pussy.Pussy;
import com.moe.pussy.Target;
import com.moe.pussy.Transformer;
import com.moe.pussy.ContentBuilder;
import java.lang.ref.WeakReference;
import com.moe.pussy.Listener;
import android.graphics.Rect;
import java.io.File;
import java.lang.ref.SoftReference;

public abstract class ViewTarget  implements Target,View.OnAttachStateChangeListener
{
	private  ContentBuilder content;
	private SoftReference<View> view;
	public ViewTarget(View view)
	{
		this.view =new SoftReference<View>( view);
		view.addOnAttachStateChangeListener(this);
	}

	@Override
	public void onViewAttachedToWindow(View p1)
	{
		if(content==null)return;
		if(content.getRefresh().isCancel())
		content.getRefresh().refresh(this);
	}

	@Override
	public void onViewDetachedFromWindow(View p1)
	{
		boolean v=p1.getGlobalVisibleRect(new Rect());
		if(!v&&content!=null)
		content.getRefresh().cancel();
		//getView().removeOnAttachStateChangeListener(this);
	}

	@Override
	public Listener getListener()
	{
		return content.getListener();
	}




	@Override
	public void onAttachContent(ContentBuilder c)
	{
		content=c;
	}

	
	public View getView()
	{
		return view.get();
	}
	@Override
	public final void onResourceReady(String cache)
	{
		if(getView()!=null){
		if (getView().getMeasuredWidth()==0&&getView().getMeasuredHeight()==0)
		{
			getView().post(new Runnable(){

					@Override
					public void run()
					{
						onSizeReady(getView().getMeasuredWidth(),getView().getMeasuredHeight());
					}
					
				
			});
		}
		else
		{
			onSizeReady(getView().getMeasuredWidth(),getView().getMeasuredHeight());
		}}
	}

	/*@Override
	public boolean onPreDraw()
	{
		final View v=getView();
		if(v==null)return false;
		v.getViewTreeObserver().removeOnPreDrawListener(this);
		onSizeReady(v.getWidth(),v.getHeight());
		return false;
	}*/

	protected DrawableAnimator getAnim(){
		if(content!=null)
			return content.getAnim();
		return null;
	}
	protected Pussy.Refresh getRefresh(){
		return content.getRefresh();
	}

	@Override
	public ContentBuilder getContent()
	{
		return content;
	}

	@Override
	public final void onSizeReady(int w, int h)
	{
		
		content.onSizeReady(w,h);
	}


	
}
