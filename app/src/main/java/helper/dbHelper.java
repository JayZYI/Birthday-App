package helper;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import androidx.annotation.Nullable;

import java.io.File;
import java.util.ArrayList;


public class dbHelper extends SQLiteOpenHelper {
    public static String DB_NAME = "BApp";
    private static final int DB_VERSION = 1;
    private static final String TBL_NAME = "list";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_PHONE = "phone_number";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_STATUS = "note";
    private static final String KEY_BIRTH_DATE = "birth_date";
    private static final String KEY_TIME = "time";


    ArrayList images = new ArrayList<>();

    private static final String CREATE_TABLE_CONTACTS =
            "CREATE TABLE " + TBL_NAME + "("
                    + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + KEY_NAME + " TEXT NOT NULL,"
                    + KEY_IMAGE + " BLOB,"
                    + KEY_BIRTH_DATE + " TEXT NOT NULL,"
                    + KEY_TIME + " TEXT NOT NULL,"
                    + KEY_PHONE + " TEXT,"
                    + KEY_EMAIL + " TEXT,"
                    + KEY_STATUS + " TEXT);";

    private static final String SEEDING =
            "INSERT INTO " + TBL_NAME + " (" + KEY_NAME + "," +  KEY_IMAGE
                    + "," + KEY_PHONE + "," + KEY_EMAIL + "," + KEY_STATUS + ","
                    + KEY_BIRTH_DATE + "," + KEY_TIME + ") "
                    + "VALUES('dummy',null,'082121',null,null,'3/3/2003','00:00');";

    public dbHelper(Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        sqLiteDatabase.execSQL(CREATE_TABLE_CONTACTS);
        sqLiteDatabase.execSQL(SEEDING);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion)
    {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS'" + TBL_NAME + "'");
        onCreate(sqLiteDatabase);
    }

    @SuppressLint("Range")
    public ArrayList<modelHelper> getAll()
    {
        ArrayList<modelHelper> contactArrayList = new ArrayList<>();
        String q = "SELECT * FROM " + TBL_NAME + " ORDER BY id DESC";
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(q, null);

        if(cursor.moveToFirst())
        {
            do {
                modelHelper contact = new modelHelper();
                contact.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                contact.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                contact.setPhone_number(cursor.getString(cursor.getColumnIndex(KEY_PHONE)));
                contact.setEmail(cursor.getString(cursor.getColumnIndex(KEY_EMAIL)));
                contact.setImage(cursor.getBlob(cursor.getColumnIndex(KEY_IMAGE)));
                contact.setStatus(cursor.getString(cursor.getColumnIndex(KEY_STATUS)));
                contact.setBirth_date(cursor.getString(cursor.getColumnIndex(KEY_BIRTH_DATE)));
                contact.setTime(cursor.getString(cursor.getColumnIndex(KEY_TIME)));
                contactArrayList.add(contact);
            } while(cursor.moveToNext());
        }

        return contactArrayList;
    }

    public long store(String name, String phone_number, String email, byte[] image,
                      String status, String birth_date, String time)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_NAME, name);
        values.put(KEY_PHONE, phone_number);
        values.put(KEY_EMAIL, email);
        values.put(KEY_IMAGE, image);
        values.put(KEY_STATUS, status);
        values.put(KEY_BIRTH_DATE, birth_date);
        values.put(KEY_TIME, time);

        long insert = sqLiteDatabase.insert(TBL_NAME, null, values);
        return insert;
    }

    public int update(int id, String name, String phone_number, String email, byte[] image,
                      String status, String birth_date, String time)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_NAME, name);
        values.put(KEY_PHONE, phone_number);
        values.put(KEY_EMAIL, email);
        values.put(KEY_IMAGE, image);
        values.put(KEY_STATUS, status);
        values.put(KEY_BIRTH_DATE, birth_date);
        values.put(KEY_TIME, time);

        return sqLiteDatabase.update(TBL_NAME, values, KEY_ID + "= ?",
                new String[]{String.valueOf(id)});
    }

    public void delete(int id, Uri uri)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        if(uri != null){
            String path = uri.getEncodedPath();
            File file = new File(path);
            if(file.exists()) {
                file.delete();
            }
        }
        sqLiteDatabase.delete(TBL_NAME, KEY_ID + "= ?", new String[]{String.valueOf(id)});
    }

}
