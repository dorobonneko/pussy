package com.moe.pussy;

public interface NetListener
{
	void onStart(Request r);
	void onEnd(Request r,Throwable e);
	void onProgress(long current,long length,Request r);
}
