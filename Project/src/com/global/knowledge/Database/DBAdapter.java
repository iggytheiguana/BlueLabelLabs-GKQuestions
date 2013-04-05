package com.global.knowledge.Database;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.global.knowledge.itil.Constant;

public class DBAdapter extends SQLiteOpenHelper implements Constant {
	public static String Lock = "DBlock";
	private static String DB_PATH = "";
	private static final String DB_NAME = "CCNA.s3db";
	private SQLiteDatabase myDataBase;
	private final Context myContext;
	private static DBAdapter mDBConnection;

	/**
	 * Constructor Takes and keeps a reference of the passed context in order to
	 * access to the application assets and resources.
	 * 
	 * @param context
	 */
	private DBAdapter(Context context) {
		super(context, DB_NAME, null, 1);
		this.myContext = context;
		DB_PATH = "/data/data/"
				+ context.getApplicationContext().getPackageName()
				+ "/databases/";

	}

	/**
	 * getting Instance
	 * 
	 * @param context
	 * @return DBAdapter
	 */
	public static synchronized DBAdapter getDBAdapterInstance(Context context) {
		if (mDBConnection == null) {
			mDBConnection = new DBAdapter(context);
		}
		return mDBConnection;
	}

	public static synchronized DBAdapter getDBAdapterInstance() {
		return mDBConnection;
	}

	/**
	 * Creates an empty database on the system and rewrites it with your own
	 * database.
	 **/
	public void createDataBase() throws IOException {

		boolean dbExist = checkDataBase();
		if (dbExist) {
		} else {
			this.getReadableDatabase();
			try {
				copyDataBase();
			} catch (IOException e) {
				throw new Error("Error copying database");
			}
		}
	}

	/**
	 * Check if the database already exist to avoid re-copying the file each
	 * time you open the application.
	 * 
	 * @return true if it exists, false if it doesn't
	 */
	private boolean checkDataBase() {
		File dbFile = new File(DB_PATH + DB_NAME);
		return dbFile.exists();

	}

	/**
	 * Copies your database from your local assets-folder to the just created
	 * empty database in the system folder, from where it can be accessed and
	 * handled. This is done by transfering bytestream.
	 * */
	private void copyDataBase() throws IOException {
		// Open your local db as the input stream
		InputStream myInput = myContext.getAssets().open(DB_NAME);
		// Path to the just created empty db
		String outFileName = DB_PATH + DB_NAME;
		// Open the empty db as the output stream
		OutputStream myOutput = new FileOutputStream(outFileName);
		// transfer bytes from the inputfile to the outputfile
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}
		// Close the streams
		myOutput.flush();
		myOutput.close();
		myInput.close();
	}

	/**
	 * Open the database
	 * 
	 * @throws SQLException
	 */
	public void openDataBase() throws SQLException {
		String myPath = DB_PATH + DB_NAME;
		myDataBase = SQLiteDatabase.openDatabase(myPath, null,
				SQLiteDatabase.NO_LOCALIZED_COLLATORS);
	}

	/**
	 * Close the database if exist
	 */
	@Override
	public synchronized void close() {
		if (myDataBase != null)
			myDataBase.close();
		super.close();
	}

	/**
	 * Call on creating data base for example for creating tables at run time
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
	}

	/**
	 * can used for drop tables then call onCreate(db) function to create tables
	 * again - upgrade
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	// ----------------------- CRUD Functions ------------------------------

	/**
	 * This function used to select the records from DB.
	 * 
	 * @param tableName
	 * @param tableColumns
	 * @param whereClase
	 * @param whereArgs
	 * @param groupBy
	 * @param having
	 * @param orderBy
	 * @return A Cursor object, which is positioned before the first entry.
	 */
	public Cursor selectRecordsFromDB(String tableName, String[] tableColumns,
			String whereClase, String whereArgs[], String groupBy,
			String having, String orderBy) {
		return myDataBase.query(tableName, tableColumns, whereClase, whereArgs,
				groupBy, having, orderBy);
	}

	/**
	 * select records from db and return in list
	 * 
	 * @param tableName
	 * @param tableColumns
	 * @param whereClase
	 * @param whereArgs
	 * @param groupBy
	 * @param having
	 * @param orderBy
	 * @return ArrayList>
	 */
	/*
	 * public ArrayList selectRecordsFromDBList(String tableName, String[]
	 * tableColumns, String whereClase, String whereArgs[], String groupBy,
	 * String having, String orderBy) {
	 * 
	 * ArrayList retList = new ArrayList>(); ArrayList list = new ArrayList();
	 * Cursor cursor = myDataBase.query(tableName, tableColumns, whereClase,
	 * whereArgs, groupBy, having, orderBy); if (cursor.moveToFirst()) {
	 */
	/*
	 * do { list = new ArrayList(); for(int i=0; ) }
	 */

	/**
	 * This function used to update the Record in DB.
	 * 
	 * @param tableName
	 * @param initialValues
	 * @param whereClause
	 * @param whereArgs
	 * @return 0 in case of failure otherwise return no of row(s) are updated
	 */
	public int updateRecordsInDB(String tableName, ContentValues initialValues,
			String whereClause, String whereArgs[]) {
		return myDataBase.update(tableName, initialValues, whereClause,
				whereArgs);
	}

	/**
	 * This function used to delete the Record in DB.
	 * 
	 * @param tableName
	 * @param whereClause
	 * @param whereArgs
	 * @return 0 in case of failure otherwise return no of row(s) are deleted.
	 */
	public int deleteRecordInDB(String tableName, String whereClause,
			String[] whereArgs) {
		return myDataBase.delete(tableName, whereClause, whereArgs);
	}

	// --------------------- Select Raw Query Functions ---------------------

	/**
	 * apply raw Query
	 * 
	 * @param query
	 * @param selectionArgs
	 * @return Cursor
	 */
	public Cursor selectRecordsFromDB(String query, String[] selectionArgs) {
		return myDataBase.rawQuery(query, selectionArgs);
	}
}
