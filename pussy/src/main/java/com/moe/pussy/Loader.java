package com.moe.pussy;
import java.io.File;
import java.io.FileInputStream;
import android.graphics.Bitmap;
import java.io.FileOutputStream;
import com.moe.pussy.handle.HandleThread;
import java.io.InputStream;
import java.io.IOException;
import com.moe.pussy.RequestHandler.Response;
import java.io.FileNotFoundException;
import android.widget.Toast;
import java.lang.ref.WeakReference;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import android.os.Looper;
import android.net.Uri;
import java.lang.ref.SoftReference;

public class Loader implements Runnable,HandleThread.Callback,SizeReady
{
	private ContentBuilder content;
	//loader绑定一个target，多个loader可绑定一个handler
	private String resource;
	private int w,h;
	//private AtomicBoolean pause=new AtomicBoolean();
	private boolean cancel;
	private String key,requestKey;
	private Thread transThread;
	private Resource image;
	public Loader(ContentBuilder content)
	{
		this.content = content;
		key = content.getKey();
		requestKey = content.getRequest().getKey();
		//request = content.getRequest();
		//pussy = request.getPussy();
	}

	@Override
	public void onStart()
	{
		Pussy.post(new Runnable(){
				public void run()
				{
					if (getRequest().getListener() != null)
					{
						getRequest().getListener().onStart(getRequest());
					}
				}});
	}

	@Override
	public void onProgress(final long current, final long length)
	{
		Pussy.post(new Runnable(){
				public void run()
				{
					if (getRequest().getListener() != null)
					{
						getRequest().getListener().onProgress(current, length, getRequest());
					}
				}});
	}
	
	/*public void pause()
	 {
	 pause.set(true);
	 }
	 public boolean resume()
	 {
	 boolean e=pause.get();
	 pause.set(false);
	 synchronized (pauseLock)
	 {
	 pauseLock.notify();
	 }
	 return e;
	 //begin();
	 }
	 void waitForPause()
	 {
	 if (pause.get())
	 synchronized (pauseLock)
	 {
	 try
	 {
	 pauseLock.wait();
	 }
	 catch (InterruptedException e)
	 {}
	 }
	 }*/
	boolean isCancel()
	{
		return cancel;
	}
	Pussy getPussy()
	{
		return getRequest().getPussy();
	}
	Request getRequest()
	{
		return content.getRequest();
	}
	Target getTarget()
	{
		return content.getTarget();
	}
	public void begin()
	{
		if(isCancel())return;
		Resource res=getPussy().getActiveResource().get(key);
		if (res != null)
		{
			//throw new RuntimeException("resource not cancel");
			success(res, null);
		}
		else
		{
			Image bitmap=getPussy().getMemoryCache().remove(key);
			if (bitmap != null)
			{
			res=getPussy().getActiveResource().create(key, bitmap);
				success(res, null);
			}
			else
			{
				Pussy.execute(new Runnable(){
					public void run(){
				loadFromCache();
				}});
			}

		}
	}
	void loadFromCache()
	{
		Pussy.checkThread(false);
		try
		{
			DiskCache dc=getPussy().getDiskCache();
			//查询内存缓存
			final File cache_file=dc.getCache(key);
			if (cache_file != null)
			{
				//解码
				Image image=getPussy().decoder.decode(getPussy().getBitmapPool(), Uri.fromFile(cache_file), content.isAsBitmap(), 0, 0);
				success(getPussy().getActiveResource().create(key, image), null);
				//getTarget().onResourceReady(cache_file);
				//getPussy().fileThreadPool.execute(new BitmapLoader(getPussy().getBitmapPool(),getPussy().decoder, content.get().getKey(), cache_file, this));

			}
			else
			{
				final File disk_file = dc.getCache(requestKey);
				if (disk_file != null)
				{
					//原始缓存
					Target t=getTarget();
					if (t != null)
						t.onResourceReady(Uri.fromFile(disk_file).toString(), getRequest());
					//getPussy().fileThreadPool.execute(new BitmapLoader(getPussy().getBitmapPool(),getPussy().decoder, getRequest().getKey(), cache_file, this));
				}
				else
				{
					//加载数据
					HandleThread ht=getPussy().request_handler.get(requestKey);
					if (ht == null)
						getPussy().request_handler.put(requestKey, ht = new HandleThread(getRequest(), getPussy().mThreadPoolExecutor));
					ht.addCallback(this);
				}

			}

			//解码加载
		}
		finally
		{}
	}
	@Override
	public void onSizeReady(int w, int h)
	{
		this.w = w;this.h = h;
		if(!isCancel())
		transThread=Pussy.execute(this);
	}

