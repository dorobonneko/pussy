package com.moe.pussy;
import java.util.Map;
import java.util.HashMap;
import android.graphics.Bitmap;
import java.util.ArrayList;
import android.graphics.Canvas;
import java.util.List;
import android.os.Build;
import android.util.ArrayMap;
import android.util.SparseArray;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Collections;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Set;
import java.util.HashSet;
import java.util.Stack;
import java.util.Vector;
import android.graphics.Color;
import java.util.Collection;
import java.util.function.Predicate;

public class BitmapPool extends LinkedHashMap<Integer,Stack<Bitmap>>
{
 long maxSize,currentSize;
	private Lock lock=new ReentrantLock();
	public BitmapPool(long maxSize)
	{
		this.maxSize = maxSize;
	}

	public Bitmap getBitmap(int w, int h, Bitmap.Config config)
	{
		lock.lock();
		try
		{
			if(w<=0||h<=0)
				return null;
			int size=getBitmapByteSize(w, h, config);
			Stack<Bitmap> list=remove(size);
			if (list != null && !list.isEmpty())
			{
				put(size, list);
				Bitmap bitmap=list.pop();
				bitmap.eraseColor(Color.TRANSPARENT);
				bitmap.reconfigure(w, h, config);
				currentSize -= size;
				return bitmap;
			}
			return Bitmap.createBitmap(w, h, config);
		}
		finally
		{
			lock.unlock();
		}
	}
	public void recycle(Bitmap bitmap)
	{
		//bitmap.recycle();
		if (bitmap == null || bitmap.isRecycled())return;
		if (!bitmap.isMutable())
		{
			bitmap.recycle();
			return;
		}

		lock.lock();
		try
		{
			int size=getBitmapByteSize(bitmap);
			Stack<Bitmap> list=get(size);
			if (list == null)
			{
				put(size, list = new Stack<>());
			}
			if (list.search(bitmap) == -1)
			{
				list.push(bitmap);
				currentSize += size;
				trimToSize(maxSize);
			}
		}
		finally
		{
			lock.unlock();
		}
	}
	public void trimToSize(final long size)
	{
		if (currentSize > size)
		{
            
			Iterator<Map.Entry<Integer,Stack<Bitmap>>> iterator_map=entrySet().iterator();
			while (iterator_map.hasNext())
			{
				Map.Entry<Integer,Stack<Bitmap>> list=iterator_map.next();
				list.getValue().removeIf(new Predicate<Bitmap>(){

                        @Override
                        public boolean test(Bitmap bitmap) {
                            if (currentSize < size)return false;
                            
                            currentSize -= getBitmapByteSize(bitmap);
                            synchronized (bitmap)
                            {
                                if (!bitmap.isRecycled())
                                    bitmap.recycle();
                            }
                            return true;
                        }
                    });
                
				//if (list.getValue().isEmpty())
					//iterator_map.remove();
                if (currentSize < size)break;
                
			}
		}
	}
	/*public boolean isRecycled(Bitmap bitmap)
	 {
	 List<Bitmap> list=map.get(bitmap.getWidth() * bitmap.getHeight() + bitmap.getConfig().name());
	 if (list == null)return false;
	 return list.contains(bitmap);
	 }*/
	public static int getBitmapByteSize(Bitmap bitmap)
	{
		// The return value of getAllocationByteCount silently changes for recycled bitmaps from the
		// internal buffer size to row bytes * height. To avoid random inconsistencies in caches, we
		// instead assert here.
		if (bitmap.isRecycled())
		{
			throw new IllegalStateException(
				"Cannot obtain size for recycled Bitmap: "
				+ bitmap
				+ "["
				+ bitmap.getWidth()
				+ "x"
				+ bitmap.getHeight()
				+ "] "
				+ bitmap.getConfig());
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
		{
			// Workaround for KitKat initial release NPE in Bitmap, fixed in MR1. See issue #148.
			try
			{
				return bitmap.getAllocationByteCount();
			}
			catch (
			NullPointerException e)
			{
				// Do nothing.
			}
		}
		return bitmap.getHeight() * bitmap.getRowBytes();
	}

	/**
	 * Returns the in memory size of {@link android.graphics.Bitmap} with the given width, height, and
	 * {@link android.graphics.Bitmap.Config}.
	 */
	public static int getBitmapByteSize(int width, int height, Bitmap.Config config)
	{
		return width * height * getBytesPerPixel(config);
	}

	private static int getBytesPerPixel(Bitmap.Config config)
	{
		// A bitmap by decoding a GIF has null "config" in certain environments.
		if (config == null)
		{
			config = Bitmap.Config.ARGB_8888;
		}

		int bytesPerPixel;
		switch (config)
		{
			case ALPHA_8:
				bytesPerPixel = 1;
				break;
			case RGB_565:
			bytesPerPixel=2;
				break;
			case ARGB_4444:
			case ARGB_8888:
				bytesPerPixel=4;
				break;
			default:
				bytesPerPixel = 8;
				break;
		}
		return bytesPerPixel;
	}

}
