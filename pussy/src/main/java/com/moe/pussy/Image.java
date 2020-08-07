package com.moe.pussy;
import java.io.InputStream;
import android.graphics.Movie;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Paint;
import java.io.IOException;
import pl.droidsonroids.gif.GifDecoder;
import java.io.File;
import android.graphics.BitmapFactory;
import pl.droidsonroids.gif.InputSource;
import java.io.FileDescriptor;

public abstract class Image
{	
	public static Image parse(BitmapPool bp, String input)
	{
		return new ImageGif(bp, input);
	}
	public static Image parse(Bitmap bitmap)
	{
		if(bitmap==null)
			return null;
		return new ImageBitmap(bitmap);
	}

	public abstract int getWidth();
	public abstract int getHeight();
	public abstract long advance();
	public abstract boolean isGif();
	public abstract Bitmap getBitmap();
	public abstract void recycle();
	public static class ImageBitmap extends Image
	{
		private Bitmap bitmap;
		private boolean recycle;
		private String stack;
		public ImageBitmap(Bitmap bitmap)
		{
			this.bitmap = bitmap;
		}
		@Override
		public int getWidth()
		{
			return bitmap.getWidth();
		}

		@Override
		public int getHeight()
		{
			return bitmap.getHeight();
		}

		@Override
		public long advance()
		{
			return -1;
		}

		@Override
		public boolean isGif()
		{
			return false;
		}

		@Override
		public Bitmap getBitmap()
		{
			return bitmap;
		}

		@Override
		public void recycle()
		{
			StringBuilder sb=new StringBuilder();
			for(StackTraceElement e:Thread.currentThread().getStackTrace()){
				sb.append(e.toString());
			}
			stack=sb.toString();
			recycle=true;
		}




	}
	public static class ImageGif extends Image
	{
		private GifDecoder decoder;
		private Bitmap bitmap;
		private int frameCount;
		private long time;
		private boolean recycle;
		public ImageGif(BitmapPool bp, String input)
		{
			try
			{
				decoder = new GifDecoder(new InputSource.FileSource(input));
				frameCount = decoder.getNumberOfFrames();
				if(frameCount==1){
					//decoder.recycle();
				bitmap = BitmapFactory.decodeFile(input);
				decoder.recycle();
				}else
				{
					bitmap=bp.getBitmap(decoder.getWidth(),decoder.getHeight(),Bitmap.Config.ARGB_8888);
				}
			}
			catch (IOException e)
			{}
		}

		@Override
		public void recycle()
		{
			recycle=true;
		}

		
		@Override
		public int getWidth()
		{
			return decoder.getWidth();
		}

		@Override
		public int getHeight()
		{
			return decoder.getHeight();
		}

		@Override
		public long advance()
		{
			if (frameCount == 1){
				decoder.seekToTime(0,bitmap);
				return 0;
				}
			if(time==0)
				time=System.currentTimeMillis();
			int duration=(int)(System.currentTimeMillis()-time);
			if(duration>=decoder.getDuration()){
				time=System.currentTimeMillis();
				duration=0;
				}
			decoder.seekToTime(duration,bitmap);
			//decoder.seekToFrame(frame++, bitmap);
			//len = decoder.getFrameDuration(frame);
			return 33;
		}

		@Override
		public boolean isGif()
		{
			return decoder.isAnimated();
		}

		@Override
		public Bitmap getBitmap()
		{
			return bitmap;
		}



	}
}
