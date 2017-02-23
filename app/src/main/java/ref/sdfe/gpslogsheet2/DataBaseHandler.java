package ref.sdfe.gpslogsheet2;

//An attempt to handle all the information in the app as an SQL db.
//This is noob coding by a physicist, be gentle. :D

//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
//import android.util.Log;




public class DataBaseHandler extends SQLiteOpenHelper {

    // Define Database structure
	// Database Version
	private static final int DATABASE_VERSION = 1;
	 
    // Database Name
    private static final String DATABASE_NAME = "GPSLogSheet2.db";
 
    // Table names
    private static final String TABLE_INSTRUMENTS	= "instruments";
	private static final String TABLE_ANTENNAE		= "antennae";
    private static final String TABLE_ALARMS		= "alarms";
	private static final String TABLE_RODS			= "rods";
    private static final String TABLE_FIXEDPOINTS 	= "fixedpoints";
	private static final String TABLE_SETTINGS		= "settings";

    // Instruments column names
    private static final String KEY_INST_ID 		= "id";
    private static final String KEY_INST_NAME		= "instrument_name";

	// Antennae column names
	private static final String KEY_ANT_ID 			= "id";
	private static final String KEY_ANT_NAME		= "antenna_name";

    // Alarms column names
    private static final String KEY_ALRM_ID 		= "id";
    private static final String KEY_ALRM_NAME		= "alarm_name";
	
	// Rods column names
	private static final String KEY_ROD_ID 			= "id";
	private static final String KEY_ROD_NAME		= "rod_name";	
	private static final String KEY_ROD_LENGTH		= "rod_length";
	
	// Fixedpoints column names
    private static final String KEY_FP_ID 			= "id";
    private static final String KEY_FP_GPS_NAME 	= "gps_name";
    private static final String KEY_FP_HS_NAME 		= "hs_name";
    private static final String KEY_FP_EASTING		= "easting"; 
    private static final String KEY_FP_NORTHING		= "northing";
	
	// Settings column names
	private static final String KEY_SET_ID 			= "id";
    private static final String KEY_SET_NAME 		= "setting_name";
	private static final String KEY_SET_VALUE 		= "setting_val";
    
	// The following is from the OLD code
    //private static final long MAX_ENTRIES=5000;
    //private static final String TAG="PointData";
    //private static final double SEARCH_RADIUS=0.8; //A 0.8 deg x 0.8 deg box
	// END! :D
	
	public DataBaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_INSTRUMENTS_TABLE = "CREATE TABLE " + TABLE_INSTRUMENTS + "("
                + KEY_INST_ID       + " INTEGER PRIMARY KEY,"
                + KEY_INST_NAME     + " TEXT unique)";

        db.execSQL(CREATE_INSTRUMENTS_TABLE);
    	
    	 String CREATE_ANTENNAE_TABLE = "CREATE TABLE " + TABLE_ANTENNAE + "("
                + KEY_ANT_ID        + " INTEGER PRIMARY KEY,"
        		+ KEY_ANT_NAME      + " TEXT unique)";
                    	 
        db.execSQL(CREATE_ANTENNAE_TABLE);

        String CREATE_ALARMS_TABLE = "CREATE TABLE " + TABLE_ALARMS + "("
                + KEY_ALRM_ID       + " INTEGER PRIMARY KEY,"
                + KEY_ALRM_NAME     + " TEXT unique)";

        db.execSQL(CREATE_ALARMS_TABLE);
		
		 String CREATE_RODS_TABLE = "CREATE TABLE " + TABLE_RODS + "("
                + KEY_ROD_ID        +" INTEGER PRIMARY KEY,"
        		+ KEY_ROD_NAME      + " TEXT unique,"
				+ KEY_ROD_LENGTH    + " REAL)";
                    	 
        db.execSQL(CREATE_RODS_TABLE);
		
		 String CREATE_FIXEDPOINTS_TABLE = "CREATE TABLE " + TABLE_FIXEDPOINTS + "("
                + KEY_FP_ID         +" INTEGER PRIMARY KEY,"
        		+ KEY_FP_GPS_NAME   + " TEXT unique,"
        		+ KEY_FP_HS_NAME    + " TEXT,"
        		+ KEY_FP_EASTING    + " REAL,"
        		+ KEY_FP_NORTHING   + " REAL)";
                    	 
        db.execSQL(CREATE_FIXEDPOINTS_TABLE);
		
		 String CREATE_SETTING_TABLE = "CREATE TABLE " + TABLE_SETTINGS + "("
                + KEY_SET_ID        + " INTEGER PRIMARY KEY,"
        		+ KEY_SET_NAME      + " TEXT unique,"
        		+ KEY_SET_VALUE     + " INTEGER)";
                    	 
        db.execSQL(CREATE_SETTING_TABLE);
        
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INSTRUMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ANTENNAE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALARMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RODS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FIXEDPOINTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SETTINGS);
        // Create tables again
        onCreate(db);
    }
    public static class RodEntry{
        public String rod_name;
        public double rod_length; //in meters

        public RodEntry(String rod_name, double rod_length){
            this.rod_name=rod_name;
            this.rod_length=rod_length;
        }
    }
    public RodEntry getRodEntry(String id){
        SQLiteDatabase db = this.getReadableDatabase();
        if (db==null){
            return null;
        }

        // OLDJO: This is from the old code.
        //TODO: build queries properly....
        String query="SELECT * FROM "+TABLE_RODS+" WHERE ("+KEY_ROD_ID+"="+"\""+id+"\"" + ")";
        Cursor cursor = db.rawQuery(query,null);
        if (cursor == null || cursor.getCount()==0){
            db.close();
            return null;
        }
        cursor.moveToFirst();
        RodEntry point=new RodEntry(cursor.getString(1),cursor.getDouble(2));
        cursor.close();
        db.close();
        return point;
    }
}