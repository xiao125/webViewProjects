// IWebAidlCallback.aidl
package com.mc.weblib;

//aidl调用后异步回调的接口

interface IWebAidlCallback {
    // responseCode 分为：成功 1， 失败 0. 失败时response返回{"code": 1, "message":"error message"}
  void onResult(int responseCode, String actionName, String response);
}
