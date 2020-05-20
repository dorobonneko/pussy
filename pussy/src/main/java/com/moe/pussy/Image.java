package com.moe.pussy;
import java.io.InputStream;
import android.graphics.Movie;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Paint;
import java.io.IOException;

public class Image
{
	private Movie mMovie;
	private long time;
	private Bitmap mBitmap;
	private BitmapPool bp;
	private Canvas canvas;
	public Image(BitmapPool bp, InputStream input)
	{
		this.bp = bp;
		mMovie = Movie.decodeStream(input);
		try
		{
			input.close();
		}
		catch (IOException e)
		{
			throw new RuntimeException("gif decode error");
		}
		mBitmap = bp.getBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
		canvas = new Canvas(mBitmap);
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.DITHER_FLAG | Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
	}
	public Image(BitmapPool bp, Bitmap mBitmap)
	{
		this.bp = bp;
		this.mBitmap = mBitmap;
	}

	public void setBitmap(Bitmap sourceBitmap)
	{
		mBitmap=sourceBitmap;
	}
	public int getWidth()
	{
		if (mBitmap != null)
			return mBitmap.getWidth();
		return mMovie.width();
	}
	public int getHeight()
	{
		if (mBitmap != null)
			return mBitmap.getHeight();
		return mMovie.height();
	}
	public Bitmap source()
	{
		return mBitmap;
	}
	public Bitmap getBitmap()
	{
		if (mMovie != null)
		{
			if (time == 0)
				time = System.currentTimeMillis();
			int disc=(int)(System.currentTimeMillis() - time);
			if (disc > mMovie.duration())
			{
				disc = 0;
				time = System.currentTimeMillis();
			}
			mMovie.setTime(disc);
			mBitmap.eraseColor(0);
			mMovie.draw(canvas, 0, 0);
		}
		return mBitmap;
	}
	public boolean isGif(){
		return mMovie!=null;
	}
}
