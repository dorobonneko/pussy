package com.moe.pussy.handle;
import com.moe.pussy.RequestHandler;
import com.moe.pussy.RequestHandler.Response;
import com.moe.pussy.Request;
import android.net.Uri;
import java.io.InputStream;
import com.moe.pussy.PussyDrawable;
import java.net.URLConnection;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import java.util.Iterator;
import java.util.Map;
import java.io.File;
import com.moe.pussy.DiskCache;
import java.io.FileInputStream;
import java.util.concurrent.ThreadPoolExecutor;

public class HttpRequestHandler implements RequestHandler
{

	@Override
	public boolean canHandle(Request request)
	{
		switch(Uri.parse(request.getUrl()).getScheme()){
			case "http":
			case "https":
				return true;
		}
		return false;
	}

	@Override
	public void onHandle(ThreadPoolExecutor pool,final Request request,final Callback call)
	{
		pool.execute(new Runnable(){
			public void run(){
				try
				{
					Response res=onHandle(request);
					if(res==null)
						throw new NullPointerException();
						call.onSuccess(res);
				}
				catch (Exception e)
				{
					call.onError(e);
				}

		{}
		}});
	}
	private Response onHandle(Request request) throws IOException{
		HttpURLConnection huc=(HttpURLConnection) new URL(request.getUrl()).openConnection();
			if(huc instanceof HttpsURLConnection){
				SSLSocketFactory ssf=request.getPussy().getSSLSocketFactory();
				if(ssf!=null)
					((HttpsURLConnection)huc).setSSLSocketFactory(ssf);
			}
			Iterator<Map.Entry<String,String>> header_i=request.getHeader().entrySet().iterator();
			while(header_i.hasNext()){
				Map.Entry<String,String> entry=header_i.next();
				huc.setRequestProperty(entry.getKey(),entry.getValue());
			}
			File tmp=request.getPussy().getDiskCache().getTmp(request.getKey());
			huc.setRequestProperty("Range", "bytes=".concat(tmp.exists()?String.valueOf(tmp.length()):"0").concat("-"));
			huc.setFollowRedirects(true);
			int code=huc.getResponseCode();
			if(code==301||code==302){
				String location=huc.getHeaderField("Location");
				if(location!=null){
					request.setLocation(location);
					huc.disconnect();
					return onHandle(request);
				}
			}
			DiskCache dc=request.getPussy().getDiskCache();
			if(tmp.length()>0&&code==200)
				huc.getInputStream().skip(tmp.length());
			InputStream input= dc.getInputStream(tmp,huc.getInputStream());
			while(input.read()!=-1);
			input.close();
			huc.disconnect();
			tmp.renameTo(dc.getCache(request.getKey()));
			HttpResponse hrs=new HttpResponse();
			hrs.set(dc.getCache(request.getKey()));
			return hrs;
	}
	class HttpResponse extends Response
	{
		private File in;
		public void set(File in){
			this.in= in;
		}
		@Override
		public File get()
		{
			return in;
		}

		

		
	}
}
