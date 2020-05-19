package com.moe.pussy.decode;
import com.moe.pussy.Decoder;
import java.io.InputStream;
import com.moe.pussy.PussyDrawable;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import java.io.File;
import com.moe.pussy.BitmapPool;
import android.content.Context;
import android.net.Uri;
import com.moe.pussy.Image;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class BitmapDecoder implements Decoder
{
	//byte[] buff=new byte[32*1024];
	private Context context;
	public BitmapDecoder(Context context)
	{
		this.context = context;
	}
	@Override
	public Image decode(BitmapPool mBitmapPool, Uri input,boolean outbitmap, int w, int h)
	{
		switch (input.getScheme())
		{
			case "file":{
					BitmapFactory.Options options=new BitmapFactory.Options();
					options.inDither = true;
					options.inPreferredConfig = Bitmap.Config.RGB_565;
					options.inJustDecodeBounds = true;
					BitmapFactory.decodeFile(input.getPath(), options);
					if (options.outWidth <= 0 || options.outHeight <= 0)return null;
					if(!outbitmap){
					try
					{
						if ("image/gif".equalsIgnoreCase(options.outMimeType))
							return new Image(mBitmapPool, new FileInputStream(input.getPath()));
					}
					catch (FileNotFoundException e)
					{
						return null;
					}
					}
					options.inPreferredConfig=options.outConfig==null?Bitmap.Config.RGB_565:options.outConfig;
					options.inJustDecodeBounds = false;
					options.inSampleSize = calculateInSampleSize(options, w, h);
					options.inBitmap = mBitmapPool.getBitmap(options.outWidth, options.outHeight, options.inPreferredConfig);
					options.inMutable = true;
					Bitmap bitmap=BitmapFactory.decodeFile(input.getPath(), options);
					if(bitmap==null){
						mBitmapPool.recycle(options.inBitmap);
						return null;
						}
					if (options.outMimeType == null || options.outWidth != bitmap.getWidth() || options.outHeight != bitmap.getHeight())
					{
						mBitmapPool.recycle(bitmap);
						bitmap = null;
					}
					else if (bitmap.getWidth() <= 0 || bitmap.getHeight() <= 0)
					{
						mBitmapPool.recycle(bitmap);
						bitmap = null;
					}
					if(bitmap==null)return null;
					return new Image(mBitmapPool,bitmap);
				}
			case "drawable":
				{
					int id=Integer.parseInt(input.getSchemeSpecificPart());
					BitmapFactory.Options options=new BitmapFactory.Options();
					options.inDensity = Bitmap.DENSITY_NONE ;
					options.inScaled = false ;
					options.inPreferredConfig = Bitmap.Config.RGB_565;
					options.inJustDecodeBounds = true;
					BitmapFactory.decodeResource(context.getResources(), id, options);
					options.inJustDecodeBounds = false;
					options.inBitmap=mBitmapPool.getBitmap(options.outWidth,options.outHeight,options.inPreferredConfig);
					options.inMutable = true;
					options.inSampleSize=calculateInSampleSize(options,w,h);
					return new Image(mBitmapPool,BitmapFactory.decodeResource(context.getResources(), id, options));

				}
		}
		return null;
	}
	public static int inSampleSize(int width, int height, int reqWidth, int reqHeight)
	{
		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth)
		{
			int halfHeight = height / 2;
			int halfWidth = width / 2;
			try
			{
				while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth)
				{
					if (inSampleSize == 0)
						throw new NullPointerException();
					int now =inSampleSize * 2;
					inSampleSize = now;
					if (now == 0)
						throw new NullPointerException("inSampleSize" + inSampleSize);

				}
			}
			catch (ArithmeticException e)
			{
				throw new ArithmeticException("inSampleSize" + inSampleSize + "/halfWidth" + halfWidth + "/halfHeight" + halfHeight);
			}
		}
		return inSampleSize;
	}
	public static int computeSampleSize(BitmapFactory.Options options,

										int minSideLength, int maxNumOfPixels)
	{

		int initialSize = computeInitialSampleSize(options, minSideLength,

												   maxNumOfPixels);



		int roundedSize;

		if (initialSize <= 8)
		{

			roundedSize = 1;

			while (roundedSize < initialSize)
			{

				roundedSize <<= 1;

			}

		}
		else
		{

			roundedSize = (initialSize + 7) / 8 * 8;

		}



		return roundedSize;

	}

	private static int computeInitialSampleSize(BitmapFactory.Options options,

												int minSideLength, int maxNumOfPixels)
	{

		double w = options.outWidth;

		double h = options.outHeight;



		int lowerBound = (maxNumOfPixels == -1) ? 1 :

            (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));

		int upperBound = (minSideLength == -1) ? 128 :

            (int) Math.min(Math.floor(w / minSideLength),

						   Math.floor(h / minSideLength));



		if (upperBound < lowerBound)
		{

			// return the larger one when there is no overlapping zone.

			return lowerBound;

		}



		if ((maxNumOfPixels == -1) &&

            (minSideLength == -1))
		{

			return 1;

		}
		else if (minSideLength == -1)
		{

			return lowerBound;

		}
		else
		{

			return upperBound;

		}

	}
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight)
	{
        final int width = options.outWidth;
        final int height = options.outHeight;
        int inSampleSize = 1;
		if (reqHeight == 0 && reqWidth == 0)
		{
			return inSampleSize;
		}
		else
		if (reqHeight == 0)
		{
			inSampleSize = Math.round((float) width / (float) reqWidth);
		}
		else if (reqWidth == 0)
		{
			inSampleSize = Math.round((float) height / (float) reqHeight);
		}
		else
        if (height > reqHeight || width > reqWidth)
		{
            //计算图片高度和我们需要高度的最接近比例值
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            //宽度比例值
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            //取比例值中的较大值作为inSampleSize
            inSampleSize = Math.min(heightRatio,widthRatio);
        }

        return inSampleSize;
    }
}
