package com.mc.weblib.interfaces;
import android.content.Context;

import com.mc.weblib.view.X5WebView;

import java.util.Map;


public interface Command {

    String name();

    void exec(Context context, Map params, ResultBack resultBack);

}
