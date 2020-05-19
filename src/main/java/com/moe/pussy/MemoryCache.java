package com.moe.pussy;
import android.util.LruCache;
import android.graphics.Bitmap;
import java.util.Map;
import java.lang.ref.WeakReference;
import java.util.HashMap;

public class MemoryCache extends LruCache<String,Image>
{
	//private Map<String,WeakReference> cache=new HashMap<>();
	private BitmapPool bp;
	public MemoryCache(BitmapPool bp){
		super((int)bp.maxSize);
		this.bp=bp;
	}
	
	@Override
	protected void entryRemoved(boolean evicted, String key, Image oldValue, Image newValue)
	{
		if(evicted){
			bp.recycle(oldValue.source());
		}else{
			if(newValue!=null){
				bp.recycle(oldValue.source());
			}
		}
	}

	@Override
	protected int sizeOf(String key, Image value)
	{
		return value.source().getByteCount();
	}



	
}
