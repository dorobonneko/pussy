package com.moe.pussy.decode;
import com.moe.pussy.Decoder;
import java.io.InputStream;
import com.moe.pussy.PussyDrawable;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import java.io.File;
import com.moe.pussy.BitmapPool;

public class BitmapDecoder implements Decoder
{
	//byte[] buff=new byte[32*1024];
	@Override
	public Bitmap decode(File input)
	{
		BitmapFactory.Options options=new BitmapFactory.Options();
		
		options.inPreferredConfig=Bitmap.Config.ARGB_8888;
		options.inJustDecodeBounds=true;
		BitmapFactory.decodeFile(input.getAbsolutePath(),options);
		if(options.outWidth<=0||options.outHeight<=0)return null;
		options.inJustDecodeBounds=false;
		options.inBitmap=BitmapPool.getBitmap(options.outWidth,options.outHeight,options.inPreferredConfig);
		options.inMutable=true;
		return BitmapFactory.decodeFile(input.getAbsolutePath(),options);
	}
	
}
