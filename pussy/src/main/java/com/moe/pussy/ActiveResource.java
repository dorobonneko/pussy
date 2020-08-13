package com.moe.pussy;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import android.graphics.Bitmap;
import java.util.Hashtable;

public class ActiveResource implements Resource.OnResourceListener
{
	private Map<String,Resource> list=new Hashtable<>();
	private Pussy pussy;
	public ActiveResource(Pussy pussy){
		this.pussy=pussy;
	}
	public int size(){
		return list.size();
	}
	public Resource create(String key, Image bitmap)
	{
		//if(key==null||bitmap==null||bitmap.isRecycled())throw new RuntimeException("create resource error, key android bitmap must not null and not recycle");
		Resource res=get(key);
		if(res==null){
			res=new Resource(key,bitmap);
		add(res);
		}
		return res;
	}

	public void clear()
	{
		Iterator iterator=list.entrySet().iterator();
		while(iterator.hasNext()){
			Map.Entry<String,Resource> item=(Map.Entry<String, Resource>) iterator.next();
			iterator.remove();
			pussy.getMemoryCache().put(item.getKey(),item.getValue().image);

		}
	}
	protected void add(Resource res){
		res.setOnResourceListener(this);
		list.put(res.key,res);
	}
	public Resource get(String key){
		if(key==null)return null;
		Resource res=list.get(key);
		return res;
	}
	public void recycle(String key){
		if(key==null)return;
		Resource res=list.get(key);
		if(res!=null)
			res.release();
	}
	protected Resource remove(String key){
		if(key==null)return null;
		return list.remove(key);
	}
	@Override
	public void onResourceRelease(final Resource res)
	{
		remove(res.key);
		res.setOnResourceListener(null);
		//res.image.getBitmap().recycle();
		pussy.getMemoryCache().put(res.key,res.image);
		res.image.recycle();
		res.image=null;
		
	}

	@Override
	public String toString()
	{
		StringBuilder sb=new StringBuilder();
		for(Map.Entry<String,Resource> entry:list.entrySet())
		sb.append(entry.getKey()).append("##").append(entry.getValue().toString()).append("\n");
		return sb.toString();
	}

	
}
