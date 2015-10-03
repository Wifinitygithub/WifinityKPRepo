package wifinity.nfc;

import java.io.File;
import java.io.IOException;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBClass extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "NFCAPPS.db";
	private static final int DATABASE_VERSION = 1;
	public static final String TABLE_NAME = "NFC_TABLE";
	public static final String COLUMN_TAG_ID = "TAG_ID";
	public static final String COLUMN_LONG = "LONGITUDE";
	public static final String COLUMN_LATI = "LATITUDE";
	public static final String COLUMN_LOCATION = "LOCATION";
	public static final String COLUMN_MOB_ID = "MOBILE_ID";
	public static final String COLUMN_CURR="CURR_DATETIME";
	private SQLiteDatabase sqlDB;
	public Cursor C;
	public Context contextDb;

	public DBClass(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		contextDb = context;
		sqlDB = getWritableDatabase();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		/*arg0.execSQL("Create table " + TABLE_NAME + "( " + COLUMN_TAG_ID + ","
				+ COLUMN_MOB_ID + "," + COLUMN_LONG + "," + COLUMN_LATI + ","
				+ COLUMN_LOCATION +","+ COLUMN_CURR+ ");");*/
		
		String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
				+ COLUMN_TAG_ID + " TEXT ," + COLUMN_MOB_ID + " TEXT,"
				+ COLUMN_LONG + " DOUBLE," + COLUMN_LATI + " DOUBLE," + COLUMN_LOCATION + " TEXT," + COLUMN_CURR + " TEXT " + ")";
		db.execSQL(CREATE_TABLE);
		
		// db.execSQL("INSERT INTO "+ TABLE_NAME
		// +"  VALUES ('0001','000','RAJL','RAJL','N','2');");
		// db.execSQL("INSERT INTO "+ TABLE_NAME
		// +"  VALUES ('0002','000','LS01','12345','N','1');");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

		// Create tables again
		onCreate(db);

	}
	
	// Getting Particular RFID count
		public int getParticularRFIDCount(String tagId) {
			String countQuery = "SELECT "+COLUMN_TAG_ID+" FROM "+TABLE_NAME+ " WHERE " +COLUMN_TAG_ID+"="+tagId+"";
			if (sqlDB.isOpen()) {
				sqlDB.close();
			}
			sqlDB = getReadableDatabase();
			Cursor cursor = sqlDB.rawQuery(countQuery, null);
			int count = cursor.getCount();
			cursor.close();
			sqlDB.close();
			return count;
		}

	public void deleteTbldata(String table) {
		if (sqlDB.isOpen()) {
			sqlDB.close();
		}
		sqlDB = getWritableDatabase();
		sqlDB.delete(table, null, null);

		sqlDB.close();
	}

	public long insertRFID(ContentValues cv, String tablename) {
		long i = 0;
		if (sqlDB.isOpen()) {
			sqlDB.close();
		}
		sqlDB = getWritableDatabase();
		i = sqlDB.insert(tablename, null, cv);
		sqlDB.close();
		return i;
	}

	public File writeToFile(String fileName) {
		//===boolean done = false;
		// sqlDB.close();
		if (sqlDB.isOpen()) {
			sqlDB.close();
		}
		sqlDB = getWritableDatabase();

		try {
			//Log.d("calling..", "writeToFile()");
			File file= new FileExport(sqlDB, contextDb)
					.exportTable(DBClass.TABLE_NAME,0,fileName);
			
			//done = true;
			return file;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			NFC.done=false;
			NFC.msgs="Exception while writing,"+e.getMessage();
			return null;
		} finally {
			sqlDB.close();
		}

	}
}
