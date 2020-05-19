package com.moe.pussy.target;
import android.view.View;
import com.moe.pussy.PussyDrawable;
import android.graphics.drawable.Drawable;
import com.moe.pussy.Listener;

public class ViewBackgroundTarget extends ViewTarget
{
	public ViewBackgroundTarget(View v){
		super(v);
	}

	@Override
	public void onCancel()
	{
		if(getView()!=null)
		getView().setBackground(null);
	}


	@Override
	public void onSuccess(PussyDrawable pd)
	{
		if(pd!=null){
			pd.stop();
			if(getView()!=null)
			getView().setBackground(pd);
			//pd.setAnimator(getAnim());
			pd.start();
			Listener l=getListener();
			if(l!=null)l.onSuccess(this,pd);
			
			}else{
			error(null,null);
		}
	}

	@Override
	public void error(Throwable e,Drawable d)
	{
		if(getAnim()!=null)getAnim().stop();
		if(getView()!=null)
		getView().setBackground(d);
		Listener l=getListener();
		if(l!=null)l.onError(this,d);
		
	}

	@Override
	public void placeHolder(Drawable placeHolder)
	{
		if(getAnim()!=null)getAnim().stop();
		if(getView()!=null)
		getView().setBackground(placeHolder);
		Listener l=getListener();
		if(l!=null)l.onPlaceHolder(this,placeHolder);
		
	}


	
}
