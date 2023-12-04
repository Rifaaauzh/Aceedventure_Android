package com.smartlab.aceedventure;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "users.db";
    public static final String TABLE_NAME = "users";
    static final int DB_VERSION = 1; //
    public static final String USER_ID = "user_id";
    public static final String USER_NAME = "name";
    public static final String PASS_HASH = "passhash";
    public static final String SIGNED_IN = "signin";



    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        String sql = "create table " + TABLE_NAME + " (" + USER_ID + " text primary key, "+ USER_NAME + " text, "
                + PASS_HASH + " text, "+ SIGNED_IN + " text)"; //
        db.execSQL(sql); //
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String name,String userid,String pass, String signed_in) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_NAME,name);
        contentValues.put(USER_ID,userid);
        contentValues.put(PASS_HASH,pass);
        contentValues.put(SIGNED_IN,signed_in);
        long result = db.insert(TABLE_NAME,null ,contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }


    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, TABLE_NAME);
        return numRows;
    }

    public boolean updateContact(String name,String userid,String pass, String signed_in) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_NAME, name);
        contentValues.put(PASS_HASH, pass);
        contentValues.put(SIGNED_IN, signed_in);

        db.update(TABLE_NAME, contentValues, USER_ID + " = ? ", new String[]{userid});
        return true;
    }

    public Integer deleteContact(String userid) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME,
                USER_ID +" = ? ",
                new String[]{userid});
    }

    public Integer deleteAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, null, null);
    }

    public Cursor getData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        return data;

    }

    /*
    public int getMaxId(){
        int primary = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT max(id) FROM " + TABLE_NAME, null);
        if (data.moveToFirst())
        {
            do
            {
                primary = data.getInt(0);
            } while (data.moveToNext());
        }
        else
        {
            primary = 0;
        }

        db.close();
        return primary;
    }
    */
    public String[] getResultAtt(String att, String selection,String order,String limit)
    {
        Log.i("getResultAtt", "Get: " + att);

        String[] value;
        int i = 0;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cur = null ;
        cur = db.query(TABLE_NAME,
                new String[] {"*"},
                selection,
                null,
                null,
                null,
                null,
                null);
        value = new String[cur.getColumnCount()];

        if (cur.moveToFirst())
        {
            do
            {
                for (i=0;i<4;i++)
                {
                    value[i]= cur.getString(i);
                    Log.i("DB getResultConfig","val = "+cur.getString(i));
                }

            } while (cur.moveToNext());
        }
        else
        {
            Log.d("DB getResultConfig","no val");
        }
        db.close();
        return value;
    }

    public boolean getContact(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME
                + " where " + USER_NAME + "=?", new String[]{name});
        boolean exists = (cursor.getCount() > 0);
    /*cursor.close();
    db.close();*/
        return exists;

    }

    public boolean checkUser(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select name from " + TABLE_NAME
                + " where " + USER_NAME + "=?", new String[]{name});
        boolean exists = (cursor.getCount() > 0);
    /*cursor.close();
    db.close();*/
        return exists;
    }

    public Boolean deletedata(String name) {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE "+USER_ID+"=?", new String[]{name});
        if(cursor.getCount()>0) {
            long result = DB.delete(TABLE_NAME, USER_ID+"=?", new String[]{name});
            if (result == -1) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }
}
