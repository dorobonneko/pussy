package com.moe.pussy.handle;
import com.moe.pussy.Request;
import com.moe.pussy.RequestHandler;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import com.moe.pussy.RequestHandler.Response;
import java.util.concurrent.ThreadPoolExecutor;
import java.lang.ref.WeakReference;
import com.moe.pussy.Pussy;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class HandleThread implements Runnable,RequestHandler.Callback
{
	private RequestHandler.Response response;
	private Request request;
	private Set<Callback> calls=new CopyOnWriteArraySet<>();
	private boolean success;
	private WeakReference<ThreadPoolExecutor> pool;
	private int error;
	public HandleThread(Request request, ThreadPoolExecutor pool)
	{
		this.request = request;
		request.cancel(false);
		this.pool = new WeakReference<ThreadPoolExecutor>(pool);
		pool.execute(this);
	}

	public void cancel()
	{
		request.cancel(true);
	}
	public void addCallback(Callback call)
	{
		if (!success)
			calls.add(call);
		else
			call.onResponse(response);
	}
	public void removeCallback(Callback call)
	{
		calls.remove(call);
	}
	@Override
	public void run()
	{
		if (success)return;
		RequestHandler h=request.getPussy().getDispatcher().getHandler(request);
		if (h != null)
			h.onHandle(pool.get(), request, this);

	}

	@Override
	public void onSuccess(final RequestHandler.Response response)
	{
		this.response = response;
		success = true;
		for (final Callback call:calls)
		{
			call.onResponse(response);
		}
		calls.clear();
	}

	@Override
	public void onError(Throwable e)
	{
		error++;
		if (error < 1)
			try
			{
				Thread.sleep(3000);
				pool.get().execute(this);}
			catch (Exception ee)
			{}
			else{
				success=true;
				for (final Callback call:calls)
				{
					call.onResponse(response);
				}
				calls.clear();
			}
	}



	public interface Callback
	{
		void onResponse(RequestHandler.Response response);
	}
}
