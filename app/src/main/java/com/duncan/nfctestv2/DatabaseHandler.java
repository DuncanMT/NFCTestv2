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
    private final static String STUDENT_ID = "id";
    private final static String STUDENT_NAME = "name";
    private final static String STUDENT_MATRIC = "matric";
    private final static String STUDENT_CARDUID = "carduid";
    private final static String STUDENT_MODULE = "module";

    private final static String TABLE_STUDENT_CLASSES = "studentclasses";
    private final static String STUDENT_CLASSES_ID = "id";
    private final static String STUDENT_CLASSES_STUDENT_ID = "sid";
    private final static String STUDENT_CLASSES_CLASS_ID = "cid";

    private final static String TABLE_CLASSES = "classes";
    private final static String CLASSES_ID = "id";
    private final static String CLASSES_NAME = "name";
    private final static String CLASSES_ROOM = "room";
    private final static String CLASSES_TIME = "time";
    private final static String CLASSES_DATE = "date";

    public DatabaseHandler(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        String CREATE_STUDENTS_TABLE = "CREATE TABLE "+ TABLE_STUDENTS
                + "("
                + STUDENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + STUDENT_NAME + " TEXT,"
                + STUDENT_MATRIC + " TEXT,"
                + STUDENT_CARDUID + " TEXT,"
                + STUDENT_MODULE + " TEXT" + ")";

        String CREATE_STUDENT_CLASSES_TABLE = "CREATE TABLE "+TABLE_STUDENT_CLASSES
                + "("+ STUDENT_CLASSES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + STUDENT_CLASSES_STUDENT_ID + " INTEGER,"
                + STUDENT_CLASSES_CLASS_ID + " INTEGER"
                + " FOREIGN KEY ("+STUDENT_CLASSES_STUDENT_ID+") REFERENCES "+TABLE_STUDENTS+"("+STUDENT_CLASSES_ID+")"
                + " FOREIGN KEY ("+STUDENT_CLASSES_CLASS_ID+") REFERENCES "+TABLE_CLASSES+"("+CLASSES_ID+"));";

        String CREATE_CLASSES_TABLE = "CREATE TABLE "+TABLE_CLASSES
                +"("+ CLASSES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + CLASSES_NAME + " TEXT,"
                + CLASSES_ROOM + " TEXT,"
                + CLASSES_TIME + " TEXT,"
                + CLASSES_DATE + " TEXT," + ")";

        db.execSQL(CREATE_STUDENTS_TABLE);
        db.execSQL(CREATE_STUDENT_CLASSES_TABLE);
        db.execSQL(CREATE_CLASSES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENT_CLASSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLASSES);
        onCreate(db);
    }

    public void addStudent(Student student){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(STUDENT_NAME, student.getName());
        values.put(STUDENT_MATRIC, student.get_matric_number());
        values.put(STUDENT_CARDUID, student.get_card_uid());
        values.put(STUDENT_MODULE, student.get_module());

        db.insert(TABLE_STUDENTS, null, values);
        db.close();
    }

    public Student getStudent(int id){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_STUDENTS, new String[]{STUDENT_ID, STUDENT_NAME, STUDENT_MATRIC,
                        STUDENT_CARDUID, STUDENT_MODULE}, STUDENT_ID + "= ?",
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
        values.put(STUDENT_NAME, student.getName());
        values.put(STUDENT_MATRIC, student.get_matric_number());
        values.put(STUDENT_CARDUID, student.get_card_uid());
        values.put(STUDENT_MODULE, student.get_module());

        return db.update(TABLE_STUDENTS, values, STUDENT_ID + " = ?",
                new String[]{String.valueOf(student.getID())});

    }

    public void deleteStudent(Student student){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_STUDENTS, STUDENT_ID + "= ?",
                new String[]{String.valueOf(student.getID())});
        db.close();
    }

    public boolean checkExists(String carduid) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_STUDENTS, new String[]{STUDENT_ID, STUDENT_NAME, STUDENT_MATRIC,
                        STUDENT_CARDUID, STUDENT_MODULE}, STUDENT_CARDUID + "= ?",
                new String[]{String.valueOf(carduid)}, null, null, null, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }
    public void addClass(ClassInfo classInfo){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CLASSES_NAME, classInfo.getName());
        values.put(CLASSES_ROOM, classInfo.get_room());
        values.put(CLASSES_TIME, classInfo.get_time());
        values.put(CLASSES_DATE, classInfo.get_date());

        db.insert(TABLE_CLASSES, null, values);
        db.close();
    }

    public ClassInfo getClassInfo(int id){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CLASSES, new String[]{CLASSES_ID, CLASSES_NAME, CLASSES_ROOM,
                       CLASSES_TIME, CLASSES_DATE}, CLASSES_ID + "= ?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if(cursor != null) {
            cursor.moveToFirst();
        }
        ClassInfo classinfo = new ClassInfo (Integer.parseInt(cursor.getString(0)),cursor.getString(1),
                cursor.getString(2), cursor.getString(3), cursor.getString(4));
        return classinfo;
    }

    public List<ClassInfo> getAllClasses() {
        List<ClassInfo> classInfoList = new ArrayList();
        String selectQuery = "SELECT * FROM " + TABLE_STUDENTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()){
            do {
                ClassInfo classInfo = new ClassInfo();
                classInfo.setID(Integer.parseInt(cursor.getString(0)));
                classInfo.setName(cursor.getString(1));
                classInfo.set_room(cursor.getString(2));
                classInfo.set_time(cursor.getString(3));
                classInfo.set_date(cursor.getString(4));
                classInfoList.add(classInfo);
            } while (cursor.moveToNext());
        }
        return classInfoList;
    }

    public int getClassCount(){
        String countQuery = "SELECT * FROM " + TABLE_CLASSES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        return cursor.getCount();
    }

    public int updateClass(ClassInfo classInfo){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CLASSES_NAME, classInfo.getName());
        values.put(CLASSES_ROOM, classInfo.get_room());
        values.put(CLASSES_TIME, classInfo.get_time());
        values.put(CLASSES_DATE, classInfo.get_date());

        return db.update(TABLE_CLASSES, values, CLASSES_ID + " = ?",
                new String[]{String.valueOf(classInfo.getID())});

    }

    public void deleteClass(ClassInfo classInfo){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CLASSES, CLASSES_ID + "= ?",
                new String[]{String.valueOf(classInfo.getID())});
        db.close();
    }

    public void addStudentClasses(StudentClassInfo studentClassInfo){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(STUDENT_CLASSES_STUDENT_ID, studentClassInfo.get_studentID());
        values.put(STUDENT_CLASSES_CLASS_ID, studentClassInfo.get_classID());

        db.insert(TABLE_STUDENT_CLASSES, null, values);
        db.close();
    }

    public StudentClassInfo getStudentClasses(int id){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_STUDENT_CLASSES, new String[]{STUDENT_CLASSES_ID, STUDENT_CLASSES_STUDENT_ID, STUDENT_CLASSES_CLASS_ID}, STUDENT_CLASSES_ID + "= ?",
                new String[]{String.valueOf(id)}, null,null,null,null);
        if(cursor != null) {
            cursor.moveToFirst();
        }
        StudentClassInfo studentClassInfo = new StudentClassInfo (Integer.parseInt(cursor.getString(0)),cursor.getString(1),
                cursor.getString(2));
        return studentClassInfo;
    }

    public List<StudentClassInfo> getAllStudentClasses() {
        List<StudentClassInfo> studentClassInfoList = new ArrayList();
        String selectQuery = "SELECT * FROM " + TABLE_STUDENT_CLASSES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()){
            do {
                StudentClassInfo studentClassInfo = new StudentClassInfo();
                studentClassInfo.set_ID(Integer.parseInt(cursor.getString(0)));
                studentClassInfo.set_studentID(cursor.getString(1));
                studentClassInfo.set_classID(cursor.getString(2));
                studentClassInfoList.add(studentClassInfo);
            } while (cursor.moveToNext());
        }
        return studentClassInfoList;
    }

    public int getStudentClassesCount(){
        String countQuery = "SELECT * FROM " + TABLE_STUDENT_CLASSES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        return cursor.getCount();
    }

    public int updateStudentClasses(StudentClassInfo studentClassInfo){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(STUDENT_CLASSES_STUDENT_ID, studentClassInfo.get_studentID());
        values.put(STUDENT_CLASSES_CLASS_ID, studentClassInfo.get_classID());

        return db.update(TABLE_STUDENT_CLASSES, values, STUDENT_CLASSES_ID + " = ?",
                new String[]{String.valueOf(studentClassInfo.get_ID())});
    }

    public void deleteStudentClasses(StudentClassInfo studentClassInfo){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_STUDENT_CLASSES, STUDENT_CLASSES_ID + "= ?",
                new String[]{String.valueOf(studentClassInfo.get_ID())});
        db.close();
    }

}