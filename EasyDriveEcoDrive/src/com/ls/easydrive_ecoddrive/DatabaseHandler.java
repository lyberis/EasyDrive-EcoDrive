package com.ls.easydrive_ecoddrive;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
 
public class DatabaseHandler extends SQLiteOpenHelper {
 
    // Database Version
    private static final int DATABASE_VERSION = 3;
 
    // Database Name
    private static final String DATABASE_NAME = "easydrivedb";
 
    // table name
    private static final String TABLE_HEADER = "tripheader";
    private static final String TABLE_DETAIL = "tripdetail";
    private static final String TABLE_UPARMS = "userparms";
    private static final String TABLE_GPARMS = "generalparms";
    private static final String TABLE_LEVELS = "levels";
    
    // Trip Header Table Columns names
    private static final String	H_TRIP_AA 		= "trip_aa";
    private static final String H_START_DATE 	= "start_date";
    private static final String H_START_TIME 	= "start_time";
    private static final String H_END_DATE 		= "end_date";
    private static final String H_END_TIME 		= "end_time";
    private static final String H_DISTANCE 		= "distance";
    private static final String H_AVG_SPD 		= "avg_spd";
    private static final String H_AVG_RPM 		= "avg_rpm";
    private static final String H_AVG_THR 		= "avg_thr";
    private static final String H_CALIBR_FLAG 	= "calibr_flag";
    private static final String H_LEVEL 		= "level";
    
    // Trip Detail Table Columns names
    private static final String	D_TRIP_AA 		= "trip_aa";
    private static final String	D_GAUGE_AA		= "gauge_aa";
    private static final String	D_TIMESTAMP		= "timestamp";
    private static final String	D_GAUGE_COD		= "gauge_cod";
    private static final String	D_GAUGE_VALUE	= "gauge_value";

    // User Parameter Table Columns names
    private static final String	U_LEVEL			= "level";
    private static final String	U_RPM_LIMIT		= "rpm_limit";
    private static final String	U_THR_LIMIT		= "thr_limit";
    private static final String	U_CALIBR_FLAG	= "calibr_flag";
    private static final String	U_LEVEL_RPM		= "level_rpm";
    private static final String	U_LEVEL_THR		= "level_thr";

    // General Parameter Table Columns names	
    private static final String	G_RPM_LIMIT_Y	= "rpm_limit_y";
    private static final String	G_THR_LIMIT_Y	= "thr_limit_y";
    private static final String	G_LEVEL_PRC_RPM	= "level_prc_rpm";
    private static final String	G_LEVEL_PRC_THR	= "level_prc_thr";
    private static final String	G_CALIBR_DIST	= "calibr_dist";
    private static final String	G_CALIBR_NUM	= "calibr_num";
    private static final String	G_VALID_DIST	= "valid_dist";
    
    // User Levels Table Columns names
    private static final String	L_CODE			= "code";
    private static final String	L_LEVEL			= "level";
    private static final String	L_LIMIT			= "limit_val";
    
    //destination path (location) of our database on device
    private static String TAG = "DatabaseHandler"; // Tag just for the LogCat window
    private static String DB_PATH = ""; 
    private SQLiteDatabase mDataBase; 
    private final Context mContext;
    
    public DatabaseHandler(Context context) {
              
        //super(context, DATABASE_NAME, null, DATABASE_VERSION);
        
        super(context, Environment.getExternalStorageDirectory()
                + File.separator + DATABASE_NAME, null, DATABASE_VERSION); 
        
//        if(android.os.Build.VERSION.SDK_INT >= 17){
//           DB_PATH = context.getApplicationInfo().dataDir + "/databases/";         
//        }else{
//           DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
//        }
        
        DB_PATH = Environment.getExternalStorageDirectory() + File.separator;         
                
        mContext = context;
        
    }
    
