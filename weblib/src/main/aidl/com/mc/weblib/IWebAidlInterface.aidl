// IWebAidlInterface.aidl
package com.mc.weblib;
import com.mc.weblib.IWebAidlCallback;

interface IWebAidlInterface {

     /**
      *   这里把WebView进程对主进程的每一个调用看做一次action， 每个action都会有唯一的actionName,
      *   主进程会提前注册好这些action，action 也有级别level，每次调用结束通过IWebAidlCallback返回结果
      *
      * actionName: 不同的action， jsonParams: 需要根据不同的action从map中读取并依次转成其他
      */
      void handleWebAction(int level, String actionName, String jsonParams, in IWebAidlCallback callback);
}