	public void cancel(){
		cancel=true;
		if(transThread!=null)
			transThread.interrupt();
			HandleThread ht=getPussy().request_handler.get(requestKey);
			if(ht!=null)
				ht.removeCallback(this);
			if(image!=null)
				image.release();
			//content=null;
		
	}
	@Override
	public void run()
	{
		Pussy.checkThread(false);
		
		if(!isCancel()){
			Pussy pussy=getPussy();
			ContentBuilder content=this.content;
			if(isCancel())return;
			pussy.waitForPaused();
		
			Image source=null;
			File cache=pussy.getDiskCache().getCache(requestKey);
			if (cache != null)
				source = pussy.decoder.decode(pussy.getBitmapPool(), Uri.fromFile(cache), content.isAsBitmap(), w, h);
			else if (resource != null)
				source = pussy.decoder.decode(pussy.getBitmapPool(), Uri.parse(resource), content.isAsBitmap(), w, h);
			if (source == null || source.getBitmap() == null)
			{
				if (cache != null)
					cache.delete();
				pussy.request_handler.remove(requestKey);
				success(null, new NullPointerException("possible bitmap decoder error"));
				return;
			}
			if (!source.isGif())
			{
				Bitmap sourceBitmap=source.getBitmap();
				for (Transformer transformer:content.getTransformer())
				{
					sourceBitmap = transformer.onTransformer(pussy.mBitmapPool, sourceBitmap, w, h);
				}
				source = Image.parse(sourceBitmap);
			}
			success(pussy.getActiveResource().create(key, source), null);
			try
			{
				if (!source.isGif() && content.getCache() == DiskCache.Cache.MASK)
					source.getBitmap().compress(Bitmap.CompressFormat.WEBP, 99, new FileOutputStream(getPussy().getDiskCache().getCache(content.getKey())));
			}
			catch (FileNotFoundException e)
			{}
			source = null;
			resource = null;
		}
	}

	@Override
	public void onResponse(RequestHandler.Response response)
	{
		Pussy.checkThread(false);
		if (response == null)
		{
			getPussy().request_handler.remove(requestKey);
			success(null, new IOException("image download error"));
		}
		else if (isCancel())
		{
			return;
		}
		else if (response.getBitmap() != null)
		{
			resource = response.getBitmap();
			getTarget().onResourceReady(response.getBitmap(), getRequest());
		}
		else if (response.get() == null)
		{
			getPussy().request_handler.remove(requestKey);
			success(null, new IOException("newwork load error"));
		}
		else
		{
			File input=response.get();

			//加入缓存
			if (!isCancel())
			{
				getTarget().onResourceReady(Uri.fromFile(input).toString(), getRequest());
			}
		}
	}





	private void success(final Resource res, final Throwable e)
	{
		if (isCancel())
		{
			res.acquire();
			res.release();
			return;
		}
		
		Pussy.post(new Runnable(){
				public void run()
				{
					if(isCancel())return;
					if (getRequest().getListener() != null)
					{
						getRequest().getListener().onEnd(getRequest(), e);
					}
				}});
		
		if (res == null)
		{
			//throw new NullPointerException(e.getMessage());
			Pussy.post(new Runnable(){
					public void run()
					{
						Target t=getTarget();
						if (t != null)t.error(e, content.error);
					}
				});
		}
		else
		{
			Pussy.post(new Runnable(){
					public void run()
					{
						if(isCancel()){
							res.acquire();
							res.release();
							return;
							}
						Target t=getTarget();
						if (t != null)
						{
							getPussy().getDiskCache().invalidate(key);
							getPussy().getDiskCache().invalidate(requestKey);
							Image img=res.image;
							res.acquire();
							image=res;
							t.onSuccess(new PussyDrawable(img, content.getAnim()));
							
						}
						
					}
				});}
	}


}
