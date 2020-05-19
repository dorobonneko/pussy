package com.moe.pussy.transformer;
import com.moe.pussy.Transformer;
import com.moe.pussy.BitmapPool;
import android.graphics.Bitmap;
import java.util.Random;

public class SprayTransFormer implements Transformer
{
	private int radius;
	public SprayTransFormer(int radius){
		this.radius=radius;
	}

	@Override
	public String getKey()
	{
		return "spray&"+radius;
	}

	@Override
	public Bitmap onTransformer(BitmapPool mBitmapPool, Bitmap bitmap, int w, int h)
	{
		if(bitmap==null)return bitmap;
		int rx=0,ry=0;
		int[] pix=new int[bitmap.getWidth()*bitmap.getHeight()];
		int[] newpix=new int[bitmap.getWidth()*bitmap.getHeight()];
		Random random=new Random();
		bitmap.getPixels(pix,0,bitmap.getWidth(),0,0,bitmap.getWidth(),bitmap.getHeight());
		for(int x=0;x<bitmap.getWidth();x++){
			for(int y=0;y<bitmap.getHeight();y++){
				for(int m=0;m<6;m+=2){
					if(m+x>=bitmap.getWidth())continue;
					for(int n=0;n<6;n+=2){
						if(y+n>=bitmap.getHeight())continue;
							//计算偏移坐标点
						//int radius=random.nextInt(this.radius);
//						if(m%2==0){
//							if(n%2==0){
//								rx=x-radius;
//								ry=y-radius;
//							}else{
//								rx=x-radius;
//								ry=y+radius;
//							}
//						}else{
//							if(n%2==0){
//								rx=x+radius;
//								ry=y-radius;
//							}else{
//								rx=x+radius;
//								ry=y+radius;
//							}
//						}
						switch(m%6){
							case 0:
								switch(n%6){
									case 0:
										rx=x-random(random,this.radius);
										ry=y-random(random,this.radius);
										break;
									case 2:
										rx=x-random(random,this.radius);
										ry=y;
										break;
									case 4:
										rx=x-random(random,this.radius);
										ry=y+random(random,this.radius);
										break;
								}
								break;
							case 2:
								switch(n%6){
									case 0:
										rx=x;
										ry=y-random(random,this.radius);
										break;
									case 2:
										rx=x;
										ry=y;
										break;
									case 4:
										rx=x;
										ry=y+random(random,this.radius);
										break;
								}
								break;
							case 4:
								switch(n%6){
									case 0:
										rx=x+random(random,this.radius);
										ry=y-random(random,this.radius);
										break;
									case 2:
										rx=x+random(random,this.radius);
										ry=y;
										break;
									case 4:
										rx=x+random(random,this.radius);
										ry=y+random(random,this.radius);
										break;
								}
								break;
						}
						/*if(m%2==0){
							if(n%2==0){
								rx=x;
								ry=y-radius;
							}else{
								rx=x-radius;
								ry=y;
							}
						}else{
							if(n%2==0){
								rx=x+radius;
								ry=y;
							}else{
								rx=x;
								ry=y+radius;
							}
						}*/
						//rx=x+(random.nextInt(radius)*(random.nextBoolean()?1:-1));
						//ry=y+(random.nextInt(radius)*(random.nextBoolean()?1:-1));
						if(rx>=0&&ry>=0&&rx<bitmap.getWidth()&&ry<bitmap.getHeight()){
							newpix[rx+ry*bitmap.getWidth()]=pix[x+m+(y+n)*bitmap.getWidth()];
						}
					}
				}
			}
		}
		//消除噪点
		for(int i=0;i<pix.length;i++)
		if(newpix[i]==0)newpix[i]=pix[i];
		bitmap.setPixels(newpix,0,bitmap.getWidth(),0,0,bitmap.getWidth(),bitmap.getHeight());
		//BlurTransformer.fastblur(bitmap,1);
		return bitmap;
	}

private int random(Random random,int radius){
	return random.nextInt(radius)+1;
}
	
}
