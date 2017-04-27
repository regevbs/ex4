package com.example.android.recyclerview;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Regev on 4/26/2017.
 */

public class MissionDbHelper extends SQLiteOpenHelper {
    // TODO (1) extend the SQLiteOpenHelper class
    static final String DATABASE_NAME = "missions.db";
    static final int DATABASE_VERSION = 1;

    public MissionDbHelper(Context context)
    {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
            final String SQLCREATE_TABLE = "CREATE TABLE " + MissionContract.MissionEntry.TABLE_NAME + " (" +
                    MissionContract.MissionEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    MissionContract.MissionEntry.COLUMN_MISSION_DESC + " TEXT NOT NULL, " +
                    MissionContract.MissionEntry.COLUMN_DUE_DATE + " TEXT NOT NULL" +
                    "); ";

        // COMPLETED (7) Execute the query by calling execSQL on sqLiteDatabase and pass the string query SQL_CREATE_WAITLIST_TABLE
        db.execSQL(SQLCREATE_TABLE);
    }


    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // For now simply drop the table and create a new one. This means if you change the
        // DATABASE_VERSION the table will be dropped.
        // In a production app, this method might be modified to ALTER the table
        // instead of dropping it, so that existing data is not deleted.
        // COMPLETED (9) Inside, execute a drop table query, and then call onCreate to re-create it
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MissionContract.MissionEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }



}
