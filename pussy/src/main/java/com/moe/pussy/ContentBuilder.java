package com.moe.pussy;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import android.widget.ImageView;
import com.moe.pussy.target.ImageViewTarget;
import java.util.Map;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import java.lang.ref.WeakReference;
import com.moe.pussy.handle.HandleThread;
import com.moe.pussy.target.ViewBackgroundTarget;
import android.view.View;
import com.moe.pussy.target.DrawableTarget;
import java.io.File;
import com.moe.pussy.target.DownloadTarget;
import android.content.Context;

public class ContentBuilder implements SizeReady,Runnable
{
	private Request request;
	private String key=null,tag;
	private Target target;
	private DiskCache.Cache cache=DiskCache.Cache.NONE;
	private List<Transformer> mTransformers=new ArrayList<>();
	private DrawableAnimator anim;
	private Pussy.Refresh r;
	private Drawable placeHolder;
	protected Drawable error;
	protected Loader loader;
	private Listener listener;
	private long delay;
	private boolean asBitmap,cancel;
	public ContentBuilder(Request r)
	{
		this.request = r;
	}
	public void download(File output)
	{
		DownloadTarget t=new DownloadTarget(request.getPussy().getContext(),output);
		target = t;
		t.onAttachContent(this);
		//t.placeHolder(placeHolder);
		loader = new Loader(this);
		if (delay > 0)
			Pussy.post(new Runnable(){

					@Override
					public void run()
					{
						loader.loadFromCache();
					}
				}, delay);
		else
			loader.loadFromCache();

	}
	
	public ContentBuilder delay(long delay)
	{
		this.delay = delay;
		return this;
	}
	@Override
	public void onSizeReady(int w, int h)
	{
		if (!isCancel())
			loader.onSizeReady(w, h);
	}
	public ContentBuilder asBitmap()
	{
		asBitmap = true;
		return this;
	}
	boolean isAsBitmap()
	{
		return asBitmap;
	}
	public ContentBuilder listener(Listener l)
	{
		listener = l;
		return this;
	}
	public Listener getListener()
	{
		return listener;
	}
	void cancel()
	{
		Pussy.checkThread(true);
		loader.cancel();
		loader=null;
	}
	boolean isCancel(){
		return loader==null||loader.isCancel();
	}
	public ContentBuilder tag(String tag)
	{
		this.tag = tag;
		return this;
	}
	String tag()
	{
		return tag;
	}
	public ContentBuilder placeHolder(Drawable res)
	{
		placeHolder = res;
		return this;
	}
	public ContentBuilder error(Drawable res)
	{
		error = res;
		return this;
	}
	public ContentBuilder placeHolder(int res)
	{
		placeHolder = request.getPussy().getContext().getResources().getDrawable(res);
		return this;
	}
	public ContentBuilder error(int res)
	{
		error = request.getPussy().getContext().getResources().getDrawable(res);
		return this;
	}
	public Pussy.Refresh getRefresh()
	{
		if (r == null)
			r = new Pussy.Refresh(this);
		return r;
	}
	public DrawableAnimator getAnim()
	{
		return anim;
	}
	public ContentBuilder anime(DrawableAnimator anim)
	{
		this.anim = anim;
		return this;
	}
	public ContentBuilder transformer(Transformer... trans)
	{
		mTransformers.addAll(Arrays.asList(trans));
		return this;
	}
	Request getRequest()
	{
		return request;
	}
	Target getTarget()
	{
		return  target;
	}
	DiskCache.Cache getCache()
	{
		return cache;
	}
	public ContentBuilder diskCache(DiskCache.Cache cache)
	{
		this.cache = cache;
		return this;
	}
	void refresh(Target t)
	{
		Pussy.checkThread(true);
		if(isCancel()){
		cancel=false;
		Pussy.remove(this);
		t.placeHolder(placeHolder);
		loader=new Loader(this);
		loader.begin();
		}
	}
	public void into(Target t)
	{
		if (t == null)return;
		if(equals(t.getContent())){
			if(t.getContent().isCancel())
				t.getContent().refresh(t);
		}else{
		Pussy.remove(t.getContent());
		ContentBuilder cb=t.getContent();
		if(cb!=null&&!cb.isCancel())
		request.getPussy().cancel(t);
		target = t;
		t.onAttachContent(this);
		t.placeHolder(placeHolder);
		loader = new Loader(this);
		if (delay > 0)
			Pussy.post(this, delay);
		else
			loader.begin();
			}
	}

	@Override
	public void run()
	{
		if(!isCancel())
		loader.begin();
	}

	@Override
	public boolean equals(Object obj)

	{
		if (obj instanceof ContentBuilder)
			return getKey().equals(((ContentBuilder)obj).getKey());
		return super.equals(obj);
	}            


	public void into(ImageView view)
	{
		//view.setImageDrawable(null);
		ImageViewTarget ivt=(ImageViewTarget) view.getTag();
		if (ivt == null)
			view.setTag(ivt = new ImageViewTarget(view));
		//ivt.placeHolder(placeHolder);
		into(ivt);
	}
	public void into(View view)
	{
		//view.setImageDrawable(null);
		ViewBackgroundTarget ivt=(ViewBackgroundTarget) view.getTag();
		if (ivt == null)
			view.setTag(ivt = new ViewBackgroundTarget(view));
		//ivt.placeHolder(placeHolder);
		into(ivt);
	}
	public DrawableTarget intoPlaceHolder()
	{
		DrawableTarget dt=new DrawableTarget();
		into(dt);
		return dt;
	}
	String getKey()
	{
		if (key == null)
		{
			if (request.getKey() == null)return null;
			StringBuilder sb=new StringBuilder();
			sb.append(request.getKey());
			for (Transformer t:mTransformers)
				sb.append(t.getKey());
			key = Uid.fromString(sb.toString()).toString();
		}
		return key;
	}
	Transformer[] getTransformer()
	{
		return mTransformers.toArray(new Transformer[0]);
	}
}
