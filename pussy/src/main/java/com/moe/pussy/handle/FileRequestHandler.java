package com.moe.pussy.handle;
import android.net.Uri;
import com.moe.pussy.Request;
import com.moe.pussy.RequestHandler;
import java.util.concurrent.ThreadPoolExecutor;

public class FileRequestHandler implements RequestHandler
{
	
	@Override
	public boolean canHandle(Request request)
	{
		Uri uri=Uri.parse(request.getUrl());
		switch(uri.getScheme()){
			case "file":
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
