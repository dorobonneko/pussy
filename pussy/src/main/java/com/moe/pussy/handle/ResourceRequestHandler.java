package com.moe.pussy.handle;
import com.moe.pussy.RequestHandler;
import com.moe.pussy.RequestHandler.Response;
import com.moe.pussy.Request;
import android.net.Uri;
import android.content.Context;
import android.content.res.Resources;
import com.moe.pussy.PussyDrawable;
import java.io.InputStream;
import android.graphics.drawable.Drawable;
import android.graphics.Canvas;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.moe.pussy.BitmapPool;
import java.util.concurrent.ThreadPoolExecutor;
import java.lang.ref.WeakReference;
import java.io.File;

public class ResourceRequestHandler implements RequestHandler
{
	private WeakReference<Resources> res;
	public ResourceRequestHandler(Context context){
		res=new WeakReference<>(context.getResources());
	}
	@Override
	public boolean canHandle(Request request)
	{
		Uri uri=Uri.parse(request.getUrl());
		switch(uri.getScheme()){
			case "drawable":
				return true;
		}
		return false;
	}

	@Override
	public void onHandle(ThreadPoolExecutor pool,final Request request,final Callback call)
	{
		call.onSuccess(new ResResponse(request.getUrl()));
		/*Uri uri=Uri.parse(request.getUrl());
		int id=Integer.parseInt(uri.getSchemeSpecificPart());
		BitmapFactory.Options options=new BitmapFactory.Options();
		options.inDensity = Bitmap.DENSITY_NONE ;
		options.inScaled = false ;
		options.inPreferredConfig=Bitmap.Config.ARGB_8888;
		options.inJustDecodeBounds=true;
		BitmapFactory.decodeResource(res.get(),id,options);
		options.inJustDecodeBounds=false;
		//options.inBitmap=request.getPussy().getBitmapPool().getBitmap(options.outWidth,options.outHeight,options.inPreferredConfig);
		options.inMutable=false;
		call.onSuccess(new ResResponse(BitmapFactory.decodeResource(res.get(),id,options)));
		//pool.execute(run);*/
	}
	class ResResponse extends Response{
		private String file;
		public ResResponse(String file){
			this.file=file;
		}
		@Override
		public String getBitmap()
		{
			return file;
		}
	}
	
}