    // Adding Trip Header 
    public void addTrip(TripHeader tripH) {
    	
    	Log.d("addTrip: ", "Starts"); 
    	
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        
        values.put(H_TRIP_AA, tripH.getTrip_AA());  	
        values.put(H_START_DATE, tripH.getStart_Date());
        values.put(H_START_TIME, tripH.getStart_Time());
        values.put(H_END_DATE, tripH.getEnd_Date()); 	
        values.put(H_END_TIME, tripH.getEnd_Time()); 		
        values.put(H_DISTANCE, tripH.getDistance()); 	 		
        values.put(H_AVG_SPD, tripH.getAvg_Spd()); 	 		
        values.put(H_AVG_RPM, tripH.getAvg_Rpm()); 	 			
        values.put(H_AVG_THR, tripH.getAvg_Thr()); 	 			
        values.put(H_CALIBR_FLAG, tripH.getCalibr_Flag()); 	 	
        values.put(H_LEVEL, tripH.getLevel()); 	 			
        
        // Inserting Row
        db.insert(TABLE_HEADER, null, values);
        db.close(); // Closing database connection
        
        Log.d("addTrip: ", "Ends");
    }
    
    // Adding Trip Detail 
    public void addDetail(TripDetail tripDetail) {
    	
    	Log.d("addDetail: ", "Starts"); 
    	
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        
        values.put(D_TRIP_AA, tripDetail.getTrip_AA());
        values.put(D_GAUGE_AA, tripDetail.getGauge_AA());
        values.put(D_TIMESTAMP, tripDetail.getTimestamp());
        values.put(D_GAUGE_COD, tripDetail.getGauge_Cod());
        values.put(D_GAUGE_VALUE, tripDetail.getGauge_Value());
        
        // Inserting Row
        db.insert(TABLE_DETAIL, null, values);
        db.close(); // Closing database connection
        
        Log.d("addDetail: ", "Ends");
    }
    
    // Adding User Parameters Line 
    public void addUserParms(UserParms userParms) {
    	
    	Log.d("addUserParms: ", "Starts"); 
    	
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        
        values.put(U_LEVEL, userParms.getLevel());
        values.put(U_RPM_LIMIT, userParms.getRpm_Limit());
        values.put(U_THR_LIMIT, userParms.getThr_Limit());
        values.put(U_CALIBR_FLAG, userParms.getCalibr_Flag());
        values.put(U_LEVEL_RPM, userParms.getLevel_Rpm());
        values.put(U_LEVEL_THR, userParms.getLevel_Thr());
        
        // Inserting Row
        db.insert(TABLE_UPARMS, null, values);
        db.close(); // Closing database connection
        
        Log.d("addUserParms: ", "Ends");
    }
    
    // Adding General Parameters Line 
    public void addGeneralParms(GeneralParms generalParms) {
    	
    	Log.d("addGeneralParms: ", "Starts"); 
    	
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        
        values.put(G_RPM_LIMIT_Y, generalParms.getRpm_Limit_Y());
        values.put(G_THR_LIMIT_Y, generalParms.getThr_Limit_Y());
        values.put(G_LEVEL_PRC_RPM, generalParms.getLevel_Prc_RPM());
        values.put(G_LEVEL_PRC_THR, generalParms.getLevel_Prc_THR());
        values.put(G_CALIBR_DIST, generalParms.getCalibr_Dist());
        values.put(G_CALIBR_NUM, generalParms.getCalibr_Num());
        values.put(G_VALID_DIST, generalParms.getValid_Distance());
        
        // Inserting Row
        db.insert(TABLE_GPARMS, null, values);
        db.close(); // Closing database connection
        
        Log.d("addGeneralParms: ", "Ends");
    }
    
    // Adding Level Line 
    public void addLevel(Level level) {
    	
    	Log.d("addLevel: ", "Starts"); 
    	
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        
        values.put(L_CODE, level.getCode());
        values.put(L_LEVEL, level.getLevel());
        values.put(L_LIMIT, level.getLimit());
        
        // Inserting Row
        db.insert(TABLE_LEVELS, null, values);
        db.close(); // Closing database connection
        
        Log.d("addLevel: ", "Ends");
    }
    
