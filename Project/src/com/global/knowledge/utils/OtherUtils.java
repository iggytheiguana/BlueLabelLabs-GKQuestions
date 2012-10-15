package com.global.knowledge.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class OtherUtils {

	 Context context;
	
	public boolean isNetworkAvailable(Context context) {
		    this.context = context;
		   
		   ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		   if (connectivity == null) {
		    //  boitealerte(this.getString(R.string.alertNoNetwork),"getSystemService rend null");
		   } else {
		      NetworkInfo[] info = connectivity.getAllNetworkInfo();
		      if (info != null) {
		         for (int i = 0; i < info.length; i++) {
		            if (info[i].getState() == NetworkInfo.State.CONNECTED) {
		               return true;
		            }
		         }
		      }
		   }
		   return false;
		}
	
}
