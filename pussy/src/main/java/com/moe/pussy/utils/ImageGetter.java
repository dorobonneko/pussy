package com.moe.pussy.utils;

import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.Gravity;
import android.widget.TextView;
import com.moe.pussy.Listener;
import com.moe.pussy.Pussy;
import com.moe.pussy.Target;
import com.moe.pussy.transformer.CropTransformer;
import java.util.Map;
import java.util.HashMap;
import android.os.SystemClock;
import android.widget.TextView.BufferType;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Paint;

public class ImageGetter implements Html.ImageGetter,Drawable.Callback
{
	private TextView tv;
	private int width;
	private Map<String,String> header;
	private String host;
	private Map<String,Drawable> cache=new HashMap<>();
	public ImageGetter(TextView tv)
	{
		this(tv, 0);
	}
	public ImageGetter(TextView tv, String host)
	{
		this(tv, 0, host, null);
	}
	public ImageGetter(TextView tv, int width)
	{
		this(tv, width, null, null);
	}
	public ImageGetter(TextView tv, int width, String host, Map<String,String> header)
	{
		this.tv = tv;
		this.width = width;
		this.header = header;
		this.host = host;
	}
	@Override
	public android.graphics.drawable.Drawable getDrawable(String p1)
	{
		if (p1.startsWith("/"))
			p1 = host + p1;
		else if(p1.startsWith("http"))
		{}
		else
			p1=host+"/"+p1;
		Drawable d=cache.get(p1);
		if (d == null)
		{
			final Drawable place=Pussy.$(tv.getContext()).load(p1).header(header).execute().transformer(new CropTransformer(Gravity.CENTER_HORIZONTAL)).listener(new Listener(){

					@Override
					public void onPlaceHolder(Target t, Drawable d)
					{
					}

					@Override
					public void onSuccess(final Target t, final Drawable d)
					{
						tv.post(new Runnable(){

								@Override
								public void run()
								{
									int maxWidth=ImageGetter.this.width == 0 ?(tv.getWidth() - tv.getPaddingStart() - tv.getPaddingEnd()): (ImageGetter.this.width - tv.getPaddingStart() - tv.getPaddingEnd());
									float height=d.getIntrinsicHeight();
									float width=d.getIntrinsicWidth();
									if (width > maxWidth){
										height=(int)(maxWidth / (float)d.getIntrinsicWidth() * d.getIntrinsicHeight());
										width=maxWidth;
									}else if(d.getIntrinsicHeight()<tv.getLineHeight()+tv.getLineSpacingExtra()){
										height=tv.getLineHeight()+tv.getLineSpacingExtra();
										width=height/d.getIntrinsicHeight()*d.getIntrinsicWidth();
										}
									((Drawable)t).setBounds(0,0,(int)width,(int)height);
									//tv.setText(tv.getText());
									tv.post(new Runnable(){

											@Override
											public void run()
											{
												tv.setText(tv.getText());
											}
										});
									//int color=tv.getLayout().getPaint().getColor();
								}
							});

					}

					@Override
					public void onError(Target t, Drawable d, Throwable e)
					{
					}
				}).intoPlaceHolder();
			place.setCallback(this);
			cache.put(p1, place);
			return place;
		}
		else
		{}
		return d;
	}

	@Override
	public void scheduleDrawable(Drawable p1, Runnable p2, long p3)
	{
		//tv.postInvalidateOnAnimation();
		tv.getHandler().postDelayed(p2,p3);
		//tv.scheduleDrawable(p1, p2, p3);
	}

	@Override
	public void invalidateDrawable(Drawable p1)
	{
		//tv.setText(tv.getText());
		tv.postInvalidateOnAnimation();
	}

	@Override
	public void unscheduleDrawable(Drawable p1, Runnable p2)
	{
		tv.getHandler().removeCallbacks(p2);
		//tv.unscheduleDrawable(p1, p2);
	}



}
