package com.moe.pussy.transformer;
import android.graphics.*;

import android.view.Gravity;
import com.moe.pussy.Transformer;
import com.moe.pussy.BitmapPool;

public class CropTransformer implements Transformer
{
	private int gravity;
	public CropTransformer(int gravity)
	{
		this.gravity = gravity;
	}

	@Override
	public String getKey()
	{
		return "pussy&Crop".concat(String.valueOf(gravity));
	}


	@Override
	public Bitmap onTransformer(BitmapPool bp, Bitmap source, int w, int h)
	{
		if (source == null)throw new NullPointerException("source bitmap is null");
		if(w==0&&h==0)
			return source;
		float scale=1;
		int displayWidth=w,displayHeight=h,image_width=source.getWidth(),image_height=source.getHeight();
		float dx=0,dy=0;
		if (w == 0)
		{
			//用高度计算
			scale = (float) h / (float) image_height;
			displayWidth = (int)(image_width* scale);
		}
		else if (h == 0)
		{
			//用宽度计算
			scale = (float) w / (float) image_width;
			displayHeight = (int) (image_height * scale);
		}
		else
		{
			if (image_width * h > w * image_height) {
				scale = (float) h / (float) image_height;
				} else {
				scale = (float) w / (float) image_width;
				}
			
		}
		if(Gravity.isVertical(gravity)){
			dx = (w - image_width * scale) * 0.5f;
		}
		if(Gravity.isHorizontal(gravity)){
			dy = (h - image_height * scale) * 0.5f;
			}
		if ((gravity & Gravity.START) == Gravity.START || (gravity & Gravity.LEFT) == Gravity.LEFT)
		{
			dx=0;
		}
		else if ((gravity & Gravity.RIGHT) == Gravity.RIGHT || (gravity & Gravity.END) == Gravity.END)
		{
			dx=displayWidth-image_width*scale;
		}
		if ((gravity & Gravity.TOP) == Gravity.TOP)
		{
			dy=0;
		}
		else if ((gravity & Gravity.BOTTOM) == Gravity.BOTTOM)
		{
			dy=displayHeight-image_height*scale;
		}
		
		if (displayWidth == source.getWidth() && displayHeight == source.getHeight())
			return source;
		Bitmap buff=bp.getBitmap(displayWidth, displayHeight, Bitmap.Config.ARGB_8888);
		Canvas canvas=new Canvas(buff);
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG));
		Matrix m=new Matrix();
		m.setScale(scale, scale);
        m.postTranslate((int) (dx + 0.5f), (int) (dy + 0.5f));
		//canvas.scale(scale,scale);
		canvas.drawBitmap(source, m, null);
		//Bitmap buff=source.createBitmap(source,rect.left,rect.top,rect.width(),rect.height());
		if (buff != source)
			bp.recycle(source);
		//source.recycle();
		return buff;
	}





}
