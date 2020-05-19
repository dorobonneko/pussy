package com.moe.pussy;
import android.graphics.Bitmap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

public class Resource
{
	String key;
	Image image;
	int count;
	private Lock lock=new ReentrantLock();
	private OnResourceListener listener;
	protected Resource(String key,Image bitmap){
		this.key=key;
		this.image=bitmap;
	}
	public void acquire(){
		lock.lock();
		try{
		count++;}finally{
			lock.unlock();
		}
	}
	public void release(){
		lock.lock();
		try{
		if(--count==0)
			listener.onResourceRelease(this);
			}finally{lock.unlock();}
	}
	public void setOnResourceListener(OnResourceListener l){
		listener=l;
	}
	public interface OnResourceListener{
		void onResourceRelease(Resource res);
	}
}
