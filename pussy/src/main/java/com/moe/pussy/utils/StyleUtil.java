package com.moe.pussy.utils;
import android.content.Context;
import android.content.res.TypedArray;

public class StyleUtil
{
	public static int getColor(Context c,int attr){
		TypedArray ta=c.obtainStyledAttributes(new int[]{attr});
		int color=ta.getColor(0,0xff0000);
		ta.recycle();
		return color;
	}
}
