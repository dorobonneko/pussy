package com.moe.pussy;
import java.lang.reflect.Method;
import java.lang.annotation.Annotation;

public class Async
{
	public static void init(Object o){
		Annotation[] mAnnotation=o.getClass().getAnnotationsByType(Ui.class);
	
	}
	public @interface Ui{}
	public @interface Background{}
	public @interface ThreadPool{}
}
