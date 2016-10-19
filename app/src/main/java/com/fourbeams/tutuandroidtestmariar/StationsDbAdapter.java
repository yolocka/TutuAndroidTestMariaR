package com.fourbeams.tutuandroidtestmariar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Класс предоставляет интерфейс для работы с базой данных
 * <br/> Содержит внутренний класс {@link DatabaseHelper}, реализующий создание таблиц для полнотекстного поиска, используя FTS3
 */
public class StationsDbAdapter {

    //fields in DB
    public static final String _ID = "_id";
    public static final String COL_COUNTRY_TITLE = "country_title";
    public static final String COL_DISTRICT_TITLE = "district_title";
    public static final String COL_CITY_TITLE = "city_title";
    public static final String COL_REGION_TITLE = "region_title";
    public static final String COL_STATION_TITLE = "station_title";
    public static final String KEY_SEARCH = "searchData";

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private static final String DATABASE_NAME = "StationsDB";
    private static final String FTS_VIRTUAL_TABLE_STATIONS_TO = "StationTo";
    private static final String FTS_VIRTUAL_TABLE_STATIONS_FROM = "StationFrom";
    private static final int DATABASE_VERSION = 5;

    //Create a FTS3 Virtual Table for fast searches
    private static final String CREATE_DB_TABLE_STATIONS_TO =
            "CREATE VIRTUAL TABLE " + FTS_VIRTUAL_TABLE_STATIONS_TO + " USING fts3(" +
                    _ID + "," +
                    COL_STATION_TITLE + "," +
                    COL_REGION_TITLE + "," +
                    COL_COUNTRY_TITLE + "," +
                    COL_DISTRICT_TITLE + "," +
                    COL_CITY_TITLE + "," +
                    KEY_SEARCH + "," +
                    " UNIQUE (" + _ID + "));";

    private static final String CREATE_DB_TABLE_STATIONS_FROM =
            "CREATE VIRTUAL TABLE " + FTS_VIRTUAL_TABLE_STATIONS_FROM + " USING fts3(" +
                    _ID + "," +
                    COL_STATION_TITLE + "," +
                    COL_REGION_TITLE + "," +
                    COL_COUNTRY_TITLE + "," +
                    COL_DISTRICT_TITLE + "," +
                    COL_CITY_TITLE + "," +
                    KEY_SEARCH + "," +
                    " UNIQUE (" + _ID + "));";

    private final Context context;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_DB_TABLE_STATIONS_TO);
            db.execSQL(CREATE_DB_TABLE_STATIONS_FROM);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + FTS_VIRTUAL_TABLE_STATIONS_TO);
            db.execSQL("DROP TABLE IF EXISTS " + FTS_VIRTUAL_TABLE_STATIONS_FROM);
            onCreate(db);
        }
    }

    public StationsDbAdapter(Context context) {
        this.context = context;
    }

    public StationsDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(context);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        if (mDbHelper != null) {
            mDbHelper.close();
        }
    }

    public boolean isDbEmpty(){
        String query = "SELECT * FROM " + FTS_VIRTUAL_TABLE_STATIONS_FROM + " LIMIT 1";
        Cursor cursor = mDb.rawQuery(query, null);
        return !cursor.moveToLast();
    }

    public long createStationFrom(Integer id, String stationTitle, String countryTitle, String regionTitle, String districtTitle, String cityTitle) {

        ContentValues initialValues = new ContentValues();
        String searchValue =  stationTitle;
        initialValues.put(_ID, id);
        initialValues.put(COL_STATION_TITLE, stationTitle);
        initialValues.put(COL_COUNTRY_TITLE, countryTitle);
        initialValues.put(COL_REGION_TITLE, regionTitle);
        initialValues.put(COL_DISTRICT_TITLE, districtTitle);
        initialValues.put(COL_CITY_TITLE, cityTitle);
        initialValues.put(KEY_SEARCH, searchValue);

        return mDb.insert(FTS_VIRTUAL_TABLE_STATIONS_FROM, null, initialValues);
    }

    public long createStationTo(Integer id, String stationTitle, String countryTitle, String regionTitle, String districtTitle, String cityTitle) {

        ContentValues initialValues = new ContentValues();
        String searchValue =  stationTitle;
        initialValues.put(_ID, id);
        initialValues.put(COL_STATION_TITLE, stationTitle);
        initialValues.put(COL_COUNTRY_TITLE, countryTitle);
        initialValues.put(COL_REGION_TITLE, regionTitle);
        initialValues.put(COL_DISTRICT_TITLE, districtTitle);
        initialValues.put(COL_CITY_TITLE, cityTitle);
        initialValues.put(KEY_SEARCH, searchValue);

        return mDb.insert(FTS_VIRTUAL_TABLE_STATIONS_TO, null, initialValues);
    }

    public Cursor searchStation(String inputText, String toFromCase) throws SQLException {
        String query;
        if (toFromCase.equals("to")){
            query = "SELECT * FROM " + FTS_VIRTUAL_TABLE_STATIONS_TO + " where " +  KEY_SEARCH + " MATCH '" + inputText + "'";
        }else if (toFromCase.equals("from")){
            query = "SELECT * FROM " + FTS_VIRTUAL_TABLE_STATIONS_FROM + " where " +  KEY_SEARCH + " MATCH '" + inputText + "'";
        }else {
            query = null;
        }

        Cursor mCursor = mDb.rawQuery(query,null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    public Cursor showStations(String toFromCase) throws SQLException {
        String query;
        if (toFromCase.equals("to")){
            query = "SELECT * FROM " + FTS_VIRTUAL_TABLE_STATIONS_TO;
        }else if (toFromCase.equals("from")){
            query = "SELECT * FROM " + FTS_VIRTUAL_TABLE_STATIONS_FROM;
        }else {
            query = null;
        }

        Cursor mCursor = mDb.rawQuery(query,null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    public boolean deleteAllStations(String toFromCase) {
        int doneDelete = 0;
        if (toFromCase.equals("to")){
            doneDelete = mDb.delete(FTS_VIRTUAL_TABLE_STATIONS_TO, null , null);
        }else if (toFromCase.equals("from")){
            doneDelete = mDb.delete(FTS_VIRTUAL_TABLE_STATIONS_FROM, null , null);
        }
        return doneDelete > 0;

    }
}
