package com.mc.weblib.interfaces;

import android.os.Parcel;
import android.os.Parcelable;

/**建立要传递的对象类
 * 步骤：
 * 1.让你的对象类实现Parcelable接口
   2.在该类中实现writeToParcel方法
   3.添加一个静态构造器CREATOR
   4.创建你要传递的对象的aidl文件
 */

public class AidlError implements Parcelable {

    public int code;
    public String message;
    public String extra;

    public AidlError(int code, String message) {
        this.code = code;
        this.message = message;
    }

    //实现 Parcelable 接口所需重写的方法1，一般不用管
    @Override
    public int describeContents() {
        return 0;
    }

    //实现 Parcelable 接口所需重写的方法2，将对象的每个字段写入到Parcel中
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.code);
        dest.writeString(this.message);
        dest.writeString(this.extra);
    }

    public AidlError() {
    }

    //从Parcel中读出之前写进的数据
    protected AidlError(Parcel in) {
        this.code = in.readInt();
        this.message = in.readString();
        this.extra = in.readString();
    }

    //创建构造器CREATOR，注意要加final修饰且名字必须为CREATOR不能更改
    public static final Creator<AidlError> CREATOR = new Creator<AidlError>() {
        //反序列化，把我们通过writeToParcel方法写进的数据再读出来
        @Override
        public AidlError createFromParcel(Parcel source) {
            return new AidlError(source);
        }

        @Override
        public AidlError[] newArray(int size) {
            return new AidlError[size];
        }
    };
}
