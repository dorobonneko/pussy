package com.moe.pussy.target;
import com.moe.pussy.Target;
import com.moe.pussy.PussyDrawable;
import com.moe.pussy.ContentBuilder;
import android.graphics.drawable.Drawable;
import com.moe.pussy.Listener;
import com.moe.pussy.Request;
import java.io.File;
import java.nio.file.Files;
import java.io.IOException;
import android.net.Uri;
import com.moe.pussy.Pussy;

public class DownloadTarget implements Target
{
	private ContentBuilder cb;
	private File output;
	public DownloadTarget(File output)
	{
		this.output = output;
		File parent=output.getParentFile();
		if (!parent.exists())
			parent.mkdirs();
		else if (parent.isFile())
		{
			parent.delete();
			parent.mkdirs();
		}
	}
	@Override
	public void placeHolder(Drawable placeHolder)
	{
		if (getListener() != null)
			getListener().onPlaceHolder(this, placeHolder);
	}

	@Override
	public ContentBuilder getContent()
	{
		return cb;
	}

	@Override
	public void onResourceReady(String cache, Request request)
	{

		if (cache.startsWith("file://"))
		{
			//copy
			try
			{
				Files.copy(new File(Uri.parse(cache).getPath()).toPath(), output.toPath());
			}
			catch (IOException e)
			{
				error(e, null);
				return;
			}
		}
		//未加载图片，不需要此操作
		//getContent().getRefresh().cancel();
		onSuccess(null);
	}

	@Override
	public void onSuccess(PussyDrawable pd)
	{
		Pussy.post(new Runnable(){
			public void run(){
		if (getListener() != null)
			getListener().onSuccess(DownloadTarget.this, null);
			}
			});
	}

	@Override
	public void error(final Throwable e, Drawable d)
	{
		//无需取消
		//getContent().getRefresh().cancel();
		Pussy.post(new Runnable(){
			public void run(){
		if (getListener() != null)
			getListener().onError(DownloadTarget.this, null,e);
			}
			});
	}

	@Override
	public void onCancel()
	{
	}

	@Override
	public void onAttachContent(ContentBuilder c)
	{
		this.cb = c;
	}

	@Override
	public Listener getListener()
	{
		return cb.getListener();
	}

	@Override
	public void onSizeReady(int w, int h)
	{
	}

}
