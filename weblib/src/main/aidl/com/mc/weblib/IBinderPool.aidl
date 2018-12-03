// IBinderPool.aidl
package com.mc.weblib;

//IBinderPool用于满足调用方根据不同类型获取不同的Binder
interface IBinderPool {

   IBinder queryBinder(int binderCode); //查找特定Binder的方法
}
