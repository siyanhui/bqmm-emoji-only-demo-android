package com.melink.bqmm_eo;

import android.app.Application;

import com.melink.bqmmsdk.resourceutil.BQMMConstant;
import com.melink.bqmmsdk.sdk.BQMM;

public class DemoApplication extends Application{
	
	@Override
	public void onCreate() {
		super.onCreate();
		BQMM.getInstance().initConfig(getApplicationContext(),"925ee31c6dc649bcb7ddac5c0079c4ab", "662c0dd3d15b41bf86772670bbfa607d");	
//		BQMM.getInstance().initConfig(getApplicationContext(),"YOUR_APPID", "YOUR_APPSECRET");	
	}
}
