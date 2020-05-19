package com.moe.pussy;
import android.graphics.drawable.Drawable;

public interface Listener
{
	void onPlaceHolder(Target t,Drawable d);
	void onSuccess(Target t,Drawable d);
	void onError(Target t,Drawable d);
}
