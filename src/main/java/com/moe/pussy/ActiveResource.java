package com.moe.pussy;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import android.graphics.Bitmap;

public class ActiveResource implements Resource.OnResourceListener
{
	private Map<String,Resource> list=new ConcurrentHashMap<>();
	private Pussy pussy;
	public ActiveResource(Pussy pussy){
		this.pussy=pussy;
	}

	public Resource create(String key, Image bitmap)
	{
		//if(key==null||bitmap==null||bitmap.isRecycled())throw new RuntimeException("create resource error, key android bitmap must not null and not recycle");
		Resource res=new Resource(key,bitmap);
		add(res);
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
		return list.get(key);
	}
	protected Resource remove(String key){
		if(key==null)return null;
		return list.remove(key);
	}
	@Override
	public void onResourceRelease(Resource res)
	{
		remove(res.key);
		pussy.getMemoryCache().put(res.key,res.image);
	}

	
}