    // Selects User Parameter line
    public UserParms getUserParms(){
    	
    	Log.d("getUserParms: ", "Starts");
    	
    	UserParms up = new UserParms();
    	String selQuery = "SELECT * FROM " + TABLE_UPARMS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selQuery, null);
        
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            up.setLevel(cursor.getInt(0));
            up.setRpm_Limit(cursor.getDouble(1));
            up.setThr_Limit(cursor.getDouble(2));
            up.setCalibr_Flag(cursor.getString(3));
            up.setLevel_Rpm(cursor.getInt(4));
            up.setLevel_Thr(cursor.getInt(5));
        }

        db.close(); // Closing database connection
        Log.d("getUserParms: ", "Ends");
        
        return up;
        
    }
    
    // Select General parameter line
    public GeneralParms getGeneralParms(){
    	
    	Log.d("getGeneralParms: ", "Starts");
    	
    	GeneralParms gp = new GeneralParms();
    	String selQuery = "SELECT * FROM " + TABLE_GPARMS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selQuery, null);
       
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            gp.setRpm_Limit_Y(cursor.getDouble(0));
            gp.setThr_Limit_Y(cursor.getDouble(1));
            gp.setLevel_Prc_RPM(cursor.getDouble(2));
            gp.setLevel_Prc_THR(cursor.getDouble(3));
            gp.setCalibr_Dist(cursor.getInt(4));
            gp.setCalibr_Num(cursor.getInt(5));
            gp.setValid_Distance(cursor.getInt(6));
        }

        db.close(); // Closing database connection
        Log.d("getGeneralParms: ", "Ends");
        
        return gp;
        
    }
    
    public int getMaxTrip(){
    	
    	Log.d("getMaxTrip: ", "Starts");
    	
    	int aa = 0; 
    	String selQuery = "SELECT max(trip_aa) FROM " + TABLE_HEADER;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selQuery, null);
        
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            aa = cursor.getInt(0);
        }else{
        	aa = 0;
        }

        db.close(); // Closing database connection
        Log.d("getMaxTrip: ", "Ends");
        
        return aa;
        
    }
    
    public double getSumDistance(double distance, String calibr_flag){
    	
    	Log.d("getSumDistance: ", "Starts");
    	
    	double sumDist = 0; 
    	String selQuery = "SELECT sum(distance) FROM " + TABLE_HEADER + " WHERE " +
    	                  "distance >= " + distance + " and calibr_flag = '" + calibr_flag + "'";  
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selQuery, null);
        
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            sumDist = cursor.getDouble(0);
        }else{
        	sumDist = 0;
        }

        db.close(); // Closing database connection
        Log.d("getSumDistance: ", "Ends");
        
        return sumDist;
        
    }
    
    public int getNumbOfTrips(double distance, String calibr_flag){
    	
    	Log.d("getNumbOfTrips: ", "Starts");
    	
    	int tripNum = 0; 
    	String selQuery = "SELECT count(*) FROM " + TABLE_HEADER + " WHERE " +
                "distance >= " + distance + " and calibr_flag = '" + calibr_flag + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selQuery, null);
        
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            tripNum = cursor.getInt(0);
        }else{
        	tripNum = 0;
        }

        db.close(); // Closing database connection
        Log.d("getNumbOfTrips: ", "Ends");
        
        return tripNum;
        
    }
 
    public void updateUserParm(UserParms up) {

    	Log.d("updateUserParm: ", "Starts");
    	
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        
        values.put(U_LEVEL, up.getLevel());
        values.put(U_RPM_LIMIT, up.getRpm_Limit());
        values.put(U_THR_LIMIT, up.getThr_Limit());
        values.put(U_CALIBR_FLAG, up.getCalibr_Flag());
        values.put(U_LEVEL_RPM, up.getLevel_Rpm());
        values.put(U_LEVEL_THR, up.getLevel_Thr());
        
        // update Row
        db.update(TABLE_UPARMS,values,null,null);
        
        db.close(); // Closing database connection
        Log.d("updateUserParm: ", "Ends");

    }
    
    // Getting All Trips
    public ArrayList<TripHeader> getAllTripHeader(String calibr_flag) {
    	
    	Log.d("getAllTripHeader: ", "Starts");
    	
    	ArrayList<TripHeader> tripHeaderList = new ArrayList<TripHeader>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_HEADER + " WHERE " + H_CALIBR_FLAG + " = '" + calibr_flag + "'" + " ORDER BY " + H_TRIP_AA;
 
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
            	TripHeader th = new TripHeader();
                th.setTrip_AA(cursor.getInt(0));
                th.setStart_Date(cursor.getString(1));
                th.setStart_Time(cursor.getString(2));
                th.setEnd_Date(cursor.getString(3));
                th.setEnd_Time(cursor.getString(4));
                th.setDistance(cursor.getDouble(5));
                th.setAvg_Spd(cursor.getDouble(6));
                th.setAvg_Rpm(cursor.getDouble(7));
                th.setAvg_Thr(cursor.getDouble(8));
                th.setCalibr_Flag(cursor.getString(9));
                th.setLevel(cursor.getInt(10));   
                
                // Adding TripHeader to list
                tripHeaderList.add(th);
            } while (cursor.moveToNext());
        }
 
        db.close(); // Closing database connection
        Log.d("getAllTripHeader: ", "Ends");
        
        return tripHeaderList;
        
    }
    
    // Getting All Levels
    public ArrayList<Level> getAllLevels(String code) {
    	
    	Log.d("getAllLevels: ", "Starts");
    	
    	ArrayList<Level> levelList = new ArrayList<Level>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_LEVELS + " WHERE " + L_CODE + " = '" + code + "'" + " ORDER BY " + L_LEVEL;
 
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
            	Level lvl = new Level();
            	lvl.setCode(cursor.getString(0));
            	lvl.setLevel(cursor.getInt(1));
            	lvl.setLimit(cursor.getDouble(2));
                
                // Adding TripHeader to list
            	levelList.add(lvl);
            } while (cursor.moveToNext());
        }
 
        db.close(); // Closing database connection
        Log.d("getAllLevels: ", "Ends");
        
        return levelList;
        
    }
    
    public void createDataBase() throws IOException {
        //If database not exists copy it from the assets

        boolean mDataBaseExist = checkDataBase();
        if(!mDataBaseExist) {
            this.getReadableDatabase();
            this.close();
            try {
                //Copy the database from assets
                copyDataBase();
                Log.e(TAG, "createDatabase database created");
            } catch (IOException mIOException) {
                throw new Error("ErrorCopyingDataBase");
            }
        }
        
    }
    
    //Check that the database exists
    private boolean checkDataBase() {
    	
        File dbFile = new File(DB_PATH + DATABASE_NAME);
        Log.v("dbFile", dbFile + "   "+ dbFile.exists());
        return dbFile.exists();
        
    }

    //Copy the database from assets
    private void copyDataBase() throws IOException {
    	
        InputStream mInput = mContext.getAssets().open(DATABASE_NAME);
        String outFileName = DB_PATH + DATABASE_NAME;
        OutputStream mOutput = new FileOutputStream(outFileName);
        
        byte[] mBuffer = new byte[1024];
        int mLength;
        
        while ((mLength = mInput.read(mBuffer))>0) {
            mOutput.write(mBuffer, 0, mLength);
        }
        
        mOutput.flush();
        mOutput.close();
        mInput.close();
        
    }

    //Open the database, so we can query it
    public boolean openDataBase() throws SQLException {
    	
        String mPath = DB_PATH + DATABASE_NAME;
        // mDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        mDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
        
        return mDataBase != null;
        
    }

    @Override
    public synchronized void close() 
    {
        if(mDataBase != null)
            mDataBase.close();
        super.close();
    }
    
    
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub		
	}


	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub		
	}
 
}
