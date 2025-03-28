package com.example.universalyogaadmin;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

public class CloudSyncManager {
    private static final String TAG = "CloudSyncManager";
    private DatabaseHelper dbHelper;
    private Context context;

    public CloudSyncManager(Context context, DatabaseHelper dbHelper) {
        this.context = context;
        this.dbHelper = dbHelper;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void syncDataToCloud() {
        if (!isNetworkAvailable()) {
            Log.d(TAG, "No network available, cannot sync");
            Toast.makeText(context, "No network available, cannot sync", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d(TAG, "Syncing data to cloud...");

        // Kết nối tới Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference classesRef = database.getReference("classes");
        DatabaseReference instancesRef = database.getReference("class_instances");
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        // Đồng bộ Yoga Classes
        List<YogaClass> unsyncedClasses = dbHelper.getUnsyncedClasses();
        Log.d(TAG, "Number of unsynced classes: " + unsyncedClasses.size());
        if (unsyncedClasses.isEmpty()) {
            Log.d(TAG, "No unsynced classes to sync.");
        } else {
            for (YogaClass yogaClass : unsyncedClasses) {
                Log.d(TAG, "Syncing class: " + yogaClass.getClassName());
                String classId = String.valueOf(yogaClass.getId());

                // Xử lý hình ảnh nếu có
                Uri imageUri = yogaClass.getImageUri();
                Log.d(TAG, "Image URI for class " + yogaClass.getClassName() + ": " + (imageUri != null ? imageUri.toString() : "null"));
                if (imageUri != null && !imageUri.toString().isEmpty()) {
                    StorageReference imageRef = storageRef.child("class_images/" + classId + "_" + System.currentTimeMillis() + ".jpg");
                    try {
                        // Sao chép tệp từ imageUri vào bộ nhớ nội bộ
                        File localFile = new File(context.getCacheDir(), "temp_image_" + classId + ".jpg");
                        try (InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
                             FileOutputStream outputStream = new FileOutputStream(localFile)) {
                            if (inputStream == null) {
                                throw new Exception("Cannot open input stream for URI: " + imageUri);
                            }
                            byte[] buffer = new byte[1024];
                            int bytesRead;
                            while ((bytesRead = inputStream.read(buffer)) != -1) {
                                outputStream.write(buffer, 0, bytesRead);
                            }
                        }

                        // Kiểm tra tệp tạm có tồn tại và không rỗng không
                        if (!localFile.exists() || localFile.length() == 0) {
                            throw new Exception("Local file is empty or does not exist: " + localFile.getAbsolutePath());
                        }
                        Log.d(TAG, "Local file created successfully: " + localFile.getAbsolutePath() + ", size: " + localFile.length() + " bytes");

                        // Upload tệp từ bộ nhớ nội bộ
                        Uri fileUri = Uri.fromFile(localFile);
                        imageRef.putFile(fileUri)
                                .addOnSuccessListener(taskSnapshot -> {
                                    imageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                                        Log.d(TAG, "Image uploaded successfully, download URL: " + downloadUri);
                                        yogaClass.setImageUri(downloadUri); // Lưu URL tải xuống
                                        classesRef.child(classId).setValue(yogaClass.toMap(), (error, ref) -> {
                                            if (error == null) {
                                                dbHelper.markClassAsSynced(yogaClass.getId());
                                                Log.d(TAG, "Successfully synced class with image: " + yogaClass.getClassName());
                                                Toast.makeText(context, "Synced class: " + yogaClass.getClassName(), Toast.LENGTH_SHORT).show();
                                            } else {
                                                Log.e(TAG, "Failed to sync class: " + yogaClass.getClassName() + ", error: " + error.getMessage());
                                            }
                                        });
                                    }).addOnFailureListener(e -> {
                                        Log.e(TAG, "Failed to get download URL: " + e.getMessage());
                                        saveClassWithoutImage(classesRef, classId, yogaClass);
                                    });
                                })
                                .addOnFailureListener(e -> {
                                    Log.w(TAG, "Failed to upload image: " + e.getMessage());
                                    saveClassWithoutImage(classesRef, classId, yogaClass);
                                });

                        // Xóa tệp tạm sau khi upload
                        if (localFile.exists()) {
                            localFile.delete();
                            Log.d(TAG, "Temporary file deleted: " + localFile.getAbsolutePath());
                        }
                    } catch (Exception e) {
                        Log.w(TAG, "Failed to process image URI: " + imageUri + ", error: " + e.getMessage());
                        saveClassWithoutImage(classesRef, classId, yogaClass);
                    }
                } else {
                    saveClassWithoutImage(classesRef, classId, yogaClass);
                }
            }
        }

        // Đồng bộ Class Instances
        List<ClassInstance> unsyncedInstances = dbHelper.getUnsyncedClassInstances();
        Log.d(TAG, "Number of unsynced class instances: " + unsyncedInstances.size());
        if (unsyncedInstances.isEmpty()) {
            Log.d(TAG, "No unsynced class instances to sync.");
        } else {
            for (ClassInstance instance : unsyncedInstances) {
                Log.d(TAG, "Syncing class instance: " + instance.getInstanceId());
                String instanceId = String.valueOf(instance.getInstanceId());
                instancesRef.child(instanceId).setValue(instance, (error, ref) -> {
                    if (error == null) {
                        dbHelper.markClassInstanceAsSynced(instance.getInstanceId());
                        Log.d(TAG, "Successfully synced class instance: " + instance.getInstanceId());
                        Toast.makeText(context, "Synced class instance: " + instance.getInstanceId(), Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e(TAG, "Failed to sync class instance: " + instance.getInstanceId() + ", error: " + error.getMessage());
                    }
                });
            }
        }
    }

    private void saveClassWithoutImage(DatabaseReference classesRef, String classId, YogaClass yogaClass) {
        classesRef.child(classId).setValue(yogaClass.toMap(), (error, ref) -> {
            if (error == null) {
                dbHelper.markClassAsSynced(yogaClass.getId());
                Log.d(TAG, "Successfully synced class (no image): " + yogaClass.getClassName());
                Toast.makeText(context, "Synced class (no image): " + yogaClass.getClassName(), Toast.LENGTH_SHORT).show();
            } else {
                Log.e(TAG, "Failed to sync class: " + yogaClass.getClassName() + ", error: " + error.getMessage());
            }
        });
    }

    public void deleteClassFromCloud(int classId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference classesRef = database.getReference("classes");
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        // Xóa hình ảnh từ Firebase Storage (nếu có)
        StorageReference imageRef = storageRef.child("class_images/" + classId + ".jpg");
        imageRef.delete().addOnSuccessListener(aVoid -> {
            Log.d(TAG, "Successfully deleted image from cloud: " + classId);
        }).addOnFailureListener(e -> {
            Log.w(TAG, "No image found or failed to delete: " + classId + ", error: " + e.getMessage());
        });

        // Xóa dữ liệu từ Realtime Database
        classesRef.child(String.valueOf(classId)).removeValue((error, ref) -> {
            if (error == null) {
                Log.d(TAG, "Successfully deleted class from cloud: " + classId);
            } else {
                Log.e(TAG, "Failed to delete class from cloud: " + classId + ", error: " + error.getMessage());
            }
        });
    }

    public void deleteClassInstanceFromCloud(int classId, int instanceId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference instancesRef = database.getReference("class_instances");
        instancesRef.child(String.valueOf(instanceId)).removeValue((error, ref) -> {
            if (error == null) {
                Log.d(TAG, "Successfully deleted class instance from cloud: " + instanceId);
            } else {
                Log.e(TAG, "Failed to delete class instance from cloud: " + instanceId + ", error: " + error.getMessage());
            }
        });
    }
}