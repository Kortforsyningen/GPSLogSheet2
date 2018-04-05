package ref.sdfe.gpslogsheet2;

//An attempt to handle all the information in the app as an SQL db.
//This is noob coding by a physicist, be gentle. :D /oldjo@sdfe.dk

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class DataBaseHandler extends SQLiteOpenHelper {

    // Singleton pattern
    private static DataBaseHandler sInstance;

    // Define Database structure
	// Database Version
	private static final int DATABASE_VERSION = 1;
	 
    // Database Name
    private static final String DATABASE_NAME = "GPSLogSheet2.db";

    // Table names
    private static final String TABLE_PROJECTS      = "projects";
    private static final String TABLE_IMAGES        = "images";
    private static final String TABLE_INSTRUMENTS	= "instruments";
	private static final String TABLE_ANTENNAE		= "antennae";
    private static final String TABLE_ALARMS		= "alarms";
	private static final String TABLE_RODS			= "rods";
    private static final String TABLE_FIXEDPOINTS 	= "fixedpoints";
	private static final String TABLE_SETTINGS		= "settings";

    // Projects column names
    private static final String KEY_PROJ_ID         = "id";
    private static final String KEY_PROJ_NAME       = "name";
    private static final String KEY_PROJ_DATE_START = "date_start";
    private static final String KEY_PROJ_DATE_MOD   = "date_modified";
    private static final String KEY_PROJ_CLOB       = "clob";

    // Images column names
    private static final String KEY_IMG_ID         = "id";
    private static final String KEY_IMG_PROJ       = "project_id";
    private static final String KEY_IMG_DATE_START = "date_start";
    private static final String KEY_IMG_BLOB       = "blob";

    // Instruments column names
    private static final String KEY_INST_ID 		= "id";
    private static final String KEY_INST_NAME		= "instrument_name";

	// Antennae column names
	private static final String KEY_ANT_ID 			= "id";
	private static final String KEY_ANT_NAME		= "antenna_name";
    private static final String KEY_ANT_CODE		= "antenna_code";

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

    public static synchronized DataBaseHandler getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new DataBaseHandler(context.getApplicationContext());
        }
        return sInstance;
    }

    // Made the constructor private, use getInstance() instead.
	private DataBaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

    // Creating tables
	@Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PROJECTS_TABLE = "CREATE TABLE " + TABLE_PROJECTS + "("
                + KEY_PROJ_ID       + " INTEGER PRIMARY KEY,"
                + KEY_PROJ_NAME     + " TEXT,"
                + KEY_PROJ_DATE_START + " INTEGER,"
                + KEY_PROJ_DATE_MOD + " INTEGER,"
                + KEY_PROJ_CLOB + " CLOB)";
        db.execSQL(CREATE_PROJECTS_TABLE);
        Log.i("SQL", "table created?");

        String CREATE_IMAGES_TABLE = "CREATE TABLE " + TABLE_IMAGES + "("
                + KEY_IMG_ID       + " INTEGER PRIMARY KEY,"
                + KEY_IMG_DATE_START + " INTEGER,"
                + KEY_IMG_PROJ + " INTEGER,"
                + KEY_IMG_BLOB + " BLOB)";
        db.execSQL(CREATE_IMAGES_TABLE);

        String CREATE_INSTRUMENTS_TABLE = "CREATE TABLE " + TABLE_INSTRUMENTS + "("
                + KEY_INST_ID       + " INTEGER PRIMARY KEY,"
                + KEY_INST_NAME     + " TEXT)";
        db.execSQL(CREATE_INSTRUMENTS_TABLE);
    	
    	String CREATE_ANTENNAE_TABLE = "CREATE TABLE " + TABLE_ANTENNAE + "("
                + KEY_ANT_ID        + " INTEGER PRIMARY KEY,"
        		+ KEY_ANT_NAME      + " TEXT,"
                + KEY_ANT_CODE      + " TEXT)";
        db.execSQL(CREATE_ANTENNAE_TABLE);

        String CREATE_ALARMS_TABLE = "CREATE TABLE " + TABLE_ALARMS + "("
                + KEY_ALRM_ID       + " INTEGER PRIMARY KEY,"
                + KEY_ALRM_NAME     + " TEXT unique)";
        db.execSQL(CREATE_ALARMS_TABLE);
		
		String CREATE_RODS_TABLE = "CREATE TABLE " + TABLE_RODS + "("
                + KEY_ROD_ID        + " INTEGER PRIMARY KEY,"
        		+ KEY_ROD_NAME      + " TEXT unique,"
				+ KEY_ROD_LENGTH    + " REAL)";
        db.execSQL(CREATE_RODS_TABLE);
		
		String CREATE_FIXEDPOINTS_TABLE = "CREATE TABLE " + TABLE_FIXEDPOINTS + "("
                + KEY_FP_ID         + " INTEGER PRIMARY KEY,"
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

    // Upgrade database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROJECTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INSTRUMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ANTENNAE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALARMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RODS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FIXEDPOINTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SETTINGS);
        // Create tables again
        onCreate(db);
    }

    // CRUD (create, read, update, delete) operations

    //Projects
    void addProjectEntry(ProjectEntry projectEntry) {
        // Open database
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PROJ_ID, projectEntry.getId());
        values.put(KEY_PROJ_NAME, projectEntry.getName());
        values.put(KEY_PROJ_DATE_START, projectEntry.getStartDate());
        values.put(KEY_PROJ_DATE_MOD, projectEntry.getModDate());
        values.put(KEY_PROJ_CLOB, projectEntry.getJsonString());

        // Insert row
        db.insert(TABLE_PROJECTS, null, values);

        // Close database
        db.close();

        Log.i("SQL", "Project Entry added.");
    }

    // Update project
    public int updateProjectEntry(ProjectEntry projectEntry) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PROJ_NAME, projectEntry.getName());
        values.put(KEY_PROJ_DATE_MOD, projectEntry.getModDate());
        values.put(KEY_PROJ_CLOB, projectEntry.getJsonString());

        Log.i("SQL", "Project Entry updated.");

        // updating row
        return db.update(TABLE_PROJECTS, values, KEY_PROJ_ID + " = ?",
                new String[]{String.valueOf(projectEntry.getId())});

    }

    public ProjectEntry getProjectEntry(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PROJECTS, new String[] { KEY_PROJ_ID, KEY_PROJ_CLOB },
                KEY_PROJ_ID + "=?", new String[] { String.valueOf(id) },
                null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Gson gson = new Gson();
        //ProjectEntry projectEntry = gson.fromJson(cursor.getString(1), ProjectEntry.class);
        // Return Project entry
        return gson.fromJson(cursor.getString(1), ProjectEntry.class);
    }

    // Get list of all projects
    public List<ProjectEntry> getAllProjectEntries() {
        List<ProjectEntry> projectList = new ArrayList<ProjectEntry>();
        // Select all query
        String selectQuery = "SELECT  * FROM " + TABLE_PROJECTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                ProjectEntry projectEntry = getProjectEntry(Integer.parseInt(cursor.getString(0)));
                // Add project to list
                projectList.add(projectEntry);
            } while (cursor.moveToNext());
        }
        return projectList;
    }
    // Get list of all projects names
    public List<String> getAllProjectNames() {
        List<String> projectNamesList = new ArrayList<String>();
        // Select all query
        String selectQuery = "SELECT  * FROM " + TABLE_PROJECTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                ProjectEntry projectEntry = getProjectEntry(Integer.parseInt(cursor.getString(0)));
                // Add project to list
                projectNamesList.add(projectEntry.getName());
            } while (cursor.moveToNext());
        }
        return projectNamesList;
    }

    // Get list of all projects IDs
    public List<Integer> getAllProjectIDs() {
        List<Integer> projectIDsList = new ArrayList<Integer>();
        // Select all query
        String selectQuery = "SELECT  * FROM " + TABLE_PROJECTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                ProjectEntry projectEntry = getProjectEntry(Integer.parseInt(cursor.getString(0)));
                // Add project to list
                projectIDsList.add(projectEntry.getId());
            } while (cursor.moveToNext());
        }
        return projectIDsList;
    }



    // Get number of projects in db
    public int getProjectsCount() {
        String countQuery = "SELECT * FROM " + TABLE_PROJECTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }



    // Delete project
    public void deleteProject(ProjectEntry projectEntry) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PROJECTS, KEY_PROJ_ID + " = ? ",
                new String[] {String.valueOf(projectEntry.getId())});
        db.close();
    }
    public void deleteProjectTable() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_PROJECTS, null ,null);
        db.close();
    }

    //Alarms
    void addAlarmEntry(AlarmEntry alarmEntry) {
        // Open database
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ALRM_ID, alarmEntry.getID());  // Alarm ID
        values.put(KEY_ALRM_NAME, alarmEntry.getName());

        // Insert row
        db.insert(TABLE_ALARMS, null, values);
        // Close database
        db.close();
    }

    public AlarmEntry getAlarmEntry(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_ALARMS, new String[] { KEY_ALRM_ID, KEY_ALRM_NAME},
                KEY_ALRM_ID + "=?", new String[] { String.valueOf(id) },
                null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        //AlarmEntry alarmEntry = new AlarmEntry(Integer.parseInt(cursor.getString(0)),
        //        cursor.getString(1));
        // Return Alarm entry
        return new AlarmEntry(Integer.parseInt(cursor.getString(0)),cursor.getString(1));
    }

    // Get list of all alarms
    public List<AlarmEntry> getAllAlarmEntries() {
        List<AlarmEntry> alarmList = new ArrayList<AlarmEntry>();
        // Select all query
        String selectQuery = "SELECT  * FROM " + TABLE_ALARMS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                AlarmEntry alarmEntry = new AlarmEntry(Integer.parseInt(cursor.getString(0)),
                        cursor.getString(1));
                //alarmEntry.setID(Integer.parseInt(cursor.getString(0)));
                //alarmEntry.setName(cursor.getString(1));
                // Add alarm to list
                alarmList.add(alarmEntry);
            } while (cursor.moveToNext());
        }
        return alarmList;
    }

    // Get number of alarms in db
    public int getAlarmsCount() {
        String countQuery = "SELECT * FROM " + TABLE_ALARMS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

    // Update alarm
    public int updateAlarmEntry(AlarmEntry alarmEntry) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ALRM_NAME, alarmEntry.getName());

        // updating row
        return db.update(TABLE_ALARMS, values, KEY_ALRM_ID + " = ?",
                new String[]{String.valueOf(alarmEntry.getID())});
    }

    // Delete alarm
    public void deleteAlarm(AlarmEntry alarmEntry) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ALARMS, KEY_ALRM_ID + " = ? ",
                new String[] {String.valueOf(alarmEntry.getID())});
        db.close();
    }
    public void deleteAlarmTable() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_ALARMS, null ,null);
        db.close();
    }


    // ANTENNAE
    //Antennae
    void addAntennaEntry(AntennaEntry antennaEntry) {
        // Open database
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ANT_ID, antennaEntry.getID());  // Antenna ID
        values.put(KEY_ANT_NAME, antennaEntry.getName());
        values.put(KEY_ANT_CODE, antennaEntry.getCode());

        // Insert row
        db.insert(TABLE_ANTENNAE, null, values);
        // Close database
        db.close();
    }

    public AntennaEntry getAntennaEntry(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_ANTENNAE, new String[] { KEY_ANT_ID, KEY_ANT_NAME,
                        KEY_ANT_CODE},
                KEY_ANT_ID + "=?", new String[] { String.valueOf(id) },
                null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        AntennaEntry antennaEntry = new AntennaEntry(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1),cursor.getString(2));
        // Return Antenna entry
        return antennaEntry;
    }

    // Get list of all antennas
    public List<AntennaEntry> getAllAntennaEntries() {
        List<AntennaEntry> antennaList = new ArrayList<AntennaEntry>();
        // Select all query
        String selectQuery = "SELECT  * FROM " + TABLE_ANTENNAE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                AntennaEntry antennaEntry = new AntennaEntry(Integer.parseInt(cursor.getString(0)),
                        cursor.getString(1),cursor.getString(2));
                //antennaEntry.setID(Integer.parseInt(cursor.getString(0)));
                //antennaEntry.setName(cursor.getString(1));
                // Add antenna to list
                antennaList.add(antennaEntry);
            } while (cursor.moveToNext());
        }
        return antennaList;
    }

    // Get number of antennas in db
    public int getAntennaeCount() {
        String countQuery = "SELECT * FROM " + TABLE_ANTENNAE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

    // Update antenna
    public int updateAntennaEntry(AntennaEntry antennaEntry) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ANT_NAME, antennaEntry.getName());

        // updating row
        return db.update(TABLE_ANTENNAE, values, KEY_ANT_ID + " = ?",
                new String[]{String.valueOf(antennaEntry.getID())});
    }

    // Delete antenna
    public void deleteAntenna(AntennaEntry antennaEntry) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ANTENNAE, KEY_ANT_ID + " = ? ",
                new String[] {String.valueOf(antennaEntry.getID())});
        db.close();
    }
    public void deleteAntennaTable() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_ANTENNAE, null ,null);
        db.close();
    }

    //INSTRUMENTS
    //Instruments
    void addInstrumentEntry(InstrumentEntry instrumentEntry) {
        // Open database
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_INST_ID, instrumentEntry.getID());  // Instrument ID
        values.put(KEY_INST_NAME, instrumentEntry.getName());

        // Insert row
        db.insert(TABLE_INSTRUMENTS, null, values);
        // Close database
        db.close();
    }

    public InstrumentEntry getInstrumentEntry(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_INSTRUMENTS, new String[] { KEY_INST_ID, KEY_INST_NAME},
                KEY_INST_ID + "=?", new String[] { String.valueOf(id) },
                null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        InstrumentEntry instrumentEntry = new InstrumentEntry(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1));
        // Return Instrument entry
        return instrumentEntry;
    }

    // Get list of all instruments
    public List<InstrumentEntry> getAllInstrumentEntries() {
        List<InstrumentEntry> instrumentList = new ArrayList<InstrumentEntry>();
        // Select all query
        String selectQuery = "SELECT  * FROM " + TABLE_INSTRUMENTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                InstrumentEntry instrumentEntry = new InstrumentEntry(
                        Integer.parseInt(cursor.getString(0)),
                        cursor.getString(1));
                //instrumentEntry.setID(Integer.parseInt(cursor.getString(0)));
                //instrumentEntry.setName(cursor.getString(1));
                // Add instrument to list
                instrumentList.add(instrumentEntry);
            } while (cursor.moveToNext());
        }
        return instrumentList;
    }

    // Get number of instruments in db
    public int getInstrumentsCount() {
        String countQuery = "SELECT * FROM " + TABLE_INSTRUMENTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

    // Update instrument
    public int updateInstrumentEntry(InstrumentEntry instrumentEntry) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_INST_NAME, instrumentEntry.getName());

        // updating row
        return db.update(TABLE_INSTRUMENTS, values, KEY_INST_ID + " = ?",
                new String[]{String.valueOf(instrumentEntry.getID())});
    }

    // Delete instrument
    public void deleteInstrument(InstrumentEntry instrumentEntry) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_INSTRUMENTS, KEY_INST_ID + " = ? ",
                new String[] {String.valueOf(instrumentEntry.getID())});
        db.close();
    }
    public void deleteInstrumentTable() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_INSTRUMENTS, null ,null);
        db.close();
    }

    // RODS
    void addRodEntry(RodEntry rodEntry) {
        // Open database
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ROD_ID, rodEntry.getID());  // Rod ID
        values.put(KEY_ROD_NAME, rodEntry.getName());
        values.put(KEY_ROD_LENGTH, rodEntry.getLength());

        // Insert row
        db.insert(TABLE_RODS, null, values);
        // Close database
        db.close();
    }

    public RodEntry getRodEntry(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_RODS, new String[] { KEY_ROD_ID, KEY_ROD_NAME,
                KEY_ROD_LENGTH}, KEY_ROD_ID + "=?", new String[] { String.valueOf(id) },
                null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        RodEntry rodEntry = new RodEntry(Integer.parseInt(cursor.getString(0)),cursor.getString(1),
                Double.parseDouble(cursor.getString(2)));
        // Return Rod entry
        return rodEntry;
    }

    // Get list of all rods
    public List<RodEntry> getAllRodEntries() {
        List<RodEntry> rodList = new ArrayList<RodEntry>();
        // Select all query
        String selectQuery = "SELECT  * FROM " + TABLE_RODS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                RodEntry rodEntry = new RodEntry(Integer.parseInt(cursor.getString(0)),
                        cursor.getString(1),Double.parseDouble(cursor.getString(2)));
                //rodEntry.setID(Integer.parseInt(cursor.getString(0)));
                //rodEntry.setName(cursor.getString(1));
                //rodEntry.setLength(Double.parseDouble(cursor.getString(2)));
                // Add rod to list
                rodList.add(rodEntry);
            } while (cursor.moveToNext());
        }
        return rodList;
    }

    // Get number of rods in db
    public int getRodsCount() {
        String countQuery = "SELECT * FROM " + TABLE_RODS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

    // Update rod
    public int updateRodEntry(RodEntry rodEntry) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ROD_NAME, rodEntry.getName());
        values.put(KEY_ROD_LENGTH, rodEntry.getLength());

        // updating row
        return db.update(TABLE_RODS, values, KEY_ROD_ID + " = ?",
                new String[]{String.valueOf(rodEntry.getID())});
    }

    // Delete rod
    public void deleteRod(RodEntry rodEntry) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RODS, KEY_ROD_ID + " = ? ",
                new String[] {String.valueOf(rodEntry.getID())});
        db.close();
    }

    // Delete all rods
    public void deleteRodTable() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_RODS, null ,null);
        db.close();
    }
    //Fixedpoints
    void addFixedpointEntry(FixedpointEntry fixedpointEntry) {
        // Open database
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_FP_ID, fixedpointEntry.getID());  // Fixedpoint ID
        values.put(KEY_FP_GPS_NAME, fixedpointEntry.getGPSName());
        values.put(KEY_FP_HS_NAME, fixedpointEntry.getHSName());
        values.put(KEY_FP_EASTING, fixedpointEntry.getEasting());
        values.put(KEY_FP_NORTHING, fixedpointEntry.getNorthing());

        // Insert row
        db.insert(TABLE_FIXEDPOINTS, null, values);
        // Close database
        db.close();
    }

    public FixedpointEntry getFixedpointEntry(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_FIXEDPOINTS, new String[] {
                KEY_FP_ID,
                KEY_FP_GPS_NAME,
                KEY_FP_HS_NAME,
                KEY_FP_EASTING,
                KEY_FP_NORTHING},
                KEY_FP_ID + "=?", new String[] { String.valueOf(id) },
                null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        FixedpointEntry fixedpointEntry = new FixedpointEntry(
                Integer.parseInt(cursor.getString(0)),
                cursor.getString(1),
                cursor.getString(2),
                Double.parseDouble(cursor.getString(3)),
                Double.parseDouble(cursor.getString(4)));
        // Return Fixedpoint entry
        return fixedpointEntry;
    }

    // Get list of all fixedpoints (ID and GPS name)
    public List<FixedpointEntry> getAllFixedpointEntries() {
        List<FixedpointEntry> fixedpointList = new ArrayList<FixedpointEntry>();
        // Select all query
        String selectQuery = "SELECT  * FROM " + TABLE_FIXEDPOINTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                FixedpointEntry fixedpointEntry = new FixedpointEntry(
                        Integer.parseInt(cursor.getString(0)),
                        cursor.getString(1),cursor.getString(2),
                        Double.parseDouble(cursor.getString(3)),
                        Double.parseDouble(cursor.getString(4)));
                //fixedpointEntry.setID(Integer.parseInt(cursor.getString(0)));
                //fixedpointEntry.setName(cursor.getString(1));
                // Add fixedpoint to list
                fixedpointList.add(fixedpointEntry);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return fixedpointList;
    }

    // Get number of fixedpoints in db
    public int getFixedpointsCount() {
        String countQuery = "SELECT * FROM " + TABLE_FIXEDPOINTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

    // Update fixedpoint
    public int updateFixedpointEntry(FixedpointEntry fixedpointEntry) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_FP_GPS_NAME, fixedpointEntry.getGPSName());
        values.put(KEY_FP_HS_NAME, fixedpointEntry.getHSName());
        values.put(KEY_FP_EASTING, fixedpointEntry.getEasting());
        values.put(KEY_FP_NORTHING, fixedpointEntry.getNorthing());

        // updating row
        return db.update(TABLE_FIXEDPOINTS, values, KEY_FP_ID + " = ?",
                new String[]{String.valueOf(fixedpointEntry.getID())});
    }

    // Delete fixedpoint
    public void deleteFixedpoint(FixedpointEntry fixedpointEntry) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FIXEDPOINTS, KEY_FP_ID + " = ? ",
                new String[] {String.valueOf(fixedpointEntry.getID())});
        db.close();
    }
    public void deleteFixedpointTable() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_FIXEDPOINTS, null ,null);
        db.close();
    }

}

