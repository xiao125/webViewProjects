package com.proxy.util;

import android.os.Environment;

public class YunWaConstants {
	
	public static final String path = Environment.getExternalStorageDirectory().toString() + "/im_sdk_voice";
	public static final String voice_path = path + "/voice/";
	public static final int channel_game_world = 1;
	public static final int channel_game_camp = 2;
	public static final int channel_game_gang = 3;
	public static final String channel_word_wildcard = "0x001";
	public static final String channel_now_wildcard = "0x002";
	public static final String channel_gang_wildcard = "0x003";
	public static final String gameservicesId = "1";
	
}
