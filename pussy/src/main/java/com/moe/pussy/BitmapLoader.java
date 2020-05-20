package com.moe.pussy;
import android.graphics.Bitmap;
import java.io.File;

public class BitmapLoader implements Runnable
{
	private String key;
	private File file;
	private Decoder decoder;
	private Callback call;
	private BitmapPool bp;
	private BitmapLoader(BitmapPool bp,Decoder decoder,String key,File file,Callback call){
		this.key=key;
		this.file=file;
		this.decoder=decoder;
		this.call=call;
		this.bp=bp;
	}
	@Override
	public void run()
	{
		//call.onSuccess(key,decoder.decode(bp,file),file);
		key=null;
		decoder=null;
		file=null;
		call=null;
		bp=null;
	}

	
	public interface Callback{
		void onSuccess(String key,Bitmap bitmap,File file);
	}
}
