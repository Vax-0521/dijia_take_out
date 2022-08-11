package com.xyy.dijia.common;

/**
 * 基于ThreadLocal封装工具类，用户保存和获取当前登录用户id
 * ThreadLocal的常用方法：
 * public void set(T value) 设置当前线程的线程局部变量的值
 * public T get() 返回当前线程所对应的线程局部变量的值
 *
 */
public class BaseContext{
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    public static Long getCurrentId(){
        return threadLocal.get();
    }

}
