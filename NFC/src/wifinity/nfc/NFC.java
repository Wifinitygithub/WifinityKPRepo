package wifinity.nfc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.R.string;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class NFC extends Activity implements OnClickListener {

	List<Address> user = null;
	static double lat;
	static double lng;
	Context context;
	String id;
	String mobID;
	public static String msgs = "";
	public static int count = 0;
	public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
	public static boolean isWifiOn = false;

	private ProgressDialog mProgressDialog;
	GPSTracker gps;
	public static boolean done = true;
	
	/*Enhancement on 10/12/13*/
	TextView latTV , longTV;
	//EditText latVal, longVal;
	
	private BufferedReader r;
	EditText latVal;
	EditText longVal;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//latTV = (TextView)findViewById(R.id.latitudeLbl);
		//longTV = (TextView)findViewById(R.id.longitudeLbl);
		//latVal = (EditText)findViewById(R.id.latval);
		//longVal = (EditText)findViewById(R.id.longval);
		
		context = this;
		done = true;
		if(!isNetworkConnected()){
		GPRSSettings.setMobileDataEnabled(getApplicationContext(), true);
		isWifiOn=isNetworkConnected();
		}
        //showDialogBox("mesage", Boolean.toString(isWifiOn));
		setContentView(R.layout.nfc);
		gps = new GPSTracker(this);
		TelephonyManager tm = (TelephonyManager) this
				.getSystemService(Context.TELEPHONY_SERVICE);
		mobID = tm.getDeviceId();
		EditText setMobId = (EditText) findViewById(R.id.mobIdVal);
		setMobId.setText(mobID);
		Button save = (Button) findViewById(R.id.saveBtn);
		save.setOnClickListener(this);
		Button send = (Button) findViewById(R.id.sendBtn);
		send.setOnClickListener(this);
		Intent intent = getIntent();
		processIntent(intent);
	}
	private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
        	isWifiOn=false;
         // There are no active networks.
         return false;
        } else{
        	isWifiOn=true;
         return true;}
       }
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_DOWNLOAD_PROGRESS:
			mProgressDialog = new ProgressDialog(this);
			mProgressDialog.setMessage("Operation in progress..");
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mProgressDialog.setCancelable(false);
			mProgressDialog.show();
			return mProgressDialog;
		default:
			return null;
		}
	}

	public void updateLoc() {

		
		gps.getLocation();
		if (gps.canGetLocation()) {
			lat = gps.getLatitude(); // returns latitude
			lng = gps.getLongitude(); // returns longitude
			
		} else {
			showDialogBox("Error", "Error while getting Location");
			gps.showSettingsAlert();
		}

	}

	public void clearFields() {
		EditText Rfid = (EditText) findViewById(R.id.tagIdVal);
		Rfid.setText("");
		EditText loc = (EditText) findViewById(R.id.locVal);
		loc.setText("");
		latVal = (EditText)findViewById(R.id.latval);
		latVal.setText("");
		longVal = (EditText)findViewById(R.id.longval);
		longVal.setText("");
	}

	public void insertTags() {
		try {
			Log.i("insertTags", latVal.getText().toString().trim());
			lat = Double.parseDouble(latVal.getText().toString().trim());
			lng = Double.parseDouble(longVal.getText().toString().trim());
			boolean err = false;
			EditText getLocEdt = (EditText) findViewById(R.id.locVal);
			String getLocval = getLocEdt.getText().toString();
			if ((getLocval != null) && (!getLocval.equals(""))) {
				Log.i("getLocval", "getLocval");
			} else {
				Log.i("NO Location", "NO Location");
				err = true;
				showDialogBox("Error", "Please enter Location");

			}
			if ((id != null) && (!id.equals(""))) {

			} else {
				err = true;
				showDialogBox("Error", "Please enter RFID");

			}
			if (!err) {
				Log.i("!err", "!err");
				Date date1 = new Date();
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
				String date = dateFormat.format(date1);
				//long curr =  System.currentTimeMillis();
				if((lat==0)&&(lng==0)){
					Toast.makeText(getApplicationContext(), "Latitude & Longitutde values are wrong! Please try again", Toast.LENGTH_SHORT).show();
				}else if((latVal.getText().toString().trim().equals(0)) && (longVal.getText().toString().trim().equals(0))){
					//Log.i("lat and long", "zero");
					Toast.makeText(getApplicationContext(), "Latitude & Longitutde values are wrong! Please try again", Toast.LENGTH_LONG).show();
				}else{
					int rfidCount = new DBClass(context)
					.getParticularRFIDCount(id);
					if(rfidCount>0){
						showDialogBox("Warning!", "Tag is already registered");
					}else{
						ContentValues cv = new ContentValues();
						cv.put(DBClass.COLUMN_TAG_ID, id);
						cv.put(DBClass.COLUMN_LATI, lat);
						cv.put(DBClass.COLUMN_LONG, lng);
						cv.put(DBClass.COLUMN_MOB_ID, mobID);
						cv.put(DBClass.COLUMN_LOCATION, getLocval);
						cv.put(DBClass.COLUMN_CURR,date);
						long i = new DBClass(context)
								.insertRFID(cv, DBClass.TABLE_NAME);
						if (i >= 0) {
							showDialogBox("Message", "The row is inserted successfully");
							clearFields();
						}
					}
					
				}
			}

		} catch (Exception E) {
			E.printStackTrace();
		}
	}

	private boolean checkforconnection() {
		boolean conn = false;

		try {
			// TODO Auto-generated method stub
			WifiManager wifi = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			// Now the object wifi of WifiManager class is used to get the wifi
			// status:
			if (wifi.isWifiEnabled()) {
				conn = true;

			} else {
				conn = true;
				wifi.setWifiEnabled(true);

			}

		} catch (Exception e) {
			msgs = "unable to acess";
			// showNoticeDialogBox("unable to acess", e.getMessage());
		}

		// try {
		// // TODO Auto-generated method stub
		// WifiManager wifi = (WifiManager) context
		// .getSystemService(Context.WIFI_SERVICE);
		// // Now the object wifi of WifiManager class is used to get the wifi
		// // status:
		// if (wifi.isWifiEnabled()) {
		// conn = true;
		// // showNoticeDialogBox("show","enabled");
		// } else {
		// conn = true;
		// wifi.setWifiEnabled(true);
		// // showNoticeDialogBox("show","all ready");
		// }
		//
		// } catch (Exception e) {
		// done=false;
		// msgs= e.getMessage();
		// //showDialogBox("unable to acess", e.getMessage());
		// }
		return conn;

	}

	public void showDialogBox(final String title, final String message) {
		Builder setupAlert;
		setupAlert = new AlertDialog.Builder(this).setTitle(title)
				.setMessage(message)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// do nothing coz it's just a notice
					}
				});
		setupAlert.show();
	}

	public void processIntent(Intent intent) {
		id = "";
		boolean msb = false;
		String newString = null;
		String val;
		String action = intent.getAction();
		if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
				|| NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
				|| NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
			Parcelable p = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			Tag tag = (Tag) p;
			byte[] id1 = tag.getId();
			Log.i("Byte value", String.valueOf(id1));
			
			long tagVal = getDec(id1);
			Log.i("Long value", String.valueOf(tagVal));
			
			/*Code snippet to convert the tag values*/
			int hVal = (int)tagVal;
			String hexValue = Integer.toHexString(hVal);						//Integer value of Tag to HexString
			//String hexValue = "6ef2dba0";
			Log.i("hexValue", String.valueOf(hexValue));
			
			for (int iVal = 0; iVal < hexValue.length(); iVal += 2) {			//Iterating over MSB values
				if (String.valueOf(hexValue.charAt(iVal)).equals("0")) {
					msb = true;
				}
			}
			
			if(msb==true){
				newString = getMsbValue(hexValue);								//Method to convert Tag value
				Log.i("newString", newString);					
				
				long tagValwithZero = Long.parseLong(newString, 16);			//newString from getMsbValue() will be parsed to Long
				Log.i("hexLong", String.valueOf(tagValwithZero));
				
				val = Long.toString(tagValwithZero);
				
			}else{
				val = Long.toString(tagVal);
			}
			
			
			
			/*String tagValueStr = "1645198733";
			long lStr = Long.parseLong(tagValueStr);
			int iVal = (int)lStr;
			String hexStr = Integer.toHexString(iVal);
			Log.i("hexStr-->", String.valueOf(hexStr));*/
			
			
			
			
			
			
			EditText tagEdit = (EditText) findViewById(R.id.tagIdVal);
			tagEdit.setText(val);
			
			
			/*Advanced NFC snippet
			
			Tag tag1 = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			NFC-A (ISO 14443-3A) protocol
			NfcA nfcA = NfcA.get(tag1);
			try{
	            nfcA.connect();
	            Tag tA = nfcA.getTag();
	            byte[] bA = tA.getId();
	            long tagAVal = getDec(bA);
				String valA = Long.toString(tagAVal);
	            Toast.makeText(getApplicationContext(), "NfcA- "+valA, Toast.LENGTH_SHORT).show();
	            Short s = nfca.getSak();
	            byte[] a = nfca.getAtqa();
	            String atqa = new String(a, Charset.forName("US-ASCII"));
	            Toast.makeText(getApplicationContext(), "SAK = "+s+"\nATQA = "+atqa, Toast.LENGTH_SHORT).show();
	            nfcA.close();
	        }
	        catch(Exception e){
	            Log.e("ERROR", "Error when reading NfcA-Tag");
	            Toast.makeText(getApplicationContext(), "NfcA-Tag Error", Toast.LENGTH_SHORT).show();
	        }
			
			NFC-B (ISO 14443-3B) protocol
			NfcB nfcB = NfcB.get(tag1);
			try{
				nfcB.connect();
	            Tag tB = nfcB.getTag();
	            byte[] bB = tB.getId();
	            long tagBVal = getDec(bB);
				String valB = Long.toString(tagBVal);
	            Toast.makeText(getApplicationContext(), "NfcB- "+valB, Toast.LENGTH_SHORT).show();
	            nfcB.close();
	        }
	        catch(Exception e){
	            Log.e("ERROR", "Error when reading NfcB-Tag");
	            Toast.makeText(getApplicationContext(), "NfcB-Tag Error", Toast.LENGTH_SHORT).show();
	        }
			
			NFC-F (JIS 6319-4) protocol
			NfcF nfcF = NfcF.get(tag1);
			try{
				nfcF.connect();
	            Tag tF = nfcF.getTag();
	            byte[] bF = tF.getId();
	            long tagFVal = getDec(bF);
				String valF = Long.toString(tagFVal);
	            Toast.makeText(getApplicationContext(), "NfcF- "+valF, Toast.LENGTH_SHORT).show();
	            nfcF.close();
	        }
	        catch(Exception e){
	            Log.e("ERROR", "Error when reading NfcF-Tag");
	            Toast.makeText(getApplicationContext(), "NfcF-Tag Error", Toast.LENGTH_SHORT).show();
	        }
			
			NFC-V (ISO 15693) protocol
			NfcV nfcV = NfcV.get(tag1);
			try{
				nfcV.connect();
	            Tag tV = nfcV.getTag();
	            byte[] bV = tV.getId();
	            long tagVVal = getDec(bV);
				String valV = Long.toString(tagVVal);
	            Toast.makeText(getApplicationContext(), "NfcV- "+valV, Toast.LENGTH_SHORT).show();
	            nfcV.close();
	        }
	        catch(Exception e){
	            Log.e("ERROR", "Error when reading NfcV-Tag");
	            Toast.makeText(getApplicationContext(), "NfcV-Tag Error", Toast.LENGTH_SHORT).show();
	        }
			
			Advanced NFC snippet ends*/
			
			
			id = val;
			//updateLoc();
			gps.getLocation();
			if (gps.canGetLocation()) {
				lat = gps.getLatitude(); // returns latitude
				lng = gps.getLongitude(); // returns longitude
				latVal = (EditText)findViewById(R.id.latval);
				longVal = (EditText)findViewById(R.id.longval);
				latVal.setText(String.valueOf(lat));
				longVal.setText(String.valueOf(lng));
				Log.i("lat value", String.valueOf(lat));
				Log.i("long value", String.valueOf(lng));
				
			} else {
				showDialogBox("Error", "Error while getting Location");
				gps.showSettingsAlert();
			}

		}
	}

	private long getDec(byte[] bytes) {
		long result = 0;
		long factor = 1;
		for (int i = 0; i < bytes.length; ++i) {
			long value = bytes[i] & 0xffl;
			result += value * factor;
			factor *= 256l;
		}
		return result;
	}
	
	
	public String getMsbValue(String hexValue) {							//Get no. of zeros in MSB and adds it to end of the string
		int i, ctr = 0;
		int j = 0;
		String convertedHexValue = "";
		int startVal = 0;
		int[] intArray = new int[hexValue.length()];

		for (i = 0; i < hexValue.length(); i += 2) {
			if (String.valueOf(hexValue.charAt(i)).equals("0")) {
				intArray[ctr++] = i;										//if MSB value contains "0" and its position will be added to dynamic array
				j = j + 1;													//count of zero 
			}
		}

		for (int m = 0; m < ctr; m++) {										//Iterate over array value 
			i = intArray[m];												
			convertedHexValue = convertedHexValue.concat(hexValue.substring(startVal, i));	// creates new string by leaving MSB string if its zero 
			startVal = i + 1;
		}
		
		convertedHexValue = convertedHexValue.concat(hexValue.substring((i + 1),hexValue.length()));	//adds substring from last MSB value to end of the string
		Log.i("convertedHexValue", convertedHexValue);
		for (int jVal = 1; jVal <= j; jVal++) {								//Iterate over count of zeros and adds zero at the end of string
			convertedHexValue = convertedHexValue.concat("0");
		}

		return convertedHexValue;
	}
	
	

	
	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		setIntent(intent);
		processIntent(intent);

	}

	private void sendTag() {
		// TODO Auto-generated method stub
		File file = new DBClass(this).writeToFile(mobID);
		//Log.d("calling..", "sendTag()");
		try {
			

			if (count > 0) {
				// showDialogBox("Transfer Success", String.valueOf(count)
				// + " data  uploaded");
				uploadFile(file);
				done=true;
				msgs = String.valueOf(count) + " data  uploaded";
				new DBClass(this).deleteTbldata(DBClass.TABLE_NAME);
				file.delete();
			} else {
				done=false;
				msgs = "No  data found to upload";
				// showDialogBox("Transfer error", "No  data found");

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			done = false;
			msgs = "IO Excep "+e.getMessage();
			// e.printStackTrace();
		}

	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.saveBtn:
			insertTags();
			break;
		case R.id.sendBtn: {
			new UploadFile().execute();
			// sendTag();
			// transferTagdone();
			// showDialogBox("msg", msgs);
			break;
		}
		}
	}

	public void transferTagdone() {
		boolean addrStatus = false;
		try {
			msgs = "connection";
			//isWifiOn = checkforconnection();
			if(!isNetworkConnected()){
				GPRSSettings.setMobileDataEnabled(getApplicationContext(), true);
				isWifiOn=true;
			}
			Thread.sleep(25000);
			try {
				if (isWifiOn) {
					addrStatus = InetAddress.getByName("192.168.5.245")
							.isReachable(5000);
					if (addrStatus) {

						sendTag();

					} else {
						done = false;
						msgs = "Unable to reach Server";
						// showDialogBox("Transfer Error",
						// "Unable to reach Server");
					}
				} else {
					done = false;
					msgs = "Unable to turn on Wifi";
					// showDialogBox("Transfer Error",
					// "Unable to turn on Wifi");
				}
			} catch (Exception e) {
				done = false;
				msgs = "Excep "+e.getMessage();
				// showDialogBox("Transfer Error", e.getMessage());
			}

		} catch (Exception e) {
			done = false;
			msgs = "Unable to Switch On Wifi";
			// showDialogBox("Transfer Error", "Unable to Switch On Wifi");
		}

	}

	public void uploadFile(File file) throws IOException {

		FileInputStream fis = null;
		// fis = contextDb.openFileInput();
		fis = new FileInputStream(file);

		DefaultHttpClient httpClient = new DefaultHttpClient();
		String msg = null;

		int respCode = -1;
		String uploadUrlStr = "http://192.168.5.245:8080/Beat/registrationupload?id="
				+ mobID;
		try {
			HttpPost httpPost = new HttpPost(uploadUrlStr);

			r = new BufferedReader(new InputStreamReader(fis));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = r.readLine()) != null) {
				sb.append(line);
				sb.append('\n');
			}
			httpPost.setEntity(new StringEntity(sb.toString()));
			HttpResponse resp = httpClient.execute(httpPost);
			respCode = resp.getStatusLine().getStatusCode();
			if (respCode == HttpStatus.SC_OK) {
				/* Delete the file */

				msgs = "Uploaded.";
			} else
				msgs = resp.getStatusLine().getReasonPhrase();
		} finally {
			if (fis != null)
				fis.close();
		}

	}

	class UploadFile extends AsyncTask<Object, Object, Object> {

		@Override
		protected Object doInBackground(Object... arg0) {
			// TODO Auto-generated method stub
			// transferTagdone();
			// sendTag();
			boolean addrStatus = false;

			

				//isWifiOn = checkforconnection();
				if(!isWifiOn){
					GPRSSettings.setMobileDataEnabled(getApplicationContext(), true);
					isWifiOn=isNetworkConnected();
				}
//				try {
//					Thread.sleep(25000);
//				} catch (InterruptedException e1) {
//					// TODO Auto-generated catch block
//					msgs=e1.getMessage();
//				}
				try {
					if (isWifiOn) {
						addrStatus = InetAddress.getByName("192.168.5.245")
								.isReachable(5000);
						if (addrStatus) {
							msgs = "sending";
							sendTag();
							return null;
						} else {
							mProgressDialog.dismiss();
							//dismissDialog(DIALOG_DOWNLOAD_PROGRESS);
							done = false;
							msgs = "Unable to reach Server";
							return null;
							// showDialogBox("Transfer Error",
							// "Unable to reach Server");
						}
					} else {
						mProgressDialog.dismiss();
						//dismissDialog(DIALOG_DOWNLOAD_PROGRESS);
						done = false;
						msgs = "Please Turn on Wifi/GPRS" ;
						return null;
						// showDialogBox("Transfer Error",
						// "Unable to turn on Wifi");
					}
				} catch (Exception e) {
					done = false;
					msgs = "doinbackground "+e.getMessage();
					return null;
					// showDialogBox("Transfer Error", e.getMessage());
				}

			

			// return null;
		}

		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();
		}

		//
		//protected void onCancelled() {
			// TODO Auto-generated method stub
			//super.onCancelled();
