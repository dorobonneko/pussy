package com.moe.pussy.transformer;
import com.moe.pussy.Transformer;
import com.moe.pussy.BitmapPool;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.EmbossMaskFilter;

public class EmbossTransFormer implements Transformer
{

	@Override
	public Bitmap onTransformer(BitmapPool mBitmapPool, Bitmap bitmap, int w, int h)
	{
		Canvas c=new Canvas(bitmap);
		Paint p=new Paint();
		p.setMaskFilter(new EmbossMaskFilter(new float[]{0,1,3},0.1f,60,150));
		c.drawBitmap(bitmap,0,0,p);
		return bitmap;
	}

	@Override
	public String getKey()
	{
		return "emboss";
	}
	
}
