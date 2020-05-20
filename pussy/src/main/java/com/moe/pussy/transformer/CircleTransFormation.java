package com.moe.pussy.transformer;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import com.moe.pussy.BitmapPool;
import com.moe.pussy.Transformer;
import android.graphics.Matrix;

public class CircleTransFormation implements Transformer
{
	private int border;
	public CircleTransFormation(int whiteBorderSize){
		this.border=whiteBorderSize;
	}
	@Override
	public Bitmap onTransformer(BitmapPool mBitmapPool, Bitmap bitmap, int w, int h)
	{
		if(bitmap==null)return null;
		int size=Math.min(bitmap.getWidth(),bitmap.getHeight());
		if(w>0)
			size=Math.min(size,w);
		if(h>0)
			size=Math.min(size,h);
			float radius=size/2f;
		Bitmap buff=mBitmapPool.getBitmap(size,size,Bitmap.Config.ARGB_8888);
		Paint paint = new Paint();
		Canvas canvas = new Canvas(buff);//创建同尺寸的画布
		//canvas.drawColor(0,PorterDuff.Mode.CLEAR);
		paint.setAntiAlias(true);//画笔抗锯齿
		paint.setDither(true);
		paint.setFilterBitmap(true);
		canvas.drawCircle(radius, radius, radius, paint);//画圆角矩形
		//
		paint.setFilterBitmap(true);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		Matrix matrix=new Matrix();
		float scale=Math.min(size/(float)bitmap.getWidth(),size/(float)bitmap.getHeight());
		matrix.setScale(scale,scale);
		canvas.drawBitmap(bitmap, matrix, paint);
		if(border>0){
			paint.setXfermode(null);
			paint.setColor(0xffffffff);
			paint.setStrokeWidth(border);
			paint.setStyle(Paint.Style.STROKE);
			border/=2;
			canvas.drawArc(border,border,canvas.getWidth()-border,canvas.getHeight()-border,0,360,false,paint);
		}
		if(bitmap!=buff)
			mBitmapPool.recycle(bitmap);//释放
		return buff;
	}

	@Override
	public String getKey()
	{
		return "cicle";
	}
	
}
