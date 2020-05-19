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
	private SoftReference<ContentBuilder> content;
	//loader绑定一个target，多个loader可绑定一个handler
	private String resource;
	private int w,h;
	private AtomicBoolean pause=new AtomicBoolean();
	private Object pauseLock=new Object();
	private String key,requestKey;
	public Loader(ContentBuilder content)
	{
		this.content = new SoftReference<ContentBuilder>(content);
		key = content.getKey();
		requestKey=content.getRequest().getKey();
		//request = content.getRequest();
		//pussy = request.getPussy();
	}

	public void pause()
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
	}
	boolean isCancel()
	{
		return content.get() == null || content.get().getTarget() == null || getPussy()==null ||getPussy().mThreadPoolExecutor.isShutdown();
	}
	Pussy getPussy()
	{
		return getRequest().getPussy();
	}
	Request getRequest()
	{
		return content.get().getRequest();
	}
	Target getTarget()
	{
		return content.get().getTarget();
	}
	public void begin()
	{
		if(isCancel())return;
		Resource res=content.get().getRequest().getPussy().getActiveResource().get(key);
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
				res = getPussy().getActiveResource().create(key, bitmap);
				success(res, null);
			}
			else
			{
				Pussy.execute(new Runnable(){

						@Override
						public void run()
						{
							loadFromCache();
						}
					});
			}

		}
	}
	private void loadFromCache()
	{
		try
		{
			DiskCache dc=getPussy().getDiskCache();
			//查询内存缓存
			final File cache_file=dc.getCache(key);
			if (cache_file != null)
			{
				if (isCancel())return;
				//解码
				Image image=getPussy().decoder.decode(getPussy().getBitmapPool(),Uri.fromFile(cache_file),content.get().isAsBitmap(), 0, 0);
				Resource res=getPussy().getActiveResource().create(key, image);
				success(res, null);
				//getTarget().onResourceReady(cache_file);
				//getPussy().fileThreadPool.execute(new BitmapLoader(getPussy().getBitmapPool(),getPussy().decoder, content.get().getKey(), cache_file, this));

			}
			else
			{
				final File disk_file = dc.getCache(requestKey);
				if (disk_file != null)
				{
					//原始缓存
					if (isCancel())return;
						getTarget().onResourceReady(disk_file.getAbsolutePath());
					//getPussy().fileThreadPool.execute(new BitmapLoader(getPussy().getBitmapPool(),getPussy().decoder, getRequest().getKey(), cache_file, this));
				}
				else
				{
					//加载数据
					if (isCancel())return;
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
		if (isCancel())return;
		Pussy.execute(this);
	}


	@Override
	public void run()
	{
		if (isCancel())return;
		getPussy().waitForPaused();
		waitForPause();
		if (isCancel())return;
		synchronized (getTarget())
		{
			Image source=null;
			File cache=getPussy().getDiskCache().getCache(requestKey);
			if(requestKey.startsWith("2ec")){
				toString();
			}
			if (cache != null)
				source = getPussy().decoder.decode(getPussy().getBitmapPool(),Uri.fromFile(cache),content.get().isAsBitmap(), w, h);
			else if(resource!=null)
				source = getPussy().decoder.decode(getPussy().getBitmapPool(),Uri.parse(resource),content.get().isAsBitmap(),w,h);
			if (source == null)
			{
				if (cache != null)
					cache.delete();
				success(null, new NullPointerException("possible bitmap decoder error"));
				return;
				}
			if(!source.isGif()){
				Bitmap sourceBitmap=source.source();
			for (Transformer transformer:content.get().getTransformer())
			{
				sourceBitmap = transformer.onTransformer(getPussy().mBitmapPool, sourceBitmap, w, h);
			}
			source.setBitmap(sourceBitmap);
			}
			success(getPussy().getActiveResource().create(key, source), null);
			try
			{
				if (!source.isGif()&&content.get().getCache() == DiskCache.Cache.MASK)
					source.source().compress(Bitmap.CompressFormat.WEBP, 99, new FileOutputStream(getPussy().getDiskCache().getCache(content.get().getKey())));
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
		if (pause.get())return;
		if(response==null){
			getPussy().request_handler.remove(requestKey);
			success(null,new IOException("image download error"));
		}else if(isCancel()){
			return;
		}else
		if (response.getBitmap() != null)
		{
			/*if (isCancel())
				getPussy().getBitmapPool().recycle(response.getBitmap());
			else
			{
				//resource = response.getBitmap();
				resource=getPussy().getBitmapPool().getBitmap(response.getBitmap().getWidth(),response.getBitmap().getHeight(),response.getBitmap().getConfig());
				int[] pix=new int[response.getBitmap().getWidth()*response.getBitmap().getHeight()];
				response.getBitmap().getPixels(pix,0,resource.getWidth(),0,0,resource.getWidth(),resource.getHeight());
				resource.setPixels(pix,0,resource.getWidth(),0,0,resource.getWidth(),resource.getHeight());
				getTarget().onResourceReady(null);
			}*/
			resource=response.getBitmap();
			getTarget().onResourceReady(response.getBitmap());
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
				getTarget().onResourceReady(input.getAbsolutePath());
			}
		}
	}





	private void success(final Resource res, final Throwable e)
	{
		//Pussy.checkThread(false);
		if (isCancel())
		{
			if (res != null)
				res.release();
			return;
		}
		if (res == null)
		{
			//throw new NullPointerException(e.getMessage());
			Pussy.post(new Runnable(){
					public void run()
					{
						Target t=getTarget();
						if (t != null)t.error(e, content.get().error);
					}
				});
		}
		else
		{
			//content.getRequest().getPussy().getMemoryCache().put(content.getKey(), bitmap);
			if (Looper.myLooper() == Looper.getMainLooper())
			{
				Target t=getTarget();
				if (t != null)
				{
					getPussy().getDiskCache().invalidate(key);
					getPussy().getDiskCache().invalidate(requestKey);
					res.acquire();
					t.onSuccess(new PussyDrawable(res.image,content.get().getAnim()));
				}
			}
			else
				Pussy.post(new Runnable(){
						public void run()
						{
							Target t=getTarget();
							if (t != null)
							{
								getPussy().getDiskCache().invalidate(key);
								getPussy().getDiskCache().invalidate(requestKey);
								res.acquire();
								t.onSuccess(new PussyDrawable(res.image,content.get().getAnim()));
							}
						}
					});}
	}

	/*@Override
	 public void onSuccess(String key, Bitmap bitmap, File file)
	 {
	 Pussy.checkThread(false);
	 if (isCancel())
	 {
	 getPussy().getBitmapPool().recycle(bitmap);
	 return;
	 }
	 source = bitmap;
	 if (key.equals(content.get().getKey()))
	 {
	 if (bitmap == null)
	 {
	 file.delete();
	 File cache_file = getPussy().getDiskCache().getCache(getRequest().getKey());
	 if (cache_file.exists())
	 //原始缓存
	 getPussy().fileThreadPool.execute(new BitmapLoader(getPussy().getBitmapPool(),getPussy().decoder, content.get().getKey(), cache_file, this));
	 }
	 else
	 {
	 success(bitmap, null);
	 }
	 }
	 else
	 {
	 if (bitmap == null)
	 {
	 file.delete();
	 HandleThread ht=getPussy().request_handler.get(getRequest().getKey());
	 if (ht == null)
	 getPussy().request_handler.put(getRequest().getKey(), ht = new HandleThread(getRequest(), getPussy().netThreadPool));
	 ht.addCallback(this);
	 }
	 else
	 {
	 if (getTarget() != null)
	 {
	 getTarget().onResourceReady(bitmap, content.get().getTransformer());
	 //					if (bitmap == null)return;
	 //					try
	 //					{
	 //						if (content.getCache() == DiskCache.Cache.MASK)
	 //							bitmap.compress(Bitmap.CompressFormat.WEBP, 99, new FileOutputStream(pussy.getDiskCache().getCache(content.getKey())));
	 //					}
	 //					catch (FileNotFoundException e)
	 //					{}
	 //					success(bitmap, null);
	 }
	 else
	 {
	 getPussy().getBitmapPool().recycle(bitmap);
	 }
	 }
	 }
	 }

	 */


}