//		}

		@Override
		protected void onPostExecute(Object result) {
			// TODO Auto-generated method stub
			if (done) {
				showDialogBox("Transfer Success", msgs);
				new DBClass(context).deleteTbldata(DBClass.TABLE_NAME);
			} else {
				showDialogBox("Transfer Error", msgs);

			}
			// showDialogBox("msg", msgs);
			msgs = "";
			//dismissDialog(DIALOG_DOWNLOAD_PROGRESS);
			mProgressDialog.dismiss();
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			//showDialog(DIALOG_DOWNLOAD_PROGRESS);
			mProgressDialog = new ProgressDialog(context);
			mProgressDialog.setMessage("Operation in progress..");
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mProgressDialog.setCancelable(false);
			mProgressDialog.show();
		}

		@Override
		protected void onProgressUpdate(Object... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		}

		@Override
		protected Object clone() throws CloneNotSupportedException {
			// TODO Auto-generated method stub
			return super.clone();
		}

		@Override
		public boolean equals(Object o) {
			// TODO Auto-generated method stub
			return super.equals(o);
		}

		@Override
		protected void finalize() throws Throwable {
			// TODO Auto-generated method stub
			super.finalize();
		}

		@Override
		public int hashCode() {
			// TODO Auto-generated method stub
			return super.hashCode();
		}

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return super.toString();
		}

	}
}
