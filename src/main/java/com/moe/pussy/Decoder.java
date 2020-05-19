package com.moe.pussy;
import java.io.InputStream;
import java.io.File;
import android.graphics.Bitmap;
import android.net.Uri;

public interface Decoder
{
	public Image decode(BitmapPool mBitmapPool,Uri input,boolean bitmap,int w,int h);
}
