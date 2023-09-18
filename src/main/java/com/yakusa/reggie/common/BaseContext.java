package com.yakusa.reggie.common;


//基于ThreadLocal封装工具类,用于保存和获取当前登录用户id

public class BaseContext {
    //定义一个ThreadLocal对象
    private static ThreadLocal<Long> threadLocal=new ThreadLocal<>();

    //保存当前登录用户id
    public static void saveUserId(Long id){
        threadLocal.set(id);
    }

    //获取当前登录用户id
    public static Long getUserId(){
        return threadLocal.get();
    }

    /**
     * 设置值
     * @param id
     */
    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    /**
     * 获取值
     * @return
     */
    public static Long getCurrentId(){
        return threadLocal.get();
    }

}





