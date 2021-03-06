package wifinity.nfc;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.content.Context;
import android.net.ConnectivityManager;

public class GPRSSettings {
	 static void setMobileDataEnabled(Context context, boolean enabled) {
	        try {

	            final ConnectivityManager conman = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	          // final String conmanClass1= Class.forName(conman.getClass().getName());
	            final Class conmanClass = Class.forName(conman.getClass().getName());
	            final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
	            iConnectivityManagerField.setAccessible(true);
	            final Object iConnectivityManager = iConnectivityManagerField.get(conman);
	            final Class iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());
	            final Method setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
	            setMobileDataEnabledMethod.setAccessible(true);
	            setMobileDataEnabledMethod.invoke(iConnectivityManager, enabled);
	           
	        } 

	        catch (Exception e) 
	        {
	            e.printStackTrace();
	           
	        }         
	    }
}
