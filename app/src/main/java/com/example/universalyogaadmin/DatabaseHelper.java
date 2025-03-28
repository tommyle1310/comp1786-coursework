package com.example.universalyogaadmin;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "YogaClasses.db";
    private static final int DATABASE_VERSION = 5;

    // Bảng Classes
    private static final String TABLE_CLASSES = "classes";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TEACHER_NAME = "teacherName";
    private static final String COLUMN_CLASS_NAME = "className";
    private static final String COLUMN_CLASS_TYPE = "classType";
    private static final String COLUMN_CLASS_TIME = "classTime";
    private static final String COLUMN_DAY_OF_WEEK = "dayOfWeek";
    private static final String COLUMN_PRICE = "price";
    private static final String COLUMN_CAPACITY = "capacity";
    private static final String COLUMN_DURATION = "duration";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_IMAGE_URI = "imageUri";
    private static final String COLUMN_IS_SYNCED = "isSynced";

    // Bảng Teachers
    private static final String TABLE_TEACHERS = "teachers";
    private static final String COLUMN_TEACHER_ID = "teacherId";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PHONE = "phone";
    private static final String COLUMN_TEACHER_IMAGE_URI = "imageUri";

    // Bảng Class Instances
    private static final String TABLE_CLASS_INSTANCES = "class_instances";
    private static final String COLUMN_INSTANCE_ID = "instanceId";
    private static final String COLUMN_CLASS_ID = "classId";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_INSTANCE_TEACHER_NAME = "teacherName";
    private static final String COLUMN_COMMENTS = "comments";
    private static final String COLUMN_INSTANCE_IS_SYNCED = "isSynced";

    // Câu lệnh tạo bảng Classes
    private static final String CREATE_TABLE_CLASSES = "CREATE TABLE " + TABLE_CLASSES + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_TEACHER_NAME + " TEXT, " +
            COLUMN_CLASS_NAME + " TEXT, " +
            COLUMN_CLASS_TYPE + " TEXT, " +
            COLUMN_CLASS_TIME + " TEXT, " +
            COLUMN_DAY_OF_WEEK + " TEXT, " +
            COLUMN_PRICE + " INTEGER, " +
            COLUMN_CAPACITY + " INTEGER, " +
            COLUMN_DURATION + " TEXT, " +
            COLUMN_DESCRIPTION + " TEXT, " +
            COLUMN_IMAGE_URI + " TEXT, " +
            COLUMN_IS_SYNCED + " INTEGER DEFAULT 0)";

    // Câu lệnh tạo bảng Teachers
    private static final String CREATE_TABLE_TEACHERS = "CREATE TABLE " + TABLE_TEACHERS + " (" +
            COLUMN_TEACHER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_NAME + " TEXT, " +
            COLUMN_EMAIL + " TEXT, " +
            COLUMN_PHONE + " TEXT, " +
            COLUMN_TEACHER_IMAGE_URI + " TEXT)";

    // Câu lệnh tạo bảng Class Instances
    private static final String CREATE_TABLE_CLASS_INSTANCES = "CREATE TABLE " + TABLE_CLASS_INSTANCES + " (" +
            COLUMN_INSTANCE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_CLASS_ID + " INTEGER, " +
            COLUMN_DATE + " TEXT, " +
            COLUMN_INSTANCE_TEACHER_NAME + " TEXT, " +
            COLUMN_COMMENTS + " TEXT, " +
            COLUMN_INSTANCE_IS_SYNCED + " INTEGER DEFAULT 0, " +
            "FOREIGN KEY(" + COLUMN_CLASS_ID + ") REFERENCES " + TABLE_CLASSES + "(" + COLUMN_ID + "))";

    private CloudSyncManager cloudSyncManager;
    private Context context;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        cloudSyncManager = new CloudSyncManager(context, this);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CLASSES);
        db.execSQL(CREATE_TABLE_TEACHERS);
        db.execSQL(CREATE_TABLE_CLASS_INSTANCES);

        // Thêm dữ liệu mẫu cho bảng teachers
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, "Tammy Tao");
        values.put(COLUMN_EMAIL, "tammy@example.com");
        values.put(COLUMN_PHONE, "123-456-7890");
        values.put(COLUMN_TEACHER_IMAGE_URI, "");
        db.insert(TABLE_TEACHERS, null, values);

        values.clear();
        values.put(COLUMN_NAME, "John Doe");
        values.put(COLUMN_EMAIL, "john@example.com");
        values.put(COLUMN_PHONE, "987-654-3210");
        values.put(COLUMN_TEACHER_IMAGE_URI, "");
        db.insert(TABLE_TEACHERS, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLASSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEACHERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLASS_INSTANCES);
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
        values.put(COLUMN_DAY_OF_WEEK, yogaClass.getDayOfWeek());
        values.put(COLUMN_PRICE, yogaClass.getPrice());
        values.put(COLUMN_CAPACITY, yogaClass.getCapacity());
        values.put(COLUMN_DURATION, yogaClass.getDuration());
        values.put(COLUMN_DESCRIPTION, yogaClass.getDescription());
        values.put(COLUMN_IMAGE_URI, yogaClass.getImageUri() != null ? yogaClass.getImageUri().toString() : null);
        values.put(COLUMN_IS_SYNCED, yogaClass.isSynced() ? 1 : 0);
        long id = db.insert(TABLE_CLASSES, null, values);
        db.close();
        return id;
    }

    public List<YogaClass> getAllClasses() {
        List<YogaClass> classList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        try (Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CLASSES, null)) {
            if (cursor.moveToFirst()) {
                do {
                    YogaClass yogaClass = new YogaClass(
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TEACHER_NAME)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLASS_NAME)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLASS_TYPE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLASS_TIME)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRICE)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CAPACITY)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DURATION)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
                    );
                    yogaClass.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                    yogaClass.setDayOfWeek(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DAY_OF_WEEK)));
                    String imageUriStr = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URI));
                    if (imageUriStr != null && !imageUriStr.isEmpty()) {
                        yogaClass.setImageUri(Uri.parse(imageUriStr));
                    }
                    yogaClass.setSynced(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_SYNCED)) == 1);
                    classList.add(yogaClass);
                } while (cursor.moveToNext());
            }
        } finally {
            db.close();
        }
        return classList;
    }

    public List<YogaClass> getUnsyncedClasses() {
        List<YogaClass> unsyncedClasses = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CLASSES, null, COLUMN_IS_SYNCED + " = ?", new String[]{"0"}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                YogaClass yogaClass = new YogaClass(
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TEACHER_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLASS_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLASS_TYPE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLASS_TIME)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRICE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CAPACITY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DURATION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
                );
                yogaClass.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                yogaClass.setDayOfWeek(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DAY_OF_WEEK)));
                String imageUriStr = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URI));
                if (imageUriStr != null && !imageUriStr.isEmpty()) {
                    yogaClass.setImageUri(Uri.parse(imageUriStr));
                }
                yogaClass.setSynced(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_SYNCED)) == 1);
                unsyncedClasses.add(yogaClass);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return unsyncedClasses;
    }

    public void markClassAsSynced(int classId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_IS_SYNCED, 1);
        db.update(TABLE_CLASSES, values, COLUMN_ID + " = ?", new String[]{String.valueOf(classId)});
        db.close();
    }

    public int updateClass(YogaClass yogaClass) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TEACHER_NAME, yogaClass.getTeacherName());
        values.put(COLUMN_CLASS_NAME, yogaClass.getClassName());
        values.put(COLUMN_CLASS_TYPE, yogaClass.getClassType());
        values.put(COLUMN_CLASS_TIME, yogaClass.getClassTime());
        values.put(COLUMN_DAY_OF_WEEK, yogaClass.getDayOfWeek());
        values.put(COLUMN_PRICE, yogaClass.getPrice());
        values.put(COLUMN_CAPACITY, yogaClass.getCapacity());
        values.put(COLUMN_DURATION, yogaClass.getDuration());
        values.put(COLUMN_DESCRIPTION, yogaClass.getDescription());
        values.put(COLUMN_IMAGE_URI, yogaClass.getImageUri() != null ? yogaClass.getImageUri().toString() : null);
        values.put(COLUMN_IS_SYNCED, yogaClass.isSynced() ? 1 : 0);
        int rowsAffected = db.update(TABLE_CLASSES, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(yogaClass.getId())});
        db.close();
        return rowsAffected;
    }

    public void deleteClass(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CLASS_INSTANCES, COLUMN_CLASS_ID + " = ?", new String[]{String.valueOf(id)});
        db.delete(TABLE_CLASSES, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        cloudSyncManager.deleteClassFromCloud(id);
    }

    // --- Quản lý Teachers ---
    public long addTeacher(Teacher teacher) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, teacher.getName());
        values.put(COLUMN_EMAIL, teacher.getEmail());
        values.put(COLUMN_PHONE, teacher.getPhone());
        values.put(COLUMN_TEACHER_IMAGE_URI, teacher.getImageUri() != null ? teacher.getImageUri().toString() : null);
        long id = db.insert(TABLE_TEACHERS, null, values);
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
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE))
                );
                teacher.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TEACHER_ID)));
                String imageUriString = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TEACHER_IMAGE_URI));
                if (imageUriString != null && !imageUriString.isEmpty()) {
                    teacher.setImageUri(Uri.parse(imageUriString));
                }
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
        values.put(COLUMN_TEACHER_IMAGE_URI, teacher.getImageUri() != null ? teacher.getImageUri().toString() : null);
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

    // --- Quản lý Class Instances ---
    public long addClassInstance(ClassInstance classInstance) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CLASS_ID, classInstance.getClassId());
        values.put(COLUMN_DATE, classInstance.getDate());
        values.put(COLUMN_INSTANCE_TEACHER_NAME, classInstance.getTeacherName());
        values.put(COLUMN_COMMENTS, classInstance.getComments());
        values.put(COLUMN_INSTANCE_IS_SYNCED, 0);
        long id = db.insert(TABLE_CLASS_INSTANCES, null, values);
        db.close();
        return id;
    }

    public List<ClassInstance> getClassInstancesByClassId(int classId) {
        List<ClassInstance> instanceList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CLASS_INSTANCES + " WHERE " + COLUMN_CLASS_ID + " = ?",
                new String[]{String.valueOf(classId)});
        if (cursor.moveToFirst()) {
            do {
                ClassInstance instance = new ClassInstance(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CLASS_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INSTANCE_TEACHER_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COMMENTS))
                );
                instance.setInstanceId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_INSTANCE_ID)));
                instanceList.add(instance);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return instanceList;
    }

    public List<ClassInstance> getUnsyncedClassInstances() {
        List<ClassInstance> instanceList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CLASS_INSTANCES + " WHERE " + COLUMN_INSTANCE_IS_SYNCED + " = 0", null);
        if (cursor.moveToFirst()) {
            do {
                ClassInstance instance = new ClassInstance(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CLASS_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INSTANCE_TEACHER_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COMMENTS))
                );
                instance.setInstanceId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_INSTANCE_ID)));
                instanceList.add(instance);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return instanceList;
    }

    public int updateClassInstance(ClassInstance classInstance) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CLASS_ID, classInstance.getClassId());
        values.put(COLUMN_DATE, classInstance.getDate());
        values.put(COLUMN_INSTANCE_TEACHER_NAME, classInstance.getTeacherName());
        values.put(COLUMN_COMMENTS, classInstance.getComments());
        values.put(COLUMN_INSTANCE_IS_SYNCED, 0);
        int rowsAffected = db.update(TABLE_CLASS_INSTANCES, values, COLUMN_INSTANCE_ID + " = ?",
                new String[]{String.valueOf(classInstance.getInstanceId())});
        db.close();
        return rowsAffected;
    }

    public void deleteClassInstance(int instanceId) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_CLASS_ID + " FROM " + TABLE_CLASS_INSTANCES + " WHERE " + COLUMN_INSTANCE_ID + " = ?",
                new String[]{String.valueOf(instanceId)});
        int classId = -1;
        if (cursor.moveToFirst()) {
            classId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CLASS_ID));
        }
        cursor.close();

        db.delete(TABLE_CLASS_INSTANCES, COLUMN_INSTANCE_ID + " = ?", new String[]{String.valueOf(instanceId)});
        db.close();

        if (classId != -1) {
            cloudSyncManager.deleteClassInstanceFromCloud(classId, instanceId);
        }
    }

    public void markClassInstanceAsSynced(int instanceId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_INSTANCE_IS_SYNCED, 1);
        db.update(TABLE_CLASS_INSTANCES, values, COLUMN_INSTANCE_ID + " = ?", new String[]{String.valueOf(instanceId)});
        db.close();
    }

    public void resetDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_CLASSES);
        db.execSQL("DELETE FROM " + TABLE_TEACHERS);
        db.execSQL("DELETE FROM " + TABLE_CLASS_INSTANCES);
        db.close();
    }
}