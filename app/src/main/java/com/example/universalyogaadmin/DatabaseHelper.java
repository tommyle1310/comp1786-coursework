package com.example.universalyogaadmin;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "YogaClasses.db";
    private static final int DATABASE_VERSION = 2; // Tăng từ 1 lên 2

    // Bảng Classes
    private static final String TABLE_CLASSES = "classes";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TEACHER_NAME = "teacherName";
    private static final String COLUMN_CLASS_NAME = "className";
    private static final String COLUMN_CLASS_TYPE = "classType";
    private static final String COLUMN_CLASS_TIME = "classTime";
    private static final String COLUMN_PRICE = "price";
    private static final String COLUMN_CAPACITY = "capacity";
    private static final String COLUMN_DURATION = "duration";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_IMAGE_URI = "imageUri";

    private static final String COLUMN_TEACHER_IMAGE_URI = "teacher_image_uri";

    // Bảng Teachers
    private static final String TABLE_TEACHERS = "teachers";
    private static final String COLUMN_TEACHER_ID = "teacherId";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PHONE = "phone";

    // Câu lệnh tạo bảng Classes
    private static final String CREATE_TABLE_CLASSES = "CREATE TABLE " + TABLE_CLASSES + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_TEACHER_NAME + " TEXT, " +
            COLUMN_CLASS_NAME + " TEXT, " +
            COLUMN_CLASS_TYPE + " TEXT, " +
            COLUMN_CLASS_TIME + " TEXT, " +
            COLUMN_PRICE + " INTEGER, " +
            COLUMN_CAPACITY + " INTEGER, " +
            COLUMN_DURATION + " TEXT, " +
            COLUMN_DESCRIPTION + " TEXT, " +
            COLUMN_IMAGE_URI + " TEXT)";

    // Câu lệnh tạo bảng Teachers
    private static final String CREATE_TABLE_TEACHERS = "CREATE TABLE " + TABLE_TEACHERS + " (" +
            COLUMN_TEACHER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_NAME + " TEXT, " +
            COLUMN_EMAIL + " TEXT, " +
            COLUMN_PHONE + " TEXT)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CLASSES);
        db.execSQL(CREATE_TABLE_TEACHERS);

        // Thêm dữ liệu mẫu cho bảng teachers
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, "Tammy Tao");
        values.put(COLUMN_EMAIL, "tammy@example.com");
        values.put(COLUMN_PHONE, "123-456-7890");
        db.insert(TABLE_TEACHERS, null, values);

        values.clear();
        values.put(COLUMN_NAME, "John Doe");
        values.put(COLUMN_EMAIL, "john@example.com");
        values.put(COLUMN_PHONE, "987-654-3210");
        db.insert(TABLE_TEACHERS, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLASSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEACHERS);
        onCreate(db);
    }

    // --- Quản lý Classes ---
    public long addClass(YogaClass yogaClass) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TEACHER_NAME, yogaClass.getTeacherName());
        values.put(COLUMN_CLASS_NAME, yogaClass.getClassName());
        values.put(COLUMN_CLASS_TYPE, yogaClass.getClassType());
        values.put(COLUMN_CLASS_TIME, yogaClass.getClassTime());
        values.put(COLUMN_PRICE, yogaClass.getPrice());
        values.put(COLUMN_CAPACITY, yogaClass.getCapacity());
        values.put(COLUMN_DURATION, yogaClass.getDuration());
        values.put(COLUMN_DESCRIPTION, yogaClass.getDescription());
        String imageUriString = yogaClass.getImageUri() != null ? yogaClass.getImageUri().toString() : null;
        values.put(COLUMN_IMAGE_URI, imageUriString);
        long id = db.insert(TABLE_CLASSES, null, values);
        db.close();
        return id;
    }

    public List<YogaClass> getAllClasses() {
        List<YogaClass> classList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CLASSES, null);

        if (cursor.moveToFirst()) {
            do {
                YogaClass yogaClass = new YogaClass(
                        cursor.getString(cursor.getColumnIndex(COLUMN_TEACHER_NAME)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_CLASS_NAME)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_CLASS_TYPE)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_CLASS_TIME)),
                        cursor.getInt(cursor.getColumnIndex(COLUMN_PRICE)),
                        cursor.getInt(cursor.getColumnIndex(COLUMN_CAPACITY)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_DURATION)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION))
                );
                yogaClass.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                String imageUriString = cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_URI));
                if (imageUriString != null) {
                    yogaClass.setImageUri(android.net.Uri.parse(imageUriString));
                }
                classList.add(yogaClass);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return classList;
    }

    public int updateClass(YogaClass yogaClass) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TEACHER_NAME, yogaClass.getTeacherName());
        values.put(COLUMN_CLASS_NAME, yogaClass.getClassName());
        values.put(COLUMN_CLASS_TYPE, yogaClass.getClassType());
        values.put(COLUMN_CLASS_TIME, yogaClass.getClassTime());
        values.put(COLUMN_PRICE, yogaClass.getPrice());
        values.put(COLUMN_CAPACITY, yogaClass.getCapacity());
        values.put(COLUMN_DURATION, yogaClass.getDuration());
        values.put(COLUMN_DESCRIPTION, yogaClass.getDescription());
        // Ép kiểu rõ ràng thành String để tránh lỗi ambiguous
        String imageUriString = yogaClass.getImageUri() != null ? yogaClass.getImageUri().toString() : null;
        values.put(COLUMN_IMAGE_URI, imageUriString);
        int rowsAffected = db.update(TABLE_CLASSES, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(yogaClass.getId())});
        db.close();
        return rowsAffected;
    }

    public void deleteClass(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CLASSES, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    // --- Quản lý Teachers ---
    public long addTeacher(Teacher teacher) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, teacher.getName());
        values.put(COLUMN_EMAIL, teacher.getEmail());
        values.put(COLUMN_PHONE, teacher.getPhone());

        long id = db.insert(TABLE_TEACHERS, null, values);
        android.util.Log.d("DatabaseHelper", "addTeacher: id = " + id);
        db.close();
        return id;
    }

    public List<Teacher> getAllTeachers() {
        List<Teacher> teacherList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TEACHERS, null);

        if (cursor.moveToFirst()) {
            do {
                Teacher teacher = new Teacher(
                        cursor.getString(cursor.getColumnIndex(COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_PHONE))
                );
                teacher.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_TEACHER_ID)));
                teacherList.add(teacher);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return teacherList;
    }

    public int updateTeacher(Teacher teacher) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, teacher.getName());
        values.put(COLUMN_EMAIL, teacher.getEmail());
        values.put(COLUMN_PHONE, teacher.getPhone());

        int rowsAffected = db.update(TABLE_TEACHERS, values, COLUMN_TEACHER_ID + " = ?",
                new String[]{String.valueOf(teacher.getId())});
        db.close();
        return rowsAffected;
    }

    public void deleteTeacher(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TEACHERS, COLUMN_TEACHER_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    // Reset database (xóa cả 2 bảng)
    public void resetDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_CLASSES);
        db.execSQL("DELETE FROM " + TABLE_TEACHERS);
        db.close();
    }
}