package com.moe.pussy.transformer;

import android.graphics.*;
import com.moe.pussy.Transformer;
import com.moe.pussy.BitmapPool;

public class RoundTransformer implements Transformer
{
	private int radius;//圆角值

	public RoundTransformer(int radius)
	{
		this.radius = radius;
	}

	@Override
	public String getKey()
	{
		// TODO: Implement this method
		return "pussy&Round".concat(String.valueOf(radius));
	}

	

	@Override
	public Bitmap onTransformer(BitmapPool bp,Bitmap source, int w, int h)
	{
		int width = source.getWidth();
		int height = source.getHeight();
		//画板
		Bitmap bitmap = BitmapPool.getBitmap(width, height,Bitmap.Config.ARGB_8888);
		Paint paint = new Paint();
		Canvas canvas = new Canvas(bitmap);//创建同尺寸的画布
		canvas.drawColor(0,PorterDuff.Mode.CLEAR);
		paint.setAntiAlias(true);//画笔抗锯齿
		paint.setDither(true);
		//paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
		//画圆角背景
		RectF rectF = new RectF(new Rect(0, 0, width, height));//赋值
		canvas.drawRoundRect(rectF, radius, radius, paint);//画圆角矩形
		//
		paint.setFilterBitmap(true);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(source, 0, 0, paint);
		if(bitmap!=source)
		bp.recycle(source);//释放

		return bitmap;
	}
}
