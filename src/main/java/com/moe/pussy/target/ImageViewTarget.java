package com.moe.pussy.target;
import com.moe.pussy.Target;
import com.moe.pussy.PussyDrawable;
import android.widget.ImageView;
import com.moe.pussy.Transformer;
import android.view.ViewTreeObserver;
import android.graphics.Bitmap;
import com.moe.pussy.DrawableAnimator;
import android.graphics.drawable.Drawable;
import com.moe.pussy.BitmapPool;
import java.util.ArrayList;
import com.moe.pussy.Listener;

public class ImageViewTarget extends ViewTarget
{
	public ImageViewTarget(ImageView view){
		super(view);
	}

	@Override
	public void onCancel()
	{
		if(getView()!=null)
		((ImageView)getView()).setImageDrawable(null);
	}


	
	
	@Override
	public void onSuccess(PussyDrawable pd)
	{
		if(pd!=null){
		pd.stop();
		if(getView()!=null)
		((ImageView)getView()).setImageDrawable(pd);
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
		((ImageView)getView()).setImageDrawable(d);
		Listener l=getListener();
		if(l!=null)l.onError(this,d);
		
	}

	@Override
	public void placeHolder(Drawable placeHolder)
	{
		if(getAnim()!=null)getAnim().stop();
		if(getView()!=null)
		((ImageView)getView()).setImageDrawable(placeHolder);
		Listener l=getListener();
		if(l!=null)l.onPlaceHolder(this,placeHolder);
		
	}
	
	
}
