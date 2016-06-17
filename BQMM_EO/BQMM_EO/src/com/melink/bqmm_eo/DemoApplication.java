package com.melink.bqmm_eo;

import android.app.Application;

import com.melink.bqmmsdk.resourceutil.BQMMConstant;
import com.melink.bqmmsdk.sdk.BQMM;

public class DemoApplication extends Application{
	
	@Override
	public void onCreate() {
		super.onCreate();	
		BQMM.getInstance().initConfig(getApplicationContext(),"YOUR_APPID", "YOUR_APPSECRET");	
	}
}
