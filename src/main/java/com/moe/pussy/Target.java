package com.moe.pussy;
import android.graphics.Bitmap;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import android.graphics.drawable.Drawable;
import java.io.File;

public interface Target extends SizeReady
{
	public abstract void placeHolder(Drawable placeHolder);
	public ContentBuilder getContent();
	//原始资源准备完毕，等待回调onSizeReady
	public abstract void onResourceReady(String cache);
	public abstract void onSuccess(PussyDrawable pd);
	public abstract void error(Throwable e,Drawable d);
	public abstract void onCancel();
	/*public PussyDrawable putCache(Bitmap pd){
		if(pd==null)return null;
		Resource res=new Resource(content.getKey(),pd);
		res.acquire();
		content.getRequest().getPussy().getActiveResource().add(res);
		if(content.getCache()==DiskCache.Cache.MASK){
			//持久化
			Bitmap bitmap=pd;
			if (bitmap != null)
			{
				try
				{
					bitmap.compress(Bitmap.CompressFormat.WEBP, 99, new FileOutputStream(content.getRequest().getPussy().mDiskCache.getCache(content.getKey())));
				}
				catch (FileNotFoundException e)
				{}
			}
		}
		return new PussyDrawable(pd,getRefresh());
	}*/
	public abstract void onAttachContent(ContentBuilder c);
	public abstract Listener getListener();
	
}
