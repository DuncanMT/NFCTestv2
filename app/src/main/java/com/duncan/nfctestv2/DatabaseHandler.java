package com.duncan.nfctestv2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Duncan on 01/11/2015.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "studentRecords";

    private final static String TABLE_STUDENTS = "students";
    private final static String KEY_ID = "id";
    private final static String KEY_NAME = "name";
    private final static String KEY_MATRIC = "matric";
    private final static String KEY_CARDUID = "carduid";
    private final static String KEY_MODULE = "module";

    public DatabaseHandler(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        String CREATE_STUDENTS_TABLE = "CREATE TABLE "+ TABLE_STUDENTS
                + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_MATRIC
                + " TEXT," + KEY_CARDUID + " TEXT," + KEY_MODULE + " TEXT" + ")";
        db.execSQL(CREATE_STUDENTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENTS);
        onCreate(db);
    }

    public void addStudent(Student student){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, student.getName());
        values.put(KEY_MATRIC, student.get_matric_number());
        values.put(KEY_CARDUID, student.get_card_uid());
        values.put(KEY_MODULE, student.get_module());

        db.insert(TABLE_STUDENTS, null, values);
        db.close();
    }

    public Student getStudent(int id){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_STUDENTS, new String[]{KEY_ID, KEY_NAME, KEY_MATRIC,
                        KEY_CARDUID, KEY_MODULE}, KEY_ID + "= ?",
                new String[]{String.valueOf(id)}, null,null,null,null);
        if(cursor != null) {
            cursor.moveToFirst();
        }
        Student student = new Student (Integer.parseInt(cursor.getString(0)),cursor.getString(1),
                cursor.getString(2), cursor.getString(3), cursor.getString(4));
        return student;
    }

    public List<Student> getAllStudents() {
        List<Student> studentList = new ArrayList();
        String selectQuery = "SELECT * FROM " + TABLE_STUDENTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()){
            do {
                Student student = new Student();
                student.setID(Integer.parseInt(cursor.getString(0)));
                student.setName(cursor.getString(1));
                student.set_matric_number(cursor.getString(2));
                student.set_card_uid(cursor.getString(3));
                student.set_module(cursor.getString(4));
                studentList.add(student);
            } while (cursor.moveToNext());
        }
        return studentList;
    }

    public int getStudentCount(){
        String countQuery = "SELECT * FROM " + TABLE_STUDENTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        return cursor.getCount();
    }

    public int updateStudent(Student student){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, student.getName());
        values.put(KEY_MATRIC, student.get_matric_number());
        values.put(KEY_CARDUID, student.get_card_uid());
        values.put(KEY_MODULE, student.get_module());

        return db.update(TABLE_STUDENTS, values, KEY_ID + " = ?",
                new String[]{String.valueOf(student.getID())});

    }

    public void deleteStudent(Student student){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_STUDENTS, KEY_ID + "= ?",
                new String[]{String.valueOf(student.getID())});
        db.close();
    }

    public boolean checkExists(String carduid) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_STUDENTS, new String[]{KEY_ID, KEY_NAME, KEY_MATRIC,
                        KEY_CARDUID, KEY_MODULE}, KEY_CARDUID + "= ?",
                new String[]{String.valueOf(carduid)}, null, null, null, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }
}