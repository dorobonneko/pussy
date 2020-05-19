package com.moe.pussy.transformer;
import com.moe.pussy.Transformer;
import com.moe.pussy.BitmapPool;
import android.graphics.Bitmap;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Canvas;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Paint;
import android.graphics.Matrix;

public class PolygonTransFormer implements Transformer
{
	private int polygon;
	private float degress;
	public PolygonTransFormer(int polygonSize,float degress){
		polygon=Math.min(Math.max(3,polygonSize),360);
		this.degress=degress;
	}

	@Override
	public Bitmap onTransformer(BitmapPool mBitmapPool, Bitmap bitmap, int w, int h)
	{
		double degress=2d/polygon*Math.PI;
		int size=Math.min(bitmap.getWidth(),bitmap.getHeight());
		if(w>0)
			size=Math.min(size,w);
		if(h>0)
			size=Math.min(size,h);
		float scale=Math.min(size/(float)bitmap.getWidth(),size/(float)bitmap.getHeight());
		
		float radius=size/2f;
		float minx=radius;
		Path lines=new Path();
		for(int i=0;i<polygon;i++){
			double value=degress*i;
			float x=radius+(float)(radius*Math.cos(value));
			float y=radius+(float)(radius*Math.sin(value));
			minx=Math.min(x,minx);
			if(i==0)
			lines.moveTo(x,y);
			else
			lines.lineTo(x,y);
		}
		lines.close();
		Bitmap buff=mBitmapPool.getBitmap(size,size,Bitmap.Config.ARGB_8888);
		Canvas canvas=new Canvas(buff);
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0,Paint.ANTI_ALIAS_FLAG|Paint.DITHER_FLAG|Paint.FILTER_BITMAP_FLAG));
		canvas.rotate(this.degress,radius,radius);
		canvas.translate(-minx/2,0);
		canvas.clipPath(lines);
		canvas.rotate(-this.degress,radius,radius);
		//canvas.drawColor(0xffffffff);
		Matrix m=new Matrix();
		m.setScale(scale,scale);
		m.postTranslate((bitmap.getWidth()*scale-size)/2f,(bitmap.getHeight()*scale-size)/2f);
		//canvas.drawBitmap(bitmap,0,0,null);
		canvas.drawBitmap(bitmap,m,null);
		mBitmapPool.recycle(bitmap);
		return buff;
	}

	@Override
	public String getKey()
	{
		return "polygon"+polygon+"degress"+degress;
	}


	
}
